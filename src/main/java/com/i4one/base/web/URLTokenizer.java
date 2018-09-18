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
package com.i4one.base.web;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Utils;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.interceptor.ClientInterceptor;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Hamid Badiozamani
 */
public class URLTokenizer extends BaseLoggable
{
	private final String baseURL;
	private final String fullBaseURL;
	private final String fullRequestURI;
	private final String messagePrefix;

	public URLTokenizer(HttpServletRequest request)
	{
		SingleClient client = (SingleClient)request.getAttribute(ClientInterceptor.ATTR_CLIENT);
		String language = (String)request.getAttribute(ClientInterceptor.ATTR_LANGUAGE);

		if ( client != null && client.exists() && !Utils.isEmpty(language) )
		{
			baseURL = request.getContextPath() + "/" + client.getName() + "/" + language;
		}
		else
		{
			baseURL = request.getContextPath();
		}

		fullBaseURL = (request.isSecure() ? "https" : "http") + "://" + request.getServerName() + ":" + request.getServerPort() + baseURL;
		fullRequestURI = (request.isSecure() ? "https" : "http") + "://" + request.getServerName() + ":" + request.getServerPort() + request.getRequestURI();

		String extractKey = request.getRequestURI();

		// Jump to the point just past the base URL
		//
		int baseURLIndex = extractKey.indexOf(baseURL);
		if ( baseURLIndex >= 0 )
		{
			extractKey = extractKey.substring( baseURLIndex + baseURL.length() );
		}

		messagePrefix = extractKey.replaceAll("\\/", ".").replaceFirst("\\.html$", "").replaceFirst("^\\.+", "");
	}

	public String getBaseURL()
	{
		return baseURL;
	}

	public String getFullBaseURL()
	{
		return fullBaseURL;
	}

	public String getFullRequestURI()
	{
		return fullRequestURI;
	}

	public String getMessagePrefix()
	{
		return messagePrefix;
	}

}
