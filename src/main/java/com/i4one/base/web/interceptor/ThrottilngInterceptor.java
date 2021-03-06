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

import com.i4one.base.core.AgingLRUMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author Hamid Badiozamani
 */
public class ThrottilngInterceptor extends HandlerInterceptorAdapter
{
	private final Log logger;

	private final int maxSameIP;

	private final AgingLRUMap<String, Integer> requestCache;

	public ThrottilngInterceptor(int maxSameIP, int withinMillis, int maxSize)
	{
		logger = LogFactory.getLog(getClass());

		requestCache = new AgingLRUMap<>(maxSize, withinMillis);

		this.maxSameIP = maxSameIP;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		String ip = request.getRemoteAddr();

		Integer requestCount = requestCache.get(ip);
		if ( requestCount != null )
		{
			requestCount++;
		}
		else
		{
			requestCount = 1;
		}

		if ( requestCount > maxSameIP )
		{
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return false;
		}
		else
		{
			requestCache.put(ip, requestCount);
			return super.preHandle(request, response, handler);
		}
	}

	public Log getLogger()
	{
		return logger;
	}
}
