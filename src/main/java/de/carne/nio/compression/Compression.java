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
package de.carne.nio.compression;

import de.carne.nio.compression.util.Assert;

/**
 * Basic interface for compression engines.
 */
public abstract class Compression {

	private long processingNanos = 0L;

	private long totalIn = 0L;

	private long totalOut = 0L;

	/**
	 * Get the compression name.
	 *
	 * @return The he compression name.
	 */
	public abstract String name();

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
	 * @return The input processing rate.
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
	 * @return The output processing rate.
	 */
	public final synchronized long rateOut() {
		return (this.processingNanos >= 1000000L ? (this.totalOut * 1000L) / (this.processingNanos / 1000000L) : 0L);
	}

	/**
	 * Record the start time every time engine processing begins.
	 * <p>
	 * Derived class have to call this function to make sure engine statistics are properly tracked.
	 * </p>
	 *
	 * @return The recorded start time, which should be submitted to the {@linkplain #endProcessing(long, long, long)}
	 *         call when engine processing ends.
	 */
	protected final synchronized long beginProcessing() {
		return System.nanoTime();
	}

	/**
	 * Record the processing time and input/ouput bytes time every time engine processing ends.
	 * <p>
	 * Derived class have to call this function to make sure engine statistics are properly tracked.
	 * </p>
	 *
	 * @param beginTime The begin time as returned by {@linkplain #beginProcessing()}.
	 * @param in The number of consumed bytes.
	 * @param out The number of emitted bytes.
	 */
	protected final synchronized void endProcessing(long beginTime, long in, long out) {
		long currentNanos = System.nanoTime();

		Assert.isValid(in >= 0, "in", in);
		Assert.isValid(out >= 0, "out", out);

		this.processingNanos += currentNanos - beginTime;
		this.totalIn += in;
		this.totalOut += out;
	}

}
