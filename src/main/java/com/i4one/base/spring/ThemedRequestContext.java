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
package com.i4one.base.spring;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.util.HtmlUtils;

/**
 * A RequestContext that funnels all messages through the theme that is
 * set as an HttpServletRequest attribute.
 *
 * @author Hamid Badiozamani
 */
public class ThemedRequestContext extends RequestContext
{
	private Log logger;

	public ThemedRequestContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, Map<String, Object> model)
	{
		// The superclass constructor calls initContext
		//
		super(request, response, servletContext, model);
	}

	@Override
	protected void initContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, Map<String, Object> model)
	{
		logger = LogFactory.getLog(getClass());

		super.initContext(request, response, servletContext, model);
	}

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, boolean htmlEscape)
	{
		getLogger().debug("Get message (string) returning themed message instead for " + code);

		String msg = this.getThemeMessage(code, args, defaultMessage);
		return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, boolean htmlEscape) throws NoSuchMessageException
	{
		getLogger().debug("Get message (resolvable) returning themed message instead for " + resolvable);

		String msg = this.getThemeMessage(resolvable);
		return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
	}

	public Log getLogger()
	{
		return logger;
	}
}
