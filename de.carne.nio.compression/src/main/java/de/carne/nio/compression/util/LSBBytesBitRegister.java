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
package de.carne.nio.compression.util;

/**
 * Bit register for LSB byte-wise data access.
 */
public final class LSBBytesBitRegister extends BitRegister {

	/*
	 * (non-Javadoc)
	 * @see de.carne.nio.compression.util.BitRegister#feedBits(byte)
	 */
	@Override
	public int feedBits(byte b) throws IllegalStateException {
		ensureUnusedBits(8);
		this.register |= (b & 0xff) << this.bitCount;
		this.bitCount += 8;
		return this.bitCount;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.nio.compression.util.BitRegister#peekBits(int)
	 */
	@Override
	public int peekBits(int count) throws IllegalArgumentException {
		ensureAvailableBits(count);

		int bits;

		switch (count) {
		case 0:
			bits = 0;
			break;
		case 32:
			bits = this.register;
			break;
		default:
			bits = (this.register & (~(-1 << count)));
		}
		return bits;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.nio.compression.util.BitRegister#discardBits(int)
	 */
	@Override
	public int discardBits(int count) throws IllegalArgumentException {
		ensureAvailableBits(count);
		this.register >>>= count;
		this.bitCount -= count;
		return this.bitCount;
	}

}
