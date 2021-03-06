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

import com.i4one.base.web.interceptor.ClientInterceptor;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

/**
 * Resolves the locale of the request by using the ClientInterceptor-set request
 * language attribute.
 * 
 * @author Hamid Badiozamani
 */
public class LocaleResolver extends AbstractLocaleResolver
{
	@Override
	public Locale resolveLocale(HttpServletRequest request)
	{
		// XXX: Consider settting the timezone as well if a user object is found, otherwise defaulting to the client's timezone
		//
		String language = (String) request.getAttribute(ClientInterceptor.ATTR_LANGUAGE);
		return new Locale(language);
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale)
	{
		throw new UnsupportedOperationException("Cannot change locale");
	}
}
