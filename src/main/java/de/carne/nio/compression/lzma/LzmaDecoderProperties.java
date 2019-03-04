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
package de.carne.nio.compression.lzma;

import de.carne.nio.compression.CompressionProperties;
import de.carne.nio.compression.CompressionProperty;

/**
 * {@linkplain LzmaDecoder} properties.
 */
public class LzmaDecoderProperties extends CompressionProperties {

	private static final CompressionProperty FORMAT = new CompressionProperty("FORMAT", LzmaFormat.class);
	private static final CompressionProperty LCLPBP = new CompressionProperty("LCLPBP", Byte.class);
	private static final CompressionProperty DICTIONARY_SIZE = new CompressionProperty("DICTIONARY_SIZE",
			Integer.class);
	private static final CompressionProperty DECODED_SIZE = new CompressionProperty("DECODED_SIZE", Long.class);

	/**
	 * Constructs a new {@linkplain LzmaDecoderProperties} instance with default values.
	 */
	public LzmaDecoderProperties() {
		registerProperty(FORMAT, LzmaFormat.DEFAULT);
		registerProperty(LCLPBP, Byte.valueOf((byte) 0x5d));
		registerProperty(DICTIONARY_SIZE, Integer.valueOf(0x00800000));
		registerProperty(DECODED_SIZE, Long.valueOf(-1l));
	}

	/**
	 * Sets the format property.
	 *
	 * @param format the format to set.
	 */
	public void setFormat(LzmaFormat format) {
		setEnumProperty(FORMAT, format);
	}

	/**
	 * Gets the format property.
	 *
	 * @return the format property.
	 */
	public LzmaFormat getFormat() {
		return getEnumProperty(FORMAT, LzmaFormat.class);
	}

	/**
	 * Sets the lc/lp/bp parameters to use for decoding.
	 *
	 * @param lclpbp the lc/lp/bp parameters to use for decoding.
	 */
	public void setLcLpBpProperty(byte lclpbp) {
		setByteProperty(LCLPBP, lclpbp);
	}

	/**
	 * Gets the lc/lp/bp parameters to use for decoding.
	 *
	 * @return the lc/lp/bp parameters to use for decoding.
	 */
	public byte getLcLpBpProperty() {
		return getByteProperty(LCLPBP);
	}

	/**
	 * Sets the dictionary size to use for decoding.
	 *
	 * @param dictionarySize the dictionary size to use for decoding.
	 */
	public void setDictionarySizeProperty(int dictionarySize) {
		setIntProperty(DICTIONARY_SIZE, dictionarySize);
	}

	/**
	 * Gets the dictionary size to use for decoding.
	 *
	 * @return the dictionary size to use for decoding.
	 */
	public int getDictionarySizeProperty() {
		return getIntProperty(DICTIONARY_SIZE);
	}

	/**
	 * Sets the decoded size property.
	 *
	 * @param decodedSize the decoded size property
	 */
	public void setDecodedSizeProperty(long decodedSize) {
		setLongProperty(DECODED_SIZE, decodedSize);
	}

	/**
	 * Gets the decoded size property.
	 *
	 * @return the decoded size property
	 */
	public long getDecodedSizeProperty() {
		return getLongProperty(DECODED_SIZE);
	}

}
