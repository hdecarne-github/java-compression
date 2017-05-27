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
package de.carne.nio.compression.common;

import de.carne.nio.compression.util.Assert;

/**
 * Bit register for LSB byte-wise data access.
 */
public final class LSBBytesBitRegister extends BitRegister {

	@Override
	public int feedBits(byte b) {
		Assert.inState(this.bitCount < MAX_BIT_COUNT, "bitCount", this.bitCount);

		this.register |= (b & 0xff) << this.bitCount;
		this.bitCount += 8;
		return this.bitCount;
	}

	@Override
	public int peekBits(int count) {
		Assert.isValid(count >= 0, "count", count);
		Assert.inState((this.bitCount + count) < (MAX_BIT_COUNT + 8), "bitCount", this.bitCount, "count", count);

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
		Assert.isValid(count >= 0, "count", count);
		Assert.inState((this.bitCount + count) < (MAX_BIT_COUNT + 8), "bitCount", this.bitCount, "count", count);

		this.register >>>= count;
		this.bitCount -= count;
		return this.bitCount;
	}

}
