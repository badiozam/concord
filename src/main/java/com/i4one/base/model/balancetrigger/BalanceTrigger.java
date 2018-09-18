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

import com.i4one.base.core.Utils;
import com.i4one.base.model.manager.terminable.BaseTerminableSingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.i18n.IString;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class BalanceTrigger extends BaseTerminableSingleClientType<BalanceTriggerRecord>
{
	static final long serialVersionUID = 42L;

	private transient Balance balance;

	public BalanceTrigger()
	{
		super(new BalanceTriggerRecord());
	}

	protected BalanceTrigger(BalanceTriggerRecord delegate)
	{
		super(delegate);
	}
	
	@Override
	protected void init()
	{
		super.init();

		if ( balance == null )
		{
			balance = new Balance();
		}
		balance.resetDelegateBySer(getDelegate().getBalid());
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

		if ( !belongsTo(getBalance().getClient()) )
		{
			retVal.addError("client", new ErrorMessage("msg.base.BalanceTrigger.clientMismatch", "The trigger $item is not available for the balance's client $balance.client", new Object[]{ "item", this, "balance", getBalance()}));
		}

		return retVal;
	}

	public IString getTitle()
	{
		return getDelegate().getTitle();
	}

	public void setTitle(IString title)
	{
		getDelegate().setTitle(title);
	}


	public int getAmount()
	{
		return getDelegate().getAmount();
	}

	public void setAmount(int amount)
	{
		getDelegate().setAmount(amount);
	}

	public int getMaxUserUsage()
	{
		return getDelegate().getMaxuserusage();
	}

	public void setMaxUserUsage(int maxuserusage)
	{
		getDelegate().setMaxuserusage(maxuserusage);
	}

	public int getMaxGlobalUsage()
	{
		return getDelegate().getMaxglobalusage();
	}

	public void setMaxGlobalUsage(int maxglobalusage)
	{
		getDelegate().setMaxglobalusage(maxglobalusage);
	}

	public int getFrequency()
	{
		return getDelegate().getFrequency();
	}

	public void setFrequency(int frequency)
	{
		getDelegate().setFrequency(frequency);
	}

	public boolean getSynced()
	{
		return getDelegate().getSynced();
	}

	public void setSynced(boolean synced)
	{
		getDelegate().setSynced(synced);
	}

	public boolean isExclusive()
	{
		return getDelegate().getExclusive();
	}

	public void setExclusive(boolean exclusive)
	{
		getDelegate().setExclusive(exclusive);
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
		getDelegate().setBalid(balance.getSer());
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		boolean exclusive = Utils.defaultIfNaB(csv.get(0), true); csv.remove(0);

		if ( exclusive )
		{
			setExclusive(true);
			setAmount(Utils.defaultIfNaN(csv.get(0), 0)); csv.remove(0);
			getBalance().resetDelegateBySer(Utils.defaultIfNaN(csv.get(0), 0)); csv.remove(0);

			return true;
		}
		else
		{
			setExclusive(false);
			resetDelegateBySer(Utils.defaultIfNaN(csv.get(0), 0)); csv.remove(0);

			return exists();
		}
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
			retVal.append("Exclusive").append(",");
			if ( isExclusive() )
			{
				// XXX: Needs i18n
				retVal.append("Amount").append(",");
				retVal.append("Balance ID").append(",");
			}
			else
			{
				// XXX: Needs i18n
				retVal.append("ID").append(",");
			}
		}
		else
		{
			retVal.append(isExclusive()).append(",");
			if ( isExclusive() )
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
			else
			{
				retVal.append(getSer()).append(",");
			}
		}

		return retVal;
	}

	@Override
	protected String uniqueKeyInternal()
	{
		// A client/title uniquely identifies a balance trigger and since
		// we inherit from ClientType<>, the client is already included
		// in the uniqueKey()
		//
		return getTitle() + "-" + getBalance().getSer() + "-" + getAmount();
	}
}
