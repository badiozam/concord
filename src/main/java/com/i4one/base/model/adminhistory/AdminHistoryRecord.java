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
package com.i4one.base.model.adminhistory;

import com.i4one.base.dao.BaseClientRecordType;
import com.i4one.base.dao.ClientRecordType;

/**
 * @author Hamid Badiozamani
 */
public class AdminHistoryRecord extends BaseClientRecordType implements ClientRecordType
{
	static final long serialVersionUID = 42L;

	private Integer adminid;
	private Integer parentid;

	private String feature;
	private Integer featureid;
	private Integer timestamp;
	private String action;
	private String sourceip;
	private String descr;

	private String before;
	private String after;

	public AdminHistoryRecord()
	{
		adminid = 0;
	}

	@Override
	public String getTableName()
	{
		return "adminhistory";
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public Integer getAdminid()
	{
		return adminid;
	}

	public void setAdminid(Integer adminid)
	{
		this.adminid = adminid;
	}

	public String getDescr()
	{
		return descr;
	}

	public void setDescr(String descr)
	{
		this.descr = descr;
	}

	public String getFeature()
	{
		return feature;
	}

	public void setFeature(String feature)
	{
		this.feature = feature;
	}

	public Integer getFeatureid()
	{
		return featureid;
	}

	public void setFeatureid(Integer featureid)
	{
		this.featureid = featureid;
	}

	public String getSourceip()
	{
		return sourceip;
	}

	public void setSourceip(String sourceip)
	{
		this.sourceip = sourceip;
	}

	public Integer getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Integer timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getAfter()
	{
		return after;
	}

	public void setAfter(String after)
	{
		this.after = after;
	}

	public String getBefore()
	{
		return before;
	}

	public void setBefore(String before)
	{
		this.before = before;
	}

	public Integer getParentid()
	{
		return parentid;
	}

	public void setParentid(Integer parentid)
	{
		this.parentid = parentid;
	}

}
