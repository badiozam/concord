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

import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseCategorizableListingController;
import com.i4one.rewards.model.prize.PrizeManager;
import com.i4one.rewards.model.raffle.Raffle;
import com.i4one.rewards.model.raffle.RaffleManager;
import com.i4one.rewards.model.raffle.RaffleEntryManager;
import com.i4one.rewards.model.raffle.category.RaffleCategory;
import com.i4one.rewards.model.raffle.category.RaffleCategoryManager;
import com.i4one.rewards.web.interceptor.RewardsCategoriesModelInterceptor;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseRaffleEntryFormController extends BaseCategorizableListingController<Raffle, RaffleCategory>
{
	private RaffleManager raffleManager;
	private PrizeManager prizeManager;
	private RaffleEntryManager raffleEntryManager;
	private RaffleCategoryManager raffleCategoryManager;

	@Override
	public boolean isAuthRequired()
	{
		return false;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelRaffleEntry )
		{
			WebModelRaffleEntry raffleEntry = (WebModelRaffleEntry)modelAttribute;
		}

		return model;
	}

	@Override
	public Model initResponse(Model model, HttpServletResponse response, Object modelObject)
	{
		Model retVal = super.initResponse(model, response, modelObject);

		if ( modelObject instanceof WebModelRaffleEntry )
		{
			WebModelRaffleEntry raffleEntry = (WebModelRaffleEntry)modelObject;

			User user = model.getUser();
			if ( user.exists() )
			{
				raffleEntry.setCurrentBalance(getRaffleEntryManager().getRaffleEntryBalance(raffleEntry.getRaffle(false), user));
				raffleEntry.setPastEntries(getRaffleEntryManager().getRaffleEntries(raffleEntry.getRaffle(false), user, SimplePaginationFilter.NONE));
			}

			// XXX: This needs to be more efficient
			//
			raffleEntry.setRaffle(getRaffleManager().getById(raffleEntry.getRaffle(false).getSer()));
		}

		return retVal;
	}

	@Override
	public Set<RaffleCategory> loadCategories(Model model)
	{
		return (Set<RaffleCategory>) model.get(RewardsCategoriesModelInterceptor.RAFFLE_CATEGORIES);
	}

	public RaffleEntryManager getRaffleEntryManager()
	{
		return raffleEntryManager;
	}

	@Autowired
	public void setRaffleEntryManager(RaffleEntryManager raffleEntryManager)
	{
		this.raffleEntryManager = raffleEntryManager;
	}

	public RaffleManager getRaffleManager()
	{
		return raffleManager;
	}

	@Autowired
	public void setRaffleManager(RaffleManager raffleManager)
	{
		this.raffleManager = raffleManager;
	}

	public PrizeManager getPrizeManager()
	{
		return prizeManager;
	}

	@Autowired
	public void setPrizeManager(PrizeManager prizeManager)
	{
		this.prizeManager = prizeManager;
	}

	public RaffleCategoryManager getRaffleCategoryManager()
	{
		return raffleCategoryManager;
	}

	@Autowired
	public void setRaffleCategoryManager(RaffleCategoryManager raffleCategoryManager)
	{
		this.raffleCategoryManager = raffleCategoryManager;
	}

	@Override
	public RaffleCategoryManager getCategoryManager()
	{
		return getRaffleCategoryManager();
	}
}
