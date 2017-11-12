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
 * LZMA BitTreeDecoder.
 */
final class LzmaBitTreeDecoder {

	int numBitLevels;
	short[] models;

	public LzmaBitTreeDecoder(int numBitLevels) {
		this.numBitLevels = numBitLevels;
		this.models = new short[1 << this.numBitLevels];
	}

	public void reset() {
		LzmaRangeDecoder.initBitModels(this.models);
	}

	public int decode(ReadableByteChannel src, LzmaRangeDecoder rangeDecoder) throws IOException {
		int m = 1;

		for (int bitIndex = this.numBitLevels; bitIndex != 0; bitIndex--) {
			m = (m << 1) + rangeDecoder.decodeBit(src, this.models, m);
		}
		return m - (1 << this.numBitLevels);
	}

	public int reverseDecode(ReadableByteChannel src, LzmaRangeDecoder rangeDecoder) throws IOException {
		int m = 1;
		int symbol = 0;

		for (int bitIndex = 0; bitIndex < this.numBitLevels; bitIndex++) {
			final int bit = rangeDecoder.decodeBit(src, this.models, m);

			m <<= 1;
			m += bit;
			symbol |= (bit << bitIndex);
		}
		return symbol;
	}

	public static int reverseDecode(ReadableByteChannel src, short[] models, int startIndex,
			LzmaRangeDecoder rangeDecoder, int numBitLevels) throws IOException {
		int m = 1;
		int symbol = 0;

		for (int bitIndex = 0; bitIndex < numBitLevels; bitIndex++) {
			final int bit = rangeDecoder.decodeBit(src, models, startIndex + m);

			m <<= 1;
			m += bit;
			symbol |= (bit << bitIndex);
		}
		return symbol;
	}

}
