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
package com.i4one.base.web.controller;

import com.i4one.base.model.Authenticable;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.manager.AuthenticationManager;
import com.i4one.base.web.RequestState;
import com.i4one.base.web.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This superclass consolidates some of the login and log out implementation logic
 *
 * @author Hamid Badiozamani
 * @param <T>
 */
public abstract class BaseAuthenticator<T extends Authenticable> extends BaseLoggable implements Authenticator<T>
{
	@Override
	public T login(Model model, T login, HttpServletResponse response)
	{
		// Attempt to log the user in
		//
		T user = getAuthManager().authenticate(login, model.getSingleClient());
		if ( !user.exists() )
		{
			throw new Errors(new ErrorMessage("msg." + getAuthManager().getInterfaceName() + ".authenticate.failed", "Login failed for $login.username", new Object[] { "login", login}));
		}

		// By persisting the login object, we've logged the user in
		//
		Persistence.getPersistence(getRequestState().getRequest(), response).put(user, 86400);

		return user;
	}

	@Override
	public void relogin(T user, HttpServletRequest request, HttpServletResponse response)
	{
		// Replace the currently logged in user with the given user
		//
		Persistence.getPersistence(request, response).put(user, 86400);
	}

	@Override
	public void logout(T unauthItem, HttpServletResponse response)
	{
		Persistence.getPersistence(getRequestState().getRequest(), response).remove(unauthItem.getClass());
	}

	public abstract AuthenticationManager<T> getAuthManager();

	public abstract RequestState getRequestState();
}
