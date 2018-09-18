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

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseQuantifiedActivityType;
import com.i4one.base.model.QuantifiedActivityType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.manager.GenericFeature;
import com.i4one.rewards.model.PrizeType;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class PrizeWinning extends BaseQuantifiedActivityType<PrizeWinningRecord, PrizeRecord, Prize> implements QuantifiedActivityType<PrizeWinningRecord, Prize>,PrizeType<PrizeWinningRecord>
{
	static final long serialVersionUID = 42L;

	private transient Set<PrizeFulfillment> prizeFulfillments;

	public PrizeWinning()
	{
		super(new PrizeWinningRecord());
	}

	protected PrizeWinning(PrizeWinningRecord delegate)
	{
		super(delegate);
	}

	@Override
	public void init()
	{
		super.init();

		if ( prizeFulfillments == null )
		{
			prizeFulfillments = new LinkedHashSet<>();
		}
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getItemid() + "-" + getDelegate().getUserid() + "-" + getOwner().uniqueKey() + "-" + getTimeStampSeconds();
	}

	public GenericFeature getOwner()
	{
		GenericFeature retVal = new GenericFeature(getDelegate().getFeatureid(), getDelegate().getFeature());

		return retVal;
	}

	public void setOwner(PrizeType<?> owner)
	{
		getDelegate().setFeature(owner.getFeatureName());
		getDelegate().setFeatureid(owner.getSer());

		setPrize(owner.getPrize());
	}

	@Override
	public Prize getPrize()
	{
		return getActionItem();
	}

	public Prize getPrize(boolean doLoad)
	{
		return getActionItem(doLoad);
	}

	@Override
	public void setPrize(Prize prize)
	{
		setActionItem(prize);
	}

	public boolean isFulfilled()
	{
		int totalFulfilled = 0;
		for ( PrizeFulfillment currFulfillment : getPrizeFulfillments() )
		{
			totalFulfilled += currFulfillment.getQuantity();
		}

		return totalFulfilled == getQuantity();
	}

	@Override
	protected Prize newActionItem()
	{
		return new Prize();
	}

	public Set<PrizeFulfillment> getPrizeFulfillments()
	{
		return Collections.unmodifiableSet(prizeFulfillments);
	}

	public void setPrizeFulfillments(Collection<PrizeFulfillment> fulfillments)
	{
		this.prizeFulfillments.clear();
		this.prizeFulfillments.addAll(fulfillments);
	}

	@Override
	protected void copyFromInternal(RecordTypeDelegator<PrizeWinningRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyFromInternal(right);
		if ( right instanceof PrizeWinning )
		{
			PrizeWinning rightPrizeWinning = (PrizeWinning)right;

			prizeFulfillments.clear();
			for ( PrizeFulfillment fulfillment : rightPrizeWinning.getPrizeFulfillments() )
			{
				PrizeFulfillment newAnswer = new PrizeFulfillment();
				newAnswer.copyFrom(fulfillment);
				newAnswer.setSer(0);

				prizeFulfillments.add(newAnswer);
			}
		}
	}

	@Override
	protected void copyOverInternal(RecordTypeDelegator<PrizeWinningRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyOverInternal(right);

		if ( right instanceof PrizeWinning )
		{
			PrizeWinning rightPrizeWinning = (PrizeWinning)right;

			// If the right prize winning's fulfillments aren't empty, then regardless
			// of our fulfillments we'll want to copy the right's
			//
			if ( !rightPrizeWinning.getPrizeFulfillments().isEmpty() )
			{
				copyFromInternal(right);
			}
		}
	}

	@Override
	public String toString()
	{
		return super.toString() + " w/ fulfillments = " + Utils.toCSV(getPrizeFulfillments());
	}

}