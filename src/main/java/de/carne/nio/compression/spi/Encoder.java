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
package de.carne.nio.compression.spi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Properties;

/**
 * Base class for compression encoders
 */
public abstract class Encoder extends Compression {

	/**
	 * Construct {@linkplain Encoder}.
	 *
	 * @param name The compression name.
	 * @param properties The encoder properties.
	 */
	protected Encoder(String name, Properties properties) {
		super(name, properties);
	}

	/**
	 * Encode data.
	 *
	 * @param src The {@linkplain ByteBuffer} providing the data to encode.
	 * @param dst The {@linkplain WritableByteChannel} receiving the encoded bytes.
	 * @return The number of encoded bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract int encode(ByteBuffer src, WritableByteChannel dst) throws IOException;

	/**
	 * Close the encoding data stream and write any needed termination mark.
	 *
	 * @param dst The {@linkplain WritableByteChannel} receiving the encoded bytes.
	 * @return The number of encoded bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract int closeEncoding(WritableByteChannel dst) throws IOException;

}
