/*
 * Copyright (c) 2016-2022 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.nio.compression.lzma;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import de.carne.nio.compression.InsufficientDataException;

/**
 * LZMA RangeDecoder
 */
final class LzmaRangeDecoder {

	private static final int TOP_MASK = ~((1 << 24) - 1);

	private static final int NUM_BIT_MODEL_TOTAL_BITS = 11;
	private static final int BIT_MODEL_TOTAL = (1 << NUM_BIT_MODEL_TOTAL_BITS);
	private static final int NUM_MOVE_BITS = 5;

	private long totalIn;
	private int code;
	private int range;

	LzmaRangeDecoder() {
		reset();
	}

	public void reset() {
		this.totalIn = 0;
		this.code = 0;
		this.range = -1;
	}

	public void beginDecode(ReadableByteChannel src) throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocate(5);
		final int read = src.read(buffer);

		if (read < buffer.capacity()) {
			throw new InsufficientDataException(buffer.capacity(), read);
		}
		this.totalIn += read;
		buffer.flip();
		this.code = buffer.get() & 0xff;
		this.code <<= 8;
		this.code |= buffer.get() & 0xff;
		this.code <<= 8;
		this.code |= buffer.get() & 0xff;
		this.code <<= 8;
		this.code |= buffer.get() & 0xff;
		this.code <<= 8;
		this.code |= buffer.get() & 0xff;
	}

	public int decodeDirectBits(ReadableByteChannel src, int numTotalBits) throws IOException {
		int bits = 0;

		for (int i = numTotalBits; i != 0; i--) {
			this.range >>>= 1;

			final int tmp = (this.code - this.range) >>> 31;

			this.code -= this.range & (tmp - 1);
			bits = (bits << 1) | (1 - tmp);
			if ((this.range & TOP_MASK) == 0) {
				this.code = (this.code << 8) | readByte(src);
				this.range <<= 8;
			}
		}
		return bits;
	}

	public int decodeBit(ReadableByteChannel src, short[] probs, int index) throws IOException {
		final int prob = probs[index];
		final int newBound = (this.range >>> NUM_BIT_MODEL_TOTAL_BITS) * prob;
		int bit;

		if ((this.code ^ 0x80000000) < (newBound ^ 0x80000000)) {
			this.range = newBound;
			probs[index] = (short) (prob + ((BIT_MODEL_TOTAL - prob) >>> NUM_MOVE_BITS));
			if ((this.range & TOP_MASK) == 0) {
				this.code = (this.code << 8) | readByte(src);
				this.range <<= 8;
			}
			bit = 0;
		} else {
			this.range -= newBound;
			this.code -= newBound;
			probs[index] = (short) (prob - ((prob) >>> NUM_MOVE_BITS));
			if ((this.range & TOP_MASK) == 0) {
				this.code = (this.code << 8) | readByte(src);
				this.range <<= 8;
			}
			bit = 1;
		}
		return bit;
	}

	private int readByte(ReadableByteChannel src) throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocate(1);
		final int read = src.read(buffer);

		if (read < buffer.capacity()) {
			buffer.put((byte) 0xff);
		}
		this.totalIn += read;
		buffer.flip();
		return buffer.get() & 0xff;
	}

	public long totalIn() {
		return this.totalIn;
	}

	public static void initBitModels(short[] probs) {
		for (int i = 0; i < probs.length; i++) {
			probs[i] = (BIT_MODEL_TOTAL >>> 1);
		}
	}

}
