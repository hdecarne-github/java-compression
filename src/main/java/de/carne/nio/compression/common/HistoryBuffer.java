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
package de.carne.nio.compression.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import de.carne.nio.compression.Check;
import de.carne.nio.compression.InsufficientDataException;
import de.carne.nio.compression.InvalidDataException;

/**
 * History buffer as used for Deflate processing.
 */
public final class HistoryBuffer {

	private final byte[] buffer;
	private int bufferBeginPos;
	private int bufferEndPos;
	private int bufferCopyLimit;

	/**
	 * Construct {@code HistoryBuffer}.
	 *
	 * @param size The history buffer size.
	 */
	public HistoryBuffer(int size) {
		Check.assertTrue(size > 0, "Invalid size: %1$d", size);

		this.buffer = new byte[size];
		clear();
	}

	/**
	 * Get the history buffer size.
	 *
	 * @return The history buffer size.
	 */
	public int getSize() {
		return this.buffer.length;
	}

	/**
	 * Clear the history buffer.
	 */
	public void clear() {
		this.bufferBeginPos = 0;
		this.bufferEndPos = 0;
		this.bufferCopyLimit = 0;
	}

	/**
	 * Put a single byte into the history buffer.
	 *
	 * @param b The byte to put into the history buffer.
	 */
	public void putByte(byte b) {
		this.buffer[this.bufferBeginPos] = b;
		this.bufferBeginPos = (this.bufferBeginPos + 1) % this.buffer.length;
		this.bufferCopyLimit = Math.min(this.bufferCopyLimit + 1, this.buffer.length);
	}

	/**
	 * Read a number of bytes from a channel and put them into the history buffer.
	 *
	 * @param bitDecoder The {@linkplain BitDecoder} to use for reading the bytes.
	 * @param src The {@linkplain ReadableByteChannel} to read the bytes from.
	 * @param length The number of bytes to read and to put into the history buffer.
	 * @throws IOException if an I/O error occurs.
	 */
	public void putBytes(BitDecoder bitDecoder, ReadableByteChannel src, int length) throws IOException {
		int remaining = length;

		while (remaining > 0) {
			int readLength = Math.min(length, this.buffer.length - this.bufferBeginPos);
			ByteBuffer readBuffer = ByteBuffer.wrap(this.buffer, this.bufferBeginPos, readLength);
			int read = bitDecoder.readBytes(src, readBuffer);

			if (read < readLength) {
				throw new InsufficientDataException(readLength, read);
			}
			this.bufferBeginPos = (this.bufferBeginPos + readLength) % this.buffer.length;
			this.bufferCopyLimit = Math.min(this.bufferCopyLimit + readLength, this.buffer.length);
			remaining -= readLength;
		}
	}

	/**
	 * Copy (repeat) a number of bytes from the history buffer.
	 *
	 * @param dist The distance of the history bytes to copy.
	 * @param len The number of bytes to copy.
	 * @throws IOException if an I/O error occurs.
	 */
	public void copyBlock(int dist, int len) throws IOException {
		if (dist >= this.bufferCopyLimit) {
			throw new InvalidDataException(this.bufferCopyLimit, dist, len);
		}

		int copyPos = this.bufferBeginPos - dist - 1;

		if (copyPos < 0) {
			copyPos += this.buffer.length;
		}

		int remaining = len;

		while (remaining > 0) {
			int copyLen = Math.min(this.buffer.length - Math.max(this.bufferBeginPos, copyPos), remaining);

			for (int copyIndex = 0; copyIndex < copyLen; copyIndex++) {
				this.buffer[this.bufferBeginPos + copyIndex] = this.buffer[copyPos + copyIndex];
			}
			this.bufferBeginPos = (this.bufferBeginPos + copyLen) % this.buffer.length;
			copyPos = (copyPos + copyLen) % this.buffer.length;
			remaining -= copyLen;
		}
		this.bufferCopyLimit = Math.min(this.bufferCopyLimit + len, this.buffer.length);
	}

	/**
	 * Flush the history data to a byte buffer.
	 *
	 * @param dst The {@linkplain ByteBuffer} receiving the history data.
	 * @return The number of flushed bytes.
	 */
	public int flush(ByteBuffer dst) {
		int len = 0;

		if (this.bufferEndPos != this.bufferBeginPos) {
			if (this.bufferEndPos > this.bufferBeginPos) {
				len = Math.min(this.buffer.length - this.bufferEndPos, dst.remaining());
				dst.put(this.buffer, this.bufferEndPos, len);
				this.bufferEndPos = (this.bufferEndPos + len) % this.buffer.length;
			}
			if (this.bufferEndPos < this.bufferBeginPos) {
				len = Math.min(this.bufferBeginPos - this.bufferEndPos, dst.remaining());
				dst.put(this.buffer, this.bufferEndPos, len);
				this.bufferEndPos = (this.bufferEndPos + len) % this.buffer.length;
			}
		}
		return len;
	}

}
