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
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.client.SiteGroupManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Client and locale interceptor. Sets the client, site groups, language and
 * time zone attributes for a given request based on the incoming request
 * URI.
 * 
 * @author Hamid Badiozamani
 */
public class ClientInterceptor extends HandlerInterceptorAdapter
{
	public static final String ATTR_CLIENT = "com.i4one.base.Client";
	public static final String ATTR_SITEGROUPS = "com.i4one.base.SiteGroup[]";
	public static final String ATTR_LANGUAGE = "com.i4one.base.Client.language";
	public static final String ATTR_TIMEZONE = "com.i4one.base.Client.timeZone";

	private Log logger;
	private SingleClientManager cachedSingleClientManager;
	private SiteGroupManager cachedSiteGroupManager;

	private String defaultPage;
	private String defaultLanguage;
	private String defaultClient;
	private String autoDefaultClient;

	public ClientInterceptor()
	{
		logger = LogFactory.getLog(getClass());
	}

	public void init()
	{
		autoDefaultClient = initAutoDefaultClient();
	}

	public String initAutoDefaultClient()
	{
		List<SingleClient> allClients = getSingleClientManager().getAllClients(SingleClient.getRoot(), SimplePaginationFilter.NONE);
		for ( SingleClient client : allClients )
		{
			if ( !Utils.isEmpty(client.getEmail()) )
			{
				return client.getName();
			}
		}

		// No clients exist
		//
		return SingleClient.getRoot().getName();
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		getLogger().debug("preHandle for " + request.getContextPath() + ", servletPath = " + request.getServletPath() + " with handler " + handler);

		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		int contextPathChomp = requestURI.indexOf(contextPath) + contextPath.length();
		String path = requestURI.substring(contextPathChomp);

		// The path is split in the form of /[client name]/[language]/[resource]
		//
		String[] pathSplit = path.split("/", 4);
		String clientName = pathSplit.length > 2 ? pathSplit[1] : "";
		String language = pathSplit.length > 2 ? pathSplit[2] : "en";

		SingleClient client = getSingleClientManager().getClient(clientName);
		Set<SiteGroup> siteGroups = getCachedSiteGroupManager().getSiteGroups(client);

		// If the client is not found, attempt to look up the first client that shares
		// the domain name
		//
		if ( !client.exists() )
		{
			// Priority goes to the WEB-INFO set variable, and if none exists, then
			// the first client by domain, and finally, the auto default
			//
			String redirClientName = defaultClient;
			if ( Utils.isEmpty(redirClientName) )
			{
				String domain = request.getServerName();
				SingleClient redirClient = getSingleClientManager().getClientByDomain(domain);

				if ( redirClient.exists() )
				{
					redirClientName = redirClient.getName();
				}
				else
				{
					redirClientName = autoDefaultClient;
				}
			}

			String redirURL = request.getContextPath() + "/" + redirClientName + "/" + defaultLanguage + "/" + defaultPage;
			getLogger().debug("Client '" + clientName + "' not found! Redirecting to " + redirURL);

			response.sendRedirect(redirURL);

			return false;
		}
		else
		{
			getLogger().debug("Setting client to " + client);
			getLogger().debug("Setting client groups to " + Utils.toCSV(siteGroups));

			request.setAttribute(ATTR_CLIENT, client);
			request.setAttribute(ATTR_SITEGROUPS, Collections.unmodifiableSet(siteGroups));

			request.setAttribute(ATTR_LANGUAGE, language);
			request.setAttribute(ATTR_TIMEZONE, client.getTimeZone());
			return super.preHandle(request, response, handler);
		}
	}

	public static String getLanguage(HttpServletRequest request)
	{
		String defaultLanguage = getSingleClient(request).getLocale().getLanguage();

		String language = (String) request.getAttribute(ATTR_LANGUAGE);
		if ( language != null )
		{
			return language;
		}
		else
		{
			return defaultLanguage;
		}
	}

	/**
	 * Get the client for a given request
	 *
	 * @param request The incoming request
	 *
	 * @return The client associated with the request
	 */
	public static SingleClient getSingleClient(HttpServletRequest request)
	{
		SingleClient retVal = new SingleClient();

		SingleClient client = (SingleClient) request.getAttribute(ATTR_CLIENT);
		if ( client != null  )
		{
			retVal.setDelegate(client.getDelegate());
		}

		return retVal;
	}

	/**
	 * Get the client groups for a given request
	 *
	 * @param request The incoming request
	 *
	 * @return The client groups associated with the request
	 */
	public static Set<SiteGroup> getSiteGroups(HttpServletRequest request)
	{
		Set<SiteGroup> siteGroups = (Set<SiteGroup>) request.getAttribute(ATTR_SITEGROUPS);
		if ( siteGroups != null  )
		{
			return siteGroups;
		}
		else
		{
			return Collections.emptySet();
		}
	}

	public SingleClientManager getSingleClientManager()
	{
		return cachedSingleClientManager;
	}

	public SingleClientManager getCachedSingleClientManager()
	{
		return cachedSingleClientManager;
	}

	@Autowired
	@Qualifier("base.CachedSingleClientManager")
	public void setCachedSingleClientManager(SingleClientManager cachedSingleClientManager)
	{
		this.cachedSingleClientManager = cachedSingleClientManager;
	}

	public SiteGroupManager getCachedSiteGroupManager()
	{
		return cachedSiteGroupManager;
	}

	@Autowired
	public void setCachedSiteGroupManager(SiteGroupManager cachedSiteGroupManager)
	{
		this.cachedSiteGroupManager = cachedSiteGroupManager;
	}

	public Log getLogger()
	{
		return logger;
	}

	public void setLogger(Log logger)
	{
		this.logger = logger;
	}

	public String getDefaultLanguage()
	{
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage)
	{
		this.defaultLanguage = defaultLanguage;
	}

	public String getDefaultPage()
	{
		return defaultPage;
	}

	public void setDefaultPage(String defaultPage)
	{
		this.defaultPage = defaultPage;
	}

	public String getDefaultClient()
	{
		return defaultClient;
	}

	public void setDefaultClient(String defaultClient)
	{
		this.defaultClient = defaultClient;
	}

}