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
package de.carne.nio.compression.deflate;

import de.carne.nio.compression.CompressionProperties;
import de.carne.nio.compression.CompressionProperty;
import de.carne.nio.compression.CompressionPropertyType;

/**
 * {@linkplain DeflateDecoder} properties.
 */
public class DeflateDecoderProperties extends CompressionProperties {

	private static final CompressionProperty FORMAT = new CompressionProperty("FORMAT", CompressionPropertyType.ENUM);
	private static final CompressionProperty HISTORY64 = new CompressionProperty("HISTORY64",
			CompressionPropertyType.BOOLEAN);
	private static final CompressionProperty KEEP_HISTORY = new CompressionProperty("KEEP_HISTORY",
			CompressionPropertyType.BOOLEAN);
	private static final CompressionProperty RESTART_AFTER_EOS = new CompressionProperty("RESTART_AFTER_EOS",
			CompressionPropertyType.BOOLEAN);

	/**
	 * Construct {@linkplain DeflateDecoderProperties} with default values.
	 */
	public DeflateDecoderProperties() {
		registerProperty(FORMAT, DeflateFormat.DEFAULT);
		registerProperty(HISTORY64, Boolean.FALSE);
		registerProperty(KEEP_HISTORY, Boolean.FALSE);
		registerProperty(RESTART_AFTER_EOS, Boolean.FALSE);
	}

	/**
	 * Set the stream format to use for decoding.
	 *
	 * @param format The stream format to use for decoding.
	 */
	public void setFormatProperty(DeflateFormat format) {
		setEnumProperty(FORMAT, format);
	}

	/**
	 * Get the stream format to use for decoding.
	 *
	 * @return The stream format for decoding.
	 */
	public DeflateFormat getFormatProperty() {
		return getEnumProperty(FORMAT, DeflateFormat.class);
	}

	/**
	 * Set the 64k history option.
	 *
	 * @param history64 Whether to use a 64k history.
	 */
	public void setHistory64Property(boolean history64) {
		setBooleanProperty(HISTORY64, history64);
	}

	/**
	 * Get the 64k history option.
	 *
	 * @return The 64k history option.
	 */
	public boolean getHistory64Property() {
		return getBooleanProperty(HISTORY64);
	}

	/**
	 * Set the keep history option.
	 *
	 * @param keepHistory The keep history option.
	 */
	public void setKeepHistoryProperty(boolean keepHistory) {
		setBooleanProperty(KEEP_HISTORY, keepHistory);
	}

	/**
	 * Get the keep history option.
	 *
	 * @return The keep history option.
	 */
	public boolean getKeepHistroyProperty() {
		return getBooleanProperty(KEEP_HISTORY);
	}

	/**
	 * Set the restart after EOS option.
	 *
	 * @param restartAfterEos The restart after EOS option.
	 */
	public void setRestartAfterEosProperty(boolean restartAfterEos) {
		setBooleanProperty(RESTART_AFTER_EOS, restartAfterEos);
	}

	/**
	 * Get the restart after EOS option.
	 *
	 * @return The restart after EOS option.
	 */
	public boolean getRestartAfterEosProperty() {
		return getBooleanProperty(RESTART_AFTER_EOS);
	}

}
