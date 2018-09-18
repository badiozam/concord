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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.manager.terminable.BaseTerminableSingleClientType;
import com.i4one.base.model.UserType;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.triggerable.SimpleTerminableTriggerable;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import com.i4one.base.model.user.User;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class InstantWin extends BaseTerminableSingleClientType<InstantWinRecord> implements TerminableTriggerableClientType<InstantWinRecord, InstantWin>, UserType
{
	static final long serialVersionUID = 42L;

	private final transient SimpleTerminableTriggerable<InstantWinRecord, InstantWin> triggerable;

	private transient User user;

	public InstantWin()
	{
		super(new InstantWinRecord());

		triggerable = new SimpleTerminableTriggerable<>(this);
	}

	public InstantWin(InstantWinRecord delegate)
	{
		super(delegate);

		triggerable = new SimpleTerminableTriggerable<>(this);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( user == null )
		{
			user = new User();
		}

		if ( getDelegate().getUserid() != null )
		{
			user.resetDelegateBySer(getDelegate().getUserid());
		}
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setUser(getUser());
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getPercentWin() > 1.0f || getPercentWin() < 0.0f )
		{
			retVal.addError("percentWin", new ErrorMessage("msg.base.InstantWin.percentWin.invalidPercentage", "The percentage needs to be a number between 0.00 and 1.00", new Object[]{ "item", this }));
		}

		if ( getWinnerLimit() != 0 && getWinnerLimit() < getWinnerCount() )
		{
			retVal.addError("winnerLimit", new ErrorMessage("msg.base.InstantWin.winnerLimit.invalidLimit", "Winner limit ($item.winnerLimit) cannot be less than the current number of winners ($item.winnerCount)", new Object[]{ "item", this }));
		}

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		if ( isCherryPick() && getPercentWin() == 0.0f )
		{
			// Doesn't make sense to have cherry picked a name and then
			// have that user not win.
			//
			setPercentWin(1.0f);
		}
	}

	public IString getTitle()
	{
		return getDelegate().getTitle();
	}

	public void setTitle(IString title)
	{
		getDelegate().setTitle(title);
	}

	public float getPercentWin()
	{
		return getDelegate().getPercentwin();
	}

	public void setPercentWin(float percentwin)
	{
		getDelegate().setPercentwin(percentwin);
	}

	public int getWinnerLimit()
	{
		return getDelegate().getWinnerlimit();
	}

	public void setWinnerLimit(int winnerlimit)
	{
		getDelegate().setWinnerlimit(winnerlimit);
	}

	public int getWinnerCount()
	{
		return getDelegate().getWinnercount();
	}

	public void setWinnerCount(int winnercount)
	{
		getDelegate().setWinnercount(winnercount);
	}

	public IString getWinnerMessage()
	{
		return getDelegate().getWinnermsg();
	}

	public void setWinnerMessage(IString msg)
	{
		getDelegate().setWinnermsg(msg);
	}

	public IString getLoserMessage()
	{
		return getDelegate().getLosermsg();
	}

	public void setLoserMessage(IString msg)
	{
		getDelegate().setLosermsg(msg);
	}

	public boolean isInfiniteWinners()
	{
		return getWinnerLimit() <= 0;
	}

	public boolean isCherryPick()
	{
		return !Utils.isEmpty(getUser(false).getUsername()) || getUser(false).getSer() > 0;
	}

	@Override
	public User getUser()
	{
		return getUser(true);
	}

	public User getUser(boolean doLoad)
	{
		if ( doLoad )
		{
			user.loadedVersion();
		}

		return user;
	}

	@Override
	public void setUser(User user)
	{
		this.user = user;

		if ( user.exists() )
		{
			getDelegate().setUserid(user.getSer());
		}
		else
		{
			getDelegate().setUserid(null);
		}
	}

	public boolean isExclusive()
	{
		return getDelegate().getExclusive();
	}

	public void setExclusive(boolean exclusive)
	{
		getDelegate().setExclusive(exclusive);
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		boolean exclusive = Utils.defaultIfNaB(csv.get(0), true); csv.remove(0);

		if ( exclusive )
		{
			setExclusive(true);
			setPercentWin(Utils.defaultIfNaF(csv.get(0), 0.0f)); csv.remove(0);
			setWinnerLimit(Utils.defaultIfNaN(csv.get(0), 1)); csv.remove(0);
			getUser().setUsername(csv.get(0)); csv.remove(0);

			triggerable.resetDelegateBySer(Utils.defaultIfNaN(csv.get(0), 0)); csv.remove(0);

			setWinnerMessage(new IString(csv.get(0))); csv.remove(0);
			setLoserMessage(new IString(csv.get(0))); csv.remove(0);

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
				retVal.append("Percentage").append(",");
				retVal.append("Max Winners").append(",");
				retVal.append("Cherry-Picked User").append(",");
				retVal.append("Balance Trigger ID").append(",");
				retVal.append("Display to Winners").append(",");
				retVal.append("Display to Losers").append(",");
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
				retVal.append(Utils.formatDouble(getPercentWin(), 2, getClient().getLocale())).append(",");
				retVal.append(getWinnerLimit()).append(",");
				retVal.append("\"").append(Utils.csvEscape(Utils.forceEmptyStr(getUser().getUsername()))).append("\",");

				if ( triggerable.getNonExclusiveBalanceTriggers().isEmpty() )
				{
					retVal.append(0);
					retVal.append(",");
				}
				else
				{
					retVal.append(triggerable.getNonExclusiveBalanceTriggers().iterator().next().getSer());
					retVal.append(",");
				}
				retVal.append("\"").append(Utils.csvEscape(getWinnerMessage().toString())).append("\",");
				retVal.append("\"").append(Utils.csvEscape(getLoserMessage().toString())).append("\",");
			}
			else
			{
				retVal.append(getSer()).append(",");
			}
		}

		return retVal;
	}

	@Override
	public Set<ExclusiveBalanceTrigger<InstantWin>> getExclusiveBalanceTriggers()
	{
		return triggerable.getExclusiveBalanceTriggers();
	}

	@Override
	public Set<BalanceTrigger> getNonExclusiveBalanceTriggers()
	{
		return triggerable.getNonExclusiveBalanceTriggers();
	}

	@Override
	public Set<BalanceTrigger> getBalanceTriggers()
	{
		return triggerable.getBalanceTriggers();
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> balanceTriggers)
	{
		triggerable.setBalanceTriggers(balanceTriggers);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle() + "-" + getDelegate().getUserid() + "-" + getPercentWin();
	}
}