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
package com.i4one.rewards.model;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ForeignKey;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.model.category.Category;
import com.i4one.base.model.manager.categorizable.BaseCategorizableTerminableSiteGroupType;
import com.i4one.base.model.manager.expendable.ExpendableClientType;
import com.i4one.base.model.manager.expendable.SimpleExpendable;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.prize.PrizeRecord;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseTerminableRewardsClientType<U extends TerminableRewardsClientRecordType, T extends TerminableRewardsClientType<U,V>, V extends Category<?>> extends BaseCategorizableTerminableSiteGroupType<U, V> implements TerminableRewardsClientType<U,V>,ExpendableClientType<U, T>
{
	private transient ForeignKey<PrizeRecord, Prize> prizeFk;
	private final transient SimpleExpendable<U, ?> expendable;

	protected BaseTerminableRewardsClientType(U delegate)
	{
		super(delegate);

		expendable = new SimpleExpendable<>(this);
	}

	@Override
	public void init()
	{
		super.init();

		prizeFk = new ForeignKey<>(this,
			getDelegate()::getPrizeid,
			getDelegate()::setPrizeid,
			() -> { return new Prize(); } );
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if (getPurchaseStartTimeSeconds() > getPurchaseEndTimeSeconds() )
		{
			retVal.addError("purchaseStartTimeSeconds", new ErrorMessage("purchaseStartTimeSeconds", "msg.base.TerminableRewards.timeMismatch", "The purchase start time ($item.purchaseStartTimeSeconds) cannot be after the purchase end time ($item.purchaseEndTimeSeconds).", new Object[]{"item", this}));
		}

		if (getPurchaseStartTimeSeconds() < getStartTimeSeconds() )
		{
			retVal.addError("purchaseStartTimeSeconds", new ErrorMessage("purchaseStartTimeSeconds", "msg.base.TerminableRewards.purchaseStartMismatch", "The purchase start time ($item.purchaseStartTimeSeconds) cannot be before the display start time ($item.startTimeSeconds).", new Object[]{"item", this}));
		}

		if (getPurchaseEndTimeSeconds() > getEndTimeSeconds() )
		{
			retVal.addError("purchaseEndTimeSeconds", new ErrorMessage("purchaseEndTimeSeconds", "msg.base.TerminableRewards.purchaseEndMismatch", "The purchase end time ($item.purchaseEndTimeSeconds) cannot be after the display end time ($item.endTimeSeconds).", new Object[]{"item", this}));
		}

		retVal.merge(expendable.validate());

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		expendable.setOverrides();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		expendable.actualizeRelations();

		prizeFk.actualize();
	}

	@Override
	public boolean isAvailableDuring(int startTimeSeconds, int endTimeSeconds)
	{
		return getPurchaseStartTimeSeconds() <= startTimeSeconds && getPurchaseEndTimeSeconds() >= endTimeSeconds;
	}

	@Override
	public boolean isAvailableAt(int seconds)
	{
		return isAvailableDuring(seconds, seconds);
	}

	@Override
	public boolean isAvailable()
	{
		int currTime = Utils.currentTimeSeconds();

		return isAvailableDuring(currTime, currTime);
	}

	@Override
	public Date getPurchaseStartTime()
	{
		return Utils.toDate(getPurchaseStartTimeSeconds());
	}

	@Override
	public String getPurchaseStartTimeString()
	{
		return getClient().toDateString(getPurchaseStartTimeSeconds(), getParseLocale());
	}

	@Override
	public void setPurchaseStartTimeString(String purchaseStartTimeStr) throws ParseException
	{
		int seconds = getClient().parseToSeconds(purchaseStartTimeStr, getParseLocale());
		setPurchaseStartTimeSeconds(seconds);
	}

	@Override
	public int getPurchaseStartTimeSeconds()
	{
		return getDelegate().getPurchasestarttm();
	}

	@Override
	public void setPurchaseStartTimeSeconds(int starttm)
	{
		getDelegate().setPurchasestarttm(starttm);
	}

	@Override
	public Date getPurchaseEndTime()
	{
		return Utils.toDate(getPurchaseEndTimeSeconds());
	}

	@Override
	public String getPurchaseEndTimeString()
	{
		return getClient().toDateString(getPurchaseEndTimeSeconds(), getParseLocale());
	}

	@Override
	public void setPurchaseEndTimeString(String purchaseEndTimeStr) throws ParseException
	{
		int seconds = getClient().parseToSeconds(purchaseEndTimeStr, getParseLocale());
		setPurchaseEndTimeSeconds(seconds);
	}

	@Override
	public int getPurchaseEndTimeSeconds()
	{
		return getDelegate().getPurchaseendtm();
	}

	@Override
	public void setPurchaseEndTimeSeconds(int endtm)
	{
		getDelegate().setPurchaseendtm(endtm);
	}

	@Override
	public Set<BalanceExpense> getBalanceExpenses()
	{
		return expendable.getBalanceExpenses();
	}

	@Override
	public void setBalanceExpenses(Collection<BalanceExpense> balanceExpenses)
	{
		expendable.setBalanceExpenses(balanceExpenses);
	}

	@Override
	public Prize getPrize()
	{
		return prizeFk.get(true);
	}

	@Override
	public Prize getPrize(boolean doLoad)
	{
		return prizeFk.get(doLoad);
	}

	@Override
	public void setPrize(Prize prize)
	{
		prizeFk.set(prize);
	}

	@Override
	public int getInitialReserve()
	{
		return getDelegate().getInitreserve();
	}

	@Override
	public void setInitialReserve(int initReserve)
	{
		getDelegate().setInitreserve(initReserve);
	}
	
	@Override
	public int getCurrentReserve()
	{
		return getDelegate().getCurrreserve();
	}

	@Override
	public void setCurrentReserve(int currReserve)
	{
		getDelegate().setCurrreserve(currReserve);
	}

	@Override
	protected void copyFromInternal(RecordTypeDelegator<U> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyFromInternal(right);

		if ( right instanceof TerminableRewardsClientType )
		{
			TerminableRewardsClientType rightTerminableRewards = (TerminableRewardsClientType)right;

			setPrize(rightTerminableRewards.getPrize());
		}
	}
}