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
package com.i4one.base.model.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.RecordTypeDelegator;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 */
public class Report extends BaseRecordTypeDelegator<ReportRecord> implements RecordTypeDelegator<ReportRecord>
{
	private Report parent;

	public Report()
	{
		super(new ReportRecord());
	}

	protected Report(ReportRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		// We have to do this type of initialization here and set the parent to null due to circular
		// dependency issues which would cause infinite recursion. By deferring the parent reference's
		// initialization we avoid this problem.
		//
		ReportRecord parentRecord = new ReportRecord();
		if ( getDelegate().getParentid() != null && getDelegate().getParentid() > 0 )
		{
			parentRecord.setSer(getDelegate().getParentid());
			parent = new Report(parentRecord);
		}
		else
		{
			parent = null;
		}
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setParent(getParent());
	}

	@JsonIgnore
	public Report getParent()
	{
		return getParent(true);
	}

	public Report getParent(boolean doLoad)
	{
		if ( parent == null )
		{
			parent = new Report(new ReportRecord());
		}
		else if ( doLoad )
		{
			parent.loadedVersion();
		}

		return parent;
	}

	public void setParent(Report parent)
	{
		this.parent = parent;
		getDelegate().setParentid(parent.getSer());
	}

	public String getTitle()
	{
		return getDelegate().getTitle();
	}

	public void setTitle(String title)
	{
		getDelegate().setTitle(title);
	}

	public String getProperties()
	{
		return getDelegate().getProperties();
	}

	public void setProperties(String props)
	{
		getDelegate().setProperties(props);
	}

	public int getLastSer()
	{
		return getDelegate().getLastser();
	}

	public void setLastSer(int lastSer)
	{
		getDelegate().setLastser(lastSer);
	}

	public Date getTimeStamp()
	{
		return Utils.toDate(getTimeStampSeconds());
	}

	public int getTimeStampSeconds()
	{
		return getDelegate().getTimestamp();
	}

	public void setTimeStampSeconds(int timestamp)
	{
		getDelegate().setTimestamp(timestamp);
	}

	public int getStartTimeSeconds()
	{
		return getDelegate().getStarttm();
	}

	public void setStartTimeSeconds(int starttm)
	{
		getDelegate().setStarttm(starttm);
	}

	public int getEndTimeSeconds()
	{
		return getDelegate().getEndtm();
	}

	public void setEndTimeSeconds(int endtm)
	{
		getDelegate().setEndtm(endtm);
	}

	public int getTotal()
	{
		return getDelegate().getTotal();
	}

	public void setTotal(int total)
	{
		getDelegate().setTotal(total);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle();
	}
}
