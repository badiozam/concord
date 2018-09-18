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

import com.i4one.base.model.BaseActivityType;
import com.i4one.base.model.UsageType;
import com.i4one.base.model.transaction.Transaction;

/**
 * @author Hamid Badiozamani
 */
public class UserInstantWin extends BaseActivityType<UserInstantWinRecord, InstantWinRecord, InstantWin> implements UsageType<UserInstantWinRecord>
{
	static final long serialVersionUID = 42L;

	private transient Transaction transaction;
	private transient boolean didWin;

	public UserInstantWin()
	{
		super(new UserInstantWinRecord());

		didWin = false;
	}

	protected UserInstantWin(UserInstantWinRecord delegate)
	{
		super(delegate);

		didWin = false;
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

	public InstantWin getInstantWin()
	{
		return getActionItem();
	}

	public InstantWin getInstantWin(boolean doLoad)
	{
		return getActionItem(doLoad);
	}

	public void setInstantWin(InstantWin instantWin)
	{
		setActionItem(instantWin);
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

	public boolean getDidWin()
	{
		return didWin;
	}

	public void setDidWin(boolean didWin)
	{
		this.didWin = didWin;
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getUserid() + "-" + getDelegate().getItemid();
	}

	@Override
	protected InstantWin newActionItem()
	{
		return new InstantWin();
	}
}
