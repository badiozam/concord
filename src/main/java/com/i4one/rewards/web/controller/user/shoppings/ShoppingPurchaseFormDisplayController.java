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
package com.i4one.rewards.web.controller.user.shoppings;

import com.i4one.base.model.Errors;
import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import com.i4one.rewards.model.shopping.Shopping;
import com.i4one.rewards.model.shopping.ShoppingPurchase;
import com.i4one.rewards.model.shopping.category.ShoppingCategory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
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
public class ShoppingPurchaseFormDisplayController extends BaseShoppingPurchaseFormController
{
	@RequestMapping("**/rewards/user/shoppings/index")
	public ModelAndView listAllShoppings(@RequestParam(value = "categoryid", defaultValue = "0") int categoryid, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, null);

		ShoppingCategory category = viewListingImpl(categoryid, model, request, response);

		// Only display the live shoppings
		//
		Set<Shopping> liveShoppings = getShoppingManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
			new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE)));

		if ( liveShoppings.size() == 1 )
		{
			return redirWithDefault(model, null, null, model.getBaseURL() + "/rewards/user/shoppings/shopping.html?shoppingid=" + liveShoppings.iterator().next().getSer(), null, request, response);
		}
		else
		{
			User user = model.getUser();
	
			// Go through all of the available shoppings and load the status of any the 
			// user may have participated in
			//
			Map<Shopping, Set<ShoppingPurchase>> shoppings = new LinkedHashMap<>();
			liveShoppings.forEach((shopping) ->
			{
				if ( user.exists() )
				{
					// Display all of the purchases for this shoppingPurchase item for this user
					//
					shoppings.put(shopping, getShoppingPurchaseManager().getShoppingPurchases(shopping, user, SimplePaginationFilter.NONE));
				}
				else
				{
					shoppings.put(shopping, Collections.EMPTY_SET);
				}
			});
	
			getLogger().debug("We have " + shoppings.size() + " shoppings in category " + categoryid);
	
			model.put("shoppings", shoppings);
			addMessageToModel(model, Model.TITLE, "msg.rewards.user.shoppings.index.title");
	
			// We have more than one or we have no shoppings, in either case we can have the view
			// determine the outcome
			//
			ModelAndView retVal = new ModelAndView();
			retVal.addAllObjects(initResponse(model, response, null));
	
			return retVal;
		}
	}

	@RequestMapping(value = "**/rewards/user/shoppings/shopping", method = RequestMethod.GET)
	public Model shoppingForm(@ModelAttribute("shoppingPurchase") WebModelShoppingPurchase shoppingPurchase,
					@RequestParam(value = "shoppingid", required = false) Integer shoppingId,
					@RequestParam(value = "shoppingpurchaseid", required = false) Integer shoppingPurchaseId,
					BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException
	{
		Model model = initRequest(request, shoppingPurchase);

		if ( shoppingPurchaseId != null )
		{
			try
			{
				shoppingPurchase.consume(getShoppingPurchaseManager().getById(shoppingPurchaseId));
			}
			catch (Errors errors)
			{
				getLogger().debug("Couldn't load shopping purchase {}", shoppingPurchaseId, errors);
				response.sendRedirect("index.html");
			}
		}
		else if ( shoppingId != null )
		{
			// Make sure someone can't directly view another person's
			// info by setting the serial number of the purchase
			//
			shoppingPurchase.setSer(0);
			shoppingPurchase.getShopping().consume(getShoppingManager().getById(shoppingId));
		}
		else
		{
			response.sendRedirect("index.html");
		}

		return initResponse(model, response, shoppingPurchase);
	}
}
