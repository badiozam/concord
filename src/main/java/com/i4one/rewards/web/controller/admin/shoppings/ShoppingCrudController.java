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
package com.i4one.rewards.web.controller.admin.shoppings;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.model.category.CategorySelectStringifier;
import com.i4one.rewards.model.WinnablePrizeTypeManager;
import com.i4one.rewards.model.shopping.Shopping;
import com.i4one.rewards.model.shopping.ShoppingManager;
import com.i4one.rewards.model.shopping.ShoppingRecord;
import com.i4one.rewards.model.shopping.category.ShoppingCategory;
import com.i4one.rewards.model.shopping.category.ShoppingCategoryManager;
import com.i4one.rewards.web.controller.admin.BaseRewardsCrudController;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
public class ShoppingCrudController extends BaseRewardsCrudController<ShoppingRecord, Shopping, ShoppingCategory>
{
	private ShoppingManager shoppingManager;
	private ShoppingCategoryManager shoppingCategoryManager;

	@Override
	public Model initRequest(HttpServletRequest request, Shopping modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelShopping )
		{
			WebModelShopping shopping = (WebModelShopping)modelAttribute;
			SingleClient client = model.getSingleClient();

			// Same thing for categories
			//
			Set<ShoppingCategory> shoppingCategories = getShoppingCategoryManager().getAllCategories(new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));
			model.put("categories", toSelectMapping(shoppingCategories, new CategorySelectStringifier(client), model.getLanguage()));
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.rewards.shoppings.admin";
	}

	@Override
	protected Manager<ShoppingRecord, Shopping> getManager()
	{
		return getShoppingManager();
	}

	@RequestMapping(value = { "**/rewards/admin/shoppings/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer shoppingId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(shoppingId, request, response);
	}

	@RequestMapping(value = { "**/rewards/admin/shoppings/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("shopping") WebModelShopping shopping,
					@RequestParam(value = "id", required = false) Integer shoppingId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = createUpdateImpl(shopping, shoppingId, request, response);

		if ( !shopping.exists() )
		{
			shopping.setIntro(model.buildIMessage("rewards.shoppingManager.defaultIntro"));
			shopping.setOutro(model.buildIMessage("rewards.shoppingManager.defaultOutro"));
		}

		return model;
	}

	@RequestMapping(value = "**/rewards/admin/shoppings/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("shopping") WebModelShopping shopping, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(shopping, result, request, response);
	}

	@RequestMapping(value = "**/rewards/admin/shoppings/reserve", method = RequestMethod.GET)
	public Model updateReserve(@ModelAttribute("winnable") WebModelShopping shopping,
					@RequestParam(value = "id", required = false) Integer shoppingId,
					HttpServletRequest request, HttpServletResponse response) 
	{
		return updateReserveInternal(shopping, shoppingId, request, response);
	}

	@RequestMapping(value = "**/rewards/admin/shoppings/reserve", method = RequestMethod.POST)
	public Model doUpdateReserve(@ModelAttribute("winnable") WebModelShopping shopping, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateReserveInternal(shopping, shopping.getIncrementReserveAmount(), result, request, response);
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

	public ShoppingCategoryManager getShoppingCategoryManager()
	{
		return shoppingCategoryManager;
	}

	@Autowired
	public void setShoppingCategoryManager(ShoppingCategoryManager shoppingCategoryManager)
	{
		this.shoppingCategoryManager = shoppingCategoryManager;
	}

	@Override
	public WinnablePrizeTypeManager getWinnablePrizeTypeManager()
	{
		return getShoppingManager();
	}
}
