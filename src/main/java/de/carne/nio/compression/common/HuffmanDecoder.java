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
package de.carne.nio.compression.common;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

import de.carne.nio.compression.InvalidDataException;
import de.carne.nio.compression.util.Assert;

/**
 * Huffman symbol decoding support.
 */
public final class HuffmanDecoder {

	private static final int LENGTHS_TABLE_BITS = 9;

	private final int[] limits;
	private final int[] positions;
	private final int[] symbols;
	byte[] lengths;

	/**
	 * Construct {@code HuffmanDecoder}.
	 *
	 * @param maxBits Maximum length of a symbol.
	 * @param maxSymbols Maximum number of symbols.
	 */
	public HuffmanDecoder(int maxBits, int maxSymbols) {
		Assert.isValid(maxBits > 0, "maxBits", maxBits);
		Assert.isValid(maxSymbols > 0, "maxSymbols", maxSymbols);

		this.limits = new int[maxBits + 1];
		this.positions = new int[maxBits + 1];
		this.symbols = new int[maxSymbols];
		this.lengths = new byte[1 << LENGTHS_TABLE_BITS];
	}

	/**
	 * Set code lengths.
	 *
	 * @param codeLengths The code lengths to set.
	 * @throws IOException if inconsistent data is encountered.
	 */
	public void setCodeLengths(byte[] codeLengths) throws IOException {
		int maxBits = this.limits.length - 1;
		int maxSymbols = this.symbols.length;
		int[] lengthCounts = new int[maxBits + 1];
		int[] positions2 = new int[maxBits + 1];

		for (int symbol = 0; symbol < maxSymbols; symbol++) {
			int length = codeLengths[symbol] & 0xff;

			if (length > maxBits) {
				throw new InvalidDataException(length);
			}
			lengthCounts[length]++;
			this.symbols[symbol] = -1;
		}
		lengthCounts[0] = this.positions[0] = this.limits[0] = 0;

		int startPosition = 0;
		int index = 0;
		final int maxValue = (1 << maxBits);

		for (int value = 1; value <= maxBits; value++) {
			startPosition += lengthCounts[value] << (maxBits - value);
			if (startPosition > maxValue) {
				throw new InvalidDataException();
			}
			this.limits[value] = (value == maxBits ? maxValue : startPosition);
			this.positions[value] = this.positions[value - 1] + lengthCounts[value - 1];
			positions2[value] = this.positions[value];
			if (value <= LENGTHS_TABLE_BITS) {
				final int limit = (this.limits[value] >> (maxBits - LENGTHS_TABLE_BITS));

				while (index < limit) {
					this.lengths[index] = (byte) value;
					index++;
				}
			}
		}
		for (int symbol = 0; symbol < maxSymbols; symbol++) {
			int length = codeLengths[symbol] & 0xff;

			if (length != 0) {
				this.symbols[positions2[length]++] = symbol;
			}
		}
	}

	/**
	 * Decode next symbol.
	 *
	 * @param src The channel to read the symbol from.
	 * @param bitDecoder The <code>BitDecoder</code> to use for bit decoding.
	 * @param bufferIndex The bit buffer to use for bit decoding.
	 * @return The read symbol.
	 * @throws IOException If an I/O error occurred.
	 */
	public int decodeSymbol(ReadableByteChannel src, BitDecoder bitDecoder, int bufferIndex) throws IOException {
		int maxBits = this.positions.length - 1;
		int value = bitDecoder.peekBits(src, maxBits, bufferIndex);
		int symbolLength;

		if (value < this.limits[LENGTHS_TABLE_BITS]) {
			symbolLength = this.lengths[value >> (maxBits - LENGTHS_TABLE_BITS)] & 0xff;
		} else {
			symbolLength = LENGTHS_TABLE_BITS + 1;
			while (value >= this.limits[symbolLength]) {
				symbolLength++;
			}
		}
		bitDecoder.decodeBits(src, symbolLength, bufferIndex);

		int index = this.positions[symbolLength]
				+ ((value - this.limits[symbolLength - 1]) >> (maxBits - symbolLength));

		return (index < this.symbols.length ? this.symbols[index] : -1);
	}

}
