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
package com.i4one.research.web.interceptor;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.BaseModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.base.web.interceptor.model.UserMenuModelInterceptor;
import com.i4one.research.model.poll.PollManager;
import com.i4one.research.model.poll.PollSettings;
import com.i4one.research.model.poll.category.PollCategory;
import com.i4one.research.model.survey.SurveyManager;
import com.i4one.research.model.survey.SurveySettings;
import com.i4one.research.model.survey.category.SurveyCategory;
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
public class ResearchUserMenuModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	private Map<String, Object> researchMenu;

	private SurveyManager surveyManager;
	private PollManager pollManager;

	@Override
	public void init()
	{
		super.init();

		researchMenu = new LinkedHashMap<>();
		//researchMenu.put("msg.research.user.surveys.index.title", "/research/user/surveys/index.html");
		//researchMenu.put("msg.research.user.polls.index.title", "/research/user/polls/index.html");
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
			computedMenu.putAll(researchMenu);

			SingleClient client = model.getSingleClient();

			// We go under the promotion menu if enabled, otherwise we remain standalone
			//
			Map<String,Object> promotionMenu = (Map<String, Object>) menu.get("msg.promotion.user.menu");

			SurveySettings surveySettings = getSurveyManager().getSettings(client);
			if ( surveySettings.isEnabled() )
			{
				Set<SurveyCategory> surveyCategories = (Set<SurveyCategory>) model.get(SurveyCategoriesModelInterceptor.SURVEY_CATEGORIES);
				if ( surveyCategories != null && !surveyCategories.isEmpty() && promotionMenu == null )
				{
					if ( surveyCategories.size() > 1 )
					{
						Map<String, Object> surveyCategoryMenu = new LinkedHashMap<>();
						for ( SurveyCategory category : surveyCategories )
						{
							surveyCategoryMenu.put(category.getTitle().get(model.getLanguage()), "/research/user/surveys/survey.html?categoryid=" + category.getSer());
						}
	
						// Replace the survey menu entry with the subcategories
						//
						computedMenu.put("msg.research.user.surveys.index.title", surveyCategoryMenu);
					}
					else
					{
						SurveyCategory category = surveyCategories.iterator().next();
						computedMenu.put("msg.research.user.surveys.index.title", "/research/user/surveys/survey.html?categoryid=" + category.getSer());
					}
				}
				else
				{
					computedMenu.put("msg.research.user.surveys.index.title", "/research/user/surveys/index.html");
				}
			}

			PollSettings pollSettings = getPollManager().getSettings(client);

			if ( pollSettings.isEnabled() )
			{
				Set<PollCategory> pollCategories = (Set<PollCategory>) model.get(PollCategoriesModelInterceptor.POLL_CATEGORIES);
				if ( pollCategories != null && !pollCategories.isEmpty() && promotionMenu == null )
				{
					if ( pollCategories.size() > 1 )
					{
						Map<String, Object> pollCategoryMenu = new LinkedHashMap<>();
						for ( PollCategory category : pollCategories )
						{
							pollCategoryMenu.put(category.getTitle().get(model.getLanguage()), "/research/user/polls/poll.html?categoryid=" + category.getSer());
						}
		
						// Replace the poll menu entry with the subcategories
						//
						computedMenu.put("msg.research.user.polls.index.title", pollCategoryMenu);
					}
					else
					{
						PollCategory category = pollCategories.iterator().next();
						computedMenu.put("msg.research.user.polls.index.title", "/research/user/polls/poll.html?categoryid=" + category.getSer());
					}
				}
				else
				{
					computedMenu.put("msg.research.user.polls.index.title", "/research/user/polls/index.html");
				}
			}

			if ( promotionMenu != null )
			{
				promotionMenu.putAll(computedMenu);
			}
			else
			{
				if ( !computedMenu.isEmpty())
				{
					menu.put("msg.research.user.menu", computedMenu);
				}
			}
		}

		// We already manipulated the menu from the model and don't need
		// to add any more items to the model itself
		//
		return Collections.emptyMap();
	}

	public SurveyManager getSurveyManager()
	{
		return surveyManager;
	}

	@Autowired
	public void setSurveyManager(SurveyManager surveyManager)
	{
		this.surveyManager = surveyManager;
	}

	public PollManager getPollManager()
	{
		return pollManager;
	}

	@Autowired
	public void setPollManager(PollManager pollManager)
	{
		this.pollManager = pollManager;
	}

}
