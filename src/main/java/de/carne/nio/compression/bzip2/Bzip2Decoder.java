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
package de.carne.nio.compression.bzip2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import de.carne.nio.compression.InvalidDataException;
import de.carne.nio.compression.Nullable;
import de.carne.nio.compression.common.BitDecoder;
import de.carne.nio.compression.common.HuffmanDecoder;
import de.carne.nio.compression.common.MsbBitstreamBitRegister;
import de.carne.nio.compression.spi.Decoder;

/**
 * Decoder for bzip2 compressed data.
 */
public class Bzip2Decoder extends Decoder<Bzip2DecoderProperties> {

	private enum State {
		BLOCKBEGIN, BLOCKDECODEA, BLOCKDECODEB, BLOCKDECODEC, EOF
	}

	private final BitDecoder bitDecoder = new BitDecoder(new MsbBitstreamBitRegister());
	private final int blockSizeLimit;
	private int blockSize;
	private int blockCRC;
	private int blockCRCReg;
	private int combinedCRCReg;
	private boolean crcTestsPassed;
	private boolean blockRandomized;
	private int blockOrigPtr;
	@Nullable
	private int mtfTable[];
	@Nullable
	private byte selectors[];
	@Nullable
	private HuffmanDecoder decoders[];
	@Nullable
	private int counters[];
	private int decodePosition;
	private int decodePrevious;
	private int decodeRepeatCount;
	private int decodeCurrent;
	private int rndIndex;
	private int rndCounter;
	private State state = State.EOF;

	/**
	 * Construct {@linkplain Bzip2Decoder}.
	 */
	public Bzip2Decoder() {
		this(new Bzip2DecoderProperties());
	}

	/**
	 * Construct {@linkplain Bzip2Decoder}.
	 *
	 * @param properties The decoder properties.
	 */
	public Bzip2Decoder(Bzip2DecoderProperties properties) {
		super(Bzip2Factory.COMPRESSION_NAME, properties);
		this.blockSizeLimit = properties.getBlockSizeProperty().ordinal() * Bzip2.BLOCK_SIZE_UNIT;
		reset0();
	}

	private void reset0() {
		this.bitDecoder.reset();
		this.combinedCRCReg = 0;
		this.crcTestsPassed = true;
		this.mtfTable = null;
		this.selectors = null;
		this.decoders = null;
		this.counters = null;
		this.state = State.BLOCKBEGIN;
	}

	@Override
	public synchronized void reset() {
		super.reset();
		reset0();
	}

	@Override
	public int decode(ByteBuffer dst, ReadableByteChannel src) throws IOException {
		long beginTime = beginProcessing();
		int decoded = 0;
		int dstRemainingStart = dst.remaining();

		try {
			decoded = decode0(dst, src);
		} finally {
			endProcessing(beginTime, Math.max(decoded, 0), dstRemainingStart - dst.remaining());
		}
		return decoded;
	}

	private int decode0(ByteBuffer dst, ReadableByteChannel src) throws IOException {
		int decoded = 0;

		switch (this.state) {
		case BLOCKBEGIN:
			decoded = blockBegin(src) + decode0(dst, src);
			break;
		case BLOCKDECODEA:
		case BLOCKDECODEB:
		case BLOCKDECODEC:
			if (this.blockRandomized) {
				blockDecodeR(dst);
			} else {
				blockDecode(dst);
			}
			break;
		case EOF:
			decoded = -1;
			break;
		}
		return decoded;
	}

	private int blockBegin(ReadableByteChannel src) throws IOException {
		final long totalInStart = this.bitDecoder.totalIn();
		final byte sig0 = (byte) this.bitDecoder.decodeBits(src, 8);
		final byte sig1 = (byte) this.bitDecoder.decodeBits(src, 8);
		final byte sig2 = (byte) this.bitDecoder.decodeBits(src, 8);
		final byte sig3 = (byte) this.bitDecoder.decodeBits(src, 8);
		final byte sig4 = (byte) this.bitDecoder.decodeBits(src, 8);
		final byte sig5 = (byte) this.bitDecoder.decodeBits(src, 8);

		if (sig0 == (byte) 0x31 && sig1 == (byte) 0x41 && sig2 == (byte) 0x59 && sig3 == (byte) 0x26
				&& sig4 == (byte) 0x53 && sig5 == (byte) 0x59) {
			this.blockCRC = this.bitDecoder.decodeBits(src, 32);
			shiftCombinedCRC();
			this.blockCRCReg = -1;
			this.blockRandomized = this.bitDecoder.decodeBits(src, 1) != 0;
			this.blockOrigPtr = this.bitDecoder.decodeBits(src, 24);
			if (this.blockOrigPtr >= this.blockSizeLimit) {
				throw new InvalidDataException(this.blockOrigPtr);
			}

			final boolean inUse[] = new boolean[16];

			for (int inUseIndex = 0; inUseIndex < inUse.length; inUseIndex++) {
				inUse[inUseIndex] = this.bitDecoder.decodeBits(src, 1) != 0;
			}
			this.mtfTable = new int[64];

			int mtfCount = 0;

			for (int mtf = 0; mtf < 0x100; mtf++) {
				if (inUse[mtf >> 4]) {
					if (this.bitDecoder.decodeBits(src, 1) != 0) {
						this.mtfTable[mtfCount >> 2] |= (mtf << ((mtfCount & 3) << 3));
						mtfCount++;
					}
				}
			}
			if (mtfCount == 0) {
				throw new InvalidDataException();
			}

			final int symbolCount = mtfCount + 2;
			final int huffmanCount = this.bitDecoder.decodeBits(src, 3);

			if (huffmanCount < Bzip2.MIN_HUFFMAN_COUNT || Bzip2.MAX_HUFFMAN_COUNT < huffmanCount) {
				throw new InvalidDataException(huffmanCount);
			}

			final int selectorCount = this.bitDecoder.decodeBits(src, 15);

			if (selectorCount < Bzip2.MIN_SELECTOR_COUNT || Bzip2.MAX_SELECTOR_COUNT < selectorCount) {
				throw new InvalidDataException(selectorCount);
			}

			final byte mtfPositions[] = new byte[huffmanCount];

			for (byte mtfPositionIndex = 0; mtfPositionIndex < mtfPositions.length; mtfPositionIndex++) {
				mtfPositions[mtfPositionIndex] = mtfPositionIndex;
			}
			this.selectors = new byte[selectorCount];
			for (int selectorIndex = 0; selectorIndex < this.selectors.length; selectorIndex++) {
				int runLength = 0;

				while (this.bitDecoder.decodeBits(src, 1) != 0) {
					runLength++;
					if (runLength >= mtfPositions.length) {
						throw new InvalidDataException();
					}
				}

				final byte position = mtfPositions[runLength];

				while (runLength > 0) {
					mtfPositions[runLength] = mtfPositions[runLength - 1];
					runLength--;
				}
				this.selectors[selectorIndex] = mtfPositions[0] = position;
			}
			this.decoders = new HuffmanDecoder[huffmanCount];
			for (int huffmanIndex = 0; huffmanIndex < this.decoders.length; huffmanIndex++) {
				final byte lengths[] = new byte[Bzip2.MAX_HUFFMAN_SYMBOL_COUNT];
				int length = this.bitDecoder.decodeBits(src, 5);
				int lengthsIndex = 0;

				while (lengthsIndex < symbolCount) {
					while (this.bitDecoder.decodeBits(src, 1) != 0) {
						if (length < 1 || Bzip2.MAX_HUFFMAN_BITS < length) {
							throw new InvalidDataException();
						}
						length += 1 - (this.bitDecoder.decodeBits(src, 1) << 1);
					}
					lengths[lengthsIndex] = (byte) (length & 0xff);
					lengthsIndex++;
				}

				final HuffmanDecoder decoder = new HuffmanDecoder(Bzip2.MAX_HUFFMAN_BITS,
						Bzip2.MAX_HUFFMAN_SYMBOL_COUNT);

				decoder.setCodeLengths(lengths);
				this.decoders[huffmanIndex] = decoder;
			}
			this.counters = new int[0x100 + this.blockSizeLimit];
			this.blockSize = 0;

			boolean done = false;
			int groupSize = 0;
			int groupIndex = 0;
			HuffmanDecoder currentDecoder = null;
			int runCounter = 0;
			int runPower = 0;

			while (!done) {
				if (groupSize == 0) {
					if (groupIndex >= selectorCount) {
						throw new InvalidDataException();
					}
					groupSize = 50;
					currentDecoder = this.decoders[this.selectors[groupIndex]];
					groupIndex++;
				}
				groupSize--;

				assert currentDecoder != null;

				final int nextSymbol = currentDecoder.decodeSymbol(src, this.bitDecoder, 0);

				if (nextSymbol < 2) {
					runCounter += (nextSymbol + 1) << runPower;
					runPower++;
					if (this.blockSizeLimit - this.blockSize < runCounter) {
						throw new InvalidDataException();
					}
				} else {
					if (runCounter != 0) {
						final int mtf = mtfHead();

						this.counters[mtf] += runCounter;
						do {
							this.counters[0x100 + this.blockSize] = mtf;
							this.blockSize++;
							runCounter--;
						} while (runCounter != 0);
						runPower = 0;
					}
					if (nextSymbol <= mtfCount) {
						final int mtf = mtfGetAndMove(nextSymbol - 1);

						if (this.blockSize >= this.blockSizeLimit) {
							throw new InvalidDataException();
						}
						this.counters[mtf]++;
						this.counters[0x100 + this.blockSize] = mtf;
						this.blockSize++;
					} else if (nextSymbol == (mtfCount + 1)) {
						done = true;
					} else {
						throw new InvalidDataException();
					}
				}
			}
			if (this.blockSize > this.blockSizeLimit) {
				throw new InvalidDataException();
			}

			int sum = 0;

			for (int counterIndex = 0; counterIndex < 0x100; counterIndex++) {
				sum += this.counters[counterIndex];
				this.counters[counterIndex] = sum - this.counters[counterIndex];
			}
			for (int counterIndex = 0; counterIndex < this.blockSize; counterIndex++) {
				this.counters[0x100
						+ this.counters[this.counters[0x100 + counterIndex] & 0xff]++] |= (counterIndex << 8);
			}
			this.state = State.BLOCKDECODEA;
		} else if (sig0 == (byte) 0x17 && sig1 == (byte) 0x72 && sig2 == (byte) 0x45 && sig3 == (byte) 0x38
				&& sig4 == (byte) 0x50 && sig5 == (byte) 0x90) {
			this.blockCRC = this.bitDecoder.decodeBits(src, 32);
			this.crcTestsPassed = this.crcTestsPassed && this.blockCRC == this.combinedCRCReg;
			this.state = State.EOF;
		} else {
			throw new InvalidDataException(sig0, sig1, sig2, sig3, sig4, sig5);
		}
		return (int) (this.bitDecoder.totalIn() - totalInStart);
	}

	private void blockDecode(ByteBuffer dst) {
		if (this.state == State.BLOCKDECODEA) {
			this.decodePosition = this.counters[0x100 + (this.counters[0x100 + this.blockOrigPtr] >>> 8)];
			this.decodePrevious = this.decodePosition & 0xff;
			this.decodeRepeatCount = 0;
			this.state = State.BLOCKDECODEB;
		}
		while (this.blockSize > 0 && dst.remaining() > 0) {
			if (this.state == State.BLOCKDECODEB) {
				this.decodeCurrent = this.decodePosition & 0xff;
				this.decodePosition = this.counters[0x100 + (this.decodePosition >>> 8)];
				this.state = State.BLOCKDECODEC;
			}
			if (this.decodeRepeatCount == 4) {
				while (this.decodeCurrent > 0 && dst.remaining() > 0) {
					final byte b = (byte) (this.decodePrevious & 0xff);

					shiftBlockCRC(b);
					dst.put(b);
					this.decodeCurrent--;
				}
				if (this.decodeCurrent == 0) {
					this.decodeRepeatCount = 0;
					this.state = State.BLOCKDECODEB;
					this.blockSize--;
				}
			} else {
				if (this.decodeCurrent != this.decodePrevious) {
					this.decodeRepeatCount = 0;
				}
				this.decodeRepeatCount++;
				this.decodePrevious = this.decodeCurrent;

				final byte b = (byte) (this.decodeCurrent & 0xff);

				shiftBlockCRC(b);
				dst.put(b);
				this.state = State.BLOCKDECODEB;
				this.blockSize--;
			}
		}
		if (this.blockSize == 0) {
			this.crcTestsPassed = this.crcTestsPassed && this.blockCRC == (this.blockCRCReg ^ -1);
			this.state = State.BLOCKBEGIN;
		}
	}

	private void blockDecodeR(ByteBuffer dst) {
		if (this.state == State.BLOCKDECODEA) {
			this.decodePosition = this.counters[0x100 + (this.counters[0x100 + this.blockOrigPtr] >>> 8)];
			this.decodePrevious = this.decodePosition & 0xff;
			this.decodeRepeatCount = 0;
			this.rndIndex = 1;
			this.rndCounter = Bzip2.RNDTABLE[0] - 2;
			this.state = State.BLOCKDECODEB;
		}
		while (this.blockSize > 0 && dst.remaining() > 0) {
			if (this.state == State.BLOCKDECODEB) {
				this.decodeCurrent = this.decodePosition & 0xff;
				this.decodePosition = this.counters[0x100 + (this.decodePosition >>> 8)];
				if (this.rndCounter == 0) {
					this.decodeCurrent ^= 1;
					this.rndCounter = Bzip2.RNDTABLE[this.rndIndex];
					this.rndIndex = (this.rndIndex + 1) & 0x1ff;
				}
				this.rndCounter--;
				this.state = State.BLOCKDECODEC;
			}
			if (this.decodeRepeatCount == 4) {
				while (this.decodeCurrent > 0 && dst.remaining() > 0) {
					dst.put((byte) (this.decodePrevious & 0xff));
					this.decodeCurrent--;
				}
				if (this.decodeCurrent == 0) {
					this.decodeRepeatCount = 0;
					this.state = State.BLOCKDECODEB;
					this.blockSize--;
				}
			} else {
				if (this.decodeCurrent != this.decodePrevious) {
					this.decodeRepeatCount = 0;
				}
				this.decodeRepeatCount++;
				this.decodePrevious = this.decodeCurrent;
				dst.put((byte) (this.decodeCurrent & 0xff));
				this.state = State.BLOCKDECODEB;
				this.blockSize--;
			}
		}
		if (this.blockSize == 0) {
			this.state = State.BLOCKBEGIN;
		}
	}

	private int mtfHead() {
		return this.mtfTable[0] & 0xff;
	}

	private int mtfGetAndMove(int position) {
		int limit = position >> 2;
		final int position2 = (position & 3) << 3;
		int previous = (this.mtfTable[limit] >>> position2) & 0xff;
		int index = 0;
		int next;

		if ((limit & 1) != 0) {
			next = this.mtfTable[0];
			this.mtfTable[0] = (next << 8) | previous;
			previous = (next >>> (3 << 3));
			index = 1;
			limit--;
		}
		while (index < limit) {
			final int mtf0 = this.mtfTable[index];
			final int mtf1 = this.mtfTable[index + 1];

			this.mtfTable[index] = (mtf0 << 8) | previous;
			this.mtfTable[index + 1] = (mtf1 << 8) | (mtf0 >>> (3 << 3));
			previous = (mtf1 >>> (3 << 3));
			index += 2;
		}
		next = this.mtfTable[index];

		final int mask = (0x100 << position2) - 1;

		this.mtfTable[index] = (next & ~mask) | (((next << 8) | previous) & mask);
		return this.mtfTable[0] & 0xff;
	}

	private void shiftBlockCRC(byte b) {
		this.blockCRCReg = Bzip2.CRCTABLE[(this.blockCRCReg >>> 24) ^ (b & 0xff)] ^ (this.blockCRCReg << 8);
	}

	private void shiftCombinedCRC() {
		this.combinedCRCReg = ((this.combinedCRCReg << 1) | (this.combinedCRCReg >>> 31)) ^ this.blockCRC;
	}

}
