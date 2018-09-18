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
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.manager.GenericFeature;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 */
public class PrizeReservation extends BaseRecordTypeDelegator<PrizeReservationRecord> implements RecordTypeDelegator<PrizeReservationRecord>
{
	private Admin admin;
	private Prize prize;

	public PrizeReservation()
	{
		super(new PrizeReservationRecord());
	}

	protected PrizeReservation(PrizeReservationRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		if ( admin == null )
		{
			admin = new Admin();
		}
		admin.resetDelegateBySer(getDelegate().getAdminid());

		if ( prize == null )
		{
			prize = new Prize();
		}
		prize.resetDelegateBySer(getDelegate().getPrizeid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setPrize(getPrize());
		setAdmin(getAdmin());
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getPrizeid() + "." + getDelegate().getFeatureid() + "." + getDelegate().getFeature() + "." + getDelegate().getTimestamp();
	}

	public GenericFeature getOwner()
	{
		GenericFeature retVal = new GenericFeature(getDelegate().getFeatureid(), getDelegate().getFeature());

		return retVal;
	}

	public void setOwner(RecordTypeDelegator<?> owner)
	{
		getDelegate().setFeature(owner.getFeatureName());
		getDelegate().setFeatureid(owner.getSer());
	}

	public int getAmount()
	{
		return getDelegate().getAmount();
	}

	public void setAmount(int amount)
	{
		getDelegate().setAmount(amount);
	}

	public Admin getAdmin()
	{
		return getAdmin(true);
	}

	public Admin getAdmin(boolean doLoad)
	{
		if ( doLoad )
		{
			admin.loadedVersion();
		}

		return admin;
	}

	public void setAdmin(Admin admin)
	{
		this.admin = admin;
		getDelegate().setAdminid(admin.getSer());
	}

	public Prize getPrize()
	{
		return getPrize(true);
	}

	public Prize getPrize(boolean doLoad)
	{
		if ( doLoad )
		{
			prize.loadedVersion();
		}

		return prize;
	}

	public void setPrize(Prize prize)
	{
		this.prize = prize;
		getDelegate().setPrizeid(prize.getSer());
	}

	public Date getTimeStamp()
	{
		return Utils.toDate(getTimeStampSeconds());
	}

	public int getTimeStampSeconds()
	{
		return getDelegate().getTimestamp();
	}

	public void setTimeStampSeconds(int timeStamp)
	{
		getDelegate().setTimestamp(timeStamp);
	}
}
