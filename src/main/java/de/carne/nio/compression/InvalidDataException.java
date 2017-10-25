/*
 * Copyright (c) 2016-2017 Holger de Carne and contributors, All Rights Reserved.
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
 * This exception is thrown when some unexpected or invalid data is encountered.
 */
public class InvalidDataException extends CompressionException {

	private static final long serialVersionUID = 8521323840663502987L;

	/**
	 * Construct {@linkplain InvalidDataException}.
	 *
	 * @param data The data causing this exception.
	 */
	public InvalidDataException(Number... data) {
		super(formatMessage(data));
	}

	private static final String formatMessage(Number... data) {
		StringBuilder messageBuilder = new StringBuilder();

		messageBuilder.append("Invalid data");

		String nextSeparator = ": ";

		for (Number value : data) {
			messageBuilder.append(nextSeparator);
			nextSeparator = ", ";
			if (value instanceof Byte) {
				messageBuilder.append(String.format("0x%02x", value.byteValue()));
			} else {
				messageBuilder.append(value);
			}
		}
		return messageBuilder.toString();
	}

}
