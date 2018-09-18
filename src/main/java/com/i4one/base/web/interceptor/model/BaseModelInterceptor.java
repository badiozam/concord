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
package com.i4one.base.web.interceptor.model;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.persistence.Persistence;
import java.util.Collections;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseModelInterceptor extends BaseLoggable implements ModelInterceptor
{
	/** The logged in user */
	public static final String USER = User.class.getSimpleName();

	/** The logged in Administrator */
	public static final String ADMIN = Admin.class.getSimpleName();

	/** The administrator URL identifying substring */
	public static final String ADMIN_URL = "/admin/";

	/** The front-end URL identifying substring */
	public static final String USER_URL = "/user/";

	@PostConstruct
	public void init()
	{
	}

	public boolean isAdminRequest(HttpServletRequest request)
	{
		if ( request == null )
		{
			return false;
		}
		else
		{
			return request.getRequestURI().contains(ADMIN_URL);
		}
	}

	public boolean isUserRequest(HttpServletRequest request)
	{
		if ( request == null )
		{
			return false;
		}
		else
		{
			return request.getRequestURI().contains(USER_URL);
		}
	}

	public User getUser(Model model)
	{
		//return (User) model.get(UserAdminModelInterceptor.USER);
		return model.getUser();
	}

	@Override
	public Map<String, Object> initRequestModel(Model model)
	{
		return Collections.EMPTY_MAP;
	}

	/**
	 * Get the user that is currently logged in
	 *
	 * @param request The incoming request potentially containing the currently logged in user
	 *
	 * @return The user that is currently logged in, or a non-existent User object
	 */
	public static User getUser(HttpServletRequest request)
	{
		User retVal = new User();

		User user = (User) Persistence.getPersistence(request, null).get(User.class);
		if ( user != null  )
		{
			retVal.setDelegate(user.getDelegate());
		}
		else
		{
			user = (User) request.getAttribute(USER);
			if ( user != null )
			{
				retVal.setDelegate(user.getDelegate());
			}
			else
			{
				user = (User) request.getSession().getAttribute(USER);
				if ( user != null )
				{
					retVal.setDelegate(user.getDelegate());
				}
			}
		}

		return retVal;
	}

	/**
	 * Get the admin that is currently logged in
	 *
	 * @param request The incoming request potentially containing the currently logged in admin
	 *
	 * @return The admin that is currently logged in, or a non-existent Admin object
	 */
	public static Admin getAdmin(HttpServletRequest request)
	{
		Admin retVal = new Admin();
		Admin admin = (Admin) Persistence.getPersistence(request, null).get(Admin.class);

		if ( admin != null )
		{
			retVal.setDelegate(admin.getDelegate());
		}

		return retVal;
	}
}
