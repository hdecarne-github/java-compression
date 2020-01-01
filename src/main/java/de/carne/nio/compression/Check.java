/*
 * Copyright (c) 2016-2020 Holger de Carne and contributors, All Rights Reserved.
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

import org.eclipse.jdt.annotation.Nullable;

/**
 * Utility class providing code check related functions.
 */
public final class Check {

	private Check() {
		// Prevent instantiation
	}

	/**
	 * Checks and ensures that an {@linkplain Object} is an instance of a specific type.
	 *
	 * @param <T> the type to ensure.
	 * @param object the {@linkplain Object} to check.
	 * @param type the type to ensure.
	 * @return the checked {@linkplain Object} (casted to the checked type}).
	 * @throws IllegalArgumentException if the submitted argument is not an instance of the given type.
	 */
	public static <T> T isInstanceOf(@Nullable Object object, Class<T> type) {
		if (object == null) {
			throw new NullPointerException();
		}
		if (!type.isAssignableFrom(object.getClass())) {
			throw new IllegalArgumentException();
		}
		return type.cast(object);
	}

	/**
	 * Checks and ensures that an {@linkplain Object} is an instance of a specific type.
	 * <p>
	 * In case the check fails the {@linkplain String#format(String, Object...)} function is used to format the
	 * exception message.
	 * </p>
	 *
	 * @param <T> the type to ensure.
	 * @param object the {@linkplain Object} to check.
	 * @param type the type to ensure.
	 * @param format the format of the message to issue if the check fails.
	 * @param args the arguments to use for message formatting.
	 * @return the checked {@linkplain Object} (casted to the checked type}).
	 * @throws IllegalArgumentException if the submitted argument is not an instance of the given type.
	 */
	public static <T> T isInstanceOf(@Nullable Object object, Class<T> type, String format, Object... args) {
		if (object == null) {
			throw new NullPointerException(String.format(format, args));
		}
		if (!type.isAssignableFrom(object.getClass())) {
			throw new IllegalArgumentException(String.format(format, args));
		}
		return type.cast(object);
	}

	/**
	 * Checks and ensures that a specific condition is met.
	 *
	 * @param condition the condition to check.
	 * @throws IllegalStateException if the condition is not met.
	 */
	public static void assertTrue(boolean condition) {
		if (!condition) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Checks and ensures that a specific condition is met.
	 * <p>
	 * In case the check fails the {@linkplain String#format(String, Object...)} function is used to format the
	 * exception message.
	 * </p>
	 *
	 * @param condition the condition to check.
	 * @param format the format of the message to issue if the check fails.
	 * @param args the arguments to use for message formatting.
	 * @throws IllegalStateException if the condition is not met.
	 */
	public static void assertTrue(boolean condition, String format, Object... args) {
		if (!condition) {
			throw new IllegalStateException(String.format(format, args));
		}
	}

	/**
	 * Throws an {@linkplain IllegalStateException} to indicate that an unexpected execution state occurred.
	 *
	 * @param <T> the generic return type.
	 * @return nothing (function never returns).
	 * @throws IllegalStateException any time this function is called.
	 */
	public static <T> T fail() {
		throw new IllegalStateException();
	}

	/**
	 * Throws an {@linkplain IllegalStateException} to indicate that an unexpected execution state occurred.
	 * <p>
	 * In case the check fails the {@linkplain String#format(String, Object...)} function is used to format the
	 * exception message.
	 * </p>
	 *
	 * @param <T> the generic return type.
	 * @param format the format of the message to issue if the check fails.
	 * @param args the arguments to use for message formatting.
	 * @return nothing (function never returns).
	 * @throws IllegalStateException any time this function is called.
	 */
	public static <T> T fail(String format, Object... args) {
		throw new IllegalStateException(String.format(format, args));
	}

}
