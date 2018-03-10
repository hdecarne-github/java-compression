/*
 * Copyright (c) 2016-2018 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.nio.compression.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.carne.nio.compression.CompressionException;
import de.carne.nio.compression.CompressionInitializationException;
import de.carne.nio.compression.InsufficientDataException;
import de.carne.nio.compression.InvalidDataException;

/**
 * Test {@linkplain CompressionException} and derived classes.
 */
class CompressionExceptionTest {

	@Test
	void testCompressionInitializationException1() {
		Assertions.assertEquals("CompressionExceptionTest",
				new CompressionInitializationException(getClass().getSimpleName()).getMessage());
		Assertions.assertEquals("CompressionExceptionTest",
				new CompressionInitializationException(getClass().getSimpleName(), new NullPointerException())
						.getMessage());
	}

	@Test
	void testInsufficientDataException() {
		Assertions.assertEquals("Failed to read the requested number of bytes: Requested = 42; Read = 41",
				new InsufficientDataException(42, 41).getMessage());
	}

	@Test
	void testInvalidDataException() {
		Assertions.assertEquals("Invalid data: 0x2a, 43",
				new InvalidDataException(Byte.valueOf((byte) 42), 43).getMessage());
	}

}
