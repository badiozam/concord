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
package com.i4one.rewards.web.controller.admin;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.category.Category;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import com.i4one.rewards.model.TerminableRewardsClientRecordType;
import com.i4one.rewards.model.TerminableRewardsClientType;
import com.i4one.rewards.model.WinnablePrizeTypeManager;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.prize.PrizeManager;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseRewardsCrudController<U extends TerminableRewardsClientRecordType, T extends TerminableRewardsClientType<U,V>, V extends Category<?>> extends BaseTerminableSiteGroupTypeCrudController<U, T>
{
	private PrizeManager prizeManager;

	@Override
	public Model initRequest(HttpServletRequest request, T modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		boolean isRequestGet = request.getMethod().equals("GET");

		// Set the default purchase start/end times
		if ( modelAttribute != null && !modelAttribute.exists() && isRequestGet )
		{
			Calendar cal = model.getRequest().getCalendar();

			modelAttribute.setPurchaseStartTimeSeconds(Utils.getStartOfDay(cal));
			modelAttribute.setPurchaseEndTimeSeconds(Utils.getEndOfDay(cal));
		}

		if ( modelAttribute instanceof WebModelWinnablePrizeType)
		{
			WebModelWinnablePrizeType prizeType = (WebModelWinnablePrizeType)modelAttribute;
			String titleSearch = prizeType.getSearchByTitle();

			// Disallow empty searches for now
			//
			if ( !Utils.isEmpty(titleSearch) )
			{
				Set<Prize> possiblePrizes = getPrizeManager().search(titleSearch, new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));
				model.put("prizes", toRadioButtonMapping(possiblePrizes, Prize::getTitle, model.getLanguage()));
			}
		}

		return model;
	}

	protected Model updateReserveInternal(T item,
					Integer id,
					HttpServletRequest request, HttpServletResponse response)
	{
		getLogger().debug("updateReserve(..) called with id = " + id);
		if ( id != null )
		{
			item.setSer(id);
			item.loadedVersion();
		}

		Model model = initRequest(request, item);

		return initResponse(model, response, item);
	}

	protected Model doUpdateReserveInternal(T item, int amount, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, item);

		try
		{
			item.loadedVersion();

			ReturnType<T> updateResult = getWinnablePrizeTypeManager().incrementTotalReserve(item, amount);
			item.copyFrom(updateResult.getPost());

			success(model, getMessageRoot() + ".incrementCurrentReserve.success");
		}
		catch (Errors errors)
		{
			fail(model, getMessageRoot() + ".incrementCurrentReserve.failure", result, errors);
		}

		return initResponse(model, response, item);
	}

	public abstract WinnablePrizeTypeManager getWinnablePrizeTypeManager();

	public PrizeManager getPrizeManager()
	{
		return prizeManager;
	}

	@Autowired
	public void setPrizeManager(PrizeManager prizeManager)
	{
		this.prizeManager = prizeManager;
	}

}
