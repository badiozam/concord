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

import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.BaseCategoriesModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.promotion.model.event.EventManager;
import com.i4one.promotion.model.event.category.EventCategory;
import com.i4one.promotion.model.event.category.EventCategoryManager;
import com.i4one.promotion.model.trivia.TriviaManager;
import com.i4one.promotion.model.trivia.category.TriviaCategory;
import com.i4one.promotion.model.trivia.category.TriviaCategoryManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Adds promotion categories to the model map
 *
 * @author Hamid Badiozamani
 */
@Service
public class PromotionCategoriesModelInterceptor extends BaseCategoriesModelInterceptor implements ModelInterceptor
{
	private TriviaCategoryManager triviaCategoryManager;
	private TriviaManager triviaManager;

	private EventCategoryManager eventCategoryManager;
	private EventManager eventManager;

	public static final String TRIVIA_CATEGORIES = "triviaCategories";
	public static final String EVENT_CATEGORIES = "promotionEventCategories";

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public Map<String, Object> initRequestModel(Model model)
	{
		Map<String, Object> retVal = new HashMap<>();

		HttpServletRequest request = model.getRequest();
		if ( isUserRequest(request) )
		{
			retVal.put(TRIVIA_CATEGORIES, getValidTriviaCategories(model));
			retVal.put(EVENT_CATEGORIES, getValidEventCategories(model));
		}

		return retVal;
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		return Collections.EMPTY_MAP;
	}

	public Set<TriviaCategory> getValidTriviaCategories(Model model)
	{
		Set<TriviaCategory> categories = getTriviaCategoryManager().getAllCategories(new ClientPagination(model.getSingleClient(), SimplePaginationFilter.NONE));

		return filterOutEmptyCategories(categories, (currCategory) ->
			{
				return getTriviaManager().getLive(new CategoryPagination(currCategory, model.getTimeInSeconds(),
					new ClientPagination(model.getSingleClient(),
						SimplePaginationFilter.SINGLE)));
			});
	}

	public Set<EventCategory> getValidEventCategories(Model model)
	{
		Set<EventCategory> categories = getEventCategoryManager().getAllCategories(new ClientPagination(model.getSingleClient(), SimplePaginationFilter.NONE));

		return filterOutEmptyCategories(categories, (currCategory) ->
			{
				return getEventManager().getLive(new CategoryPagination(currCategory, model.getTimeInSeconds(),
					new ClientPagination(model.getSingleClient(),
						SimplePaginationFilter.SINGLE)));
			});
	}

	public TriviaCategoryManager getTriviaCategoryManager()
	{
		return triviaCategoryManager;
	}

	@Autowired
	public void setTriviaCategoryManager(TriviaCategoryManager triviaCategoryManager)
	{
		this.triviaCategoryManager = triviaCategoryManager;
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

	public EventCategoryManager getEventCategoryManager()
	{
		return eventCategoryManager;
	}

	@Autowired
	public void setEventCategoryManager(EventCategoryManager eventCategoryManager)
	{
		this.eventCategoryManager = eventCategoryManager;
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
