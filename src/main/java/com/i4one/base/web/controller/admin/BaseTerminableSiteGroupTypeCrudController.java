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
package com.i4one.base.web.controller.admin;

import com.i4one.base.dao.terminable.TerminableSiteGroupRecordType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balance.BalanceSelectStringifier;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.TerminableSiteGroupType;
import com.i4one.base.web.controller.Model;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseTerminableSiteGroupTypeCrudController<U extends TerminableSiteGroupRecordType, T extends TerminableSiteGroupType<U>> extends BaseSiteGroupTypeCrudController<U, T>
{
	private BalanceManager balanceManager;

	@Override
	public Model initRequest(HttpServletRequest request, T modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		SingleClient client = model.getSingleClient();

		// We only display the balances that are at this client level, any other
		// balance associations need to be set elsewhere
		//
		Set<Balance> balances = getBalanceManager().getAllBalances(client, SimplePaginationFilter.NONE);
		model.put("balances", toSelectMapping(balances, new BalanceSelectStringifier(client, getBalanceManager()), model.getLanguage()));

		return model;
	}


	@Override
	public Model initResponse(Model model, HttpServletResponse response, T modelAttribute)
	{
		Model retVal = super.initResponse(model, response, modelAttribute);

		if ( modelAttribute != null )
		{
			modelAttribute.setParseLocale(model.getRequest().getLocale());
		}

		return retVal;
	}

	@Override
	protected String getRemoveRedirURL(Model model, T item)
	{
		return "index.html?display=" + (item.isPast(model.getTimeInSeconds()) ? "past" : item.isLive(model.getTimeInSeconds()) ? "live" : "future");
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
