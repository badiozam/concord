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

import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.AdminMenuModelInterceptor;
import com.i4one.base.web.interceptor.model.BaseAdminMenuModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.base.web.interceptor.model.menu.AdminMenu;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class RewardsAdminMenuModelInterceptor extends BaseAdminMenuModelInterceptor implements ModelInterceptor
{
	@Override
	public void init()
	{
		super.init();

		AdminMenu shoppingsMenu = new AdminMenu();
		shoppingsMenu.put("msg.rewards.admin.shoppings.listing.pageTitle", "/rewards/admin/shoppings/index.html");
		shoppingsMenu.put("msg.rewards.admin.shoppings.categories.index.pageTitle", "/rewards/admin/shoppings/categories/index.html");
		shoppingsMenu.put("msg.rewards.admin.shoppings.settings.index.pageTitle", "/rewards/admin/shoppings/settings/index.html");

		AdminMenu rafflesMenu = new AdminMenu();
		rafflesMenu.put("msg.rewards.admin.raffles.listing.pageTitle", "/rewards/admin/raffles/index.html");
		rafflesMenu.put("msg.rewards.admin.raffles.categories.index.pageTitle", "/rewards/admin/raffles/categories/index.html");
		rafflesMenu.put("msg.rewards.admin.raffles.settings.index.pageTitle", "/rewards/admin/raffles/settings/index.html");

		getMenu().put("msg.rewards.admin.prizes.menuTitle", "/rewards/admin/prizes/index.html", "rewards.prizes");
		getMenu().put("msg.rewards.admin.shoppings.menuTitle", shoppingsMenu, "rewards.shoppings");
		getMenu().put("msg.rewards.admin.raffles.menuTitle", rafflesMenu, "rewards.raffles");
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		HttpServletRequest request = model.getRequest();
		if ( isAdminRequest(request))
		{
			@SuppressWarnings("unchecked")
			Map<String,Object> menu = (Map<String, Object>) model.get(AdminMenuModelInterceptor.MENU);

			Map<String, Object> rewardsMenu = buildMenu(getAdmin(request), model);
			if ( !rewardsMenu.isEmpty() )
			{
				menu.put("msg.rewards.admin.menu", rewardsMenu);
			}
		}

		// We already manipulated one of the internal model object's states
		//
		return Collections.emptyMap();
	}
}
