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
package com.i4one.base.web.controller.admin.client;

import com.i4one.base.model.Errors;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.ClientOptionManager;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.base.model.client.ClientSettings;
import com.i4one.base.web.interceptor.ClientInterceptor;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class ClientSettingsController extends BaseAdminViewController
{
	private SingleClientManager singleClientManager;
	private ClientOptionManager clientOptionManager;
	private BalanceManager balanceManager;

	@Override
	public Model initRequest(HttpServletRequest request, Object object)
	{
		Model retVal = super.initRequest(request, object);

		if ( object instanceof ClientSettings )
		{
			addLanguageMap(retVal);

			// Map the timezones to their own values and make available to the
			// model for rendering the drop-down menu
			//
			retVal.put("timezones", Arrays.asList(TimeZone.getAvailableIDs())
						.stream()
						.collect(Collectors.toMap((tz) -> { return tz; }, (tz) -> { return tz; })));
		}

		return retVal;
	}

	@ModelAttribute("settings")
	public ClientSettings initSettings(HttpServletRequest request)
	{
		return getSingleClientManager().getSettings(ClientInterceptor.getSingleClient(request));
	}

	@RequestMapping(value = "**/base/admin/client/index", method = RequestMethod.GET)
	public Model viewClientSettings(@ModelAttribute("settings") ClientSettings settings,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		/*
		// We get our own copy since we may want to perform edits which may affect other
		// requests that use the same client reference
		//
		SingleClient client = getSingleClientManager().getClient(model.getSingleClient().getSer());
		settings.setClient(client);

		// Initialize the default balance values
		//
		Balance defBal = getBalanceManager().getDefaultBalance(client);
		settings.setDefBalSingle(defBal.getSingleName());
		settings.setDefBalPlural(defBal.getPluralName());
		*/

		return initResponse(model, response, settings);
	}

	@RequestMapping(value = "**/base/admin/client/index", method = RequestMethod.POST)
	public Model updateClientSettings(@ModelAttribute("settings") ClientSettings settings, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		try
		{
			getSingleClientManager().updateSettings(settings);

			success(model, "msg.base.admin.client.index.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.client.index.failure", result, errors);
		}

		return initResponse(model, response, settings);
	}

	public SingleClientManager getSingleClientManager()
	{
		return singleClientManager;
	}

	@Autowired
	public void setSingleClientManager(SingleClientManager singleClientManager)
	{
		this.singleClientManager = singleClientManager;
	}

	public ClientOptionManager getClientOptionManager()
	{
		return clientOptionManager;
	}

	@Autowired
	public void setClientOptionManager(ClientOptionManager clientOptionManager)
	{
		this.clientOptionManager = clientOptionManager;
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
