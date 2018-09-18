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
package com.i4one.base.model.friendref.reports;

import com.i4one.base.model.friendref.FriendRef;
import com.i4one.base.model.friendref.FriendRefRecord;
import com.i4one.base.model.report.BaseReportUsageType;
import com.i4one.base.model.report.ReportUsageType;

/**
 * @author Hamid Badiozamani
 */
public class FriendRefSignUpUsage extends BaseReportUsageType<FriendRefRecord> implements ReportUsageType<FriendRefRecord>
{
	private FriendRef friendRef;

	public FriendRefSignUpUsage(FriendRefRecord delegate)
	{
		super(delegate);
		friendRef = new FriendRef(delegate);
	}

	public FriendRef getFriendRef()
	{
		return friendRef;
	}

	@Override
	public int getTimeStampSeconds()
	{
		if ( friendRef.hasRegistered() )
		{
			return friendRef.getUser().getCreateTimeSeconds();
		}
		else
		{
			return 0;
		}
	}
}
