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

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Hamid Badiozamani
 */
@JsonTypeName("g")
public class GenderReport extends BaseDemo implements Demo
{
	private String gender;

	public GenderReport()
	{
		super();

		initInternal("o");
	}

	public GenderReport(String gender)
	{
		super();

		initInternal(gender);
	}

	private void initInternal(String gender)
	{
		this.gender = gender;

		newDisplayName(gender);
		setTitle(getDisplayName());
	}

	@Override
	public boolean isEligible(ReportUsageType item)
	{
		String userGender = item.getUser().getGender();
		if ( gender.equals("a") )
		{
			return true;
		}
		else
		{
			return userGender.equalsIgnoreCase(gender);
		}
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	@Override
	public GenderReport cloneReport()
	{
		return new GenderReport(gender);
	}

	@Override
	protected boolean isExclusiveToInternal(ReportType right)
	{
		if ( right instanceof GenderReport )
		{
			GenderReport rightReport = (GenderReport)right;
			if ( getGender().equals("a") || rightReport.getGender().equals("a"))
			{
				return false;
			}
			else
			{
				// If we have a different gender, then none of our data will be 
				// in the incoming's set
				//
				return !getGender().equals(((GenderReport)right).getGender());
			}
		}
		else
		{
			return false;
		}
	}
}
