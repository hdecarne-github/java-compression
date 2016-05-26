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
package de.carne.nio.compression;

import de.carne.nio.compression.util.Assert;

/**
 * Basic interface for compression engines.
 */
public abstract class Compression {

	private long processingTime = 0L;

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
		this.processingTime = 0L;
		this.totalIn = 0L;
		this.totalOut = 0L;
	}

	/**
	 * Get the time (in milliseconds) spent in this engine since the last call
	 * to {@linkplain #reset()}.
	 *
	 * @return The time (in milliseconds) spent in this engine since the last
	 *         call to {@linkplain #reset()}.
	 */
	public synchronized final long processingTime() {
		return this.processingTime;
	}

	/**
	 * Get the number of bytes consumed by this engine since the last call to
	 * {@linkplain #reset()}.
	 *
	 * @return The number of bytes consumed by this engine since the last call
	 *         to {@linkplain #reset()}.
	 */
	public synchronized final long totalIn() {
		return this.totalIn;
	}

	/**
	 * Get the number of bytes emitted by this engine since the last call to
	 * {@linkplain #reset()}.
	 *
	 * @return The number of bytes emitted by this engine since the last call to
	 *         {@linkplain #reset()}.
	 */
	public synchronized final long totalOut() {
		return this.totalOut;
	}

	/**
	 * Record the start time every time engine processing begins.
	 * <p>
	 * Derived class have to call this function to make sure engine statistics
	 * are properly tracked.
	 * </p>
	 *
	 * @return The recorded start time, which should be submitted to the
	 *         {@linkplain #endProcessing(long, long, long)} call when engine
	 *         processing ends.
	 */
	protected synchronized final long beginProcessin() {
		return System.currentTimeMillis();
	}

	/**
	 * Record the processing time and input/ouput bytes time every time engine
	 * processing ends.
	 * <p>
	 * Derived class have to call this function to make sure engine statistics
	 * are properly tracked.
	 * </p>
	 *
	 * @param beginTime The begin time as returned by
	 *        {@linkplain #beginProcessin()}.
	 * @param in The number of consumed bytes.
	 * @param out The number of emitted bytes.
	 */
	protected synchronized final void endProcessing(long beginTime, long in, long out) {
		long currentTime = System.currentTimeMillis();

		Assert.isValid(beginTime <= currentTime, "beginTime", beginTime);
		Assert.isValid(in >= 0, "in", in);
		Assert.isValid(out >= 0, "out", out);

		this.processingTime += currentTime - beginTime;
		this.totalIn += in;
		this.totalOut += out;
	}

}
