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

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.BaseModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.base.web.interceptor.model.UserMenuModelInterceptor;
import com.i4one.rewards.model.raffle.RaffleManager;
import com.i4one.rewards.model.raffle.RaffleSettings;
import com.i4one.rewards.model.shopping.ShoppingManager;
import com.i4one.rewards.model.shopping.ShoppingSettings;
import com.i4one.rewards.model.shopping.category.ShoppingCategory;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class RewardsUserMenuModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	private Map<String, Object> rewardsMenu;

	private ShoppingManager shoppingManager;
	private RaffleManager raffleManager;

	private RewardsCategoriesModelInterceptor rewardsCategoriesModelInterceptor;

	private static final int MAX_CATEGORY_MENU_SIZE = 2;

	@Override
	public void init()
	{
		super.init();

		rewardsMenu = new LinkedHashMap<>();
		//rewardsMenu.put("msg.rewards.user.shoppings.index.title", "/rewards/user/shoppings/index.html");
		//rewardsMenu.put("msg.rewards.user.raffles.index.title", "/rewards/user/raffles/index.html");
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		HttpServletRequest request = model.getRequest();
		if ( isUserRequest(request))
		{
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> menus = (List<Map<String, Object>>) model.get(UserMenuModelInterceptor.MENUS);
			Map<String,Object> menu = menus.get(0);
	
			Map<String, Object> computedMenu = new LinkedHashMap<>();
			computedMenu.putAll(rewardsMenu);

			SingleClient client = model.getSingleClient();
			String lang = model.getLanguage();

			ShoppingSettings shoppingSettings = getShoppingManager().getSettings(client);
			if ( shoppingSettings.isEnabled() )
			{
				Set<ShoppingCategory> shoppingCategories = getRewardsCategoriesModelInterceptor().getValidShoppingCategories(model);
				if (shoppingCategories.isEmpty() || shoppingCategories.size() > MAX_CATEGORY_MENU_SIZE)
				{
					computedMenu.put("msg.rewards.user.shoppings.index.title", "/rewards/user/shoppings/index.html");
				}
				else
				{
					for (ShoppingCategory shoppingCategory : shoppingCategories)
					{
						computedMenu.put(shoppingCategory.getTitle().get(lang), "/rewards/user/shoppings/index.html?categoryid=" + shoppingCategory.getSer());
					}
				}
			}

			RaffleSettings raffleSettings = getRaffleManager().getSettings(client);
			if ( raffleSettings.isEnabled() )
			{
				computedMenu.put("msg.rewards.user.raffles.index.title", "/rewards/user/raffles/index.html");
			}

			if ( !computedMenu.isEmpty() )
			{
				menu.put("msg.rewards.user.menu", computedMenu);
			}
		}

		// We already manipulated the menu from the model and don't need
		// to add any more items
		//
		return Collections.emptyMap();
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

	public RaffleManager getRaffleManager()
	{
		return raffleManager;
	}

	@Autowired
	public void setRaffleManager(RaffleManager raffleManager)
	{
		this.raffleManager = raffleManager;
	}

	public RewardsCategoriesModelInterceptor getRewardsCategoriesModelInterceptor()
	{
		return rewardsCategoriesModelInterceptor;
	}

	@Autowired
	public void setRewardsCategoriesModelInterceptor(RewardsCategoriesModelInterceptor rewardsCategoriesModelInterceptor)
	{
		this.rewardsCategoriesModelInterceptor = rewardsCategoriesModelInterceptor;
	}

}
