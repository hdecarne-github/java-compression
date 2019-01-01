/*
 * Copyright (c) 2016-2019 Holger de Carne and contributors, All Rights Reserved.
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

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.carne.nio.compression.bzip2.Bzip2Factory;
import de.carne.nio.compression.deflate.DeflateFactory;
import de.carne.nio.compression.lzma.LzmaFactory;
import de.carne.nio.compression.spi.DecoderFactory;
import de.carne.nio.compression.spi.EncoderFactory;

/**
 * Test encode and encoder service definitions.
 */
class ServiceTest {

	private static Set<String> ENCODER_NAMES = new HashSet<>();

	static {
		// Add if available
	}

	private static Set<String> DECODER_NAMES = new HashSet<>();

	static {
		DECODER_NAMES.add(Bzip2Factory.COMPRESSION_NAME);
		DECODER_NAMES.add(DeflateFactory.COMPRESSION_NAME);
		DECODER_NAMES.add(LzmaFactory.COMPRESSION_NAME);
	}

	@Test
	void testEncodeServices() {
		ServiceLoader<EncoderFactory> encoderFactories = ServiceLoader.load(EncoderFactory.class);
		Set<String> encoderNames = new HashSet<>();

		for (EncoderFactory encoderFactory : encoderFactories) {
			encoderNames.add(encoderFactory.compressionName());
		}
		Assertions.assertEquals(ENCODER_NAMES.size(), encoderNames.size());
		for (String encoderName : encoderNames) {
			Assertions.assertTrue(ENCODER_NAMES.contains(encoderName));
		}
	}

	@Test
	void testDecodeServices() {
		ServiceLoader<DecoderFactory> decoderFactories = ServiceLoader.load(DecoderFactory.class);
		Set<String> decoderNames = new HashSet<>();

		for (DecoderFactory decoderFactory : decoderFactories) {
			decoderNames.add(decoderFactory.compressionName());
		}
		Assertions.assertEquals(DECODER_NAMES.size(), decoderNames.size());
		for (String decoderName : decoderNames) {
			Assertions.assertTrue(DECODER_NAMES.contains(decoderName));
		}
	}

}
