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
package com.i4one.base.web.controller.user.support;

import com.i4one.base.model.ClientType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.User;
import com.i4one.base.web.Browser;
import java.util.Enumeration;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Hamid Badiozamani
 */
public class UserClient implements ClientType
{
	private String sourceIP;
	private String headers;
	private String cookies;
	private Browser browser;

	private SingleClient client;
	private User loggedInUser;
	private String email;
	private String category;
	private String description;

	public UserClient()
	{
		loggedInUser = new User();
	}

	@NotEmpty(message = "msg.base.UserClient.emptyEmail")
	@Email(message = "msg.base.UserClient.invalidEmail")
	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	@NotEmpty(message = "msg.base.UserClient.emptyDescription")
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public SingleClient getClient()
	{
		return client;
	}

	@Override
	public void setClient(SingleClient client)
	{
		this.client = client;
	}

	public User getLoggedInUser()
	{
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser)
	{
		this.loggedInUser = loggedInUser;
	}

	public Browser getBrowser()
	{
		return browser;
	}

	public String getSourceIP()
	{
		return sourceIP;
	}

	public String getHeaders()
	{
		return headers;
	}

	public String getCookies()
	{
		return cookies;
	}

	public void setRequest(HttpServletRequest request)
	{
		this.sourceIP = request.getRemoteAddr();
		this.browser = new Browser(request);

		// Get the headers
		//
		StringBuilder headerPairs = new StringBuilder();

		@SuppressWarnings("unchecked")
		Enumeration<String> names = request.getHeaderNames();

		while (names.hasMoreElements())
		{
			String name = names.nextElement();

			if ( name.equals("cookie") || name.equals("user-agent"))
			{
				// Ignore these as they are captured separately
				//
			}
			else
			{
				@SuppressWarnings("unchecked")
				Enumeration<String> values = request.getHeaders(name);
				if (values != null)
				{
					while (values.hasMoreElements())
					{
						String value = values.nextElement();
						headerPairs.append(name).append(" = ").append(value).append("\n");
					}
				}
			}
		}

		this.headers = headerPairs.toString();

		// Get the cookies
		//
		StringBuilder cookiePairs = new StringBuilder();
		Cookie[] requestCookies = request.getCookies();
		for ( Cookie cookie : requestCookies )
		{
			cookiePairs.append(cookie.getName()).append(" = ").append(cookie.getValue());
			cookiePairs.append(", Domain = ").append(cookie.getDomain()).append(", Path = ").append(cookie.getPath());
			cookiePairs.append(", Max Age = ").append(cookie.getMaxAge());
			cookiePairs.append("\n");
		}

		this.cookies = cookiePairs.toString();
	}
}
