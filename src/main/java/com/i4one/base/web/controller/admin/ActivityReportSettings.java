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
package com.i4one.base.web.controller.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.model.ClientType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.activity.ActivityPagination;
import com.i4one.base.model.manager.terminable.SimpleParsingTerminable;
import java.text.ParseException;

/**
 * Report settings that allows a date range to be set for activity reports.
 * 
 * @author Hamid Badiozamani
 */
public class ActivityReportSettings extends ReportSettings implements ClientType
{
	private String timeStampColumn;

	private transient boolean uniqueUsers;
	private transient SimpleParsingTerminable parsingTerminable;
	private transient SingleClient client;

	public ActivityReportSettings()
	{
		super();

		init(SingleClient.getRoot(), "timestamp");
	}

	public ActivityReportSettings(SingleClient client)
	{
		super();

		init(client, "timestamp");
	}

	protected ActivityReportSettings(SingleClient client, String timeStampColumn)
	{
		super();

		init(client, timeStampColumn);
	}

	private void init(SingleClient client, String timeStampColumn)
	{
		this.timeStampColumn = timeStampColumn;
		this.uniqueUsers = false;

		this.client = client;

		parsingTerminable = new SimpleParsingTerminable(this);
		parsingTerminable.setDateOnly(true);

		initInternal();
	}

	protected void initInternal()
	{
		setShowTables(true);
		setUniqueUsers(true);
	}

	@Override
	public ActivityPagination getPagination()
	{
		ActivityPagination pagination = new ActivityPagination(timeStampColumn,
			parsingTerminable.getStartTimeSeconds(),
			parsingTerminable.getEndTimeSeconds(),
			isUniqueUsers(),
			super.getPagination());

		pagination.setOrderBy(null);

		return pagination;
	}

	@JsonIgnore
	public String getStartTimeString()
	{
		return parsingTerminable.toDateString(getStartTimeSeconds());
	}

	@JsonIgnore
	public void setStartTimeString(String startTimeStr) throws ParseException
	{
		setStartTimeSeconds(parsingTerminable.parseToSeconds(startTimeStr));
	}

	public int getStartTimeSeconds()
	{
		return parsingTerminable.getStartTimeSeconds();
	}

	public void setStartTimeSeconds(int startTimeSeconds)
	{
		parsingTerminable.setStartTimeSeconds(startTimeSeconds);
	}

	@JsonIgnore
	public String getEndTimeString()
	{
		return parsingTerminable.toDateString(getEndTimeSeconds());
	}

	@JsonIgnore
	public void setEndTimeString(String endTimeStr) throws ParseException
	{
		setEndTimeSeconds(parsingTerminable.parseToSeconds(endTimeStr));
	}

	public int getEndTimeSeconds()
	{
		return parsingTerminable.getEndTimeSeconds();
	}

	public void setEndTimeSeconds(int endTimeSeconds)
	{
		parsingTerminable.setEndTimeSeconds(endTimeSeconds);
	}

	@JsonIgnore
	@Override
	public SingleClient getClient()
	{
		return client;
	}

	@JsonIgnore
	@Override
	public void setClient(SingleClient client)
	{
		this.client = client;
	}

	@Override
	protected void copyFromInternal(ReportSettings right)
	{
		super.copyFromInternal(right);
		if ( right instanceof ActivityReportSettings )
		{
			ActivityReportSettings rightActivity = (ActivityReportSettings)right;

			this.setStartTimeSeconds(rightActivity.getStartTimeSeconds());
			this.setEndTimeSeconds(rightActivity.getEndTimeSeconds());

			this.setUniqueUsers(rightActivity.isUniqueUsers());
		}
	}

	public boolean isUniqueUsers()
	{
		return uniqueUsers;
	}

	public void setUniqueUsers(boolean uniqueUsers)
	{
		this.uniqueUsers = uniqueUsers;
	}
}
