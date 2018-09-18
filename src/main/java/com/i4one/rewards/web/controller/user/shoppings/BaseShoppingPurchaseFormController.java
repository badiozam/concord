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

import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseCategorizableListingController;
import com.i4one.rewards.model.prize.PrizeManager;
import com.i4one.rewards.model.shopping.Shopping;
import com.i4one.rewards.model.shopping.ShoppingManager;
import com.i4one.rewards.model.shopping.ShoppingPurchase;
import com.i4one.rewards.model.shopping.ShoppingPurchaseManager;
import com.i4one.rewards.model.shopping.category.ShoppingCategory;
import com.i4one.rewards.model.shopping.category.ShoppingCategoryManager;
import com.i4one.rewards.web.interceptor.RewardsCategoriesModelInterceptor;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseShoppingPurchaseFormController extends BaseCategorizableListingController<Shopping, ShoppingCategory>
{
	private ShoppingManager shoppingManager;
	private ShoppingCategoryManager shoppingCategoryManager;
	private PrizeManager prizeManager;
	private ShoppingPurchaseManager shoppingPurchaseManager;

	@Override
	public boolean isAuthRequired()
	{
		return false;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelShoppingPurchase )
		{
			WebModelShoppingPurchase shoppingPurchase = (WebModelShoppingPurchase)modelAttribute;
		}

		return model;
	}

	@Override
	public Model initResponse(Model model, HttpServletResponse response, Object modelAttribute)
	{
		Model retVal = super.initResponse(model, response, modelAttribute);

		if ( modelAttribute instanceof ShoppingPurchase )
		{
			ShoppingPurchase shoppingPurchase = (ShoppingPurchase)modelAttribute;

			// XXX: This needs to be more efficient
			//
			shoppingPurchase.setShopping(getShoppingManager().getById(shoppingPurchase.getShopping().getSer()));

			setTitle(model, shoppingPurchase.getShopping().getTitle());
		}

		return retVal;
	}

	@Override
	protected Set<ShoppingCategory> loadCategories(Model model)
	{
		return (Set<ShoppingCategory>) model.get(RewardsCategoriesModelInterceptor.SHOPPING_CATEGORIES);
	}

	public ShoppingPurchaseManager getShoppingPurchaseManager()
	{
		return shoppingPurchaseManager;
	}

	@Autowired
	public void setShoppingPurchaseManager(ShoppingPurchaseManager shoppingPurchaseManager)
	{
		this.shoppingPurchaseManager = shoppingPurchaseManager;
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

	public PrizeManager getPrizeManager()
	{
		return prizeManager;
	}

	@Autowired
	public void setPrizeManager(PrizeManager prizeManager)
	{
		this.prizeManager = prizeManager;
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
	public ShoppingCategoryManager getCategoryManager()
	{
		return getShoppingCategoryManager();
	}
}
