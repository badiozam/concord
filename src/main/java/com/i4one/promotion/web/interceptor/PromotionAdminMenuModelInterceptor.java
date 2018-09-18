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
package com.i4one.promotion.web.interceptor;

import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.AdminMenuModelInterceptor;
import com.i4one.base.web.interceptor.model.BaseAdminMenuModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.base.web.interceptor.model.menu.AdminMenu;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PromotionAdminMenuModelInterceptor extends BaseAdminMenuModelInterceptor implements ModelInterceptor
{
	@Override
	public void init()
	{
		super.init();

		AdminMenu clickThrusMenu = new AdminMenu();
		clickThrusMenu.put("msg.promotion.admin.clickthrus.listing.pageTitle", "/promotion/admin/clickthrus/index.html");
		clickThrusMenu.put("msg.promotion.admin.clickthrus.settings.index.pageTitle", "/promotion/admin/clickthrus/settings/index.html");

		AdminMenu codesMenu = new AdminMenu();
		codesMenu.put("msg.promotion.admin.codes.listing.pageTitle", "/promotion/admin/codes/index.html");
		codesMenu.put("msg.promotion.admin.codes.import.pageTitle", "/promotion/admin/codes/import.html");
		codesMenu.put("msg.promotion.admin.codes.settings.index.pageTitle", "/promotion/admin/codes/settings/index.html");

		AdminMenu triviasMenu = new AdminMenu();
		triviasMenu.put("msg.promotion.admin.trivias.listing.pageTitle", "/promotion/admin/trivias/index.html");
		triviasMenu.put("msg.promotion.admin.trivias.categories.index.pageTitle", "/promotion/admin/trivias/categories/index.html");
		triviasMenu.put("msg.promotion.admin.trivias.settings.index.pageTitle", "/promotion/admin/trivias/settings/index.html");

		AdminMenu eventsMenu = new AdminMenu();
		eventsMenu.put("msg.promotion.admin.events.listing.pageTitle", "/promotion/admin/events/index.html");
		eventsMenu.put("msg.promotion.admin.events.categories.index.pageTitle", "/promotion/admin/events/categories/index.html");
		eventsMenu.put("msg.promotion.admin.events.settings.index.pageTitle", "/promotion/admin/events/settings/index.html");

		AdminMenu socialPhrases = new AdminMenu();
		socialPhrases.put("msg.promotion.admin.socialphrases.listing.pageTitle", "/promotion/admin/socialphrases/index.html");
		socialPhrases.put("msg.promotion.admin.socialphrases.settings.index.pageTitle", "/promotion/admin/socialphrases/settings/index.html");

		getMenu().put("msg.promotion.admin.clickthrus.menuTitle", clickThrusMenu, "promotion.clickthrus");
		getMenu().put("msg.promotion.admin.codes.menuTitle", codesMenu, "promotion.codes");
		getMenu().put("msg.promotion.admin.trivias.menuTitle", triviasMenu, "promotion.trivias");
		getMenu().put("msg.promotion.admin.events.menuTitle", eventsMenu, "promotion.events");
		getMenu().put("msg.promotion.admin.socialphrases.menuTitle", socialPhrases, "promotion.socialphrases");
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		HttpServletRequest request = model.getRequest();
		if ( isAdminRequest(request))
		{
			@SuppressWarnings("unchecked")
			Map<String,Object> menu = (Map<String, Object>) model.get(AdminMenuModelInterceptor.MENU);

			Map<String, Object> promotionMenu = buildMenu(getAdmin(request), model);
			if ( !promotionMenu.isEmpty() )
			{
				menu.put("msg.promotion.admin.menu", promotionMenu);
			}
		}

		// We already manipulated one of the internal model object's states
		//
		return new HashMap<>();
	}
}
