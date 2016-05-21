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
package de.carne.nio.compression.deflate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.HashSet;
import java.util.Set;

import de.carne.nio.compression.spi.Decoder;

/**
 * Deflate decoder:
 * <a href="https://en.wikipedia.org/wiki/DEFLATE">https://en.wikipedia.org/wiki
 * /DEFLATE</a>
 */
public class DeflateDecoder extends Decoder {

	private final HashSet<DeflateMode> modes;

	/**
	 * Construct {@code DeflateDecoder}.
	 *
	 * @param modes Decoder modes to use.
	 */
	public DeflateDecoder(Set<DeflateMode> modes) {
		assert modes != null;

		this.modes = new HashSet<>(modes);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.nio.compression.spi.Decoder#decode(java.nio.ByteBuffer,
	 * java.nio.channels.ReadableByteChannel)
	 */
	@Override
	public int decode(ByteBuffer dst, ReadableByteChannel src) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.nio.compression.Compression#name()
	 */
	@Override
	public String name() {
		return Deflate.COMPRESSION_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append(name()).append(' ').append(this.modes);
		return buffer.toString();
	}

}
