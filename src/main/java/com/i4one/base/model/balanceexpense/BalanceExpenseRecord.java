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
package com.i4one.base.model.balanceexpense;

import com.i4one.base.dao.terminable.BaseTerminableClientRecordType;

/**
 * @author Hamid Badiozamani
 */
public class BalanceExpenseRecord extends BaseTerminableClientRecordType
{
	static final long serialVersionUID = 42L;

	private Integer amount;

	private Integer balanceid;

	private Integer featureid;
	private String feature;

	public BalanceExpenseRecord()
	{
		balanceid = 0;
		amount = 0;
	}

	@Override
	public String getTableName()
	{
		return "balanceexpenses";
	}

	public Integer getAmount()
	{
		return amount;
	}

	public void setAmount(Integer amount)
	{
		this.amount = amount;
	}

	public Integer getFeatureid()
	{
		return featureid;
	}

	public void setFeatureid(Integer featureid)
	{
		this.featureid = featureid;
	}

	public String getFeature()
	{
		return feature;
	}

	public void setFeature(String feature)
	{
		this.feature = feature;
	}

	public Integer getBalanceid()
	{
		return balanceid;
	}

	public final  void setBalanceid(Integer balid)
	{
		this.balanceid = balid;
	}
}
