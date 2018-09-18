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

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.manager.GenericFeature;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class BalanceExpense extends BaseRecordTypeDelegator<BalanceExpenseRecord>
{
	static final long serialVersionUID = 42L;

	private transient Balance balance;

	public BalanceExpense()
	{
		super(new BalanceExpenseRecord());
	}

	protected BalanceExpense(BalanceExpenseRecord delegate)
	{
		super(delegate);
	}
	
	@Override
	protected void init()
	{
		if ( balance == null )
		{
			balance = new Balance();
		}
		balance.resetDelegateBySer(getDelegate().getBalanceid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setBalance(getBalance());
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		// We leave the possibility of a zero value in order to remove
		// an expense
		//
		if ( getAmount() < 0 )
		{
			retVal.addError("amount", new ErrorMessage("msg.base.BalanceExpense.invalidAmount", "The amount must be greater than zero", new Object[]{ "item", this, "balance", getBalance()}));
		}

		return retVal;
	}

	public int getAmount()
	{
		return getDelegate().getAmount();
	}

	public void setAmount(int amount)
	{
		getDelegate().setAmount(amount);
	}

	public Balance getBalance()
	{
		return getBalance(true);
	}

	public Balance getBalance(boolean doLoad)
	{
		if ( doLoad )
		{
			balance.loadedVersion();
		}

		return balance;
	}

	public void setBalance(Balance balance)
	{
		this.balance = balance;
		getDelegate().setBalanceid(balance.getSer());
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		setAmount(Utils.defaultIfNaN(csv.get(0), 0)); csv.remove(0);
		getBalance().resetDelegateBySer(Utils.defaultIfNaN(csv.get(0), 0)); csv.remove(0);

		return true;
	}

	@Override
	public StringBuilder toCSVInternal(boolean header)
	{
		// Skip start/end times because we assume this trigger is part of
		// some attachable. NOTE: This means that triggers are not CSV
		// importable as standalone objects.
		//
		StringBuilder retVal = new StringBuilder();

		if ( header )
		{
			// XXX: Needs i18n
			retVal.append("Amount").append(",");
			retVal.append("Balance ID").append(",");
		}
		else
		{
			retVal.append(getAmount()).append(",");
			if ( !getBalance().exists() || getBalance().isDefaultBalance() )
			{
				// We leave the default balance empty to
				// facilitate importing on other clients
				//
				retVal.append(",");
			}
			else
			{
				retVal.append(getBalance().getSer()).append(",");
			}
		}

		return retVal;
	}

	public GenericFeature getOwner()
	{
		GenericFeature retVal = new GenericFeature(getDelegate().getFeatureid(), getDelegate().getFeature());

		return retVal;
	}

	public void setOwner(SingleClientType<?> owner)
	{
		getDelegate().setFeature(owner.getFeatureName());
		getDelegate().setFeatureid(owner.getSer());
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getBalance().getSer() + "-" + getDelegate().getFeature() + ":" + getDelegate().getFeatureid();
	}
}
