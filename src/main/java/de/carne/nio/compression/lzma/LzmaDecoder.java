/*
 * Copyright (c) 2016-2017 Holger de Carne and contributors, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.carne.nio.compression.lzma;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import de.carne.nio.compression.spi.Decoder;

/**
 * Decoder for LZMA compressed data.
 */
public class LzmaDecoder extends Decoder<LzmaDecoderProperties> {

	private enum State {
		BEGIN, DECODE, COPYFLUSH, EOFFLUSH, EOF
	}

	private final byte lzmaProperties;

	private final LzmaRangeDecoder rangeDecoder = new LzmaRangeDecoder();

	private final short[] isMatchDecoders = new short[Lzma.NUM_STATES << Lzma.NUM_POS_STATES_BITS_MAX];
	private final short[] isRepDecoders = new short[Lzma.NUM_STATES];
	private final short[] isRepG0Decoders = new short[Lzma.NUM_STATES];
	private final short[] isRepG1Decoders = new short[Lzma.NUM_STATES];
	private final short[] isRepG2Decoders = new short[Lzma.NUM_STATES];
	private final short[] isRep0LongDecoders = new short[Lzma.NUM_STATES << Lzma.NUM_POS_STATES_BITS_MAX];

	private final LzmaBitTreeDecoder[] posSlotDecoder = new LzmaBitTreeDecoder[Lzma.NUM_LEN2POS_STATES];
	private final short[] posDecoders = new short[Lzma.NUM_FULL_DISTANCES - Lzma.END_POS_MODEL_INDEX];

	private final LzmaBitTreeDecoder posAlignDecoder = new LzmaBitTreeDecoder(Lzma.NUM_ALIGN_BITS);

	private final LzmaLenDecoder lenDecoder;
	private final LzmaLenDecoder repLenDecoder;

	private final LzmaLiteralDecoder literalDecoder;

	private final int posStateMask;

	private final int dictionarySize;
	private final int dictionarySizeCheck;

	private final byte[] outBuffer;
	private int outBufferStart;
	private int outBufferEnd;
	private int copyDistance;
	private int copyLength;

	int lzmaState;
	int rep0;
	int rep1;
	int rep2;
	int rep3;

	private long currentPos;
	private byte prevByte;

	private long totalOut;
	private final long totalOutLimit;
	private State state = State.EOF;

	/**
	 * Construct {@linkplain LzmaDecoder}.
	 *
	 * @param properties The decoder properties to use.
	 */
	public LzmaDecoder(LzmaDecoderProperties properties) {
		super(LzmaFactory.COMPRESSION_NAME, properties);
		for (int decoderIndex = 0; decoderIndex < this.posSlotDecoder.length; decoderIndex++) {
			this.posSlotDecoder[decoderIndex] = new LzmaBitTreeDecoder(Lzma.NUM_POS_SLOT_BITS);
		}

		this.lzmaProperties = properties.getLcLpBpProperty();

		final int lzmaPropertiesValue = this.lzmaProperties & 0xFF;
		final int lc = lzmaPropertiesValue % 9;
		final int remainder = lzmaPropertiesValue / 9;
		final int lp = remainder % 5;
		final int pb = remainder / 5;

		if (lc > Lzma.NUM_LIT_CONTEXT_BITS_MAX || lp > 4 || pb > Lzma.NUM_POS_STATES_BITS_MAX) {
			throw new IllegalArgumentException("Invalid LZMA properties: " + lzmaPropertiesValue);
		}
		this.literalDecoder = new LzmaLiteralDecoder(lp, lc);

		final int numPosStates = 1 << pb;

		this.lenDecoder = new LzmaLenDecoder(numPosStates);
		this.repLenDecoder = new LzmaLenDecoder(numPosStates);
		this.posStateMask = numPosStates - 1;
		if (properties.getDictionarySizeProperty() < 0) {
			throw new IllegalArgumentException(
					"Invalid LZMA dictionary size: " + properties.getDictionarySizeProperty());
		}
		this.dictionarySize = properties.getDictionarySizeProperty();
		this.dictionarySizeCheck = Math.max(this.dictionarySize, 1);
		this.outBuffer = new byte[Math.max(this.dictionarySizeCheck, (1 << 12))];
		this.totalOutLimit = (properties.getDecodedSizeProperty() >= 0 ? properties.getDecodedSizeProperty()
				: Long.MAX_VALUE);
		init();
	}

	private void init() {
		LzmaRangeDecoder.initBitModels(this.isMatchDecoders);
		LzmaRangeDecoder.initBitModels(this.isRep0LongDecoders);
		LzmaRangeDecoder.initBitModels(this.isRepDecoders);
		LzmaRangeDecoder.initBitModels(this.isRepG0Decoders);
		LzmaRangeDecoder.initBitModels(this.isRepG1Decoders);
		LzmaRangeDecoder.initBitModels(this.isRepG2Decoders);
		LzmaRangeDecoder.initBitModels(this.posDecoders);
		this.literalDecoder.reset();
		for (final LzmaBitTreeDecoder decoder : this.posSlotDecoder) {
			decoder.reset();
		}
		this.lenDecoder.reset();
		this.repLenDecoder.reset();
		this.posAlignDecoder.reset();
		this.rangeDecoder.reset();
		this.outBufferStart = 0;
		this.outBufferEnd = 0;
		this.copyDistance = 0;
		this.copyLength = 0;
		this.totalOut = 0;
		this.state = State.BEGIN;
	}

	@Override
	public int decode(ByteBuffer dst, ReadableByteChannel src) throws IOException {
		long beginTime = beginProcessing();
		int decoded = -1;
		int dstRemainingStart = dst.remaining();

		try {
			if (this.state != State.EOF) {
				final long totalOutStart = this.totalOut;

				while (this.state != State.EOF && dst.remaining() > 0) {
					switch (this.state) {
					case BEGIN:
						this.rangeDecoder.beginDecode(src);
						this.lzmaState = Lzma.stateInit();
						this.rep0 = this.rep1 = this.rep2 = this.rep3 = 0;
						this.currentPos = 0;
						this.prevByte = 0;
						this.state = State.DECODE;
					case DECODE:
						decodeChunk(dst, src);
						break;
					case COPYFLUSH:
						copyBlock(dst);
						break;
					case EOFFLUSH:
						flush(dst, State.EOF);
						break;
					case EOF:
						break;
					}
				}
				decoded = (int) (this.totalOut - totalOutStart);
			}
		} finally {
			endProcessing(beginTime, Math.max(decoded, 0), dst.remaining() - dstRemainingStart);
		}
		return decoded;
	}

	private void decodeChunk(ByteBuffer dst, ReadableByteChannel src) throws IOException {
		while (this.state == State.DECODE) {
			final int posState = (int) this.currentPos & this.posStateMask;

			if (this.rangeDecoder.decodeBit(src, this.isMatchDecoders,
					(this.lzmaState << Lzma.NUM_POS_STATES_BITS_MAX) + posState) == 0) {
				final LzmaLiteralDecoder.Decoder2 decoder2 = this.literalDecoder.getDecoder((int) this.currentPos,
						this.prevByte);

				if (!Lzma.stateIsCharState(this.lzmaState)) {
					this.prevByte = decoder2.decodeWithMatchByte(src, this.rangeDecoder, getByte(this.rep0));
				} else {
					this.prevByte = decoder2.decodeNormal(src, this.rangeDecoder);
				}
				this.lzmaState = Lzma.stateUpdateChar(this.lzmaState);
				this.currentPos++;
				putByte(dst, this.prevByte);
			} else {
				int len;

				if (this.rangeDecoder.decodeBit(src, this.isRepDecoders, this.lzmaState) == 1) {
					len = 0;
					if (this.rangeDecoder.decodeBit(src, this.isRepG0Decoders, this.lzmaState) == 0) {
						if (this.rangeDecoder.decodeBit(src, this.isRep0LongDecoders,
								(this.lzmaState << Lzma.NUM_POS_STATES_BITS_MAX) + posState) == 0) {
							this.lzmaState = Lzma.stateUpdateShortRep(this.lzmaState);
							len = 1;
						}
					} else {
						int distance;

						if (this.rangeDecoder.decodeBit(src, this.isRepG1Decoders, this.lzmaState) == 0) {
							distance = this.rep1;
						} else {
							if (this.rangeDecoder.decodeBit(src, this.isRepG2Decoders, this.lzmaState) == 0) {
								distance = this.rep2;
							} else {
								distance = this.rep3;
								this.rep3 = this.rep2;
							}
							this.rep2 = this.rep1;
						}
						this.rep1 = this.rep0;
						this.rep0 = distance;
					}
					if (len == 0) {
						len = this.repLenDecoder.decode(src, this.rangeDecoder, posState) + Lzma.MATCH_MIN_LEN;
						this.lzmaState = Lzma.stateUpdateRep(this.lzmaState);
					}
				} else {
					this.rep3 = this.rep2;
					this.rep2 = this.rep1;
					this.rep1 = this.rep0;
					len = Lzma.MATCH_MIN_LEN + this.lenDecoder.decode(src, this.rangeDecoder, posState);
					this.lzmaState = Lzma.stateUpdateMatch(this.lzmaState);

					final int posSlot = this.posSlotDecoder[Lzma.getLenToPosState(len)].decode(src, this.rangeDecoder);

					if (posSlot >= Lzma.START_POS_MODEL_INDEX) {
						final int numDirectBits = (posSlot >> 1) - 1;

						this.rep0 = ((2 | (posSlot & 1)) << numDirectBits);
						if (posSlot < Lzma.END_POS_MODEL_INDEX) {
							this.rep0 += LzmaBitTreeDecoder.reverseDecode(src, this.posDecoders,
									this.rep0 - posSlot - 1, this.rangeDecoder, numDirectBits);
						} else {
							this.rep0 += this.rangeDecoder.decodeDirectBits(src,
									numDirectBits - Lzma.NUM_ALIGN_BITS) << Lzma.NUM_ALIGN_BITS;
							this.rep0 += this.posAlignDecoder.reverseDecode(src, this.rangeDecoder);
							if (this.rep0 < 0) {
								if (this.rep0 == -1) {
									this.state = State.EOFFLUSH;
								} else {
									this.state = State.EOFFLUSH;
								}
							}
						}
					} else {
						this.rep0 = posSlot;
					}
				}
				if (this.rep0 >= this.currentPos || this.rep0 >= this.dictionarySizeCheck) {
					this.state = State.EOFFLUSH;
				}
				if (this.state == State.DECODE) {
					this.copyDistance = this.rep0;
					this.copyLength = len;
					copyBlock(dst);
					this.currentPos += len;
					this.prevByte = getByte(this.copyLength);
				}
			}
		}
	}

	private void flush(ByteBuffer dst, State nextState) {
		final int outBufferLength = this.outBufferEnd - this.outBufferStart;
		final long outRemaining = this.totalOutLimit - this.totalOut;
		final int flushLength = Math.min(outBufferLength, (int) Math.min(outRemaining, dst.remaining()));

		dst.put(this.outBuffer, this.outBufferStart, flushLength);
		this.totalOut += flushLength;
		this.outBufferStart += flushLength;
		if (this.outBufferStart >= this.outBufferEnd) {
			this.state = nextState;
		}
		if (this.outBufferEnd >= this.outBuffer.length) {
			this.outBufferStart = this.outBufferEnd = 0;
		}
		if (this.totalOut >= this.totalOutLimit) {
			this.state = State.EOF;
		}
	}

	private boolean outBufferLimitReached() {
		return this.outBufferEnd >= this.outBuffer.length
				|| (this.totalOut + (this.outBufferEnd - this.outBufferStart) >= this.totalOutLimit);
	}

	private void copyBlock(ByteBuffer dst) {
		int pos = this.outBufferEnd - this.copyDistance - 1;

		if (pos < 0) {
			pos += this.outBuffer.length;
		}
		for (; this.copyLength > 0; this.copyLength--) {
			if (pos >= this.outBuffer.length) {
				pos = 0;
			}
			this.outBuffer[this.outBufferEnd++] = this.outBuffer[pos++];
			if (outBufferLimitReached()) {
				flush(dst, State.COPYFLUSH);
			}
		}
		if (this.copyLength == 0) {
			this.state = State.DECODE;
		}
	}

	private void putByte(ByteBuffer dst, byte b) {
		this.outBuffer[this.outBufferEnd++] = b;
		if (outBufferLimitReached()) {
			flush(dst, State.DECODE);
		}
	}

	private byte getByte(int distance) {
		int pos = this.outBufferEnd - distance - 1;

		if (pos < 0) {
			pos += this.outBuffer.length;
		}
		return this.outBuffer[pos];
	}

}
