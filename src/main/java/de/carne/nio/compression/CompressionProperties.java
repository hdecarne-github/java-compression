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

import org.eclipse.jdt.annotation.NonNull;

/**
 * Base class for declaration and definition of compression properties.
 */
public abstract class CompressionProperties implements CompressionInfos {

	private final Map<CompressionProperty, Object> properties = new HashMap<>();

	/**
	 * Registers a property.
	 * <p>
	 * Only registered properties can be later on set and retrieved.
	 * </p>
	 *
	 * @param property the {@linkplain CompressionProperty} to register.
	 * @param defaultValue the property's default value.
	 */
	protected final void registerProperty(CompressionProperty property, Object defaultValue) {
		this.properties.put(property, defaultValue);
	}

	@Override
	public Object getProperty(@NonNull CompressionProperty property) {
		return getProperty(property, Object.class);
	}

	@Override
	public final byte getByteProperty(CompressionProperty property) {
		return getProperty(property, Byte.class).byteValue();
	}

	/**
	 * Sets a {@code byte} property.
	 *
	 * @param property the property to set.
	 * @param value the property value to set.
	 */
	public final void setByteProperty(CompressionProperty property, byte value) {
		setProperty(property, Byte.valueOf(value));
	}

	@Override
	public final int getIntProperty(CompressionProperty property) {
		return getProperty(property, Integer.class).intValue();
	}

	/**
	 * Sets a {@code int} property.
	 *
	 * @param property the property to set.
	 * @param value the property value to set.
	 */
	public final void setIntProperty(CompressionProperty property, int value) {
		setProperty(property, Integer.valueOf(value));
	}

	@Override
	public final long getLongProperty(CompressionProperty property) {
		return getProperty(property, Long.class).longValue();
	}

	/**
	 * Sets a {@code byte} property.
	 *
	 * @param property the property to set.
	 * @param value the property value to set.
	 */
	public final void setLongProperty(CompressionProperty property, long value) {
		setProperty(property, Long.valueOf(value));
	}

	@Override
	public final boolean getBooleanProperty(CompressionProperty property) {
		return getProperty(property, Boolean.class).booleanValue();
	}

	/**
	 * Sets a {@code boolean} property.
	 *
	 * @param property the property to set.
	 * @param value the property value to set.
	 */
	public final void setBooleanProperty(CompressionProperty property, boolean value) {
		setProperty(property, Boolean.valueOf(value));
	}

	@Override
	@SuppressWarnings("squid:S1452")
	public final Enum<?> getEnumProperty(CompressionProperty property) {
		return getProperty(property, Enum.class);
	}

	/**
	 * Gets a {@linkplain Enum} property.
	 *
	 * @param <E> the actual enum type.
	 * @param property the property to get.
	 * @param enumType the enum type.
	 * @return the current property value.
	 */
	protected final <E extends Enum<E>> E getEnumProperty(CompressionProperty property, Class<E> enumType) {
		return Enum.valueOf(enumType, getEnumProperty(property).name());
	}

	/**
	 * Sets a {@linkplain Enum} property.
	 *
	 * @param property the property to set.
	 * @param value the property value to set.
	 */
	public final void setEnumProperty(CompressionProperty property, Enum<?> value) {
		setProperty(property, value);
	}

	@Override
	public Iterator<CompressionProperty> iterator() {
		return Collections.unmodifiableSet(this.properties.keySet()).iterator();
	}

	private <T> T getProperty(CompressionProperty property, Class<T> type) {
		Class<?> propertyType = property.type();
		String propertyKey = property.key();

		if (!type.isAssignableFrom(propertyType)) {
			throw new IllegalArgumentException("Property type mismatch while accessing property: " + propertyKey
					+ " (property type: " + propertyType + "; access type: " + type);
		}

		Object propertyValue = this.properties.get(property);

		if (propertyValue == null) {
			throw new IllegalArgumentException("Unknown property: " + propertyKey);
		}
		return type.cast(propertyValue);
	}

	private <T> void setProperty(CompressionProperty property, @NonNull T value) {
		Class<?> propertyType = property.type();
		Class<?> valueType = value.getClass();

		if (!propertyType.isAssignableFrom(valueType)) {
			throw new IllegalArgumentException("Property type mismatch while accessing property: " + property.key()
					+ " (property class: " + propertyType + "; access class: " + valueType);
		}
		this.properties.put(property, value);
	}

}
