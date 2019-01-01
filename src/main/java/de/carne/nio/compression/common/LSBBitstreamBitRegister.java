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
package de.carne.nio.compression.common;

import de.carne.nio.compression.Check;

/**
 * Bit register for LSB bit-wise data access.
 */
public final class LSBBitstreamBitRegister extends BitRegister {

	@Override
	public int feedBits(byte b) {
		Check.assertTrue(this.bitCount < MAX_BIT_COUNT, "Invalid bit count: %1$d", this.bitCount);

		this.register = (this.register << 8) | (SWAP_MAP[b & 0xff] & 0xff);
		this.bitCount += 8;
		return this.bitCount;
	}

	@Override
	public int peekBits(int count) {
		Check.assertTrue((count >= 0) && (this.bitCount + count) < (MAX_BIT_COUNT + 8), "Invalid bit access %1$d +%2$d",
				this.bitCount, count);

		int bits;

		switch (count) {
		case 0:
			bits = 0;
			break;
		case MAX_BIT_COUNT:
			bits = (int) this.register;
			break;
		default:
			bits = (int) ((this.register >>> (this.bitCount - count)) & (~(-1 << count)));
		}
		return bits;
	}

	@Override
	public int discardBits(int count) {
		Check.assertTrue((count >= 0) && (this.bitCount + count) < (MAX_BIT_COUNT + 8), "Invalid bit access %1$d +%2$d",
				this.bitCount, count);

		this.bitCount -= count;
		return this.bitCount;
	}

	private static final byte[] SWAP_MAP = {

			0, (byte) 128,

			64, (byte) 192,

			32, (byte) 160,

			96, (byte) 224,

			16, (byte) 144,

			80, (byte) 208,

			48, (byte) 176,

			112, (byte) 240,

			8, (byte) 136,

			72, (byte) 200,

			40, (byte) 168,

			104, (byte) 232,

			24, (byte) 152,

			88, (byte) 216,

			56, (byte) 184,

			120, (byte) 248,

			4, (byte) 132,

			68, (byte) 196,

			36, (byte) 164,

			100, (byte) 228,

			20, (byte) 148,

			84, (byte) 212,

			52, (byte) 180,

			116, (byte) 244,

			12, (byte) 140,

			76, (byte) 204,

			44, (byte) 172,

			108, (byte) 236,

			28, (byte) 156,

			92, (byte) 220,

			60, (byte) 188,

			124, (byte) 252,

			2, (byte) 130,

			66, (byte) 194,

			34, (byte) 162,

			98, (byte) 226,

			18, (byte) 146,

			82, (byte) 210,

			50, (byte) 178,

			114, (byte) 242,

			10, (byte) 138,

			74, (byte) 202,

			42, (byte) 170,

			106, (byte) 234,

			26, (byte) 154,

			90, (byte) 218,

			58, (byte) 186,

			122, (byte) 250,

			6, (byte) 134,

			70, (byte) 198,

			38, (byte) 166,

			102, (byte) 230,

			22, (byte) 150,

			86, (byte) 214,

			54, (byte) 182,

			118, (byte) 246,

			14, (byte) 142,

			78, (byte) 206,

			46, (byte) 174,

			110, (byte) 238,

			30, (byte) 158,

			94, (byte) 222,

			62, (byte) 190,

			126, (byte) 254,

			1, (byte) 129,

			65, (byte) 193,

			33, (byte) 161,

			97, (byte) 225,

			17, (byte) 145,

			81, (byte) 209,

			49, (byte) 177,

			113, (byte) 241,

			9, (byte) 137,

			73, (byte) 201,

			41, (byte) 169,

			105, (byte) 233,

			25, (byte) 153,

			89, (byte) 217,

			57, (byte) 185,

			121, (byte) 249,

			5, (byte) 133,

			69, (byte) 197,

			37, (byte) 165,

			101, (byte) 229,

			21, (byte) 149,

			85, (byte) 213,

			53, (byte) 181,

			117, (byte) 245,

			13, (byte) 141,

			77, (byte) 205,

			45, (byte) 173,

			109, (byte) 237,

			29, (byte) 157,

			93, (byte) 221,

			61, (byte) 189,

			125, (byte) 253,

			3, (byte) 131,

			67, (byte) 195,

			35, (byte) 163,

			99, (byte) 227,

			19, (byte) 147,

			83, (byte) 211,

			51, (byte) 179,

			115, (byte) 243,

			11, (byte) 139,

			75, (byte) 203,

			43, (byte) 171,

			107, (byte) 235,

			27, (byte) 155,

			91, (byte) 219,

			59, (byte) 187,

			123, (byte) 251,

			7, (byte) 135,

			71, (byte) 199,

			39, (byte) 167,

			103, (byte) 231,

			23, (byte) 151,

			87, (byte) 215,

			55, (byte) 183,

			119, (byte) 247,

			15, (byte) 143,

			79, (byte) 207,

			47, (byte) 175,

			111, (byte) 239,

			31, (byte) 159,

			95, (byte) 223,

			63, (byte) 191,

			127, (byte) 255

	};

}
