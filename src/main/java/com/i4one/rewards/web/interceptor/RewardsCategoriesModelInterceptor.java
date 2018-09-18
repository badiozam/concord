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
package com.i4one.rewards.web.interceptor;

import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.BaseCategoriesModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.rewards.model.raffle.Raffle;
import com.i4one.rewards.model.raffle.RaffleManager;
import com.i4one.rewards.model.raffle.category.RaffleCategory;
import com.i4one.rewards.model.raffle.category.RaffleCategoryManager;
import com.i4one.rewards.model.shopping.Shopping;
import com.i4one.rewards.model.shopping.ShoppingManager;
import com.i4one.rewards.model.shopping.category.ShoppingCategory;
import com.i4one.rewards.model.shopping.category.ShoppingCategoryManager;
import com.i4one.rewards.model.shopping.category.ShoppingCategoryRecord;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Adds rewards categories to the model map
 *
 * @author Hamid Badiozamani
 */
@Service
public class RewardsCategoriesModelInterceptor extends BaseCategoriesModelInterceptor implements ModelInterceptor
{
	private RaffleCategoryManager raffleCategoryManager;
	private RaffleManager raffleManager;

	private ShoppingCategoryManager shoppingCategoryManager;
	private ShoppingManager shoppingManager;

	private SimplePaginationFilter featuredPagination;

	public static final String RAFFLE_CATEGORIES = "raffleCategories";
	public static final String SHOPPING_CATEGORIES = "shoppingCategories";

	public static final String FEATURED_RAFFLES = "featuredRaffles";
	public static final String FEATURED_SHOPPINGS = "featuredShoppings";

	@Override
	public void init()
	{
		super.init();

		featuredPagination = new SimplePaginationFilter();
		featuredPagination.setOrderBy("ser DESC");

		// Removed limitation per Reg: 7/22/2017
		//
		//featuredPagination.setPerPage(3);
		featuredPagination.setPerPage(0);
	}

	@Override
	public Map<String, Object> initRequestModel(Model model)
	{
		Map<String, Object> retVal = new HashMap<>();

		HttpServletRequest request = model.getRequest();
		SingleClient client = model.getSingleClient();

		if ( isUserRequest(request) )
		{
			if ( getShoppingManager().getSettings(client).isEnabled() )
			{
				retVal.put(SHOPPING_CATEGORIES, getValidShoppingCategories(model));
				retVal.put(FEATURED_SHOPPINGS, getFeaturedShoppings(model));
			}

			if ( getRaffleManager().getSettings(client).isEnabled() )
			{
				retVal.put(RAFFLE_CATEGORIES, getValidRaffleCategories(model));
				retVal.put(FEATURED_RAFFLES, getFeaturedRaffles(model));
			}
		}

		return retVal;
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		return Collections.EMPTY_MAP;
	}

	public Set<Shopping> getFeaturedShoppings(Model model)
	{
		return getShoppingManager().getLive(new TerminablePagination(model.getTimeInSeconds(),
				new SiteGroupPagination(model.getSiteGroups(), featuredPagination)));
	}

	public Set<Raffle> getFeaturedRaffles(Model model)
	{
		return getRaffleManager().getLive(new TerminablePagination(model.getTimeInSeconds(),
			new SiteGroupPagination(model.getSiteGroups(), featuredPagination)));
	}

	public Set<RaffleCategory> getValidRaffleCategories(Model model)
	{
		// Get all of the categories
		//
		Set<RaffleCategory> categories = getRaffleCategoryManager().getAllCategories(new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));

		return filterOutEmptyCategories(categories, (currCategory) ->
			{
				return getRaffleManager().getLive(new CategoryPagination(currCategory, model.getTimeInSeconds(),
					new SiteGroupPagination(model.getSiteGroups(),
						SimplePaginationFilter.SINGLE)));
			});
	}

	public Set<ShoppingCategory> getValidShoppingCategories(Model model)
	{
		// Get all of the categories for the client
		//
		Set<ShoppingCategory> categories = getShoppingCategoryManager().getAllCategories(new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));

		return filterOutEmptyCategories(categories, (currCategory) ->
			{
				return getShoppingManager().getLive(new CategoryPagination(currCategory, model.getTimeInSeconds(),
					new SiteGroupPagination(model.getSiteGroups(),
						SimplePaginationFilter.SINGLE)));
			});

	}


	public CategoryManager<ShoppingCategoryRecord, ShoppingCategory> getShoppingCategoryManager()
	{
		return shoppingCategoryManager;
	}

	@Autowired
	public void setShoppingCategoryManager(ShoppingCategoryManager shoppingCategoryManager)
	{
		this.shoppingCategoryManager = shoppingCategoryManager;
	}

	public ShoppingManager getShoppingManager()
	{
		return shoppingManager;
	}

	@Autowired
	public void setShoppingManager(ShoppingManager shoppingManager)
	{
		this.shoppingManager = shoppingManager;
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

	public RaffleManager getRaffleManager()
	{
		return raffleManager;
	}

	@Autowired
	public void setRaffleManager(RaffleManager raffleManager)
	{
		this.raffleManager = raffleManager;
	}
}
