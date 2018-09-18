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
package com.i4one.base.model.instantwin;

import com.i4one.base.dao.terminable.BaseTerminableClientRecordType;
import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class InstantWinRecord extends BaseTerminableClientRecordType implements TerminableClientRecordType
{
	static final long serialVersionUID = 42L;

	private Integer userid;

	private Float percentwin;
	private Integer winnerlimit;
	private Integer winnercount;

	private IString title;
	private IString winnermsg;
	private IString losermsg;

	private Boolean exclusive;

	public InstantWinRecord()
	{
		super();

		percentwin = 0.0f;
		winnerlimit = 0;
		winnercount = 0;

		title = new IString();
		winnermsg = new IString();
		losermsg = new IString();
		exclusive = false;
	}

	@Override
	public String getTableName()
	{
		return "instantwins";
	}

	public Integer getUserid()
	{
		return userid;
	}

	public void setUserid(Integer userid)
	{
		this.userid = userid;
	}

	public IString getTitle()
	{
		return title;
	}

	public void setTitle(IString title)
	{
		this.title = title;
	}

	public Float getPercentwin()
	{
		return percentwin;
	}

	public void setPercentwin(Float percentwin)
	{
		this.percentwin = percentwin;
	}

	public Integer getWinnerlimit()
	{
		return winnerlimit;
	}

	public void setWinnerlimit(Integer winnerlimit)
	{
		this.winnerlimit = winnerlimit;
	}

	public Integer getWinnercount()
	{
		return winnercount;
	}

	public void setWinnercount(Integer winnercount)
	{
		this.winnercount = winnercount;
	}

	public IString getWinnermsg()
	{
		return winnermsg;
	}

	public void setWinnermsg(IString winnermsg)
	{
		this.winnermsg = winnermsg;
	}

	public IString getLosermsg()
	{
		return losermsg;
	}

	public void setLosermsg(IString losermsg)
	{
		this.losermsg = losermsg;
	}

	public Boolean getExclusive()
	{
		return exclusive;
	}

	public void setExclusive(Boolean exclusive)
	{
		this.exclusive = exclusive;
	}

}
