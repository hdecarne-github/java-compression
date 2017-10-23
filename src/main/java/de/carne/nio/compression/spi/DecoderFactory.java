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

import java.util.Properties;

/**
 * Factory interface for compression decoder creation.
 */
public interface DecoderFactory extends Compression {

	/**
	 * Get the default {@linkplain Decoder} properties.
	 *
	 * @return The default {@linkplain Decoder} properties.
	 */
	Properties defaultDecoderProperties();

	/**
	 * Create a new {@linkplain Decoder} instance with the given decoder properties.
	 *
	 * @param properties The {@linkplain Decoder} properties to use.
	 * @return The created {@linkplain Decoder} instance.
	 * @see #defaultDecoderProperties()
	 */
	Decoder newDecoder(Properties properties);

	/**
	 * Create a new {@linkplain Decoder} instance with default properties.
	 *
	 * @return The created {@linkplain Decoder} instance.
	 * @see #defaultDecoderProperties()
	 */
	default Decoder newDecoder() {
		return newDecoder(defaultDecoderProperties());
	}

}
