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
package com.i4one.predict.web.interceptor;

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
public class PredictAdminMenuModelInterceptor extends BaseAdminMenuModelInterceptor implements ModelInterceptor
{
	@Override
	public void init()
	{
		super.init();

		AdminMenu predictMenu = new AdminMenu();
		//predictMenu.put("msg.predict.admin.terms.listing.pageTitle", "/predict/admin/terms/index.html");
		getMenu().put("msg.predict.admin.events.listing.pageTitle", "/predict/admin/events/index.html", "predict.events");
		getMenu().put("msg.predict.admin.categories.index.pageTitle", "/predict/admin/categories/index.html", "predict.events");
		getMenu().put("msg.predict.admin.settings.index.pageTitle", "/predict/admin/settings/index.html", "predict.events");
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		HttpServletRequest request = model.getRequest();
		if ( isAdminRequest(request) )
		{
			@SuppressWarnings("unchecked")
			Map<String,Object> menu = (Map<String, Object>) model.get(AdminMenuModelInterceptor.MENU);

			Map<String, Object> predictMenu = buildMenu(getAdmin(request), model);
			if ( !predictMenu.isEmpty() )
			{
				menu.put("msg.predict.admin.menu", predictMenu);
			}
		}

		// We already manipulated one of the internal model object's states
		//
		return Collections.emptyMap();
	}
}
