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

import com.i4one.base.core.SimpleLogger;
import com.i4one.base.core.Utils;
import com.i4one.base.web.Browser;
import com.i4one.base.web.controller.Model;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author Hamid Badiozamani
 */
public class BrowserInterceptor extends HandlerInterceptorAdapter
{
	private final SimpleLogger logger;
	private String incompatibleView;

	public BrowserInterceptor()
	{
		logger = new SimpleLogger(getClass());
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		Browser browser = new Browser(request);

		getLogger().trace("preHandle for {}, requestURI = {}, with referer {}", request.getContextPath(), request.getRequestURI(), request.getHeader("referer"));

		boolean isReferredByIncompatibleView = Utils.forceEmptyStr(request.getHeader("referer")).lastIndexOf(getIncompatibleView()) < 0;
		boolean isHtmlRequest = request.getRequestURI().lastIndexOf(".html") > 0;
		boolean isIncompatibleView = request.getRequestURI().lastIndexOf(getIncompatibleView()) > 0;

		getLogger().trace("isHtmlRequest: {}, isIncompatibleView = {}", isHtmlRequest, isIncompatibleView);
		if ( isHtmlRequest &&
			!isIncompatibleView &&
			browser.isIE() &&
			!browser.isIE(10) )
		{
			// We can bypass getting the model initialized via the model manager since we
			// only need the base URL
			//
			Model model = new Model(request);

			String redirURL = model.getBaseURL() + "/" + getIncompatibleView();
			response.sendRedirect(redirURL);

			return false;
		}

		return super.preHandle(request, response, handler);
	}

	public String getIncompatibleView()
	{
		return incompatibleView;
	}

	public void setIncompatibleView(String incompatibleView)
	{
		this.incompatibleView = incompatibleView;
	}

	public Logger getLogger()
	{
		return logger.getLog();
	}
}
