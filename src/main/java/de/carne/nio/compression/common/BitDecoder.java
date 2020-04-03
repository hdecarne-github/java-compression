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

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import de.carne.nio.compression.Check;

/**
 * Utility class providing bit-level access to {@linkplain ReadableByteChannel}.
 * <p>
 * The actual bit access is done via on or more {@linkplain BitRegister} instances.
 */
public final class BitDecoder {

	private final BitRegister[] registers;
	private final byte[] trailingBytes;
	private int trailingBytesIndex;
	private long totalInBits;

	/**
	 * Constructs a new {@linkplain BitDecoder} instance.
	 *
	 * @param register the [@linkplain BitRegister} to use for bit access.
	 * @param trailingBytes the optional bytes to feed after the underlying reader has reached EOF.
	 */
	public BitDecoder(BitRegister register, byte... trailingBytes) {
		this(new BitRegister[] { register }, trailingBytes);
	}

	/**
	 * Constructs a new {@linkplain BitDecoder} instance.
	 *
	 * @param registers the [@linkplain BitRegister}s to use for bit access.
	 * @param trailingBytes the optional bytes to feed after the underlying reader has reached EOF.
	 */
	public BitDecoder(BitRegister[] registers, byte... trailingBytes) {
		Check.assertTrue(registers.length > 0, "Empty registers");

		this.registers = new BitRegister[registers.length];
		System.arraycopy(registers, 0, this.registers, 0, registers.length);
		this.trailingBytes = new byte[trailingBytes.length];
		System.arraycopy(trailingBytes, 0, this.trailingBytes, 0, trailingBytes.length);
		init();
	}

	private void init() {
		this.trailingBytesIndex = 0;
		this.totalInBits = 0L;
	}

	/**
	 * Resets the decoder to it's initial state.
	 */
	public void reset() {
		clear();
		init();
	}

	/**
	 * Clears all pending bits.
	 */
	public void clear() {
		this.totalInBits += this.registers[0].bitCount();
		for (BitRegister register : this.registers) {
			register.clear();
		}
	}

	/**
	 * Gets the total number of bytes decoded.
	 *
	 * @return the total number of bytes decoded.
	 */
	public long totalIn() {
		return (this.totalInBits + 7) >>> 3;
	}

	/**
	 * Decodes a number of bits from a {@linkplain ReadableByteChannel} without discarding them.
	 * <p>
	 * This function uses the register {@code 0} for bit decoding.
	 *
	 * @param src the {@linkplain ReadableByteChannel} to decode from.
	 * @param count the number of bits to decode.
	 * @return the decoded bits.
	 * @throws IOException if an I/O error occurs.
	 */
	public int peekBits(ReadableByteChannel src, int count) throws IOException {
		return peekBits(src, count, 0);
	}

	/**
	 * Decodes a number of bits from a {@linkplain ReadableByteChannel} without discarding them.
	 *
	 * @param src the {@linkplain ReadableByteChannel} to decode from.
	 * @param count the number of bits to decode.
	 * @param registerIndex the register to use for bit decoding.
	 * @return the decoded bits.
	 * @throws IOException if an I/O error occurs.
	 */
	public int peekBits(ReadableByteChannel src, int count, int registerIndex) throws IOException {
		Check.assertTrue(count >= 0, "Invalid bit count: %1$d", count);
		Check.assertTrue(0 <= registerIndex && registerIndex < this.registers.length, "Invalid register index: %1$d",
				registerIndex);

		feedBytes(src, count);
		return this.registers[registerIndex].peekBits(count);
	}

	/**
	 * Decodes a number of bits from a {@linkplain ReadableByteChannel} and discard them.
	 * <p>
	 * This function uses the register {@code 0} for bit decoding.
	 *
	 * @param src the {@linkplain ReadableByteChannel} to decode from.
	 * @param count the number of bits to decode.
	 * @return the decoded bits.
	 * @throws IOException if an I/O error occurs.
	 */
	public int decodeBits(ReadableByteChannel src, int count) throws IOException {
		return decodeBits(src, count, 0);
	}

	/**
	 * Decodes a number of bits from a {@linkplain ReadableByteChannel} and discard them.
	 *
	 * @param src the source channel to decode from.
	 * @param count the number of bits to decode.
	 * @param registerIndex the register to use for bit decoding.
	 * @return the decoded bits.
	 * @throws IOException if an I/O error occurs.
	 */
	public int decodeBits(ReadableByteChannel src, int count, int registerIndex) throws IOException {
		int bits = peekBits(src, count, registerIndex);

		this.totalInBits += count;
		for (BitRegister register : this.registers) {
			register.discardBits(count);
		}
		return bits;
	}

	/**
	 * Makes sure the next decode or read action is byte-aligned.
	 */
	public void alignToByte() {
		int discardCount = this.registers[0].bitCount() % 8;

		if (discardCount != 0) {
			this.totalInBits += discardCount;
			for (BitRegister register : this.registers) {
				register.discardBits(discardCount);
			}
		}
	}

	/**
	 * Performs a direct byte-aligned read and discard the corresponding bits.
	 * <p>
	 * This function allows optimized access bulk reading data.
	 *
	 * @param src the {@linkplain ReadableByteChannel} to read from.
	 * @param dst the {@linkplain ByteBuffer} to read into.
	 * @return the number of read bytes or {@code -1} if the channel has reached EOF.
	 * @throws IOException if an I/O error occurs.
	 */
	public int readBytes(ReadableByteChannel src, ByteBuffer dst) throws IOException {
		alignToByte();

		BitRegister register0 = this.registers[0];
		int read = 0;

		while (register0.bitCount() > 0 && dst.hasRemaining()) {
			dst.put((byte) register0.peekBits(8));
			for (BitRegister register : this.registers) {
				register.discardBits(8);
			}
			read++;
		}

		int directRead = (dst.hasRemaining() ? src.read(dst) : 0);

		if (directRead >= 0) {
			read += directRead;
			this.totalInBits += read * 8;
		} else if (read > 0) {
			this.totalInBits += read * 8;
		} else {
			read = -1;
		}
		return read;
	}

	/**
	 * Performs a direct byte-aligned read of a single byte and discard the corresponding bits.
	 *
	 * @param src the {@linkplain ReadableByteChannel} to read from.
	 * @return the read byte or {@code -1} if the channel has reached EOF.
	 * @throws IOException if an I/O error occurs.
	 */
	public int readByte(ReadableByteChannel src) throws IOException {
		alignToByte();

		BitRegister register0 = this.registers[0];
		int read;

		if (register0.bitCount() > 0) {
			read = register0.peekBits(8) & 0xff;
			this.totalInBits += 8;
			for (BitRegister register : this.registers) {
				register.discardBits(8);
			}
		} else {
			ByteBuffer readBuffer = ByteBuffer.allocate(1);

			read = src.read(readBuffer);
			readBuffer.flip();
			if (read == 1) {
				read = readBuffer.get() & 0xff;
				this.totalInBits += 8;
			}
		}
		return read;
	}

	private void feedBytes(ReadableByteChannel src, int count) throws IOException {
		int currentBitcount = this.registers[0].bitCount();

		if (count > currentBitcount) {
			int feedBytesRemainingCount = ((count - currentBitcount) + 7) / 8;

			if (this.trailingBytesIndex == 0) {
				ByteBuffer readBuffer = ByteBuffer.allocate(feedBytesRemainingCount);

				src.read(readBuffer);
				readBuffer.flip();
				while (readBuffer.hasRemaining()) {
					feedByte(readBuffer.get());
					feedBytesRemainingCount--;
				}
			}
			while (feedBytesRemainingCount > 0) {
				if (this.trailingBytesIndex >= this.trailingBytes.length) {
					throw new EOFException("Unable to read remaining bytes: " + feedBytesRemainingCount);
				}

				feedByte(this.trailingBytes[this.trailingBytesIndex]);
				this.trailingBytesIndex++;
				feedBytesRemainingCount--;
			}
		}
	}

	private void feedByte(byte b) {
		for (BitRegister register : this.registers) {
			register.feedBits(b);
		}
	}

}
