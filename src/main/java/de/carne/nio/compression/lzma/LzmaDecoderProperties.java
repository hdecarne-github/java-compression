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

import de.carne.nio.compression.CompressionProperties;
import de.carne.nio.compression.CompressionProperty;
import de.carne.nio.compression.CompressionPropertyType;

/**
 * {@linkplain LzmaDecoder} properties.
 */
public class LzmaDecoderProperties extends CompressionProperties {

	private static final CompressionProperty LCLPBP = new CompressionProperty("LCLPBP", CompressionPropertyType.BYTE);
	private static final CompressionProperty DICTIONARY_SIZE = new CompressionProperty("DICTIONARY_SIZE",
			CompressionPropertyType.INT);
	private static final CompressionProperty DECODED_SIZE = new CompressionProperty("DECODED_SIZE",
			CompressionPropertyType.LONG);

	/**
	 * Construct {@linkplain LzmaDecoderProperties} with default values.
	 */
	public LzmaDecoderProperties() {
		registerProperty(LCLPBP, Byte.valueOf((byte) 0x5d));
		registerProperty(DICTIONARY_SIZE, Integer.valueOf(0x00800000));
		registerProperty(DECODED_SIZE, Long.valueOf(-1l));
	}

	/**
	 * Set the lc/lp/bp parameters to use for decoding.
	 *
	 * @param lclpbp The lc/lp/bp parameters to use for decoding.
	 */
	public void setLcLpBpProperty(byte lclpbp) {
		setByteProperty(LCLPBP, lclpbp);
	}

	/**
	 * Get the lc/lp/bp parameters to use for decoding.
	 *
	 * @return The lc/lp/bp parameters to use for decoding.
	 */
	public byte getLcLpBpProperty() {
		return getByteProperty(LCLPBP);
	}

	/**
	 * Set the dictionary size to use for decoding.
	 *
	 * @param dictionarySize The dictionary size to use for decoding.
	 */
	public void setDictionarySizeProperty(int dictionarySize) {
		setIntProperty(DICTIONARY_SIZE, dictionarySize);
	}

	/**
	 * Get the dictionary size to use for decoding.
	 *
	 * @return The dictionary size to use for decoding.
	 */
	public int getDictionarySizeProperty() {
		return getIntProperty(DICTIONARY_SIZE);
	}

	/**
	 * Set the decoded size property.
	 *
	 * @param decodedSize The decoded size property
	 */
	public void setDecodedSizeProperty(long decodedSize) {
		setLongProperty(DECODED_SIZE, decodedSize);
	}

	/**
	 * Get the decoded size property.
	 *
	 * @return The decoded size property
	 */
	public long getDecodedSizeProperty() {
		return getLongProperty(DECODED_SIZE);
	}

}
