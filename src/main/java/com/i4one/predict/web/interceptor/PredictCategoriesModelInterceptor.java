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

import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.BaseCategoriesModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.category.EventCategoryManager;
import com.i4one.predict.model.event.EventManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Adds the prize categories to the model map
 *
 * @author Hamid Badiozamani
 */
@Service
public class PredictCategoriesModelInterceptor extends BaseCategoriesModelInterceptor implements ModelInterceptor
{
	private EventCategoryManager eventCategoryManager;
	private EventManager eventManager;

	/** The logged in user */
	public static final String EVENT_CATEGORIES = "predictionEventCategories";

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
			retVal.put(EVENT_CATEGORIES, getValidCategories(model));
		}

		return retVal;
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		return Collections.EMPTY_MAP;
	}

	public Set<EventCategory> getValidCategories(Model model)
	{
		// Get all of the categories
		//
		Set<EventCategory> categories = getEventCategoryManager().getAllCategories(new ClientPagination(model.getSingleClient(), SimplePaginationFilter.NONE));

		return filterOutEmptyCategories(categories, (currCategory) ->
			{
				return getEventManager().getLive(new CategoryPagination(currCategory, model.getTimeInSeconds(),
					new ClientPagination(model.getSingleClient(),
						SimplePaginationFilter.SINGLE)));
			});
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
