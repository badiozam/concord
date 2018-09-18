/*
 * MIT License
 * 
 * Copyright (c) 2018 i4one Interactive, LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.i4one.base.model.manager.terminable;

import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;

/**
 * Pagination filter that keeps track of start/end and current times. The SQL
 * itself is not modified by this class. Rather the BaseTerminableClientJdbcDao
 * uses the fields contained here to build its queries. This also allows other
 * DAOs to use the time fields as they see fit (for example reports that need
 * to have date ranges set).
 * 
 * @author Hamid Badiozamani
 */
public class TerminablePagination extends SimplePaginationFilter
{
	private Integer startTime;
	private Integer endTime;
	private Integer currentTime;

	public TerminablePagination(int currentTime, PaginationFilter chain)
	{
		super(chain);

		this.currentTime = currentTime;

		// By default the range will return future items, this is
		// because if neither start nor end time are specified
		// all records will be returned which is a big hit
		//
		setFuture();
		setOrderBy("endtm DESC, starttm");
	}

	/**
	 * Retrieve future items.
	 */
	public void setFuture()
	{
		setStartTime(currentTime);
		setEndTime(null);
	}

	/**
	 * Retrieve past items.
	 */
	public void setPast()
	{
		setStartTime(null);
		setEndTime(currentTime);
	}

	public Integer getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Integer startTime)
	{
		this.startTime = startTime;
	}

	public Integer getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Integer endTime)
	{
		this.endTime = endTime;
	}

	public Integer getCurrentTime()
	{
		return currentTime;
	}

	public void setCurrentTime(Integer currentTime)
	{
		this.currentTime = currentTime;
	}

	@Override
	protected String toStringInternal()
	{
		// Since the start/end and current times are based on seconds granularity, we effectively reduce the
		// cache timeout to 1 second if we include them in our cache string. As such, we floor to the nearest
		// 10 seconds to at least have a 10 second cache when matching
		//
		Integer startFloor = floorTime(startTime, 10);
		Integer endFloor = floorTime(endTime, 10);
		Integer currentFloor = floorTime(currentTime, 10);

		if ( getLogger().isTraceEnabled() )
		{
			return "startTime: " + startFloor + ", endTime: " + endFloor + ", currentTime: " + currentFloor  + super.toStringInternal();
		}
		else
		{
			return "st:" + startFloor + ",et:" + endFloor + ",ct:" + currentFloor + ", " + super.toStringInternal();
		}
	}

	private Integer floorTime(Integer time, int granularity)
	{
		if ( time == null )
		{
			return null;
		}
		else
		{
			return time - (time % granularity);
		}
	}
}