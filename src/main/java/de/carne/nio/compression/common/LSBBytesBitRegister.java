/*
 * Copyright (c) 2016-2022 Holger de Carne and contributors, All Rights Reserved.
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

import de.carne.nio.compression.Check;

/**
 * Bit register for LSB byte-wise data access.
 */
public final class LSBBytesBitRegister extends BitRegister {

	@Override
	public int feedBits(byte b) {
		Check.assertTrue(this.bitCount < MAX_BIT_COUNT, "Invalid bit count: %1$d", this.bitCount);

		this.register |= (b & 0xff) << this.bitCount;
		this.bitCount += 8;
		return this.bitCount;
	}

	@Override
	public int peekBits(int count) {
		Check.assertTrue((count >= 0) && (this.bitCount + count) < (MAX_BIT_COUNT + 8), "Invalid bit access %1$d +%2$d",
				this.bitCount, count);

		int bits;

		switch (count) {
		case 0:
			bits = 0;
			break;
		case 32:
			bits = (int) this.register;
			break;
		default:
			bits = (int) (this.register & (~(-1 << count)));
		}
		return bits;
	}

	@Override
	public int discardBits(int count) {
		Check.assertTrue((count >= 0) && (this.bitCount + count) < (MAX_BIT_COUNT + 8), "Invalid bit access %1$d +%2$d",
				this.bitCount, count);

		this.register >>>= count;
		this.bitCount -= count;
		return this.bitCount;
	}

}
