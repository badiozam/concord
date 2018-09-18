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

import com.i4one.base.model.BaseHttpRequestStateful;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.ClientInterceptor;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

/**
 * Responsible for storing and retrieving state for a given request. Instantiated
 * on a per-request basis.
 * 
 * @author Hamid Badiozamani
 */
@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class SimpleRequestState extends BaseHttpRequestStateful implements RequestState
{
	private transient ModelManager modelManager;

	private Model model;

	@PostConstruct
	@Override
	public void init()
	{
		super.init();

		model = new Model(super.getRequest());
		getModelManager().initRequestModel(model);
	}

	@Override
	public void reset()
	{
		throw new UnsupportedOperationException("Web requests cannot be reset");
	}

	@Override
	public String getLanguage()
	{
		return ClientInterceptor.getLanguage(getRequest());
	}

	@Override
	public SingleClient getSingleClient()
	{
		return ClientInterceptor.getSingleClient(getRequest());
	}

	@Override
	public void setSingleClient(SingleClient client)
	{
		throw new UnsupportedOperationException("Client must be set through request URI");
	}

	@Override
	public Set<SiteGroup> getSiteGroups()
	{
		return ClientInterceptor.getSiteGroups(getRequest());
	}

	@Override
	public User getUser()
	{
		return model.getUser();
	}

	@Override
	public void setUser(User user)
	{
		model.setUser(user);
	}

	@Override
	public Admin getAdmin()
	{
		return model.getAdmin();
	}

	@Override
	public void setAdmin(Admin admin)
	{
		throw new UnsupportedOperationException("Can't set admin directly");
	}

	@Override
	public HttpServletRequestWrapper getRequest()
	{
		return model.getRequest();
	}

	@Override
	public Model getModel()
	{
		return model;
	}

	public ModelManager getModelManager()
	{
		return modelManager;
	}

	@Autowired
	public void setModelManager(ModelManager modelManager)
	{
		this.modelManager = modelManager;
	}

}