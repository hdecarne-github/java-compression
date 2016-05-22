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

import java.nio.channels.ReadableByteChannel;

/**
 * Utility class providing bit access to a {@linkplain ReadableByteChannel}.
 * <p>
 * Actual bit access is done via the {@linkplain BitRegister} class. Multiple
 * bit registers can be used in parallel.
 * </p>
 */
public final class BitDecoder {

	private final BitRegister[] registers;
	private final byte[] trailingBytes;

	/**
	 * Construct {@code BitDecoder}.
	 *
	 * @param registers The bit registers to use.
	 * @throws NullPointerException If {@code registers} is {@code null}.
	 * @throws IllegalArgumentException If {code registers} is empty.
	 */
	public BitDecoder(BitRegister[] registers) throws NullPointerException, IllegalArgumentException {
		this(registers, null);
	}

	/**
	 * Construct {@code BitDecoder}.
	 *
	 * @param registers The bit registers to use.
	 * @param trailingBytes The optional bytes to feed after the underlying
	 *        reader has reached EOF.
	 * @throws NullPointerException If {@code registers} is {@code null}.
	 * @throws IllegalArgumentException If {code registers} is empty.
	 */
	public BitDecoder(BitRegister[] registers, byte[] trailingBytes)
			throws NullPointerException, IllegalArgumentException {
		if (registers == null) {
			throw new NullPointerException("registers null");
		}
		if (registers.length == 0) {
			throw new IllegalArgumentException("registers empty");
		}
		this.registers = new BitRegister[registers.length];
		System.arraycopy(registers, 0, this.registers, 0, registers.length);
		if (trailingBytes != null) {
			this.trailingBytes = new byte[trailingBytes.length];
			System.arraycopy(trailingBytes, 0, this.trailingBytes, 0, trailingBytes.length);
		} else {
			this.trailingBytes = new byte[0];
		}
	}

}
