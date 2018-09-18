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
package com.i4one.base.model.manager.activity;

import com.i4one.base.dao.qualifier.SimpleComparisonQualifier;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;

/**
 * @author Hamid Badiozamani
 */
public class ActivityPagination extends SimplePaginationFilter implements PaginationFilter
{
	private final String timeStampColumn;
	private final int startTime;
	private final int endTime;
	private final boolean uniqueUsers;

	public ActivityPagination(String timeStampColumn, int startTime, int endTime, boolean uniqueUsers, PaginationFilter chain)
	{
		super(chain);

		this.timeStampColumn = timeStampColumn;
		this.startTime = startTime;
		this.endTime = endTime;
		this.uniqueUsers = uniqueUsers;
	}

	public ActivityPagination(int startTime, int endTime, boolean uniqueUsers, PaginationFilter chain)
	{
		super(chain);

		this.timeStampColumn = "timestamp";
		this.startTime = startTime;
		this.endTime = endTime;
		this.uniqueUsers = uniqueUsers;
	}

	@Override
	protected void initQualifiers()
	{
		super.initQualifiers();

		if ( startTime > 0 )
		{
			getColumnQualifier().setQualifier("startTime", new SimpleComparisonQualifier(timeStampColumn, ">=", startTime));
		}

		if ( endTime > 0 )
		{
			getColumnQualifier().setQualifier("endTime", new SimpleComparisonQualifier(timeStampColumn, "<=", endTime));
		}
	}

	public int getStartTime()
	{
		return startTime;
	}

	public int getEndTime()
	{
		return endTime;
	}

	public boolean isUniqueUsers()
	{
		return uniqueUsers;
	}

}
