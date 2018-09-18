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

import com.i4one.base.model.BaseActivityType;
import com.i4one.base.model.UsageType;
import com.i4one.base.model.transaction.Transaction;

/**
 * This class represents a user's BalanceTrigger activity.
 * 
 * @author Hamid Badiozamani
 */
public class UserBalanceTrigger extends BaseActivityType<UserBalanceTriggerRecord, BalanceTriggerRecord, BalanceTrigger> implements UsageType<UserBalanceTriggerRecord>
{
	static final long serialVersionUID = 42L;

	private transient Transaction transaction;

	public UserBalanceTrigger()
	{
		super(new UserBalanceTriggerRecord());
	}

	protected UserBalanceTrigger(UserBalanceTriggerRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( transaction == null )
		{
			transaction = new Transaction();
		}
		transaction.resetDelegateBySer(getDelegate().getTransactionid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setTransaction(getTransaction());
	}

	public BalanceTrigger getBalanceTrigger()
	{
		return getActionItem();
	}

	public BalanceTrigger getBalanceTrigger(boolean doLoad)
	{
		return getActionItem(doLoad);
	}

	public void setBalanceTrigger(BalanceTrigger balanceTrigger)
	{
		setActionItem(balanceTrigger);
	}

	public Transaction getTransaction()
	{
		return getTransaction(true);
	}

	public Transaction getTransaction(boolean doLoad) 
	{
		if ( doLoad )
		{
			transaction.loadedVersion();
		}

		return transaction;
	}

	public void setTransaction(Transaction transaction)
	{
		this.transaction = transaction;
		getDelegate().setTransactionid(transaction.getSer());
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return "" + getDelegate().getTransactionid();
	}

	@Override
	protected BalanceTrigger newActionItem()
	{
		return new BalanceTrigger();
	}
}