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
package com.i4one.rewards.model.raffle;

import com.i4one.base.model.i18n.IString;
import com.i4one.rewards.model.BaseTerminableRewardsClientRecordType;
import com.i4one.rewards.model.TerminableRewardsClientRecordType;

/**
 * @author Hamid Badiozamani
 */
public class RaffleRecord extends BaseTerminableRewardsClientRecordType implements TerminableRewardsClientRecordType
{
	static final long serialVersionUID = 42L;

	private IString title;
	private Integer userlimit;
	private Integer orderweight;

	private IString intro;
	private IString outro;

	public RaffleRecord()
	{
		super();

		userlimit = 0;
		orderweight = 0;

		title = new IString();
		intro = new IString();
		outro = new IString();
	}

	@Override
	public String getTableName()
	{
		return "raffles";
	}

	public IString getTitle()
	{
		return title;
	}

	public void setTitle(IString title)
	{
		this.title = title;
	}

	public IString getIntro()
	{
		return intro;
	}

	public void setIntro(IString intro)
	{
		this.intro = intro;
	}

	public IString getOutro()
	{
		return outro;
	}

	public void setOutro(IString outro)
	{
		this.outro = outro;
	}

	public Integer getOrderweight()
	{
		return orderweight;
	}

	public void setOrderweight(Integer orderweight)
	{
		this.orderweight = orderweight;
	}

	public Integer getUserlimit()
	{
		return userlimit;
	}

	public void setUserlimit(Integer userlimit)
	{
		this.userlimit = userlimit;
	}
}
