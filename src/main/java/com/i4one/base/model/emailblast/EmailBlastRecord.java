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
package com.i4one.base.model.emailblast;

import com.i4one.base.dao.BaseClientRecordType;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class EmailBlastRecord extends BaseClientRecordType implements ClientRecordType
{
	static final long serialVersionUID = 42L;

	private IString title;

	private Integer emailtemplateid;

	private Integer status;
	private Integer totalcount;
	private Integer totalsent;

	private IString target;
	private String targetsql;

	private String schedule;

	private Integer maturetm;
	private Integer sendstarttm;
	private Integer sendendtm;

	public EmailBlastRecord()
	{
		super();

		status = 0;
		totalcount = 0;
		totalsent = 0;

		title = new IString("");
		target = new IString("");
		targetsql = "";
		schedule = "";

		maturetm = 0;
		sendstarttm = 0;
		sendendtm = 0;

		emailtemplateid = 0;
	}

	@Override
	public String getTableName()
	{
		return "emailblasts";
	}

	public IString getTitle()
	{
		return title;
	}

	public void setTitle(IString title)
	{
		this.title = title;
	}

	public Integer getStatus()
	{
		return status;
	}

	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public Integer getTotalcount()
	{
		return totalcount;
	}

	public void setTotalcount(Integer totalcount)
	{
		this.totalcount = totalcount;
	}

	public Integer getTotalsent()
	{
		return totalsent;
	}

	public void setTotalsent(Integer totalsent)
	{
		this.totalsent = totalsent;
	}

	public IString getTarget()
	{
		return target;
	}

	public void setTarget(IString target)
	{
		this.target = target;
	}

	public String getTargetsql()
	{
		return targetsql;
	}

	public void setTargetsql(String targetsql)
	{
		this.targetsql = targetsql;
	}

	public Integer getMaturetm()
	{
		return maturetm;
	}

	public void setMaturetm(Integer maturetm)
	{
		this.maturetm = maturetm;
	}

	public String getSchedule()
	{
		return schedule;
	}

	public void setSchedule(String schedule)
	{
		this.schedule = schedule;
	}

	public Integer getSendstarttm()
	{
		return sendstarttm;
	}

	public void setSendstarttm(Integer sendstarttm)
	{
		this.sendstarttm = sendstarttm;
	}

	public Integer getSendendtm()
	{
		return sendendtm;
	}

	public void setSendendtm(Integer sendendtm)
	{
		this.sendendtm = sendendtm;
	}

	public Integer getEmailtemplateid()
	{
		return emailtemplateid;
	}

	public void setEmailtemplateid(Integer emailtemplateid)
	{
		this.emailtemplateid = emailtemplateid;
	}
}
