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
package de.carne.nio.compression;

/**
 * This interface provides read only access a compression engine's actual properties.
 *
 * @see CompressionProperties
 */
public interface CompressionInfos extends Iterable<CompressionProperty> {

	/**
	 * Gets a property.
	 *
	 * @param property the property to get.
	 * @return the current property value.
	 */
	Object getProperty(CompressionProperty property);

	/**
	 * Gets a {@code byte} property.
	 *
	 * @param property the property to get.
	 * @return the current property value.
	 */
	byte getByteProperty(CompressionProperty property);

	/**
	 * Gets a {@code int} property.
	 *
	 * @param property the property to get.
	 * @return the current property value.
	 */
	int getIntProperty(CompressionProperty property);

	/**
	 * Gets a {@code long} property.
	 *
	 * @param property the property to get.
	 * @return the current property value.
	 */
	long getLongProperty(CompressionProperty property);

	/**
	 * Gets a {@code boolean} property.
	 *
	 * @param property the property to get.
	 * @return the current property value.
	 */
	boolean getBooleanProperty(CompressionProperty property);

	/**
	 * Gets a {@linkplain Enum} property.
	 *
	 * @param property the property to get.
	 * @return the current property value.
	 */
	@SuppressWarnings("squid:S1452")
	Enum<?> getEnumProperty(CompressionProperty property);

}
