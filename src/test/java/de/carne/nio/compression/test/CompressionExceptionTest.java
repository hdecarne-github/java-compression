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

import org.junit.Assert;
import org.junit.Test;

import de.carne.nio.compression.CompressionException;
import de.carne.nio.compression.CompressionInitializationException;
import de.carne.nio.compression.InsufficientDataException;
import de.carne.nio.compression.InvalidDataException;

/**
 * Test {@linkplain CompressionException} and derived classes.
 */
public class CompressionExceptionTest {

	/**
	 * Test {@linkplain CompressionInitializationException}.
	 *
	 * @throws CompressionException
	 */
	@Test(expected = CompressionInitializationException.class)
	public void testCompressionInitializationException1() throws CompressionException {
		throw new CompressionInitializationException(getClass().getSimpleName());
	}

	/**
	 * Test {@linkplain CompressionInitializationException}.
	 *
	 * @throws CompressionException
	 */
	@Test(expected = CompressionInitializationException.class)
	public void testCompressionInitializationException2() throws CompressionException {
		throw new CompressionInitializationException(getClass().getSimpleName(), new NullPointerException());
	}

	/**
	 * Test {@linkplain InsufficientDataException}.
	 *
	 * @throws CompressionException
	 */
	@Test(expected = InsufficientDataException.class)
	public void testInsufficientDataException() throws CompressionException {
		InsufficientDataException exception = new InsufficientDataException(42, 41);

		Assert.assertEquals("Failed to read the requested number of bytes: Requested = 42; Read = 41",
				exception.getMessage());
		throw exception;
	}

	/**
	 * Test {@linkplain InvalidDataException}.
	 *
	 * @throws CompressionException
	 */
	@Test(expected = InvalidDataException.class)
	public void testInvalidDataException() throws CompressionException {
		InvalidDataException exception = new InvalidDataException(Byte.valueOf((byte) 42), 43);

		Assert.assertEquals("Invalid data: 0x2a, 43", exception.getMessage());
		throw exception;
	}

}
