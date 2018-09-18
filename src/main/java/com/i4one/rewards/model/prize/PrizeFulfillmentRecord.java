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
package com.i4one.rewards.model.prize;

import com.i4one.base.dao.activity.BaseQuantifiedActivityRecordType;

/**
 * @author Hamid Badiozamani
 */
public class PrizeFulfillmentRecord extends BaseQuantifiedActivityRecordType
{
	private Integer adminid;
	private Integer status;
	private String notes;

	public PrizeFulfillmentRecord()
	{
		adminid = 0;

		status = 0;
		notes = "";
	}

	@Override
	public String getSchemaName()
	{
		return "rewards";
	}

	@Override
	public String getTableName()
	{
		return "prizefulfillments";
	}

	@Override
	public Integer getUserid()
	{
		return 0;
	}

	@Override
	public void setUserid(Integer userid)
	{
	}

	public Integer getAdminid()
	{
		return adminid;
	}

	public void setAdminid(Integer adminid)
	{
		this.adminid = adminid;
	}

	public Integer getStatus()
	{
		return status;
	}

	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

}
