/*
 * Copyright (c) 2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.nio.compression.deflate;

/**
 * Helper class for Deflate level handling.
 */
final class DeflateLevels {

	public byte[] levels = new byte[Deflate.FIXED_MAIN_TABLE_SIZE + Deflate.FIXED_DIST_TABLE_SIZE];
	public final byte[] litLenLevels = new byte[Deflate.FIXED_MAIN_TABLE_SIZE];
	public final byte[] distLevels = new byte[Deflate.FIXED_DIST_TABLE_SIZE];

	public void subClear() {
		for (int levelIndex = Deflate.NUM_LIT_LEN_CODES_MIN; levelIndex < this.litLenLevels.length; levelIndex++) {
			this.litLenLevels[levelIndex] = 0;
		}
		for (int levelIndex = 0; levelIndex < this.distLevels.length; levelIndex++) {
			this.distLevels[levelIndex] = 0;
		}
	}

	public void setFixedLevels() {
		int levelIndex = 0;

		for (; levelIndex < 144; levelIndex++) {
			this.litLenLevels[levelIndex] = 8;
		}
		for (; levelIndex < 256; levelIndex++) {
			this.litLenLevels[levelIndex] = 9;
		}
		for (; levelIndex < 280; levelIndex++) {
			this.litLenLevels[levelIndex] = 7;
		}
		for (; levelIndex < 288; levelIndex++) {
			this.litLenLevels[levelIndex] = 8;
		}
		for (levelIndex = 0; levelIndex < this.distLevels.length; levelIndex++) {
			this.distLevels[levelIndex] = 5;
		}
	}

	public void setLevels(int numLitLenLevels, int numDistLevels) {
		System.arraycopy(this.levels, 0, this.litLenLevels, 0, numLitLenLevels);
		System.arraycopy(this.levels, numLitLenLevels, this.distLevels, 0, numDistLevels);
	}

}
