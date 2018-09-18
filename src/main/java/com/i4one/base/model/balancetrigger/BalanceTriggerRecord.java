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
package com.i4one.base.model.balancetrigger;

import com.i4one.base.dao.terminable.BaseTerminableClientRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class BalanceTriggerRecord extends BaseTerminableClientRecordType
{
	static final long serialVersionUID = 42L;

	private IString title;

	private Integer amount;
	private Integer frequency;
	private Integer maxuserusage;
	private Integer maxglobalusage;
	private Boolean synced;
	private Boolean exclusive;

	private Integer balid;

	public BalanceTriggerRecord()
	{
		title = new IString("");

		balid = 0;
		amount = 0;
		frequency = 1;
		maxuserusage = 1;
		maxglobalusage = 0;

		synced = false;
		exclusive = false;
	}

	@Override
	public String getTableName()
	{
		return "balancetriggers";
	}

	public IString getTitle()
	{
		return title;
	}

	public void setTitle(IString title)
	{
		this.title = title;
	}

	public Integer getAmount()
	{
		return amount;
	}

	public void setAmount(Integer amount)
	{
		this.amount = amount;
	}

	public Integer getFrequency()
	{
		return frequency;
	}

	public void setFrequency(Integer frequency)
	{
		this.frequency = frequency;
	}

	public Integer getMaxuserusage()
	{
		return maxuserusage;
	}

	public void setMaxuserusage(Integer maxuserusage)
	{
		this.maxuserusage = maxuserusage;
	}

	public Integer getMaxglobalusage()
	{
		return maxglobalusage;
	}

	public void setMaxglobalusage(Integer maxglobalusage)
	{
		this.maxglobalusage = maxglobalusage;
	}

	public Integer getBalid()
	{
		return balid;
	}

	public final  void setBalid(Integer balid)
	{
		this.balid = balid;
	}

	public Boolean getSynced()
	{
		return synced;
	}

	public void setSynced(Boolean synced)
	{
		this.synced = synced;
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
