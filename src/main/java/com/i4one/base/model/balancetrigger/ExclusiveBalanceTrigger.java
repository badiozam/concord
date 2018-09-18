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

import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.i18n.IString;

/**
 * An exclusive balance trigger is a balance trigger that can be used only for one
 * specific item and is not shared anywhere else.
 * 
 * @author Hamid Badiozamani
 * 
 * @param <T> The item that this trigger belongs to
 */
public class ExclusiveBalanceTrigger<T extends SingleClientType> extends BalanceTrigger
{
	private transient T owner;

	public ExclusiveBalanceTrigger(T owner)
	{
		super();

		this.owner = owner;
		setClient(owner.getClient());
	}

	@Override
	protected void init()
	{
		super.init();

		setExclusive(true);
		setMaxGlobalUsage(0);
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( !getOwner().belongsTo(getClient())) 
		{
			retVal.addError("client", new ErrorMessage("msg.base.ExclusiveBalanceTrigger.clientMismatch", "This item is not available for the owner's client", new Object[]{ "item", this, "owner", getOwner()}));
		}

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		// Force the following values regardless of what was set outside
		//
		setClient(getOwner().getClient());
		setTitle(new IString(getOwner().getFeatureName() + ":" + getOwner().getSer()));
		setMaxGlobalUsage(0);
		setExclusive(true);
	}

	public T getOwner()
	{
		return owner;
	}

	public void setOwner(T owner)
	{
		this.owner = owner;
	}

	public void fromTrigger(BalanceTrigger right)
	{
		// Only the fields below will matter, all other fields are
		// forced to specific behavior in the overrides and constructor
		//
		this.setSer(right.getSer());
		this.setBalance(right.getBalance());
		this.setAmount(right.getAmount());
		this.setStartTimeSeconds(right.getStartTimeSeconds());
		this.setEndTimeSeconds(right.getEndTimeSeconds());
		this.setMaxGlobalUsage(right.getMaxGlobalUsage());
		this.setMaxUserUsage(right.getMaxUserUsage());
		this.setFrequency(right.getFrequency());
		this.setSynced(right.getSynced());

		setOverrides();
	}
}
