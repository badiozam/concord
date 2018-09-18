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

import com.i4one.base.core.Base;
import com.i4one.base.core.Utils;
import com.i4one.base.model.PermissionDeniedException;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.manager.terminable.TerminableClientType;
import com.i4one.base.model.manager.triggerable.TriggerableClientType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.balanceexpense.BalanceExpenseManager;
import com.i4one.base.model.balancetrigger.BalanceTriggerManager;
import com.i4one.base.model.instantwin.InstantWinManager;
import com.i4one.base.model.manager.expendable.ExpendableClientType;
import com.i4one.base.model.manager.instantwinnable.InstantWinnableClientType;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.BaseViewController;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import com.i4one.base.web.persistence.Persistence;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseAdminViewController extends BaseViewController implements AdminViewController
{
	private BalanceExpenseManager balanceExpenseManager;
	private BalanceTriggerManager balanceTriggerManager;
	private InstantWinManager instantWinManager;

	public static final String ADMIN_REDIRTO_KEY = "adminRedirto";
	public static final String ATTACHMENT_KEY = "attachment";


	@Override
	protected String getRedirKey()
	{
		return ADMIN_REDIRTO_KEY;
	}

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	/**
	 * Returns the administrator from the given model
	 * 
	 * @param model The model (preferably initialized thru initRequest)
	 * 
	 * @return  The administrator currently logged in
	 */
	public Admin getAdmin(Model model)
	{
		return (Admin) model.get(UserAdminModelInterceptor.ADMIN);
	}

	/**
	 * Adds a mapping of all ISO languages and their display names in
	 * the current model's locale. The model will have a key named
	 * "languages" added which is a map of (abbreviation) =&gt; (description).
	 * 
	 * For example, if model.get("lanuage") returns "en" then after this call
	 * model.get("languages") will contain the following map:
	 * <ul>
	 * 	<li>en =&gt; en - English</li>
	 * 	<li>es =&gt; es - Spanish</li>
	 * 	<li>etc.</li>
	 * </ul>
	 * 
	 * If model.get("lanuage") returns "es" then after this call
	 * model.get("languages") will contain the following map:
	 * <ul>
	 * 	<li>en =&gt; en - ingles</li>
	 * 	<li>es =&gt; es - español</li>
	 * 	<li>etc.</li>
	 * </ul>
	 * 
	 * @param model The model to populate
	 */
	protected void addLanguageMap(Model model)
	{
		Locale requestLocale = new Locale(model.getLanguage());
		model.put("languages", Arrays.asList(Locale.getISOLanguages())
			.stream()
			.collect(Collectors.toMap(
				(lang)-> { return lang; },
				(lang) ->
				{
					Locale locale = new Locale(lang);
					return lang + " - " + locale.getDisplayLanguage(requestLocale);
				})));
	}

	/**
	 * Initializes the request by performing one of several tasks:
	 * 
	 * <ul>
	 * 	<li>Performs the super-class' functionality</li>
	 * 	<li>If the attribute is a SingleClientType, sets the client to the request client</li>
	 * 	<li>If the attribute is a Triggerable, and the request type method is "GET",
	 * 		loads its balance triggers.</li>
	 * 	<li>If the attribute is a Terminable, sets the ParseLocale to that of the requests</li>
	 * </ul>
	 *
	 * @param request The incoming HTTP request
	 * @param modelAttribute The model attribute used in the form (if any)
	 *
	 * @return The initialized model
	 */
	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		boolean isRequestGet = request.getMethod().equals("GET");

		// Default the client to this one if it's not set already
		// (works only for single client types)
		//
		if ( modelAttribute instanceof SingleClientType<?> )
		{
			SingleClientType<?> clientModelAttribute = (SingleClientType<?>)modelAttribute;
			//if ( clientModelAttribute.getClient().getSer() <= 0 )
			if ( !clientModelAttribute.getClient().exists() )
			{
				clientModelAttribute.setClient(model.getSingleClient());
			}
		}

		if ( modelAttribute instanceof TriggerableClientType<?, ?> )
		{
			TriggerableClientType<?, ?> triggerable = (TriggerableClientType<?, ?>)modelAttribute;

			// If the triggers aren't already set, retrieve them from the database
			// and set them for the form to display. Note that there may be more
			// triggers associated with the item but these are specific to its
			// serial number (as opposed to ones that match manager methods)
			//
			if ( triggerable.getBalanceTriggers().isEmpty() && isRequestGet )
			{
				// Set all of the triggers associated with the model object
				//
				triggerable.setBalanceTriggers(getBalanceTriggerManager().getAllTriggersByFeature(triggerable, SimplePaginationFilter.NONE));
			}
		}

		if ( modelAttribute instanceof InstantWinnableClientType<?, ?> )
		{
			InstantWinnableClientType<?, ?> instantWinnable = (InstantWinnableClientType<?, ?>)modelAttribute;

			// If the triggers aren't already set, retrieve them from the database
			// and set them for the form to display. Note that there may be more
			// triggers associated with the item but these are specific to its
			// serial number (as opposed to ones that match manager methods)
			//
			if ( instantWinnable.getInstantWins().isEmpty() && isRequestGet )
			{
				// Set all of the triggers associated with the model object
				//
				instantWinnable.setInstantWins(getInstantWinManager().getAllInstantWinsByFeature(instantWinnable, SimplePaginationFilter.NONE));
			}
		}

		if ( modelAttribute instanceof TerminableClientType<?> )
		{
			TerminableClientType<?> terminableClientType = (TerminableClientType<?>)modelAttribute;

			// Set the date/time parsing locale to that of the incoming request
			//
			terminableClientType.setParseLocale(model.getRequest().getLocale());

			if ( !terminableClientType.exists() && isRequestGet )
			{
				Calendar cal = model.getRequest().getCalendar();

				terminableClientType.setStartTimeSeconds(Utils.getStartOfDay(cal));
				terminableClientType.setEndTimeSeconds(Utils.getEndOfDay(cal));
			}
		}

		if ( modelAttribute instanceof ExpendableClientType<?,?> )
		{
			ExpendableClientType<?,?> expendableClientType = (ExpendableClientType<?,?>)modelAttribute;

			if ( expendableClientType.getBalanceExpenses().isEmpty() && isRequestGet )
			{
				expendableClientType.setBalanceExpenses(getBalanceExpenseManager().getAllExpensesByFeature(expendableClientType));
			}
		}

		String targetable = (String)Persistence.getObject(request, ATTACHMENT_KEY);
		if ( !Utils.isEmpty(targetable) )
		{
			try
			{
				WebModelAttachment attachment = Base.getInstance().getJacksonObjectMapper().readValue(targetable, GenericWebModelAttachment.class);
				model.put(ATTACHMENT_KEY, attachment);
			}
			catch (IOException ex)
			{
				getLogger().warn("Couldn't deserialize web model attachment '" + targetable + "'", ex);
			}
		}

		return model;
	}

	@ExceptionHandler(PermissionDeniedException.class)
	public ModelAndView handlePermissionDenied(HttpServletRequest request, PermissionDeniedException pdne)
	{
		Model model = new Model(request);
		getModelManager().initRequestModel(model);
		getModelManager().initResponseModel(model);

		model.put("error", pdne);
		model.put("headers", Utils.toCSV(model.getRequest().getHeaderNames()));

		ModelAndView retVal = new ModelAndView(":base/admin/permissions", model);

		return retVal;
	}

	public BalanceTriggerManager getBalanceTriggerManager()
	{
		return balanceTriggerManager;
	}

	@Autowired
	public void setBalanceTriggerManager(BalanceTriggerManager balanceTriggerManager)
	{
		this.balanceTriggerManager = balanceTriggerManager;
	}

	public InstantWinManager getInstantWinManager()
	{
		return instantWinManager;
	}

	@Autowired
	public void setInstantWinManager(InstantWinManager instantWinManager)
	{
		this.instantWinManager = instantWinManager;
	}

	public BalanceExpenseManager getBalanceExpenseManager()
	{
		return balanceExpenseManager;
	}

	@Autowired
	public void setBalanceExpenseManager(BalanceExpenseManager balanceExpenseManager)
	{
		this.balanceExpenseManager = balanceExpenseManager;
	}
}
