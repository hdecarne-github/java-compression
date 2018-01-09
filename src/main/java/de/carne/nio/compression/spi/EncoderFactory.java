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
package de.carne.nio.compression.spi;

import de.carne.nio.compression.CompressionInitializationException;
import de.carne.nio.compression.CompressionProperties;

/**
 * Interface for compression encoder factories.
 */
public interface EncoderFactory extends CompressionFactory {

	/**
	 * Get the default {@linkplain Encoder} properties.
	 *
	 * @return The default {@linkplain Encoder} properties.
	 */
	CompressionProperties defaultEncoderProperties();

	/**
	 * Create a new {@linkplain Encoder} instance with the given encoder properties.
	 *
	 * @param properties The {@linkplain Encoder} properties to use.
	 * @return The created {@linkplain Encoder} instance.
	 * @throws CompressionInitializationException if the {@linkplain Encoder} initializer fails.
	 * @see #defaultEncoderProperties()
	 */
	Encoder newEncoder(CompressionProperties properties) throws CompressionInitializationException;

	/**
	 * Create a new {@linkplain Encoder} instance with default properties.
	 *
	 * @return The created {@linkplain Encoder} instance.
	 * @throws CompressionInitializationException if the {@linkplain Encoder} initializer fails.
	 * @see #defaultEncoderProperties()
	 */
	default Encoder newEncoder() throws CompressionInitializationException {
		return newEncoder(defaultEncoderProperties());
	}

}
