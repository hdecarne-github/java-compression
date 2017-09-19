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

import java.io.IOException;

/**
 * This exception is thrown when not all requested data could be read.
 */
public class IncompleteReadException extends IOException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct {@code IncompleteReadException}.
	 *
	 * @param requested The number of requested bytes.
	 * @param read The actual number of read bytes.
	 */
	public IncompleteReadException(int requested, int read) {
		super(formatMessage(requested, read));
	}

	private static final String formatMessage(int requested, int read) {
		return "Failed to read the requested number of bytes: Requested " + requested + ", read " + read;
	}

}
