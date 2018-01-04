/*
 * Copyright (c) 2016-2018 Holger de Carne and contributors, All Rights Reserved.
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

/**
 * Deflate constants.
 */
final class Deflate {

	private Deflate() {
		// Prevent instantiation
	}

	protected static final int HUFFMAN_BITS = 15;

	protected static final int LEN_SLOTS = 29;

	protected static final int FIXED_DIST_TABLE_SIZE = 32;
	protected static final int FIXED_LEN_TABLE_SIZE = 31;

	protected static final int SYMBOL_END_OF_BLOCK = 256;
	protected static final int SYMBOL_MATCH = SYMBOL_END_OF_BLOCK + 1;

	protected static final int MAIN_TABLE_SIZE = SYMBOL_MATCH + LEN_SLOTS;
	protected static final int FIXED_MAIN_TABLE_SIZE = SYMBOL_MATCH + FIXED_LEN_TABLE_SIZE;

	protected static final int LEVEL_TABLE_SIZE = 19;

	protected static final int HISTORY_SIZE_32 = 1 << 15;
	protected static final int HISTORY_SIZE_64 = 1 << 16;

	protected static final byte[] LEN_START_32 = {

			0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 20, 24, 28, 32, 40, 48, 56, 64, 80, 96, 112, (byte) 128,
			(byte) 160, (byte) 192, (byte) 224, (byte) 255, 0, 0

	};
	protected static final byte[] LEN_DIRECT_BITS_32 = {

			0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 0, 0

	};

	protected static final byte[] LEN_START_64 = {

			0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 20, 24, 28, 32, 40, 48, 56, 64, 80, 96, 112, (byte) 128,
			(byte) 160, (byte) 192, (byte) 224, 0, 0, 0

	};
	protected static final byte[] LEN_DIRECT_BITS_64 = {

			0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 16, 0, 0

	};

	protected static final int[] DIST_START = {

			0, 1, 2, 3, 4, 6, 8, 12, 16, 24, 32, 48, 64, 96, 128, 192, 256, 384, 512, 768, 1024, 1536, 2048, 3072, 4096,
			6144, 8192, 12288, 16384, 24576, 32768, 49152

	};
	protected static final int[] DIST_DIRECT_BITS = {

			0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14

	};

	protected static final int MATCH_MIN_LEN = 3;

	protected static final int FINAL_BLOCK_FIELD_SIZE = 1;

	protected static final int BLOCK_TYPE_FIELD_SIZE = 2;

	protected static final int BLOCK_TYPE_STORED = 0;
	protected static final int BLOCK_TYPE_FIXED_HUFFMAN = 1;
	protected static final int BLOCK_TYPE_DYNAMIC_HUFFMAN = 2;

	protected static final int STORED_BLOCK_LENGTH_FIELD_SIZE = 16;

	protected static final int DIST_TABLE_SIZE_32 = 30;
	protected static final int DIST_TABLE_SIZE_64 = 32;

	protected static final int NUM_LEN_CODES_FIELD_SIZE = 5;
	protected static final int NUM_DIST_CODES_FIELD_SIZE = 5;
	protected static final int NUM_LEVEL_CODES_FIELD_SIZE = 4;

	protected static final int NUM_LIT_LEN_CODES_MIN = 257;
	protected static final int NUM_DIST_CODES_MIN = 1;
	protected static final int NUM_LEVEL_CODES_MIN = 4;

	protected static final byte[] CODE_LENGTH_ALPHABET_ORDER = {

			16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15

	};

	protected static final int LEVEL_FIELD_SIZE = 3;

	protected static final int TABLE_DIRECT_LEVELS = 16;
	protected static final int TABLE_LEVEL_REP_NUMBER = TABLE_DIRECT_LEVELS;
	protected static final int TABLE_LEVEL0_NUMBER = TABLE_LEVEL_REP_NUMBER + 1;
	protected static final int TABLE_LEVEL0_NUMBER2 = TABLE_LEVEL0_NUMBER + 1;

}
