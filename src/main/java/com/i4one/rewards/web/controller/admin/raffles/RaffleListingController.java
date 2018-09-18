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

import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.TerminableListingPagination;
import com.i4one.base.web.controller.admin.BaseTerminableExpendableListingController;
import com.i4one.rewards.model.raffle.Raffle;
import com.i4one.rewards.model.raffle.RaffleEntryManager;
import com.i4one.rewards.model.raffle.RaffleManager;
import com.i4one.rewards.model.raffle.RaffleRecord;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class RaffleListingController extends BaseTerminableExpendableListingController<RaffleRecord, Raffle>
{
	private RaffleManager raffleManager;
	private RaffleEntryManager raffleEntryManager;

	@RequestMapping(value = { "**/rewards/admin/raffles/listing", "**/rewards/admin/raffles/index" })
	public Model viewListing(@ModelAttribute("listing") TerminableListingPagination<Raffle> listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		return viewListingImpl(listing, result, request, response);
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

	public RaffleEntryManager getRaffleEntryManager()
	{
		return raffleEntryManager;
	}

	@Autowired
	public void setRaffleEntryManager(RaffleEntryManager raffleEntryManager)
	{
		this.raffleEntryManager = raffleEntryManager;
	}

	@Override
	protected TerminablePagination getItemPagination(Model model, TerminableListingPagination<Raffle> listing)
	{
		Set<SiteGroup> siteGroups = model.getSiteGroups();
		return new TerminablePagination(model.getTimeInSeconds(),
			new SiteGroupPagination(siteGroups, listing.getPagination()));
	}

	@Override
	protected TerminableManager<RaffleRecord, Raffle> getManager()
	{
		return getRaffleManager();
	}

	@Override
	protected ActivityManager<?, ?, Raffle> getActivityManager()
	{
		return getRaffleEntryManager();
	}
}
