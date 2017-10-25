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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ServiceLoader;

import org.junit.Assert;

import de.carne.nio.compression.spi.Decoder;
import de.carne.nio.compression.spi.DecoderFactory;

/**
 * Base class for compression tests.
 */
public abstract class CompressionTest {

	/**
	 * Test decoder output.
	 *
	 * @param compressionName The compression name to test the decoder for.
	 * @param encodedDataUrl The {@linkplain URL} to the encoded test data.
	 * @param decodedDataUrl The {@linkplain URL} to the decoded test data.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void runDecoderTest(String compressionName, URL encodedDataUrl, URL decodedDataUrl) throws IOException {
		ServiceLoader<DecoderFactory> decoderFactories = ServiceLoader.load(DecoderFactory.class);
		Decoder decoder = null;

		for (DecoderFactory decoderFactory : decoderFactories) {
			if (decoderFactory.compressionName().equals(compressionName)) {
				decoder = decoderFactory.newDecoder();
				break;
			}
		}

		Assert.assertNotNull(decoder);

		byte[] encodedData = loadData(encodedDataUrl);
		byte[] decodedData = loadData(decodedDataUrl);
		byte[] decoderResult = decodeData(decoder, encodedData);

		Assert.assertArrayEquals(decodedData, decoderResult);
	}

	private byte[] loadData(URL dataUrl) throws IOException {
		ByteArrayOutputStream dataBytes = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];

		try (InputStream dataStream = dataUrl.openStream()) {
			while (true) {
				int read = dataStream.read(buffer);

				if (read < 0) {
					break;
				}
				dataBytes.write(buffer, 0, read);
			}
		}
		return dataBytes.toByteArray();
	}

	private byte[] decodeData(Decoder decoder, byte[] encodedData) throws IOException {
		ReadableByteChannel encodedChannel = Channels.newChannel(new ByteArrayInputStream(encodedData));
		ByteArrayOutputStream decodedBytes = new ByteArrayOutputStream();
		WritableByteChannel decodedChannel = Channels.newChannel(decodedBytes);
		ByteBuffer decodeBuffer = ByteBuffer.allocate(4096);

		System.out.println("Testing decoder: " + decoder.name() + "...");
		decoder.reset();
		while (true) {
			decodeBuffer.rewind();

			int decoded = decoder.decode(decodeBuffer, encodedChannel);

			if (decoded < 0) {
				break;
			}
			decodeBuffer.flip();
			decodedChannel.write(decodeBuffer);

			Assert.assertFalse(decodeBuffer.hasRemaining());
		}
		System.out.println("Total in (bytes)  : " + decoder.totalIn());
		System.out.println("Total out (bytes) : " + decoder.totalOut());
		System.out.println("Rate in (bytes/s) : " + decoder.rateIn());
		System.out.println("Rate out (bytes/s): " + decoder.rateOut());

		byte[] decodedData = decodedBytes.toByteArray();

		Assert.assertEquals(encodedData.length, decoder.totalIn());
		Assert.assertEquals(decodedData.length, decoder.totalOut());
		Assert.assertTrue(decoder.rateIn() > 0);
		Assert.assertTrue(decoder.rateOut() > 0);
		return decodedData;
	}

}
