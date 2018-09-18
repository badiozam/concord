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
package com.i4one.rewards.model.prize;

import com.i4one.base.dao.RecordType;
import com.i4one.base.model.BaseActivityType;
import com.i4one.base.model.RecordTypeDelegator;

/**
 * The base class for all PrizeWinning activity types.
 * 
 * @author Hamid Badiozamani
 * @param <T>
 */
public abstract class BasePrizeWinningActivityType<T extends PrizeWinningRecordType, AR extends RecordType, A extends RecordTypeDelegator<AR>> extends BaseActivityType<T, AR, A> implements PrizeWinningUsageType<T>
{
	private transient PrizeWinning prizeWinning;

	protected BasePrizeWinningActivityType(T delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( prizeWinning == null )
		{
			prizeWinning = new PrizeWinning();
		}
		prizeWinning.resetDelegateBySer(getDelegate().getPrizewinningid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setPrizeWinning(getPrizeWinning());
	}

	@Override
	public int getQuantity()
	{
		return getPrizeWinning().getQuantity();
	}

	@Override
	public void setQuantity(int quantity)
	{
		getPrizeWinning().setQuantity(quantity);
	}

	@Override
	public PrizeWinning getPrizeWinning()
	{
		return prizeWinning;
	}

	@Override
	public void setPrizeWinning(PrizeWinning prizeWinning)
	{
		this.prizeWinning = prizeWinning;
		getDelegate().setPrizewinningid(prizeWinning.getSer());

		this.prizeWinning.setOwner(this);

		// For most use-cases the user and the time stamp are going to be
		// the same for the winning as well as the activity that took place
		//
		this.prizeWinning.setUser(getUser());
		this.prizeWinning.setTimeStampSeconds(getTimeStampSeconds());
	}

	@Override
	public void setPrize(Prize prize)
	{
		throw new UnsupportedOperationException("Can't set prize directly on an activity.");
	}
}
