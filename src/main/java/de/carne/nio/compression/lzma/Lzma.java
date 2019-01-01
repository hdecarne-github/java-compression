/*
 * Copyright (c) 2016-2019 Holger de Carne and contributors, All Rights Reserved.
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

/**
 * Common parameters/functions for LZMA processing
 */
final class Lzma {

	private Lzma() {
		// Prevent instantiation
	}

	static final int NUM_REP_DISTANCES = 4;
	static final int NUM_STATES = 12;

	static final int STATE_INIT = 0;

	static int stateUpdateChar(int index) {
		int updateChar;

		if (index < 4) {
			updateChar = 0;
		} else if (index < 10) {
			updateChar = index - 3;
		} else {
			updateChar = index - 6;
		}
		return updateChar;
	}

	static int stateUpdateMatch(int index) {
		return (index < 7 ? 7 : 10);
	}

	static int stateUpdateRep(int index) {
		return (index < 7 ? 8 : 11);
	}

	static int stateUpdateShortRep(int index) {
		return (index < 7 ? 9 : 11);
	}

	static boolean stateIsCharState(int index) {
		return index < 7;
	}

	static final int NUM_POS_SLOT_BITS = 6;
	static final int DIC_LOG_SIZE_MIN = 0;

	static final int NUM_LEN2POS_STATES_BITS = 2;
	static final int NUM_LEN2POS_STATES = 1 << NUM_LEN2POS_STATES_BITS;

	static final int MATCH_MIN_LEN = 2;

	static int getLenToPosState(int len) {
		final int len2 = len - MATCH_MIN_LEN;

		return (len2 < NUM_LEN2POS_STATES ? len2 : NUM_LEN2POS_STATES - 1);
	}

	static final int NUM_ALIGN_BITS = 4;
	static final int ALIGN_TABLE_SIZE = 1 << NUM_ALIGN_BITS;
	static final int ALIGN_MASK = (ALIGN_TABLE_SIZE - 1);

	static final int START_POS_MODEL_INDEX = 4;
	static final int END_POS_MODEL_INDEX = 14;
	static final int NUM_POS_MODELS = END_POS_MODEL_INDEX - START_POS_MODEL_INDEX;

	static final int NUM_FULL_DISTANCES = 1 << (END_POS_MODEL_INDEX / 2);

	static final int NUM_LIT_POS_STATES_BITS_ENCODING_MAX = 4;
	static final int NUM_LIT_CONTEXT_BITS_MAX = 8;

	static final int NUM_POS_STATES_BITS_MAX = 4;
	static final int NUM_POS_STATES_MAX = (1 << NUM_POS_STATES_BITS_MAX);
	static final int NUM_POS_STATES_BITS_ENCODING_MAX = 4;
	static final int NUM_POS_STATES_ENCODING_MAX = (1 << NUM_POS_STATES_BITS_ENCODING_MAX);

	static final int NUM_LOW_LEN_BITS = 3;
	static final int NUM_MID_LEN_BITS = 3;
	static final int NUM_HIGH_LEN_BITS = 8;
	static final int NUM_LOW_LEN_SYMBOLS = 1 << NUM_LOW_LEN_BITS;
	static final int NUM_MID_LEN_SYMBOLS = 1 << NUM_MID_LEN_BITS;
	static final int NUM_LEN_SYMBOLS = NUM_LOW_LEN_SYMBOLS + NUM_MID_LEN_SYMBOLS + (1 << NUM_HIGH_LEN_BITS);
	static final int MATCH_MAX_LEN = MATCH_MIN_LEN + NUM_LEN_SYMBOLS - 1;

}
