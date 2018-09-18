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
package com.i4one.rewards.web.controller.user.raffles;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.WebModel;
import com.i4one.rewards.model.raffle.RaffleEntry;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class WebModelRaffleEntry extends RaffleEntry implements WebModel
{
	private transient Model model;
	private transient UserBalance currentBalance;
	private transient Set<RaffleEntry> pastEntries;

	@Override
	public void init()
	{
		super.init();

		if ( getQuantity() == 0 )
		{
			setQuantity(1);
		}
	}
	
	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		getLogger().debug("Validating raffle purchase for raffle " + getRaffle());
		if ( !getRaffle().exists() )
		{
			errors.addError(new ErrorMessage("msg.rewards.RaffleResponse.raffle.invalidRaffle", "This raffle item does not exist", new Object[]{"item", this}));
		}
		else if ( !getRaffle().isLive(getModel().getTimeInSeconds()) )
		{
			errors.addError(new ErrorMessage("msg.rewards.RaffleResponse.raffle.notLive", "This raffle item is not currently available", new Object[]{"item", this}));
		}

		// The user has to be set
		//
		if ( !getUser().exists() )
		{
			errors.addError(new ErrorMessage("msg.rewards.rafflePurchaseManager.create.userdne", "You must be logged in", new Object[] { "user", getUser()}, null));
		}

		return errors;
	}

	@Override
	public Model getModel()
	{
		return model;
	}

	@Override
	public void setModel(Model model)
	{
		this.model = model;
	}

	public boolean getHideTimeInfo()
	{
		return (getRaffle().getPurchaseEndTimeSeconds() - getRaffle().getPurchaseStartTimeSeconds() < 59) &&
			(getRaffle().getEndTimeSeconds() - getRaffle().getPurchaseEndTimeSeconds() < 59);
	}

	public UserBalance getCurrentBalance()
	{
		return currentBalance;
	}

	public void setCurrentBalance(UserBalance currentBalance)
	{
		this.currentBalance = currentBalance;
	}

	public Set<RaffleEntry> getPastEntries()
	{
		return pastEntries;
	}

	public void setPastEntries(Set<RaffleEntry> pastEntries)
	{
		this.pastEntries = pastEntries;
	}
}
