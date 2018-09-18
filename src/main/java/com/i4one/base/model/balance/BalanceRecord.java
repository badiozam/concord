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
package com.i4one.base.model.balance;

import com.i4one.base.dao.BaseClientRecordType;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class BalanceRecord extends BaseClientRecordType implements ClientRecordType
{
	static final long serialVersionUID = 42L;

	private Integer featureid;
	private String feature;

	private IString singleName;
	private IString pluralName;

	private Boolean active;

	public BalanceRecord()
	{
		super();

		featureid = 0;
		feature = "";

		singleName = new IString();
		pluralName = new IString();

		active = false;
	}

	@Override
	public String getTableName()
	{
		return "balances";
	}

	public IString getPluralName()
	{
		return pluralName;
	}

	public void setPluralName(IString pluralName)
	{
		this.pluralName = pluralName;
	}

	public IString getSingleName()
	{
		return singleName;
	}

	public void setSingleName(IString singleName)
	{
		this.singleName = singleName;
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

	public Boolean getActive()
	{
		return active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
	}
}
