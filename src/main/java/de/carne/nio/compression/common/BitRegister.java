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

/**
 * Base class for the various bit register types.
 */
public abstract class BitRegister {

	/**
	 * The maximum number of bits to access.
	 */
	public static final int MAX_BIT_COUNT = 32;

	/**
	 * Bit register.
	 */
	protected long register = 0;

	/**
	 * Number of bits currently stored in the register.
	 */
	protected int bitCount = 0;

	BitRegister() {
		// Not meant to be sub-classed outside this package.
	}

	/**
	 * Clears all bits.
	 */
	public final void clear() {
		this.register = 0;
		this.bitCount = 0;
	}

	/**
	 * Gets the number of bits currently stored in the register.
	 *
	 * @return the number of bits currently stored in the register.
	 */
	public final int bitCount() {
		return this.bitCount;
	}

	/**
	 * Feeds additional bits to the register.
	 *
	 * @param b the byte bits to feed.
	 * @return the updated number of bits stored in the register.
	 */
	public abstract int feedBits(byte b);

	/**
	 * Takes a peek at the register's bits.
	 *
	 * @param count the number of bits to return.
	 * @return the register bits.
	 */
	public abstract int peekBits(int count);

	/**
	 * Discards bits from the register.
	 *
	 * @param count the number of bits to discard.
	 * @return the updated number of bits stored in the register.
	 */
	public abstract int discardBits(int count);

}
