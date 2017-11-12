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
import java.nio.channels.ReadableByteChannel;

/**
 * LZMA LenDecoder
 */
final class LzmaLenDecoder {

	private final short[] choice = new short[2];
	private final int numPosStates;
	private final LzmaBitTreeDecoder[] lowCoder;
	private final LzmaBitTreeDecoder[] midCoder;
	private final LzmaBitTreeDecoder highCoder = new LzmaBitTreeDecoder(Lzma.NUM_HIGH_LEN_BITS);

	public LzmaLenDecoder(int numPosStates) {
		this.numPosStates = numPosStates;
		this.lowCoder = new LzmaBitTreeDecoder[this.numPosStates];
		this.midCoder = new LzmaBitTreeDecoder[this.numPosStates];
		for (int posState = 0; posState < this.numPosStates; posState++) {
			this.lowCoder[posState] = new LzmaBitTreeDecoder(Lzma.NUM_LOW_LEN_BITS);
			this.midCoder[posState] = new LzmaBitTreeDecoder(Lzma.NUM_MID_LEN_BITS);
		}
		reset();
	}

	public void reset() {
		LzmaRangeDecoder.initBitModels(this.choice);
		for (int posState = 0; posState < this.numPosStates; posState++) {
			this.lowCoder[posState].reset();
			this.midCoder[posState].reset();
		}
		this.highCoder.reset();
	}

	public int decode(ReadableByteChannel src, LzmaRangeDecoder rangeDecoder, int posState) throws IOException {
		int symbol;

		if (rangeDecoder.decodeBit(src, this.choice, 0) == 0) {
			symbol = this.lowCoder[posState].decode(src, rangeDecoder);
		} else {
			symbol = Lzma.NUM_LOW_LEN_SYMBOLS;
			if (rangeDecoder.decodeBit(src, this.choice, 1) == 0) {
				symbol += this.midCoder[posState].decode(src, rangeDecoder);
			} else {
				symbol += Lzma.NUM_MID_LEN_SYMBOLS + this.highCoder.decode(src, rangeDecoder);
			}
		}
		return symbol;
	}

}
