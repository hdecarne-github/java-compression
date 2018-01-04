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
package de.carne.nio.compression;

/**
 * This exception is thrown when a compression engine could not be initialized (e.g. due to invalid properties).
 */
public class CompressionInitializationException extends CompressionException {

	private static final long serialVersionUID = 4437492225453303130L;

	/**
	 * Construct {@linkplain CompressionInitializationException}.
	 *
	 * @param message The exception message.
	 */
	public CompressionInitializationException(String message) {
		super(message);
	}

	/**
	 * Construct {@linkplain CompressionInitializationException}.
	 *
	 * @param message The exception message.
	 * @param cause The exception cause.
	 */
	public CompressionInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

}
