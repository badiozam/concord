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

import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseSiteGroupTypeCrudController;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.prize.PrizeManager;
import com.i4one.rewards.model.prize.PrizeRecord;
import java.io.IOException;
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
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class PrizeCrudController extends BaseSiteGroupTypeCrudController<PrizeRecord, Prize>
{
	private BalanceManager balanceManager;
	private PrizeManager prizeManager;

	@Override
	public Model initRequest(HttpServletRequest request, Prize modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelPrize )
		{
			WebModelPrize prize = (WebModelPrize)modelAttribute;
			SingleClient client = model.getSingleClient();
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.rewards.admin.prizes";
	}

	@Override
	protected Manager<PrizeRecord, Prize> getManager()
	{
		return getPrizeManager();
	}

	@RequestMapping(value = { "**/rewards/admin/prizes/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("prize") WebModelPrize prize,
					@RequestParam(value = "id", required = false) Integer prizeId,
					HttpServletRequest request, HttpServletResponse response)
	{
		return createUpdateImpl(prize, prizeId, request, response);
	}

	@RequestMapping(value = "**/rewards/admin/prizes/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("prize") WebModelPrize prize, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(prize, result, request, response);
	}

	@RequestMapping(value = { "**/rewards/admin/prizes/remove" }, method = RequestMethod.GET)
	public ModelAndView remove(@RequestParam(value="id") Integer prizeId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return removeImpl(prizeId, request, response);
	}

	@RequestMapping(value = { "**/rewards/admin/prizes/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer prizeId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(prizeId, request, response);
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
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
}
