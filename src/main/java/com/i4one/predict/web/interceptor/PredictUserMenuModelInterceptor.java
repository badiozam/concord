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
import com.i4one.base.web.interceptor.model.BaseModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.base.web.interceptor.model.UserMenuModelInterceptor;
import com.i4one.base.web.interceptor.model.menu.UserMenu;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.term.TermManager;
import com.i4one.predict.model.term.TermSettings;
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
public class PredictUserMenuModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	private Map<String, Object> predictMenu;
	private TermManager termManager;

	@Override
	public void init()
	{
		super.init();

		predictMenu = new LinkedHashMap<>();
		predictMenu.put("msg.predict.user.menu", "/predict/user/events/index.html");
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		HttpServletRequest request = model.getRequest();
		if ( isUserRequest(request) )
		{
			TermSettings settings = getTermManager().getSettings(model.getSingleClient());
			if ( settings.isEnabled() )
			{
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> menus = (List<Map<String, Object>>) model.get(UserMenuModelInterceptor.MENUS);
				Map<String,Object> leftMenu = menus.get(0);
		
				Map<String,Object> promotionMenu = (Map<String, Object>) leftMenu.get("msg.promotion.user.menu");
				if ( promotionMenu != null )
				{
					// We put our prediction menu under promotion as yet another way to win,
					// this may need to become its own setting
					//
					promotionMenu.putAll(predictMenu);

					// Add a separate menu item for the Top 10 list for visibility
					//
					leftMenu.put("msg.predict.user.top10", "/predict/user/top10.html");
				}
				else
				{
					Map<String, Object> computedPredictMenu = new LinkedHashMap<>();
					computedPredictMenu.putAll(predictMenu);

					Set<EventCategory> categories = (Set<EventCategory>) model.get(PredictCategoriesModelInterceptor.EVENT_CATEGORIES);
					if ( categories != null && !categories.isEmpty() )
					{
						Map<String, Object> categoryMenu = new LinkedHashMap<>();
						categoryMenu.put("msg.predict.user.top10", "/predict/user/top10.html");
	
						for ( EventCategory category : categories )
						{
							categoryMenu.put(category.getTitle().get(model.getLanguage()), "/predict/user/events/index.html?categoryid=" + category.getSer());
						}
		
						computedPredictMenu.put("msg.predict.user.menu", categoryMenu);
					}

					leftMenu.put("msg.predict.user.menu", computedPredictMenu);
				}

				// Find the account listing items and the bid history
				//
				for ( Map<String, Object> menu : menus )
				{
					UserMenu accountMenu = (UserMenu) menu.get("msg.base.user.menu.account.loggedin");
					if ( accountMenu != null )
					{
						Map<String, Object> newAccountMenu = new UserMenu();
						for (Map.Entry<String, Object> item : accountMenu.entrySet() )
						{
							if ( item.getKey().equals("msg.base.user.menu.account.activity"))
							{
								newAccountMenu.put("msg.predict.user.menu.account.index", "/predict/user/account/index.html");
							}
							newAccountMenu.put(item.getKey(), item.getValue());
						}

						menu.replace("msg.base.user.menu.account.loggedin", newAccountMenu);
					}
				}
			}
		}

		// We already manipulated the menu from the model and don't need
		// to add any more items
		//
		return Collections.emptyMap();
	}

	public TermManager getTermManager()
	{
		return termManager;
	}

	@Autowired
	public void setTermManager(TermManager termManager)
	{
		this.termManager = termManager;
	}
}
