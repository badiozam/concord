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
package com.i4one.base.web.controller.admin.balancetriggers;

import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balance.BalanceSelectStringifier;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.BalanceTriggerRecord;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableCrudController;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
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
public class TriggerCrudController extends BaseTerminableCrudController<BalanceTriggerRecord, BalanceTrigger>
{
	private BalanceManager balanceManager;

	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.balancetriggers";
	}

	@Override
	protected Manager<BalanceTriggerRecord, BalanceTrigger> getManager()
	{
		return getBalanceTriggerManager();
	}

	@Override
	public Model initRequest(HttpServletRequest request, BalanceTrigger modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);
		SingleClient client = model.getSingleClient();

		// We only display the balances that are at this client level, any other
		// balance associations need to be set elsewhere
		//
		Set<Balance> balances = getBalanceManager().getAllBalances(client, SimplePaginationFilter.NONE);
		model.put("balances", toSelectMapping(balances, new BalanceSelectStringifier(client, getBalanceManager()), model.getLanguage()));

		if ( modelAttribute instanceof WebModelBalanceTrigger )
		{
			WebModelBalanceTrigger trigger = (WebModelBalanceTrigger)modelAttribute;

			trigger.setFeatureList(getBalanceTriggerManager().getAllFeatureTriggers(trigger));
		}

		return model;
	}

	@RequestMapping(value = "**/admin/balancetriggers/update", method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("trigger") WebModelBalanceTrigger trigger,
								BindingResult result,
								@RequestParam(value = "id", required = false) Integer triggerId,
								HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return createUpdateImpl(trigger, triggerId, request, response);
	}

	@RequestMapping(value = "**/admin/balancetriggers/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("trigger") WebModelBalanceTrigger trigger, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(trigger, result, request, response);
	}

	@RequestMapping(value = { "**/admin/balancetriggers/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer triggerId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(triggerId, request, response);
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
}
