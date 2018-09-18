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

import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.web.controller.Model;
import com.i4one.rewards.model.raffle.Raffle;
import com.i4one.rewards.model.raffle.category.RaffleCategory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class RaffleEntryFormDisplayController extends BaseRaffleEntryFormController
{
	@RequestMapping("**/rewards/user/raffles/index")
	public ModelAndView listAllRaffles(@RequestParam(value = "categoryid", defaultValue = "0") int categoryid, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, null);

		RaffleCategory category = viewListingImpl(categoryid, model, request, response);

		// Only display the live raffles
		//
		Set<Raffle> liveRaffles = getRaffleManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
			new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE)));

		if ( liveRaffles.size() == 1 )
		{
			return redirWithDefault(model, null, null, model.getBaseURL() + "/rewards/user/raffles/raffle.html?raffleid=" + liveRaffles.iterator().next().getSer(), null, request, response);
		}
		else
		{
			User user = model.getUser();
	
			// Go through all of the available raffles and load the status of any the 
			// user may have participated in
			//
			//Map<Raffle, Set<RaffleEntry>> raffles = new LinkedHashMap<>();
			Map<Raffle, UserBalance> raffles = new LinkedHashMap<>();
			liveRaffles.forEach((raffle) ->
			{
				if ( user.exists() )
				{
					raffles.put(raffle, getRaffleEntryManager().getRaffleEntryBalance(raffle, user));
	
					// This seems too intensive for displaying all of the raffles in a category,
					// instead displaying the total number of entries the user has
					//
					//raffles.put(raffle, getRaffleEntryManager().getRaffleEntries(raffle, user));
				}
				else
				{
					raffles.put(raffle, new UserBalance());
					//raffles.put(raffle, Collections.EMPTY_SET);
				}
			});
	
			getLogger().debug("We have " + raffles.size() + " raffles in category " + categoryid);
	
			model.put("raffles", raffles);
			addMessageToModel(model, Model.TITLE, "msg.rewards.user.raffles.index.title");
	
			// We have more than one or we have no raffles, in either case we can have the view
			// determine the outcome
			//
			ModelAndView retVal = new ModelAndView();
			retVal.addAllObjects(initResponse(model, response, null));
		
			return retVal;
		}
	}

	@RequestMapping(value = "**/rewards/user/raffles/raffle", method = RequestMethod.GET)
	public Model raffleForm(@ModelAttribute("raffleEntry") WebModelRaffleEntry raffleEntry,
					@RequestParam(value = "raffleid", required = false) Integer raffleId,
					@RequestParam(value = "raffleentryid", required = false) Integer raffleEntryId,
					BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException
	{
		Model model = initRequest(request, raffleEntry);

		if ( raffleEntryId != null )
		{
			raffleEntry.setSer(raffleEntryId);
			raffleEntry.loadedVersion();
		}
		else if ( raffleId != null )
		{
			raffleEntry.getRaffle().resetDelegateBySer(raffleId);
			raffleEntry.setRaffle(raffleEntry.getRaffle());
		}
		else
		{
			response.sendRedirect("index.html");
		}

		return initResponse(model, response, raffleEntry);
	}
}
