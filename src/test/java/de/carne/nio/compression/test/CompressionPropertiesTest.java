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
package de.carne.nio.compression.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.carne.nio.compression.CompressionProperties;
import de.carne.nio.compression.CompressionProperty;

/**
 * Test {@linkplain CompressionProperties} class.
 */
class CompressionPropertiesTest extends CompressionProperties {

	private enum AnEnum {
		A, B, C
	}

	private enum BnEnum {
		A, B, C
	}

	private static final CompressionProperty BYTE_ONE = new CompressionProperty("BYTE_ONE", Byte.class);
	private static final CompressionProperty LONG_ONE = new CompressionProperty("LONG_ONE", Long.class);
	private static final CompressionProperty BOOLEAN_FALSE = new CompressionProperty("BOOLEAN_FALSE", Boolean.class);
	private static final CompressionProperty BOOLEAN_TRUE = new CompressionProperty("BOOLEAN_TRUE", Boolean.class);
	private static final CompressionProperty AN_ENUM = new CompressionProperty("AN_ENUM", AnEnum.class);
	private static final CompressionProperty BN_ENUM = new CompressionProperty("BN_ENUM", BnEnum.class);
	private static final CompressionProperty UNDEFINED = new CompressionProperty("UNDEFINED", Boolean.class);

	CompressionPropertiesTest() {
		registerProperty(BYTE_ONE, Byte.valueOf((byte) 1));
		registerProperty(LONG_ONE, Long.valueOf(1l));
		registerProperty(BOOLEAN_FALSE, Boolean.FALSE);
		registerProperty(BOOLEAN_TRUE, Boolean.TRUE);
		registerProperty(AN_ENUM, AnEnum.B);
		registerProperty(BN_ENUM, BnEnum.B);
	}

	@Test
	void testGetByteProperty() {
		Assertions.assertEquals(1, getByteProperty(BYTE_ONE));
	}

	@Test
	void testSetByteProperty() {
		setByteProperty(BYTE_ONE, (byte) 2);
		Assertions.assertEquals(2, getByteProperty(BYTE_ONE));
		setByteProperty(BYTE_ONE, (byte) 1);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			setByteProperty(AN_ENUM, Byte.MIN_VALUE);
		});
	}

	@Test
	void testGetLongProperty() {
		Assertions.assertEquals(1l, getLongProperty(LONG_ONE));
	}

	@Test
	void testSetLongProperty() {
		setLongProperty(LONG_ONE, 2l);
		Assertions.assertEquals(2l, getLongProperty(LONG_ONE));
		setLongProperty(LONG_ONE, 2l);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			setLongProperty(AN_ENUM, Long.MIN_VALUE);
		});
	}

	@Test
	void testGetBooleanProperty() {
		Assertions.assertFalse(getBooleanProperty(BOOLEAN_FALSE));
		Assertions.assertTrue(getBooleanProperty(BOOLEAN_TRUE));
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			getBooleanProperty(UNDEFINED);
		});
	}

	@Test
	void testSetBooleanProperty() {
		setBooleanProperty(BOOLEAN_FALSE, true);
		Assertions.assertTrue(getBooleanProperty(BOOLEAN_FALSE));
		setBooleanProperty(BOOLEAN_FALSE, false);
		setBooleanProperty(BOOLEAN_TRUE, false);
		Assertions.assertFalse(getBooleanProperty(BOOLEAN_TRUE));
		setBooleanProperty(BOOLEAN_TRUE, true);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			setBooleanProperty(AN_ENUM, true);
		});
	}

	@Test
	void testGetEnumProperty() {
		Assertions.assertEquals(AnEnum.B, getEnumProperty(AN_ENUM));
		Assertions.assertEquals(BnEnum.B, getEnumProperty(BN_ENUM));
	}

	@Test
	void testSetEnumProperty() {
		setEnumProperty(AN_ENUM, AnEnum.C);
		Assertions.assertEquals(AnEnum.C, getEnumProperty(AN_ENUM));
		setEnumProperty(AN_ENUM, AnEnum.B);
		setEnumProperty(BN_ENUM, BnEnum.A);
		Assertions.assertEquals(BnEnum.A, getEnumProperty(BN_ENUM));
		setEnumProperty(BN_ENUM, BnEnum.B);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			setEnumProperty(AN_ENUM, BnEnum.C);
		});
	}

}
