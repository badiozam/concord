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
package com.i4one.base.model.emailblast.fragment;

import com.i4one.base.dao.BaseClientRecordType;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.emailblast.EmailBlastStatus;

/**
 * @author Hamid Badiozamani
 */
public class EmailBlastFragmentRecord extends BaseClientRecordType implements ClientRecordType
{
	static final long serialVersionUID = 42L;

	private Integer emailblastid;
	private Integer status;

	private Integer fragoffset;
	private Integer fraglimit;

	private Integer fragcount;
	private Integer fragsent;

	private String owner;
	private Integer lastupdatetm;

	public EmailBlastFragmentRecord()
	{
		super();

		emailblastid = 0;
		status = EmailBlastStatus.ERROR;

		fragoffset = 0;
		fraglimit = 0;

		fragcount = 0;
		fragsent = 0;

		owner = "";
		lastupdatetm = 0;
	}

	@Override
	public String getTableName()
	{
		return "emailblastfragments";
	}

	public Integer getStatus()
	{
		return status;
	}

	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public Integer getEmailblastid()
	{
		return emailblastid;
	}

	public void setEmailblastid(Integer emailblastid)
	{
		this.emailblastid = emailblastid;
	}

	public Integer getFragoffset()
	{
		return fragoffset;
	}

	public void setFragoffset(Integer fragoffset)
	{
		this.fragoffset = fragoffset;
	}

	public Integer getFraglimit()
	{
		return fraglimit;
	}

	public void setFraglimit(Integer fraglimit)
	{
		this.fraglimit = fraglimit;
	}

	public Integer getFragcount()
	{
		return fragcount;
	}

	public void setFragcount(Integer fragcount)
	{
		this.fragcount = fragcount;
	}

	public Integer getFragsent()
	{
		return fragsent;
	}

	public void setFragsent(Integer fragsent)
	{
		this.fragsent = fragsent;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public Integer getLastupdatetm()
	{
		return lastupdatetm;
	}

	public void setLastupdatetm(Integer lastupdatetm)
	{
		this.lastupdatetm = lastupdatetm;
	}
}
