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
package de.carne.nio.compression.test;

import org.junit.Assert;
import org.junit.Test;

import de.carne.nio.compression.Check;

/**
 * Test {@linkplain Check} class.
 */
public class CheckTest {

	/**
	 * Check {@linkplain Check#notNull(Object)} with non {@code null} argument.
	 */
	@Test
	public void checkNotNullPassed() {
		Assert.assertNotNull(Check.notNull(this));
	}

	/**
	 * Check {@linkplain Check#notNull(Object, String, Object...)} with non {@code null} argument.
	 */
	@Test
	public void checkNotNullMessagePassed() {
		Assert.assertNotNull(Check.notNull(this, getClass().getSimpleName()));
	}

	/**
	 * Check {@linkplain Check#notNull(Object)} with {@code null} argument.
	 */
	@Test(expected = NullPointerException.class)
	public void checkNotNullFailed() {
		Check.notNull(null);
	}

	/**
	 * Check {@linkplain Check#notNull(Object, String, Object...)} with {@code null} argument.
	 */
	@Test(expected = NullPointerException.class)
	public void checkNotNullMessageFailed() {
		Check.notNull(null, getClass().getSimpleName());
	}

	/**
	 * Check {@linkplain Check#assertTrue(boolean)} with {@code true} argument.
	 */
	@Test
	public void checkAssertTruePassed() {
		Check.assertTrue(true);
	}

	/**
	 * Check {@linkplain Check#assertTrue(boolean, String, Object...)} with {@code true} argument.
	 */
	@Test
	public void checkAssertTrueMessagePassed() {
		Check.assertTrue(true, getClass().getSimpleName());
	}

	/**
	 * Check {@linkplain Check#assertTrue(boolean)} with {@code false} argument.
	 */
	@Test(expected = IllegalStateException.class)
	public void checkAssertTrueFailed() {
		Check.assertTrue(false);
	}

	/**
	 * Check {@linkplain Check#assertTrue(boolean, String, Object...)} with {@code false} argument.
	 */
	@Test(expected = IllegalStateException.class)
	public void checkAssertTrueMessageFailed() {
		Check.assertTrue(false, getClass().getSimpleName());
	}

	/**
	 * Check {@linkplain Check#fail()}.
	 */
	@Test(expected = IllegalStateException.class)
	public void checkFail() {
		Check.fail();
	}

	/**
	 * Check {@linkplain Check#fail(String, Object...)}.
	 */
	@Test(expected = IllegalStateException.class)
	public void checkFailMessage() {
		Check.fail(getClass().getSimpleName());
	}

}