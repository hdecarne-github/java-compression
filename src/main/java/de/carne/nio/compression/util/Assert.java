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
package de.carne.nio.compression.util;

/**
 * Utility class used for argument and state validation.
 */
public final class Assert {

	/**
	 * Check whether an argument is not {@code null}.
	 *
	 * @param object The argument to check.
	 * @param name The argument's name.
	 * @throws NullPointerException if the argument is {@code null}.
	 */
	public static void notNull(Object object, String name) throws NullPointerException {
		if (object == null) {
			throw new NullPointerException("Argumrent " + name + " is null");
		}
	}

	/**
	 * Check whether an array argument is not empty.
	 *
	 * @param length The array length to check.
	 * @param name The argument's name.
	 * @throws IllegalArgumentException if the array is empty.
	 */
	public static void notEmpty(int length, String name) throws IllegalArgumentException {
		if (length == 0) {
			throw new IllegalArgumentException("Argument " + name + " is empty");
		}
	}

	/**
	 * Check whether an argument assumption is valid.
	 *
	 * @param valid The assumption result to check.
	 * @param name The argument's name name.
	 * @param value The argument's value.
	 * @throws IllegalArgumentException if the assumption is not valid.
	 */
	public static void isValid(boolean valid, String name, int value) throws IllegalArgumentException {
		if (!valid) {
			throw new IllegalArgumentException("Invalid argument " + name + ": " + value);
		}
	}

	/**
	 * Check whether an argument assumption is valid.
	 *
	 * @param valid The assumption result to check.
	 * @param name The argument's name name.
	 * @param value The argument's value.
	 * @throws IllegalArgumentException if the assumption is not valid.
	 */
	public static void isValid(boolean valid, String name, long value) throws IllegalArgumentException {
		if (!valid) {
			throw new IllegalArgumentException("Invalid argument " + name + ": " + value);
		}
	}

	/**
	 * Check whether we are in an expected state.
	 *
	 * @param state The state to check.
	 * @param name The state variable's name.
	 * @param value The state variable's value.
	 * @throws IllegalStateException if we are not in an expected state.
	 */
	public static void inState(boolean state, String name, int value) throws IllegalStateException {
		if (!state) {
			throw new IllegalStateException("Unexpected state: " + name + " " + value);
		}
	}

	/**
	 * Check whether we are in an expected state.
	 *
	 * @param state The state to check.
	 * @param name1 The 1st state variable's name.
	 * @param value1 The 1st state variable's value.
	 * @param name2 The 2nd state variable's name.
	 * @param value2 The 2nd state variable's value.
	 * @throws IllegalStateException if we are not in an expected state.
	 */
	public static void inState(boolean state, String name1, int value1, String name2, int value2)
			throws IllegalStateException {
		if (!state) {
			throw new IllegalStateException("Unexpected state: " + name1 + " " + value1 + ", " + name2 + " " + value2);
		}
	}

}
