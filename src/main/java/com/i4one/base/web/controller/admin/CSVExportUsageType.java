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

import com.i4one.base.core.Utils;
import com.i4one.base.model.UsageType;
import com.i4one.base.model.user.User;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 */
public class CSVExportUsageType implements UsageType
{
	private UsageType delegate;

	public CSVExportUsageType()
	{
		delegate = null;
	}

	public CSVExportUsageType(UsageType delegate)
	{
		this.delegate = delegate;
	}

	@Override
	public Date getTimeStamp()
	{
		return getDelegate().getTimeStamp();
	}

	@Override
	public int getTimeStampSeconds()
	{
		return getDelegate().getTimeStampSeconds();
	}

	@Override
	public void setTimeStampSeconds(int timeStamp)
	{
		throw new UnsupportedOperationException("Cannot modify delegate from here.");
	}

	@Override
	public void setUser(User user)
	{
		throw new UnsupportedOperationException("Cannot modify delegate from here.");
	}

	@Override
	public User getUser()
	{
		return getDelegate().getUser();
	}

	public UsageType getDelegate()
	{
		return delegate;
	}

	public void setDelegate(UsageType delegate)
	{
		this.delegate = delegate;
	}

	public String toCSV(boolean header)
	{
		StringBuilder csv = new StringBuilder();

		if ( header )
		{
			// XXX: Needs i18n, consider returning a list of strings
			// instead so that the caller can convert to messages
			//
			csv.append(Utils.csvEscape("Username")).append(",");
			csv.append(Utils.csvEscape("Name")).append(",");
			csv.append(Utils.csvEscape("Age")).append(",");
			csv.append(Utils.csvEscape("Gender")).append(",");
			csv.append(Utils.csvEscape("Postal Code")).append(",");
			csv.append(Utils.csvEscape("E-mail")).append(",");
			csv.append(Utils.csvEscape("Time Stamp")).append(",");
		}
		else
		{
			User user = getDelegate().getUser();
			csv.append("\"").append(Utils.csvEscape(user.getUsername())).append("\"").append(",");
			csv.append("\"").append(Utils.csvEscape(user.getFirstName() + " " + user.getLastName())).append("\"").append(",");
			csv.append("\"").append(user.getAgeInYears()).append("\"").append(",");
			csv.append("\"").append(user.getGender()).append("\"").append(",");
			csv.append("\"").append(Utils.csvEscape(user.getZipcode())).append("\"").append(",");
			csv.append("\"").append(Utils.csvEscape(user.getEmail())).append("\"").append(",");
			csv.append("\"").append(user.getCreateTimeSeconds()).append("\"").append(",");
		}

		return csv.toString();
	}
}