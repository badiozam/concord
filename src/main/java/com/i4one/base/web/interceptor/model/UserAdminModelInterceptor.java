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

import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * Adds the user and admin model properties
 *
 * @author Hamid Badiozamani
 */
@Service
public class UserAdminModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	public static final String ISADMINLOGGEDIN = "isAdminLoggedIn";

	@Override
	public void init()
	{
		super.init();

		getLogger().debug("Initializing UserAdminModelInterceptor");
	}

	@Override
	public Map<String, Object> initRequestModel(Model model)
	{
		getLogger().trace("Adding model objects user and admin");

		Map<String, Object> retVal = new HashMap<>();
		HttpServletRequest request = model.getRequest();

		initUser(retVal, model, request);
		initAdmin(retVal, model, request);

		return retVal;
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		return Collections.EMPTY_MAP;
	}

	protected void initUser(Map<String, Object> map, Model model, HttpServletRequest request)
	{
		if ( isUserRequest(request) )
		{
			User user = getUser(request);
			if ( user.exists() )
			{
				getLogger().trace("Adding " + user + "to the model");
				map.put(USER, user);
			}
			else
			{
				// Empty user means unauthorized
				//
				map.put(USER, new User());
			}
		}
		else
		{
			// Empty user means unauthorized
			//
			map.put(USER, new User());
		}
	}

	protected void initAdmin(Map<String, Object> map, Model model, HttpServletRequest request)
	{
		Admin admin = getAdmin(request);
		if ( admin.exists() )
		{
			getLogger().debug(admin + " currently logged in");
		}

		if ( isAdminRequest(request))
		{
			map.put(ADMIN, admin);
		}
		else
		{
			map.put(ISADMINLOGGEDIN, admin.exists());
		}
	}
}