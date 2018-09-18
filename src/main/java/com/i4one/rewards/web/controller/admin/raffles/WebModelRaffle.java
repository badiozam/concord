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
package com.i4one.rewards.web.controller.admin.raffles;

import com.i4one.base.model.Errors;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.web.controller.ExpendableWebModel;
import com.i4one.rewards.model.raffle.Raffle;
import com.i4one.rewards.model.raffle.RaffleRecord;
import com.i4one.rewards.web.controller.admin.WebModelWinnablePrizeType;
import java.util.Collection;

/**
 * @author Hamid Badiozamani
 */
public class WebModelRaffle extends Raffle implements WebModelWinnablePrizeType
{
	private final transient ExpendableWebModel<RaffleRecord,Raffle> expendable;

	private transient int incrementReserveAmount;
	private transient String searchByTitle;

	public WebModelRaffle()
	{
		super();

		expendable = new ExpendableWebModel<>(this);
		searchByTitle = "";
	}

	protected WebModelRaffle(RaffleRecord delegate)
	{
		super(delegate);

		expendable = new ExpendableWebModel<>(this);
		searchByTitle = "";
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		retVal = expendable.validateModel(retVal);

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		expendable.setModelOverrides();

		// We have to set the overrides here after the balance expenses have been
		// set to our filtered results
		//
		super.setOverrides();
	}

	@Override
	public void setBalanceExpenses(Collection<BalanceExpense> balanceExpenses)
	{
		super.setBalanceExpenses(balanceExpenses);

		expendable.setExpenses(balanceExpenses);
	}

	public ExpendableWebModel<RaffleRecord, Raffle> getExpendable()
	{
		return expendable;
	}

	@Override
	public String getSearchByTitle()
	{
		return searchByTitle;
	}

	@Override
	public void setSearchByTitle(String searchByTitle)
	{
		this.searchByTitle = searchByTitle;
	}

	@Override
	public int getIncrementReserveAmount()
	{
		return incrementReserveAmount;
	}

	@Override
	public void setIncrementReserveAmount(int incrementReserveAmount)
	{
		this.incrementReserveAmount = incrementReserveAmount;
	}
}