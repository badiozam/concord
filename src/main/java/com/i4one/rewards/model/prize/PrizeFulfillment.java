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

import com.i4one.base.model.BaseQuantifiedActivityType;
import com.i4one.base.model.ForeignKey;
import com.i4one.base.model.QuantifiedActivityType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminRecord;

/**
 * @author Hamid Badiozamani
 */
public class PrizeFulfillment extends BaseQuantifiedActivityType<PrizeFulfillmentRecord, PrizeWinningRecord, PrizeWinning> implements QuantifiedActivityType<PrizeFulfillmentRecord, PrizeWinning>
{
	static final long serialVersionUID = 42L;

	private transient ForeignKey<AdminRecord, Admin> adminFk;

	public PrizeFulfillment()
	{
		super(new PrizeFulfillmentRecord());
	}

	protected PrizeFulfillment(PrizeFulfillmentRecord delegate)
	{
		super(delegate);
	}

	@Override
	public void init()
	{
		super.init();

		adminFk = new ForeignKey<>(this,
			getDelegate()::getAdminid,
			getDelegate()::setAdminid,
			() -> { return new Admin(); });

	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		adminFk.actualize();
	}
	
	public int getStatus()
	{
		return getDelegate().getStatus();
	}

	public void setStatus(int status)
	{
		getDelegate().setStatus(status);
	}

	public String getNotes()
	{
		return getDelegate().getNotes();
	}

	public void setNotes(String notes)
	{
		getDelegate().setNotes(notes);
	}

	public Admin getAdmin()
	{
		return adminFk.get(true);
	}

	public Admin getAdmin(boolean doLoad)
	{
		return adminFk.get(doLoad);
	}

	public void setAdmin(Admin admin)
	{
		adminFk.set(admin);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getAdminid() + "-" + getDelegate().getItemid() + "-" + getDelegate().getNotes().hashCode();
	}

	public PrizeWinning getPrizeWinning()
	{
		return getActionItem();
	}

	public PrizeWinning getPrizeWinning(boolean doLoad)
	{
		return getActionItem(doLoad);
	}

	public void setPrizeWinning(PrizeWinning prizeWinning)
	{
		setActionItem(prizeWinning);
	}

	@Override
	protected PrizeWinning newActionItem()
	{
		return new PrizeWinning();
	}
}