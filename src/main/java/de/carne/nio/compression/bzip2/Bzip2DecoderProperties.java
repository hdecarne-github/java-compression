/*
 * Copyright (c) 2016-2022 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.nio.compression.bzip2;

import de.carne.nio.compression.CompressionProperties;
import de.carne.nio.compression.CompressionProperty;

/**
 * {@linkplain Bzip2Decoder} properties.
 */
public class Bzip2DecoderProperties extends CompressionProperties {

	private static final CompressionProperty FORMAT = new CompressionProperty("FORMAT", Bzip2Format.class);
	private static final CompressionProperty BLOCK_SIZE = new CompressionProperty("BLOCK_SIZE", Bzip2BlockSize.class);

	/**
	 * Constructs a new {@linkplain Bzip2DecoderProperties} instance with default values.
	 */
	public Bzip2DecoderProperties() {
		registerProperty(FORMAT, Bzip2Format.DEFAULT);
		registerProperty(BLOCK_SIZE, Bzip2BlockSize.SIZE9);
	}

	/**
	 * Sets the format property.
	 *
	 * @param format the format to set.
	 */
	public void setFormat(Bzip2Format format) {
		setEnumProperty(FORMAT, format);
	}

	/**
	 * Gets the format property.
	 *
	 * @return the format property.
	 */
	public Bzip2Format getFormat() {
		return getEnumProperty(FORMAT, Bzip2Format.class);
	}

	/**
	 * Sets the block size property.
	 *
	 * @param blockSize the block size to set.
	 */
	public void setBlockSizeProperty(Bzip2BlockSize blockSize) {
		setEnumProperty(BLOCK_SIZE, blockSize);
	}

	/**
	 * Gets the block size property.
	 *
	 * @return the block size property.
	 */
	public Bzip2BlockSize getBlockSizeProperty() {
		return getEnumProperty(BLOCK_SIZE, Bzip2BlockSize.class);
	}

}
