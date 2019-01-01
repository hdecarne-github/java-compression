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
package de.carne.nio.compression.spi;

import de.carne.nio.compression.Check;
import de.carne.nio.compression.CompressionProperties;

/**
 * Base class for all compression engines.
 */
public abstract class Compression {

	private final String name;
	private long processingNanos = 0L;
	private long totalIn = 0L;
	private long totalOut = 0L;

	/**
	 * Construct {@linkplain Compression}.
	 *
	 * @param name The compression name.
	 */
	protected Compression(String name) {
		this.name = name;
	}

	/**
	 * Get the compression name.
	 *
	 * @return The compression name.
	 */
	public final String name() {
		return this.name;
	}

	/**
	 * Get the compression properties.
	 *
	 * @return The compression properties.
	 */
	public abstract CompressionProperties properties();

	/**
	 * Reset the compression engine to it's initial state.
	 */
	public synchronized void reset() {
		this.processingNanos = 0L;
		this.totalIn = 0L;
		this.totalOut = 0L;
	}

	/**
	 * Get the time (in milliseconds) spent in this engine since it's creation respectively the last call to
	 * {@linkplain #reset()}.
	 *
	 * @return The time (in milliseconds) spent in this engine since the last call to {@linkplain #reset()}.
	 */
	public final synchronized long processingTime() {
		return this.processingNanos / 1000000L;
	}

	/**
	 * Get the number of bytes consumed by this engine since it's creation respectively the last call to
	 * {@linkplain #reset()}.
	 *
	 * @return The number of bytes consumed by this engine since the last call to {@linkplain #reset()}.
	 */
	public final synchronized long totalIn() {
		return this.totalIn;
	}

	/**
	 * Get the input processing rate (in bytes per second) of this engine based upon the consumed bytes
	 * {@linkplain #totalIn()} and the processing time {@linkplain #processingTime()}.
	 *
	 * @return The input processing rate (in bytes per second).
	 */
	public final synchronized long rateIn() {
		return (this.processingNanos >= 1000000L ? (this.totalIn * 1000L) / (this.processingNanos / 1000000L) : 0L);
	}

	/**
	 * Get the number of bytes emitted by this engine since it's creation respectively the last call to
	 * {@linkplain #reset()}.
	 *
	 * @return The number of bytes emitted by this engine since the last call to {@linkplain #reset()}.
	 */
	public final synchronized long totalOut() {
		return this.totalOut;
	}

	/**
	 * Get the output processing rate (in bytes per second) of this engine based upon the emitted bytes
	 * {@linkplain #totalOut()} and the processing time {@linkplain #processingTime()}.
	 *
	 * @return The output processing rate (in bytes per second).
	 */
	public final synchronized long rateOut() {
		return (this.processingNanos >= 1000000L ? (this.totalOut * 1000L) / (this.processingNanos / 1000000L) : 0L);
	}

	/**
	 * Record the start time of a processing step.
	 * <p>
	 * Derived classes have to call this function to make sure engine statistics are properly recorded.
	 *
	 * @return The recorded start time, which has to be submitted to {@linkplain #endProcessing(long, long, long)} when
	 *         the processing step is finished.
	 */
	protected final synchronized long beginProcessing() {
		return System.nanoTime();
	}

	/**
	 * Record the processing time and input/output bytes at the end of a processing step.
	 * <p>
	 * Derived classes have to call this function to make sure engine statistics are properly recorded.
	 *
	 * @param beginTime The begin time as returned by {@linkplain #beginProcessing()}.
	 * @param in The number of consumed bytes.
	 * @param out The number of emitted bytes.
	 */
	protected final synchronized void endProcessing(long beginTime, long in, long out) {
		Check.assertTrue(in >= 0, "Invalid in: %1$d", in);
		Check.assertTrue(out >= 0, "Invalid out: %1$d", out);

		long currentNanos = System.nanoTime();

		this.processingNanos += currentNanos - beginTime;
		this.totalIn += in;
		this.totalOut += out;
	}

}
