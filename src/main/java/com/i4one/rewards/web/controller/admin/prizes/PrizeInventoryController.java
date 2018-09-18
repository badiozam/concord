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
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.prize.PrizeManager;
import com.i4one.rewards.model.prize.PrizeReservationManager;
import java.lang.reflect.InvocationTargetException;
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
public class PrizeInventoryController extends BaseAdminViewController
{
	private PrizeManager prizeManager;
	private PrizeReservationManager prizeReservationManager;

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelInventoryPrize )
		{
			WebModelInventoryPrize prize = (WebModelInventoryPrize)modelAttribute;

			model.put("reservations", getPrizeReservationManager().getReservations(prize));
		}

		return model;
	}

	@RequestMapping(value = { "**/rewards/admin/prizes/inventory" }, method = RequestMethod.GET)
	public Model viewInventory(@ModelAttribute("prize") WebModelInventoryPrize prize,
					@RequestParam(value = "id", required = false) Integer prizeId,
					HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		if ( prizeId != null )
		{
			prize.setSer(prizeId);
			prize.loadedVersion();
		}

		return initResponse(initRequest(request, prize), response, prize);
	}

	@RequestMapping(value = "**/rewards/admin/prizes/inventory", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("prize") WebModelInventoryPrize prize, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Model model = initRequest(request, prize);

		try
		{
			ReturnType<Prize> newPrize = getPrizeManager().incrementTotalInventory(prize, prize.getAmount());
			prize.copyOver(newPrize.getPost());

			success(model, "msg.rewards.admin.prizes.inventory.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.rewards.admin.prizes.inventory.failure", result, errors);
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

	public PrizeReservationManager getPrizeReservationManager()
	{
		return prizeReservationManager;
	}

	@Autowired
	public void setPrizeReservationManager(PrizeReservationManager prizeReservationManager)
	{
		this.prizeReservationManager = prizeReservationManager;
	}

}
