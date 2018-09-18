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
import com.i4one.base.model.client.SingleClient;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author Hamid Badiozamani
 */
public class InfiniteRedirInterceptor extends HandlerInterceptorAdapter
{
	private final Log logger;
	private static final String REFERER_URL = "Referer";

	public InfiniteRedirInterceptor()
	{
		logger = LogFactory.getLog(getClass());
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		// Build the request URL with the query string since that's what
		// the referer URL will also have
		//
		StringBuffer requestURL = request.getRequestURL();
		String queryStr = Utils.forceEmptyStr(request.getQueryString());
		if ( !Utils.isEmpty(queryStr) )
		{
			requestURL.append("?").append(Utils.forceEmptyStr(request.getQueryString())).toString();
		}
		String refererURL = Utils.forceEmptyStr(request.getHeader(REFERER_URL));

		if ( !Utils.isEmpty(refererURL) && requestURL.toString().endsWith(refererURL) )
		{
			getLogger().error("Breaking infinite loop from referer '" + refererURL + "' to request URI '" + requestURL + "'");
			SingleClient client = (SingleClient) request.getAttribute(ClientInterceptor.ATTR_CLIENT);
			if ( client != null )
			{
				String lang = (String) request.getAttribute(ClientInterceptor.ATTR_LANGUAGE);
				response.sendRedirect(request.getContextPath() + client.getBaseURL(Utils.forceEmptyStr(lang)) + "base/user/index.html");
			}
			else
			{
				response.sendRedirect("/");
			}
			return false;
		}
		else
		{
			return true;
		}
	}

	public Log getLogger()
	{
		return logger;
	}
}
