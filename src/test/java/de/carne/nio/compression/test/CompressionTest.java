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
package de.carne.nio.compression.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.ServiceLoader;

import org.junit.Assert;

import de.carne.nio.compression.spi.Decoder;
import de.carne.nio.compression.spi.DecoderFactory;
import de.carne.nio.compression.spi.Encoder;

/**
 * Base class for compression tests.
 */
public abstract class CompressionTest {

	private final int TEST_SOURCE_SIZE = 0x10000;

	private final Encoder encoder;

	private final Decoder decoder;

	/**
	 * Construct {@code CompressionTest}.
	 *
	 * @param encoder The encoder to test (may be {@code null}).
	 * @param decoder The decoder to test.
	 */
	public CompressionTest(Encoder encoder, Decoder decoder) {
		assert decoder != null;

		this.encoder = encoder;
		this.decoder = decoder;
	}

	protected void runDecoderTest(String decoderName) {
		ServiceLoader<DecoderFactory> decoderFactories = ServiceLoader.load(DecoderFactory.class);
		Decoder decoder = null;

		for (DecoderFactory decoderFactory : decoderFactories) {
			if (decoderFactory.decoderName().equals(decoderName)) {

			}
		}
	}

	/**
	 * Perform encode/decode run and compare the results.
	 * <p>
	 * If no encoder has been provided the necessary test data chunks (SOURCE.bin and ENCODED.bin) are read from the
	 * test classe's package directory.
	 * </p>
	 */
	protected void runEncodeDecodeTest() {
		ByteBuffer source;
		ByteBuffer encoded;
		ByteBuffer decoded;

		if (this.encoder != null) {
			source = ByteBuffer.allocate(this.TEST_SOURCE_SIZE);

			Random random = new Random();
			byte[] randomBytes = new byte[1024];

			while (source.hasRemaining()) {
				random.nextBytes(randomBytes);
				source.put(randomBytes);
			}
			source.flip();
			// TODO: Do real encoding
			encoded = readResource("ENCODED.bin");
		} else {
			source = readResource("SOURCE.bin");
			encoded = readResource("ENCODED.bin");
		}
		decoded = ByteBuffer.allocate(source.capacity() + 10);
		try (ReadableByteChannel encodedChannel = new ByteBufferChannel(encoded)) {
			while (this.decoder.decode(decoded, encodedChannel) > 0) {
				// Nothing to do here
			}
			decoded.flip();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(source.equals(decoded));
	}

	private ByteBuffer readResource(String name) {
		ByteBuffer resourceBuffer = null;

		try {
			URL resourceURL = getClass().getResource(name);
			Path resourcePath = Paths.get(resourceURL.toURI());
			long resourceSize = Files.size(resourcePath);

			resourceBuffer = ByteBuffer.allocate((int) resourceSize);
			try (ByteChannel channel = Files.newByteChannel(resourcePath)) {
				channel.read(resourceBuffer);
			}
			resourceBuffer.flip();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		return resourceBuffer;
	}

	private static class ByteBufferChannel implements ByteChannel {

		private boolean isOpen = true;

		private final ByteBuffer writeBuffer;
		private final ByteBuffer readBuffer;

		public ByteBufferChannel(ByteBuffer buffer) {
			this.writeBuffer = buffer;
			this.readBuffer = this.writeBuffer.asReadOnlyBuffer();
		}

		@Override
		public int read(ByteBuffer dst) throws IOException {
			ensureOpen();

			int read;

			if (this.readBuffer.hasRemaining()) {
				read = 0;
				while (dst.hasRemaining() && this.readBuffer.hasRemaining()) {
					dst.put(this.readBuffer.get());
					read++;
				}
			} else {
				read = -1;
			}
			return read;
		}

		@Override
		public boolean isOpen() {
			return this.isOpen;
		}

		@Override
		public void close() throws IOException {
			this.isOpen = false;
		}

		@Override
		public int write(ByteBuffer src) throws IOException {
			ensureOpen();
			if (!this.writeBuffer.hasRemaining()) {
				throw new IOException("Writte buffer exhausted");
			}

			int written = 0;

			while (src.hasRemaining() && this.writeBuffer.hasRemaining()) {
				this.writeBuffer.put(src.get());
				written++;
			}
			return written;
		}

		private void ensureOpen() throws IOException {
			if (!this.isOpen) {
				throw new ClosedChannelException();
			}
		}

	}

}
