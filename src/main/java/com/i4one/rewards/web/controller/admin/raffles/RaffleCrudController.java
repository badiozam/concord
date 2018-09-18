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
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.model.category.CategorySelectStringifier;
import com.i4one.rewards.model.WinnablePrizeTypeManager;
import com.i4one.rewards.model.prize.PrizeWinning;
import com.i4one.rewards.model.raffle.Raffle;
import com.i4one.rewards.model.raffle.RaffleManager;
import com.i4one.rewards.model.raffle.RaffleRecord;
import com.i4one.rewards.model.raffle.category.RaffleCategory;
import com.i4one.rewards.model.raffle.category.RaffleCategoryManager;
import com.i4one.rewards.web.controller.admin.BaseRewardsCrudController;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class RaffleCrudController extends BaseRewardsCrudController<RaffleRecord, Raffle, RaffleCategory>
{
	private RaffleManager raffleManager;
	private RaffleCategoryManager raffleCategoryManager;

	@Override
	public Model initRequest(HttpServletRequest request, Raffle modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelRaffle )
		{
			WebModelRaffle raffle = (WebModelRaffle)modelAttribute;
			SingleClient client = model.getSingleClient();

			// Same thing for categories
			//
			Set<RaffleCategory> raffleCategories = getRaffleCategoryManager().getAllCategories(new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));
			model.put("categories", toSelectMapping(raffleCategories, new CategorySelectStringifier(client), model.getLanguage()));
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.rewards.admin.raffles";
	}

	@Override
	protected Manager<RaffleRecord, Raffle> getManager()
	{
		return getRaffleManager();
	}

	@RequestMapping(value = { "**/rewards/admin/raffles/winners" }, method = RequestMethod.GET)
	public ModelAndView selectWinners(@ModelAttribute("raffle") WebModelRaffle raffle,
					@RequestParam(value = "id", required = false) Integer raffleId,
					HttpServletRequest request, HttpServletResponse response)
	{
		if ( raffleId != null )
		{
			raffle.setSer(raffleId);
			raffle.loadedVersion();
		}

		Model model = initRequest(request, raffle);

		try
		{
			List<ReturnType<PrizeWinning>> winnings = getRaffleManager().selectWinners(raffle);

			return redirOnSuccess(model, null, null, null, "../prizes/winnings.html?id=" + raffle.getPrize().getSer() + "&feature.featureName=" + raffle.getFeatureName() + "&feature.ser=" + raffle.getSer(), request, response);
		}
		catch (Errors errors)
		{
			getLogger().debug("Failed with {}", errors);

			fail(model, getMessageRoot() + ".winners.failure", null, errors);
			return new ModelAndView().addAllObjects(initResponse(model, response, raffle));
		}
	}

	@RequestMapping(value = { "**/rewards/admin/raffles/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer raffleId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return this.cloneImpl(raffleId, request, response);
	}

	@RequestMapping(value = { "**/rewards/admin/raffles/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("raffle") WebModelRaffle raffle,
					@RequestParam(value = "id", required = false) Integer raffleId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = createUpdateImpl(raffle, raffleId, request, response);

		if ( !raffle.exists() )
		{
			raffle.setIntro(model.buildIMessage("rewards.raffleManager.defaultIntro"));
			raffle.setOutro(model.buildIMessage("rewards.raffleManager.defaultOutro"));
		}

		return model;
	}

	@RequestMapping(value = "**/rewards/admin/raffles/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("raffle") WebModelRaffle raffle, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return this.doUpdateImpl(raffle, result, request, response);
	}

	@RequestMapping(value = "**/rewards/admin/raffles/reserve", method = RequestMethod.GET)
	public Model updateReserve(@ModelAttribute("winnable") WebModelRaffle shopping,
					@RequestParam(value = "id", required = false) Integer shoppingId,
					HttpServletRequest request, HttpServletResponse response) 
	{
		return updateReserveInternal(shopping, shoppingId, request, response);
	}

	@RequestMapping(value = "**/rewards/admin/raffles/reserve", method = RequestMethod.POST)
	public Model doUpdateReserve(@ModelAttribute("winnable") WebModelRaffle shopping, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateReserveInternal(shopping, shopping.getIncrementReserveAmount(), result, request, response);
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
	public WinnablePrizeTypeManager getWinnablePrizeTypeManager()
	{
		return getRaffleManager();
	}
}
