/*
 * Copyright (c) 2016-2018 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.nio.compression.lzma;

import de.carne.nio.compression.Check;
import de.carne.nio.compression.CompressionInitializationException;
import de.carne.nio.compression.CompressionProperties;
import de.carne.nio.compression.spi.Decoder;
import de.carne.nio.compression.spi.DecoderFactory;

/**
 * LZMA compression factory
 */
public class LzmaFactory implements DecoderFactory {

	/**
	 * The compression name.
	 */
	public static final String COMPRESSION_NAME = "LZMA compression";

	@Override
	public String compressionName() {
		return COMPRESSION_NAME;
	}

	@Override
	public CompressionProperties defaultDecoderProperties() {
		return new LzmaDecoderProperties();
	}

	@Override
	public Decoder<?> newDecoder(CompressionProperties properties) throws CompressionInitializationException {
		return new LzmaDecoder(Check.isInstanceOf(properties, LzmaDecoderProperties.class));
	}

}
