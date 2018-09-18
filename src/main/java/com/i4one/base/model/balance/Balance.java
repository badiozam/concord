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

import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.GenericFeature;

/**
 * @author Hamid Badiozamani
 */
public class Balance extends BaseSingleClientType<BalanceRecord>
{
	static final long serialVersionUID = 42L;

	public Balance()
	{
		super(new BalanceRecord());
	}

	protected Balance(BalanceRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();
	}

	public GenericFeature getOwner()
	{
		GenericFeature retVal = new GenericFeature(getDelegate().getFeatureid(), getDelegate().getFeature());

		return retVal;
	}

	public void setOwner(SingleClientType<?> owner)
	{
		setClientInternal(owner.getClient());

		getDelegate().setFeature(owner.getFeatureName());
		getDelegate().setFeatureid(owner.getSer());
	}

	public IString getPluralName()
	{
		return getDelegate().getPluralName();
	}

	public void setPluralName(IString pluralName)
	{
		getDelegate().setPluralName(pluralName);
	}

	public IString getSingleName()
	{
		return getDelegate().getSingleName();
	}

	public void setSingleName(IString singleName)
	{
		getDelegate().setSingleName(singleName);
	}

	public boolean getActive()
	{
		return getDelegate().getActive();
	}

	public boolean isActive()
	{
		return getDelegate().getActive();
	}

	public void setActive(boolean active)
	{
		getDelegate().setActive(active);
	}

	public boolean isDefaultBalance()
	{
		return getDelegate().getFeature().equals(SingleClient.getRoot().getFeatureName());
	}

	public IString formatName(int amount)
	{
		if ( amount == 1 )
		{
			return getSingleName();
		}
		else
		{
			return getPluralName();
		}
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getFeatureid() + "-" + getDelegate().getFeature();
	}
}