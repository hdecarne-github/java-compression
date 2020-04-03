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
package de.carne.nio.compression.spi;

import de.carne.nio.compression.CompressionInitializationException;
import de.carne.nio.compression.CompressionProperties;

/**
 * Interface for all kinds of compression decoder factories.
 */
public interface DecoderFactory extends CompressionFactory {

	/**
	 * Gets the default {@linkplain Decoder} properties.
	 *
	 * @return the default {@linkplain Decoder} properties.
	 */
	CompressionProperties defaultDecoderProperties();

	/**
	 * Creates a new {@linkplain Decoder} instance with the given decoder properties.
	 *
	 * @param properties the {@linkplain Decoder} properties to use.
	 * @return the created {@linkplain Decoder} instance.
	 * @throws CompressionInitializationException if the {@linkplain Decoder} initializer fails.
	 * @see #defaultDecoderProperties()
	 */
	Decoder newDecoder(CompressionProperties properties) throws CompressionInitializationException;

	/**
	 * Creates a new {@linkplain Decoder} instance with default properties.
	 *
	 * @return the created {@linkplain Decoder} instance.
	 * @throws CompressionInitializationException if the {@linkplain Decoder} initializer fails.
	 * @see #defaultDecoderProperties()
	 */
	default Decoder newDecoder() throws CompressionInitializationException {
		return newDecoder(defaultDecoderProperties());
	}

}
