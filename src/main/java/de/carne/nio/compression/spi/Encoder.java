/*
 * Copyright (c) 2016-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.nio.compression.spi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Base class for all kinds of compression encoders
 */
public abstract class Encoder extends Compression {

	/**
	 * Constructs a new {@linkplain Encoder} instance.
	 *
	 * @param name the compression name.
	 */
	protected Encoder(String name) {
		super(name);
	}

	/**
	 * Encodes data.
	 *
	 * @param src the {@linkplain ByteBuffer} providing the data to encode.
	 * @param dst the {@linkplain WritableByteChannel} receiving the encoded bytes.
	 * @return the number of encoded bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract int encode(ByteBuffer src, WritableByteChannel dst) throws IOException;

	/**
	 * Finishes encoding and writes any needed termination mark.
	 *
	 * @param dst the {@linkplain WritableByteChannel} receiving the encoded bytes.
	 * @return the number of encoded bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract int finishEncoding(WritableByteChannel dst) throws IOException;

}
