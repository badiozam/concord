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
package com.i4one.base.model.transaction;

import com.i4one.base.dao.BaseClientRecordType;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.i18n.IString;

/**
 *
 * @author Hamid Badiozamani
 */
public class TransactionRecord extends BaseClientRecordType implements ClientRecordType
{
	static final long serialVersionUID = 42L;

	private Integer parentid;
	private Integer userid;
	private Integer balid;
	private Integer prevbal;
	private Integer balxacted;
	private Integer newbal;
	private Integer timestamp;
	private String status;
	private IString descr;
	private String sourceip;

	public TransactionRecord()
	{
		userid = 0;
		balid = 0;
	}

	@Override
	public String getTableName()
	{
		return "transactions";
	}

	public Integer getBalxacted()
	{
		return balxacted;
	}

	public void setBalxacted(Integer balxacted)
	{
		this.balxacted = balxacted;
	}

	public IString getDescr()
	{
		return descr;
	}

	public void setDescr(IString descr)
	{
		this.descr = descr;
	}

	public Integer getBalid()
	{
		return balid;
	}

	public void setBalid(Integer balid)
	{
		this.balid = balid;
	}

	public Integer getNewbal()
	{
		return newbal;
	}

	public void setNewbal(Integer newbal)
	{
		this.newbal = newbal;
	}

	public Integer getPrevbal()
	{
		return prevbal;
	}

	public void setPrevbal(Integer prevbal)
	{
		this.prevbal = prevbal;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public Integer getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Integer timestamp)
	{
		this.timestamp = timestamp;
	}

	public Integer getUserid()
	{
		return userid;
	}

	public void setUserid(Integer userid)
	{
		this.userid = userid;
	}

	public String getSourceip()
	{
		return sourceip;
	}

	public void setSourceip(String sourceip)
	{
		this.sourceip = sourceip;
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
