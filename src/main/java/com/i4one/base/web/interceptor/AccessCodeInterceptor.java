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
package com.i4one.base.web.interceptor;

import com.i4one.base.core.Utils;
import com.i4one.base.model.accesscode.AccessCode;
import com.i4one.base.model.accesscode.AccessCodeManager;
import com.i4one.base.model.accesscode.AccessCodeResponseManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import com.i4one.base.web.URLTokenizer;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import java.text.MessageFormat;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author Hamid Badiozamani
 */
public class AccessCodeInterceptor extends HandlerInterceptorAdapter
{
	private final Log logger;

	private AccessCodeManager accessCodeManager;
	private AccessCodeResponseManager accessCodeResponseManager;

	private static final String ACCESS_CODE_ENTRY = "base/user/accesscodes/index.html?id={0}";

	public AccessCodeInterceptor()
	{
		logger = LogFactory.getLog(getClass());
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		String requestURI = request.getRequestURI();
		User currUser = UserAdminModelInterceptor.getUser(request);

		if ( currUser.exists() )
		{
			// Pages that don't require a user to be logged in are not
			// protected by access codes.
			//
			// XXX: Set the URLTokenizer once per request instead of
			// tokenizing over and over
			//
			URLTokenizer urlTokenizer = new URLTokenizer(request);
			SingleClient client = ClientInterceptor.getSingleClient(request);

			Set<AccessCode> liveCodes = getAccessCodeManager().getLive(new TerminablePagination(Utils.currentTimeSeconds(), new ClientPagination(client, SimplePaginationFilter.DESC)));
			for ( AccessCode code : liveCodes )
			{
				getLogger().debug("Access code " + code.getTitle() + " is live, looking up pages");

				for ( String page : code.getPages() )
				{
					getLogger().debug("Access code " + code.getTitle() + " protects page " + page);
					if ( requestURI.contains(page) )
					{
						// This page requires an access code, check to see if the user
						// has already entered and if not, redirect to the access code
						// entry page.
						//
						if ( !getAccessCodeResponseManager().getActivity(code, currUser).exists() )
						{
							// User hasn't entered this code, redirect to the access code entry page
							//
							String redirURL = urlTokenizer.getBaseURL() + "/" + MessageFormat.format(ACCESS_CODE_ENTRY, code.getSer());
							response.sendRedirect(redirURL);
							return false;
						}
					}
					else
					{
						getLogger().debug("URI " + requestURI + " does not contain " + page);
					}
				}
			}
		}
		else
		{
			getLogger().debug("User not logged in, access code skipped.");
		}

		return super.preHandle(request, response, handler);
	}

	public Log getLogger()
	{
		return logger;
	}

	public AccessCodeManager getAccessCodeManager()
	{
		return accessCodeManager;
	}

	@Autowired
	public void setAccessCodeManager(AccessCodeManager accessCodeManager)
	{
		this.accessCodeManager = accessCodeManager;
	}

	public AccessCodeResponseManager getAccessCodeResponseManager()
	{
		return accessCodeResponseManager;
	}

	@Autowired
	public void setAccessCodeResponseManager(AccessCodeResponseManager accessCodeResponseManager)
	{
		this.accessCodeResponseManager = accessCodeResponseManager;
	}

}
