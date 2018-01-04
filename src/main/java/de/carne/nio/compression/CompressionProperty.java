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
package de.carne.nio.compression;

/**
 * Property definition for a single property within a {@linkplain CompressionProperties} object.
 */
public final class CompressionProperty implements Comparable<CompressionProperty> {

	private final String key;
	private final CompressionPropertyType type;

	/**
	 * Construct {@linkplain CompressionProperty}.
	 *
	 * @param key The property key.
	 * @param type The property type.
	 */
	public CompressionProperty(String key, CompressionPropertyType type) {
		this.key = key;
		this.type = type;
	}

	/**
	 * Get the property key.
	 *
	 * @return The property key.
	 */
	public String key() {
		return this.key;
	}

	/**
	 * Get the property type.
	 *
	 * @return The property type.
	 */
	public CompressionPropertyType type() {
		return this.type;
	}

	@Override
	public int hashCode() {
		return this.key.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return this == obj || (obj instanceof CompressionProperty && this.key.equals(((CompressionProperty) obj).key));
	}

	@Override
	public int compareTo(@Nullable CompressionProperty o) {
		return this.key.compareTo(Check.notNull(o).key);
	}

}
