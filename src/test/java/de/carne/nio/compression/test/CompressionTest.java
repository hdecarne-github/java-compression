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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Assertions;

import de.carne.nio.compression.CompressionInfos;
import de.carne.nio.compression.CompressionProperty;
import de.carne.nio.compression.spi.Decoder;
import de.carne.nio.compression.spi.DecoderFactory;

/**
 * Base class for compression tests.
 */
public abstract class CompressionTest {

	/**
	 * Tests decoder output.
	 *
	 * @param compressionName the compression name to test the decoder for.
	 * @param encodedDataUrl the {@linkplain URL} to the encoded test data.
	 * @param decodedDataUrl the {@linkplain URL} to the decoded test data.
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

		Assertions.assertNotNull(decoder);
		Objects.requireNonNull(decoder);

		byte[] encodedData = loadData(encodedDataUrl);
		byte[] decodedData = loadData(decodedDataUrl);
		byte[] decoderResult = decodeData(decoder, encodedData);

		Assertions.assertArrayEquals(decodedData, decoderResult);
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
		System.out.println("Decode properties:");

		CompressionInfos decoderInfos = decoder.properties();
		List<CompressionProperty> sortedProperties = new ArrayList<>();

		for (CompressionProperty decoderProperty : decoderInfos) {
			sortedProperties.add(decoderProperty);
		}
		Collections.sort(sortedProperties);
		for (CompressionProperty decoderProperty : sortedProperties) {
			Object decoderPropertyValue = decoderInfos.getProperty(decoderProperty);

			System.out.print(
					" " + decoderProperty.key() + "(" + decoderPropertyValue.getClass() + "): " + decoderPropertyValue);
		}
		while (true) {
			decodeBuffer.rewind();

			int decoded = decoder.decode(decodeBuffer, encodedChannel);

			if (decoded < 0) {
				break;
			}
			decodeBuffer.flip();
			decodedChannel.write(decodeBuffer);

			Assertions.assertFalse(decodeBuffer.hasRemaining());
		}
		System.out.println("Total in (bytes)  : " + decoder.totalIn());
		System.out.println("Total out (bytes) : " + decoder.totalOut());
		System.out.println("Rate in (bytes/s) : " + decoder.rateIn());
		System.out.println("Rate out (bytes/s): " + decoder.rateOut());

		byte[] decodedData = decodedBytes.toByteArray();

		// Assertions.assertEquals(encodedData.length, decoder.totalIn());
		Assertions.assertEquals(decodedData.length, decoder.totalOut());
		Assertions.assertTrue(decoder.rateIn() > 0);
		Assertions.assertTrue(decoder.rateOut() > 0);
		return decodedData;
	}

}
