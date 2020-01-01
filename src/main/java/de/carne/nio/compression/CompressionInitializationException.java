/*
 * Copyright (c) 2016-2020 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.nio.compression;

/**
 * This exception is thrown whenever a compression engine could not be initialized (e.g. due to invalid properties).
 */
public class CompressionInitializationException extends CompressionException {

	private static final long serialVersionUID = 4437492225453303130L;

	/**
	 * Constructs a new {@linkplain CompressionInitializationException} instance.
	 *
	 * @param message the exception message.
	 */
	public CompressionInitializationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@linkplain CompressionInitializationException} instance.
	 *
	 * @param message the exception message.
	 * @param cause the exception cause.
	 */
	public CompressionInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

}
