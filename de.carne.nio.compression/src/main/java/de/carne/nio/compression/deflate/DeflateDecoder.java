/*
 * Copyright (c) 2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.nio.compression.deflate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.carne.nio.compression.InvalidDataException;
import de.carne.nio.compression.common.BitDecoder;
import de.carne.nio.compression.common.BitRegister;
import de.carne.nio.compression.common.HistoryBuffer;
import de.carne.nio.compression.common.HuffmanDecoder;
import de.carne.nio.compression.common.LSBBitstreamBitRegister;
import de.carne.nio.compression.common.LSBBytesBitRegister;
import de.carne.nio.compression.spi.Decoder;

/**
 * Deflate decoder:
 * <a href="https://en.wikipedia.org/wiki/DEFLATE">https://en.wikipedia.org/wiki
 * /DEFLATE</a>
 */
public class DeflateDecoder extends Decoder {

	private final HashSet<DeflateMode> modes;
	private final BitDecoder bitDecoder = new BitDecoder(new BitRegister[] {

			new LSBBitstreamBitRegister(),

			new LSBBytesBitRegister()

	}, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff);
	private final HuffmanDecoder mainDecoder = new HuffmanDecoder(Deflate.HUFFMAN_BITS, Deflate.FIXED_MAIN_TABLE_SIZE);
	private final HuffmanDecoder distDecoder = new HuffmanDecoder(Deflate.HUFFMAN_BITS, Deflate.FIXED_DIST_TABLE_SIZE);
	private final HuffmanDecoder levelDecoder = new HuffmanDecoder(Deflate.HUFFMAN_BITS, Deflate.LEVEL_TABLE_SIZE);
	private final HistoryBuffer historyBuffer;
	private int blockRemaining;
	private boolean readTables;
	private boolean finalBlock;
	private int rep0Dist;
	private boolean storedMode;
	private int storedBlockSize;
	private int numDistLevels;

	/**
	 * Construct {@code DeflateDecoder}.
	 */
	public DeflateDecoder() {
		this(Collections.EMPTY_SET);
	}

	/**
	 * Construct {@code DeflateDecoder}.
	 *
	 * @param modes Decoder modes to use.
	 */
	public DeflateDecoder(Set<DeflateMode> modes) {
		assert modes != null;

		this.modes = new HashSet<>(modes);
		this.historyBuffer = new HistoryBuffer(
				this.modes.contains(DeflateMode.OPTION_HISTORY64K) ? Deflate.HISTORY_SIZE_64 : Deflate.HISTORY_SIZE_32);
		init();
	}

	private void init() {
		this.bitDecoder.reset();
		this.historyBuffer.clear();
		this.blockRemaining = -2;
		this.readTables = true;
		this.finalBlock = false;
		this.blockRemaining = -2;
		this.rep0Dist = -1;
		this.storedMode = false;
		this.storedBlockSize = 0;
		this.numDistLevels = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.nio.compression.Compression#reset()
	 */
	@Override
	public synchronized void reset() {
		super.reset();
		init();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.nio.compression.spi.Decoder#decode(java.nio.ByteBuffer,
	 * java.nio.channels.ReadableByteChannel)
	 */
	@Override
	public int decode(ByteBuffer dst, ReadableByteChannel src) throws IOException {
		long beginTime = beginProcessin();
		int decoded = -1;
		int emitted = 0;

		try {
			if (this.blockRemaining != -1) {
				long decodeStart = this.bitDecoder.totalIn();

				if (decodeStart == 0l && this.modes.contains(DeflateMode.FORMAT_ZLIB)) {
					processZLibHeader(src);
				}
				emitted += this.historyBuffer.flush(dst);

				int decodeRemaining = dst.remaining();

				while (decodeRemaining > 0 && this.blockRemaining != -1) {
					decodeBlock(src, decodeRemaining);
					emitted += this.historyBuffer.flush(dst);
					decodeRemaining = dst.remaining();
				}
				emitted += this.historyBuffer.flush(dst);
				if (this.blockRemaining == -1) {
					if (this.modes.contains(DeflateMode.FORMAT_ZLIB)) {
						processZLibTrailer(src);
					}
				}
				decoded = (int) (this.bitDecoder.totalIn() - decodeStart);
			} else if (this.modes.contains(DeflateMode.OPTION_RESTART_AFTER_EOS)) {
				this.blockRemaining = -2;
				this.bitDecoder.clear();
			}
		} finally {
			endProcessing(beginTime, Math.max(decoded, 0), emitted);
		}
		return decoded;
	}

	private void processZLibHeader(ReadableByteChannel src) throws IOException {
		this.bitDecoder.decodeBits(src, 8, 1);
		this.bitDecoder.decodeBits(src, 4, 1);
		this.bitDecoder.decodeBits(src, 4, 1);
	}

	private void processZLibTrailer(ReadableByteChannel src) throws IOException {
		this.bitDecoder.alignToByte();
		this.bitDecoder.decodeBits(src, 8, 1);
		this.bitDecoder.decodeBits(src, 8, 1);
		this.bitDecoder.decodeBits(src, 8, 1);
		this.bitDecoder.decodeBits(src, 8, 1);
	}

	private void decodeBlock(ReadableByteChannel src, int len) throws IOException {
		int decodeRemaining = len;

		if (this.blockRemaining == -2) {
			if (!this.modes.contains(DeflateMode.OPTION_KEEP_HISTORY)) {
				this.historyBuffer.clear();
			}
			this.readTables = true;
			this.finalBlock = false;
			this.blockRemaining = 0;
		} else if (this.blockRemaining > 0) {
			final int decodeLen = Math.min(this.blockRemaining, decodeRemaining);

			this.historyBuffer.copyBlock(this.rep0Dist, decodeLen);
			this.blockRemaining -= decodeLen;
			decodeRemaining -= decodeLen;
		}

		boolean done1 = !(decodeRemaining > 0);

		while (!done1) {
			if (this.readTables) {
				if (!this.finalBlock) {
					readTables(src);
					this.readTables = false;
				} else {
					this.blockRemaining = -1;
					done1 = true;
				}
			}
			if (!done1) {
				if (this.storedMode) {
					final int writeBufferLen = Math.min(decodeRemaining, this.storedBlockSize);

					this.historyBuffer.putBytes(this.bitDecoder, src, writeBufferLen);
					this.storedBlockSize -= writeBufferLen;
					this.readTables = this.storedBlockSize == 0;
					decodeRemaining -= writeBufferLen;
					done1 = !(decodeRemaining > 0);
				} else {
					boolean done2 = !(decodeRemaining > 0);

					while (!done2) {
						int symbol = this.mainDecoder.decodeSymbol(src, this.bitDecoder, 0);

						if (symbol < 0) {
							throw new InvalidDataException(symbol);
						} else if (symbol < 0x100) {
							this.historyBuffer.putByte((byte) symbol);
							decodeRemaining--;
							done2 = !(decodeRemaining > 0);
						} else if (symbol == Deflate.SYMBOL_END_OF_BLOCK) {
							this.readTables = true;
							done2 = true;
						} else if (symbol < Deflate.MAIN_TABLE_SIZE) {
							symbol -= Deflate.SYMBOL_MATCH;

							int decodeLen1;

							if (this.modes.contains(DeflateMode.OPTION_HISTORY64K)) {
								decodeLen1 = (Deflate.LEN_START_64[symbol] & 0xff) + Deflate.MATCH_MIN_LEN
										+ this.bitDecoder.decodeBits(src, Deflate.LEN_DIRECT_BITS_64[symbol] & 0xff, 1);
							} else {
								decodeLen1 = (Deflate.LEN_START_32[symbol] & 0xff) + Deflate.MATCH_MIN_LEN
										+ this.bitDecoder.decodeBits(src, Deflate.LEN_DIRECT_BITS_32[symbol] & 0xff, 1);
							}

							final int decodeLen2 = Math.min(decodeLen1, decodeRemaining);

							symbol = this.distDecoder.decodeSymbol(src, this.bitDecoder, 0);
							if (symbol >= this.numDistLevels) {
								throw new InvalidDataException(symbol);
							}

							final int dist = Deflate.DIST_START[symbol]
									+ this.bitDecoder.decodeBits(src, Deflate.DIST_DIRECT_BITS[symbol], 1);

							this.historyBuffer.copyBlock(dist, decodeLen2);
							decodeRemaining -= decodeLen2;
							decodeLen1 -= decodeLen2;
							if (decodeLen1 == 0) {
								done2 = !(decodeRemaining > 0);
							} else {
								this.blockRemaining = decodeLen1;
								this.rep0Dist = dist;
								done2 = true;
							}
						} else {
							throw new InvalidDataException(symbol);
						}
					}
					done1 = !(decodeRemaining > 0);
				}
			}
		}
	}

	private void readTables(ReadableByteChannel src) throws IOException {
		this.finalBlock = (this.bitDecoder.decodeBits(src, Deflate.FINAL_BLOCK_FIELD_SIZE, 1) != 0);

		int blockType = this.bitDecoder.decodeBits(src, Deflate.BLOCK_TYPE_FIELD_SIZE, 1);
		DeflateLevels levels;

		switch (blockType) {
		case Deflate.BLOCK_TYPE_STORED:
			this.storedMode = true;
			this.bitDecoder.alignToByte();
			this.storedBlockSize = this.bitDecoder.decodeBits(src, Deflate.STORED_BLOCK_LENGTH_FIELD_SIZE, 1);

			if (this.modes.contains(DeflateMode.FORMAT_NSIS)) {
				int storedBlockSizeCheck = this.bitDecoder.decodeBits(src, Deflate.STORED_BLOCK_LENGTH_FIELD_SIZE, 1);

				if (((this.storedBlockSize ^ storedBlockSizeCheck) & 0xffff) != 0xffff) {
					throw new InvalidDataException(this.storedBlockSize, storedBlockSizeCheck);
				}
			}
			break;
		case Deflate.BLOCK_TYPE_FIXED_HUFFMAN:
			this.storedMode = false;
			levels = new DeflateLevels();
			levels.setFixedLevels();
			this.numDistLevels = (this.modes.contains(DeflateMode.OPTION_HISTORY64K) ? Deflate.DIST_TABLE_SIZE_64
					: Deflate.DIST_TABLE_SIZE_32);
			this.mainDecoder.setCodeLengths(levels.litLenLevels);
			this.distDecoder.setCodeLengths(levels.distLevels);
			break;
		case Deflate.BLOCK_TYPE_DYNAMIC_HUFFMAN:
			this.storedMode = false;

			final int numLitLenLevels = this.bitDecoder.decodeBits(src, Deflate.NUM_LEN_CODES_FIELD_SIZE, 1)
					+ Deflate.NUM_LIT_LEN_CODES_MIN;

			this.numDistLevels = this.bitDecoder.decodeBits(src, Deflate.NUM_DIST_CODES_FIELD_SIZE, 1)
					+ Deflate.NUM_DIST_CODES_MIN;
			if (!this.modes.contains(DeflateMode.OPTION_HISTORY64K)
					&& this.numDistLevels > Deflate.DIST_TABLE_SIZE_32) {
				throw new InvalidDataException(this.numDistLevels);
			}

			final int numLevelCodes = this.bitDecoder.decodeBits(src, Deflate.NUM_LEVEL_CODES_FIELD_SIZE, 1)
					+ Deflate.NUM_LEVEL_CODES_MIN;
			final byte[] levelLevels = new byte[Deflate.LEVEL_TABLE_SIZE];

			for (int levelIndex = 0; levelIndex < levelLevels.length; levelIndex++) {
				final int position = Deflate.CODE_LENGTH_ALPHABET_ORDER[levelIndex] & 0xff;

				if (levelIndex < numLevelCodes) {
					levelLevels[position] = (byte) this.bitDecoder.decodeBits(src, Deflate.LEVEL_FIELD_SIZE, 1);
				} else {
					levelLevels[position] = 0;
				}
			}
			this.levelDecoder.setCodeLengths(levelLevels);

			levels = new DeflateLevels();

			decodeLevels(src, levels, numLitLenLevels + this.numDistLevels);
			levels.subClear();
			levels.setLevels(numLitLenLevels, this.numDistLevels);
			this.mainDecoder.setCodeLengths(levels.litLenLevels);
			this.distDecoder.setCodeLengths(levels.distLevels);
			break;
		default:
			throw new InvalidDataException(blockType);
		}
	}

	private void decodeLevels(ReadableByteChannel src, DeflateLevels levels, int numSymbols) throws IOException {
		int levelIndex = 0;

		while (levelIndex < numSymbols) {
			final int symbol = this.levelDecoder.decodeSymbol(src, this.bitDecoder, 0);

			if (symbol < 0 || Deflate.LEVEL_TABLE_SIZE <= symbol) {
				throw new InvalidDataException(symbol);
			} else if (symbol < Deflate.TABLE_DIRECT_LEVELS) {
				levels.levels[levelIndex] = (byte) symbol;
				levelIndex++;
			} else {
				if (symbol == Deflate.TABLE_LEVEL_REP_NUMBER) {
					if (levelIndex == 0) {
						throw new InvalidDataException();
					}

					int repNum = this.bitDecoder.decodeBits(src, 2, 1) + 3;

					while (repNum > 0 && levelIndex < numSymbols) {
						levels.levels[levelIndex] = levels.levels[levelIndex - 1];
						levelIndex++;
						repNum--;
					}
				} else {
					int repNum = (symbol == Deflate.TABLE_LEVEL0_NUMBER ? this.bitDecoder.decodeBits(src, 3, 1) + 3
							: this.bitDecoder.decodeBits(src, 7, 1) + 11);

					while (repNum > 0 && levelIndex < numSymbols) {
						levels.levels[levelIndex] = 0;
						levelIndex++;
						repNum--;
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.nio.compression.Compression#name()
	 */
	@Override
	public String name() {
		return Deflate.COMPRESSION_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append(name()).append(' ').append(this.modes);
		return buffer.toString();
	}

}
