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

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.BaseModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.base.web.interceptor.model.UserMenuModelInterceptor;
import com.i4one.promotion.model.code.CodeManager;
import com.i4one.promotion.model.code.CodeSettings;
import com.i4one.promotion.model.event.EventManager;
import com.i4one.promotion.model.event.EventSettings;
import com.i4one.promotion.model.trivia.TriviaManager;
import com.i4one.promotion.model.trivia.TriviaSettings;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PromotionUserMenuModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	private Map<String, Object> promotionMenu;
	private CodeManager codeManager;
	private TriviaManager triviaManager;
	private EventManager eventManager;

	@Override
	public void init()
	{
		super.init();

		promotionMenu = new LinkedHashMap<>();
		//promotionMenu.put("msg.promotion.user.codes.index.title", "/promotion/user/codes/index.html");
		//promotionMenu.put("msg.promotion.user.trivias.index.title", "/promotion/user/trivias/index.html");
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		HttpServletRequest request = model.getRequest();
		if ( isUserRequest(request) )
		{
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> menus = (List<Map<String, Object>>) model.get(UserMenuModelInterceptor.MENUS);
			Map<String,Object> menu = menus.get(0);

			Map<String, Object> computedMenu = new LinkedHashMap<>();
			computedMenu.putAll(promotionMenu);

			SingleClient client = model.getSingleClient();

			// Codes
			//
			CodeSettings codeSettings = getCodeManager().getSettings(client);
			if ( codeSettings.isEnabled() )
			{
				computedMenu.put("msg.promotion.user.codes.index.title", "/promotion/user/codes/index.html");
			}
	
			// Trivia
			//
			TriviaSettings triviaSettings = getTriviaManager().getSettings(client);
			if ( triviaSettings.isEnabled() )
			{
				computedMenu.put("msg.promotion.user.trivias.index.title", "/promotion/user/trivias/index.html");
			}
	
			// Trivia
			//
			EventSettings eventSettings = getEventManager().getSettings(client);
			if ( eventSettings.isEnabled() )
			{
				computedMenu.put("msg.promotion.user.events.index.title", "/promotion/user/events/index.html");
			}
	
			// Friend referral moved under Earn Points if it exists
			//
			{
				// We'll move friend referral under the earn points section later
				//
				Object friendRefMenuObj = menu.remove("msg.base.user.friendref.index.title");
	
				if ( friendRefMenuObj != null )
				{
					computedMenu.put("msg.base.user.friendref.index.title", friendRefMenuObj);
				}
				else
				{
					// Doesn't exist for us to remove
					//computedMenu.remove("msg.base.user.friendref.index.title");
				}
			}

			menu.put("msg.promotion.user.menu", computedMenu);
		}

		// We already manipulated the menu from the model and don't need
		// to add any more items
		//
		return Collections.emptyMap();
	}

	public CodeManager getCodeManager()
	{
		return codeManager;
	}

	@Autowired
	public void setCodeManager(CodeManager codeManager)
	{
		this.codeManager = codeManager;
	}

	public TriviaManager getTriviaManager()
	{
		return triviaManager;
	}

	@Autowired
	public void setTriviaManager(TriviaManager triviaManager)
	{
		this.triviaManager = triviaManager;
	}

	public EventManager getEventManager()
	{
		return eventManager;
	}

	@Autowired
	public void setEventManager(EventManager eventManager)
	{
		this.eventManager = eventManager;
	}

}
