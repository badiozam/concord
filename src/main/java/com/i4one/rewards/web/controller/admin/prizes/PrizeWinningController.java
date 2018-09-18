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
package com.i4one.rewards.web.controller.admin.prizes;

import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.rewards.model.prize.PrizeFulfillment;
import com.i4one.rewards.model.prize.PrizeManager;
import com.i4one.rewards.model.prize.PrizeWinningManager;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class PrizeWinningController extends BaseAdminViewController
{
	private PrizeManager prizeManager;
	private PrizeWinningManager prizeWinningManager;

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelWinningsPrize )
		{
			WebModelWinningsPrize prize = (WebModelWinningsPrize)modelAttribute;

			prize.setWinnings(getPrizeWinningManager().getAllPrizeWinnings(prize, SimplePaginationFilter.NONE));
			prize.getFulfillmentTemplate().setAdmin(model.getAdmin());
			prize.getFulfillmentTemplate().setTimeStampSeconds(model.getTimeInSeconds());

			Map<Integer, String> fulfillmentStatuses = new LinkedHashMap<>();
			for (int i = 1; i < 7; i++ )
			{
				fulfillmentStatuses.put(i, model.buildMessage("msg.rewards.PrizeFulfillment.status." + i));
			}

			model.put("validFulfillmentStatuses", fulfillmentStatuses);

		}

		return model;
	}
	
	@RequestMapping(value = { "**/rewards/admin/prizes/winnings" }, method = RequestMethod.GET)
	public Model viewWinnings(@ModelAttribute("prize") WebModelWinningsPrize prize,
					@RequestParam(value = "id", required = false) Integer prizeId,
					HttpServletRequest request,
					HttpServletResponse response) throws Exception
	{
		if ( prizeId != null )
		{
			prize.setSer(prizeId);
			prize.loadedVersion();
		}

		Model model = initRequest(request, prize);

		return initResponse(model, response, prize);
	}

	@RequestMapping(value = "**/rewards/admin/prizes/winnings", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("prize") WebModelWinningsPrize prize, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Model model = initRequest(request, prize);

		try
		{
			int winningsCount = prize.getWinnings().size();

			List<ReturnType<PrizeFulfillment>> fulfillments = getPrizeWinningManager().fulfillPrizeWinnings(prize.getWinnings(), prize.getFulfillmentTemplate());
			prize.setWinnings(getPrizeWinningManager().getAllPrizeWinnings(prize, SimplePaginationFilter.NONE));

			if ( fulfillments.isEmpty() )
			{
				success(model, "msg.rewards.admin.prizes.winnings.failed", fulfillments, SubmitStatus.ModelStatus.FAILED);
			}
			else if ( fulfillments.size() == winningsCount )
			{
				success(model, "msg.rewards.admin.prizes.winnings.success", fulfillments);
			}
			else
			{
				success(model, "msg.rewards.admin.prizes.winnings.partial", fulfillments, SubmitStatus.ModelStatus.PARTIAL);
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.rewards.admin.prizes.winnings.failure", result, errors);
		}

		return initResponse(model, response, prize);
	}

	public PrizeManager getPrizeManager()
	{
		return prizeManager;
	}

	@Autowired
	public void setPrizeManager(PrizeManager prizeManager)
	{
		this.prizeManager = prizeManager;
	}

	public PrizeWinningManager getPrizeWinningManager()
	{
		return prizeWinningManager;
	}

	@Autowired
	public void setPrizeWinningManager(PrizeWinningManager prizeWinningManager)
	{
		this.prizeWinningManager = prizeWinningManager;
	}

}
