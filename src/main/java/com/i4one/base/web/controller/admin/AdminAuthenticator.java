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
package com.i4one.base.web.controller.admin;

import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminManager;
import com.i4one.base.model.manager.AuthenticationManager;
import com.i4one.base.web.RequestState;
import com.i4one.base.web.controller.BaseAuthenticator;
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class AdminAuthenticator extends BaseAuthenticator<Admin> implements Authenticator<Admin>
{
	private RequestState requestState;
	private AdminManager adminManager;

	@Override
	public AuthenticationManager<Admin> getAuthManager()
	{
		return getAdminManager();
	}

	public AdminManager getAdminManager()
	{
		return adminManager;
	}

	@Autowired
	public void setAdminManager(AdminManager readOnlyAdminManager)
	{
		this.adminManager = readOnlyAdminManager;
	}

	@Override
	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}
}
