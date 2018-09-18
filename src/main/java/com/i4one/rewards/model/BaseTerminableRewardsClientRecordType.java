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
package com.i4one.rewards.model;

import com.i4one.base.dao.categorizable.BaseCategorizableTerminableSiteGroupRecordType;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseTerminableRewardsClientRecordType extends BaseCategorizableTerminableSiteGroupRecordType implements TerminableRewardsClientRecordType
{
	private Integer prizeid;

	private Integer initreserve;
	private Integer currreserve;


	private Integer purchasestarttm;
	private Integer purchaseendtm;

	public BaseTerminableRewardsClientRecordType()
	{
		prizeid = 0;

		initreserve = 0;
		currreserve = 0;

		purchasestarttm = 0;
		purchaseendtm = 0;
	}

	@Override
	public String getSchemaName()
	{
		return "rewards";
	}

	@Override
	public Integer getPurchasestarttm()
	{
		return purchasestarttm;
	}

	@Override
	public void setPurchasestarttm(Integer purchasestarttm)
	{
		this.purchasestarttm = purchasestarttm;
	}

	@Override
	public Integer getPurchaseendtm()
	{
		return purchaseendtm;
	}

	@Override
	public void setPurchaseendtm(Integer purchaseendtm)
	{
		this.purchaseendtm = purchaseendtm;
	}

	@Override
	public Integer getPrizeid()
	{
		return prizeid;
	}

	@Override
	public void setPrizeid(Integer prizeid)
	{
		this.prizeid = prizeid;
	}

	@Override
	public Integer getInitreserve()
	{
		return initreserve;
	}

	@Override
	public void setInitreserve(Integer initreserve)
	{
		this.initreserve = initreserve;
	}

	@Override
	public Integer getCurrreserve()
	{
		return currreserve;
	}

	@Override
	public void setCurrreserve(Integer currreserve)
	{
		this.currreserve = currreserve;
	}

}
