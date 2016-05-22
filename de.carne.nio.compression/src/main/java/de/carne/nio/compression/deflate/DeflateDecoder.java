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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.carne.nio.compression.spi.Decoder;
import de.carne.nio.compression.util.BitDecoder;
import de.carne.nio.compression.util.BitRegister;
import de.carne.nio.compression.util.HuffmanDecoder;
import de.carne.nio.compression.util.LSBBitstreamBitRegister;
import de.carne.nio.compression.util.LSBBytesBitRegister;

/**
 * Deflate decoder:
 * <a href="https://en.wikipedia.org/wiki/DEFLATE">https://en.wikipedia.org/wiki
 * /DEFLATE</a>
 */
public class DeflateDecoder extends Decoder {

	private final HashSet<DeflateMode> modes;
	private final BitDecoder bits = new BitDecoder(new BitRegister[] {

			new LSBBitstreamBitRegister(),

			new LSBBytesBitRegister()

	});
	private final HuffmanDecoder mainDecoder = new HuffmanDecoder();
	private final HuffmanDecoder distanceDecoder = new HuffmanDecoder();
	private final HuffmanDecoder levelDecoder = new HuffmanDecoder();
	private int blockRemaining;

	/**
	 * Construct {@code DeflateDecoder}.
	 */
	public DeflateDecoder() {
		this(Collections.EMPTY_SET);
	}

	/**
	 * Construct {@code DeflateDecoder}.
	 *
	 * @param modes Decoder modes to use.
	 */
	public DeflateDecoder(Set<DeflateMode> modes) {
		assert modes != null;

		this.modes = new HashSet<>(modes);
		reset();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.nio.compression.Compression#reset()
	 */
	@Override
	public synchronized void reset() {
		super.reset();
		this.blockRemaining = -2;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.nio.compression.spi.Decoder#decode(java.nio.ByteBuffer,
	 * java.nio.channels.ReadableByteChannel)
	 */
	@Override
	public int decode(ByteBuffer dst, ReadableByteChannel src) throws IOException {
		long beginTime = beginProcessin();
		int decoded = -1;
		int emitted = 0;

		try {
			if (this.blockRemaining != -1) {

			} else if (this.modes.contains(DeflateMode.OPTION_RESTART_AFTER_EOS)) {
				this.blockRemaining = -2;
			}
		} finally {
			endProcessing(beginTime, Math.max(decoded, 0), emitted);
		}
		return decoded;
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
