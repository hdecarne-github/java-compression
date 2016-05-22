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
 * Package private base class for the various bit register types.
 */
public abstract class BitRegister {

	/**
	 * The maximum number of bits in a register.
	 */
	public static final int SIZE = 32;

	/**
	 * Bit store.
	 */
	protected int register = 0;

	/**
	 * Number of bits currently stored in the register.
	 */
	protected int bitCount = 0;

	BitRegister() {
		// Not meant to be subclassed outside this package.
	}

	/**
	 * Reset the register.
	 */
	public final void reset() {
		this.register = 0;
		this.bitCount = 0;
	}

	/**
	 * Get the number of bits currently stored in the register.
	 *
	 * @return The number of bits currently stored in the register.
	 */
	public final int bitCount() {
		return this.bitCount;
	}

	/**
	 * Feed additional bits to the register.
	 *
	 * @param b The byte bits to feed.
	 * @return The updated number of bits stored in the register.
	 * @throws IllegalStateException if the register's bit count limit is
	 *         exceeded.
	 */
	public abstract int feedBits(byte b) throws IllegalStateException;

	/**
	 * Take a peek at the register's bits.
	 *
	 * @param count The number of bits to return.
	 * @return The register bits.
	 * @throws IllegalArgumentException if {@code count} exceeds the number of
	 *         bits currently in the register.
	 */
	public abstract int peekBits(int count) throws IllegalArgumentException;

	/**
	 * Discard bits from the register.
	 *
	 * @param count The number of bits to discard.
	 * @return The updated number of bits stored in the register.
	 * @throws IllegalArgumentException if {@code count} exceeds the number of
	 *         bits currently in the register.
	 */
	public abstract int discardBits(int count) throws IllegalArgumentException;

	/**
	 * Ensure that there are enough unused bits in the register.
	 *
	 * @param count The required minimum number of free bits.
	 * @throws IllegalStateException if there are not enough unused bits.
	 */
	protected void ensureUnusedBits(int count) throws IllegalStateException {
		if (this.bitCount + count > SIZE) {
			throw new IllegalStateException("Not enough free register bits: Used bits " + this.bitCount
					+ ", additionaly requested bits " + count);
		}
	}

	/**
	 * Ensure that there are enough available bits in the register.
	 *
	 * @param count The required minimum number of available bits.
	 */
	protected void ensureAvailableBits(int count) {
		if (count < 0) {
			throw new IllegalArgumentException("Negative bit count: " + count);
		}
		if (count > this.bitCount) {
			throw new IllegalArgumentException(
					"Not enough decoded bits: Available bits " + this.bitCount + ", requested bits " + count);
		}
	}

}
