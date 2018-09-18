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

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Utils;
import com.i4one.base.dao.UsageRecordType;
import com.i4one.base.model.user.User;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseReportUsageType<U extends UsageRecordType> extends BaseLoggable implements ReportUsageType<U>
{
	private transient U delegate;
	private transient User user;

	public BaseReportUsageType()
	{
		user = new User();
	}

	public BaseReportUsageType(U delegate)
	{
		user = new User();
		user.setSer(delegate.getUserid());

		this.delegate = delegate;
	}

	@Override
	public User getUser()
	{
		return user;
	}

	@Override
	public void setUser(User user)
	{
		this.user = user;
	}

	@Override
	public U getDelegate()
	{
		return delegate;
	}

	public void setDelegate(U delegate)
	{
		this.delegate = delegate;
	}

	@Override
	public void setTimeStampSeconds(int timestamp)
	{
		throw new UnsupportedOperationException("Can't set timestamp.");
	}

	@Override
	public Date getTimeStamp()
	{
		return Utils.toDate(getTimeStampSeconds());
	}

	@Override
	public String toString()
	{
		return user.getSer() + " - " + getTimeStamp();
	}
}