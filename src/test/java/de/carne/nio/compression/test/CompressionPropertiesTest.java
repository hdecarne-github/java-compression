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

import de.carne.nio.compression.CompressionProperties;
import de.carne.nio.compression.CompressionProperty;
import de.carne.nio.compression.CompressionPropertyType;

/**
 * Test {@linkplain CompressionProperties} class.
 */
public class CompressionPropertiesTest extends CompressionProperties {

	private static enum AnEnum {
		A, B, C
	}

	private static enum BnEnum {
		A, B, C
	}

	private static final CompressionProperty BYTE_ONE = new CompressionProperty("BYTE_ONE",
			CompressionPropertyType.BYTE);
	private static final CompressionProperty LONG_ONE = new CompressionProperty("LONG_ONE",
			CompressionPropertyType.LONG);
	private static final CompressionProperty BOOLEAN_FALSE = new CompressionProperty("BOOLEAN_FALSE",
			CompressionPropertyType.BOOLEAN);
	private static final CompressionProperty BOOLEAN_TRUE = new CompressionProperty("BOOLEAN_TRUE",
			CompressionPropertyType.BOOLEAN);
	private static final CompressionProperty AN_ENUM = new CompressionProperty("AN_ENUM", CompressionPropertyType.ENUM);
	private static final CompressionProperty BN_ENUM = new CompressionProperty("BN_ENUM", CompressionPropertyType.ENUM);
	private static final CompressionProperty UNDEFINED = new CompressionProperty("UNDEFINED",
			CompressionPropertyType.BOOLEAN);

	/**
	 * Construct {@linkplain CompressionPropertiesTest}.
	 */
	public CompressionPropertiesTest() {
		registerProperty(BYTE_ONE, Byte.valueOf((byte) 1));
		registerProperty(LONG_ONE, Long.valueOf(1l));
		registerProperty(BOOLEAN_FALSE, Boolean.FALSE);
		registerProperty(BOOLEAN_TRUE, Boolean.TRUE);
		registerProperty(AN_ENUM, AnEnum.B);
		registerProperty(BN_ENUM, BnEnum.B);
	}

	/**
	 * Test {@linkplain CompressionProperties#getByteProperty(CompressionProperty)}.
	 */
	@Test
	public void testGetByteProperty() {
		Assert.assertEquals(1, getByteProperty(BYTE_ONE));
	}

	/**
	 * Test {@linkplain CompressionProperties#setByteProperty(CompressionProperty, byte)}.
	 */
	@Test
	public void testSetByteProperty() {
		setByteProperty(BYTE_ONE, (byte) 2);
		Assert.assertEquals(2, getByteProperty(BYTE_ONE));
		setByteProperty(BYTE_ONE, (byte) 1);
	}

	/**
	 * Test {@linkplain CompressionProperties#setByteProperty(CompressionProperty, byte)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetBytePropertyFailure() {
		setByteProperty(AN_ENUM, Byte.MIN_VALUE);
	}

	/**
	 * Test {@linkplain CompressionProperties#getLongProperty(CompressionProperty)}.
	 */
	@Test
	public void testGetLongProperty() {
		Assert.assertEquals(1l, getLongProperty(LONG_ONE));
	}

	/**
	 * Test {@linkplain CompressionProperties#setLongProperty(CompressionProperty, long)}.
	 */
	@Test
	public void testSetLongProperty() {
		setLongProperty(LONG_ONE, 2l);
		Assert.assertEquals(2l, getLongProperty(LONG_ONE));
		setLongProperty(LONG_ONE, 2l);
	}

	/**
	 * Test {@linkplain CompressionProperties#setLongProperty(CompressionProperty, long)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetLongPropertyFailure() {
		setLongProperty(AN_ENUM, Long.MIN_VALUE);
	}

	/**
	 * Test {@linkplain CompressionProperties#getBooleanProperty(CompressionProperty)}.
	 */
	@Test
	public void testGetBooleanProperty() {
		Assert.assertFalse(getBooleanProperty(BOOLEAN_FALSE));
		Assert.assertTrue(getBooleanProperty(BOOLEAN_TRUE));
	}

	/**
	 * Test {@linkplain CompressionProperties#setBooleanProperty(CompressionProperty, boolean)}.
	 */
	@Test
	public void testSetBooleanProperty() {
		setBooleanProperty(BOOLEAN_FALSE, true);
		Assert.assertTrue(getBooleanProperty(BOOLEAN_FALSE));
		setBooleanProperty(BOOLEAN_FALSE, false);
		setBooleanProperty(BOOLEAN_TRUE, false);
		Assert.assertFalse(getBooleanProperty(BOOLEAN_TRUE));
		setBooleanProperty(BOOLEAN_TRUE, true);
	}

	/**
	 * Test {@linkplain CompressionProperties#setBooleanProperty(CompressionProperty, boolean)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetBooleanPropertyFailure() {
		setBooleanProperty(AN_ENUM, true);
	}

	/**
	 * Test {@linkplain CompressionProperties#getEnumProperty(CompressionProperty)}.
	 */
	@Test
	public void testGetEnumProperty() {
		Assert.assertEquals(AnEnum.B, getEnumProperty(AN_ENUM));
		Assert.assertEquals(BnEnum.B, getEnumProperty(BN_ENUM));
	}

	/**
	 * Test {@linkplain CompressionProperties#setEnumProperty(CompressionProperty, Enum)}.
	 */
	@Test
	public void testSetEnumProperty() {
		setEnumProperty(AN_ENUM, AnEnum.C);
		Assert.assertEquals(AnEnum.C, getEnumProperty(AN_ENUM));
		setEnumProperty(AN_ENUM, AnEnum.B);
		setEnumProperty(BN_ENUM, BnEnum.A);
		Assert.assertEquals(BnEnum.A, getEnumProperty(BN_ENUM));
		setEnumProperty(BN_ENUM, BnEnum.B);
	}

	/**
	 * Test {@linkplain CompressionProperties#setEnumProperty(CompressionProperty, Enum)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetEnumPropertyFailure() {
		setEnumProperty(AN_ENUM, BnEnum.C);
	}

	/**
	 * Test {@linkplain CompressionProperties#getBooleanProperty(CompressionProperty)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetBooleanPropertyFailure() {
		getBooleanProperty(UNDEFINED);
	}

}
