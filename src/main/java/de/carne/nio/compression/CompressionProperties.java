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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Base class for declaration and definition of compression properties.
 */
public abstract class CompressionProperties implements Iterable<CompressionProperty> {

	private final Map<CompressionProperty, Object> properties = new HashMap<>();

	/**
	 * Register a property.
	 * <p>
	 * Only registered properties can be later on set and retrieved.
	 *
	 * @param property The {@linkplain CompressionProperty} to register.
	 * @param defaultValue The property's default value.
	 */
	protected final void registerProperty(CompressionProperty property, Object defaultValue) {
		this.properties.put(property, defaultValue);
	}

	/**
	 * Get a {@code byte} property.
	 *
	 * @param property The property to get.
	 * @return The current property value.
	 */
	public final byte getByteProperty(CompressionProperty property) {
		return ((Byte) getProperty(property, CompressionPropertyType.BYTE)).byteValue();
	}

	/**
	 * Set a {@code byte} property.
	 *
	 * @param property The property to set.
	 * @param value The property value to set.
	 */
	public final void setByteProperty(CompressionProperty property, byte value) {
		setProperty(property, CompressionPropertyType.BYTE, Byte.valueOf(value));
	}

	/**
	 * Get a {@code int} property.
	 *
	 * @param property The property to get.
	 * @return The current property value.
	 */
	public final int getIntProperty(CompressionProperty property) {
		return ((Integer) getProperty(property, CompressionPropertyType.INT)).intValue();
	}

	/**
	 * Set a {@code int} property.
	 *
	 * @param property The property to set.
	 * @param value The property value to set.
	 */
	public final void setIntProperty(CompressionProperty property, int value) {
		setProperty(property, CompressionPropertyType.INT, Integer.valueOf(value));
	}

	/**
	 * Get a {@code long} property.
	 *
	 * @param property The property to get.
	 * @return The current property value.
	 */
	public final long getLongProperty(CompressionProperty property) {
		return ((Long) getProperty(property, CompressionPropertyType.LONG)).longValue();
	}

	/**
	 * Set a {@code byte} property.
	 *
	 * @param property The property to set.
	 * @param value The property value to set.
	 */
	public final void setLongProperty(CompressionProperty property, long value) {
		setProperty(property, CompressionPropertyType.LONG, Long.valueOf(value));
	}

	/**
	 * Get a {@code boolean} property.
	 *
	 * @param property The property to get.
	 * @return The current property value.
	 */
	public final boolean getBooleanProperty(CompressionProperty property) {
		return ((Boolean) getProperty(property, CompressionPropertyType.BOOLEAN)).booleanValue();
	}

	/**
	 * Set a {@code boolean} property.
	 *
	 * @param property The property to set.
	 * @param value The property value to set.
	 */
	public final void setBooleanProperty(CompressionProperty property, boolean value) {
		setProperty(property, CompressionPropertyType.BOOLEAN, Boolean.valueOf(value));
	}

	/**
	 * Get a {@linkplain Enum} property.
	 *
	 * @param property The property to get.
	 * @return The current property value.
	 */
	@SuppressWarnings("squid:S1452")
	public final Enum<?> getEnumProperty(CompressionProperty property) {
		return (Enum<?>) getProperty(property, CompressionPropertyType.ENUM);
	}

	/**
	 * Get a {@linkplain Enum} property.
	 *
	 * @param <E> The actual enum type.
	 * @param property The property to get.
	 * @param enumType The enum type.
	 * @return The current property value.
	 */
	protected final <E extends Enum<E>> E getEnumProperty(CompressionProperty property, Class<E> enumType) {
		return Enum.valueOf(enumType, getEnumProperty(property).name());
	}

	/**
	 * Set a {@linkplain Enum} property.
	 *
	 * @param property The property to set.
	 * @param value The property value to set.
	 */
	public final void setEnumProperty(CompressionProperty property, Enum<?> value) {
		setProperty(property, CompressionPropertyType.ENUM, value);
	}

	@Override
	public Iterator<CompressionProperty> iterator() {
		return Collections.unmodifiableSet(this.properties.keySet()).iterator();
	}

	private Object getProperty(CompressionProperty property, CompressionPropertyType type) {
		CompressionPropertyType propertyType = property.type();
		String propertyKey = property.key();

		if (propertyType != type) {
			throw new IllegalArgumentException("Property type mismatch while accessing property: " + propertyKey
					+ " (property type: " + propertyType + "; access type: " + type);
		}

		Object propertyValue = this.properties.get(property);

		if (propertyValue == null) {
			throw new IllegalArgumentException("Unknown property: " + propertyKey);
		}
		return propertyValue;
	}

	private void setProperty(CompressionProperty property, CompressionPropertyType type, Object value) {
		Class<?> oldValueClass = getProperty(property, type).getClass();
		Class<?> newValueClass = value.getClass();

		if (!oldValueClass.equals(newValueClass)) {
			throw new IllegalArgumentException("Property type mismatch while accessing property: " + property.key()
					+ " (property class: " + oldValueClass.getName() + "; access class: " + newValueClass.getName());
		}
		this.properties.put(property, value);
	}

}
