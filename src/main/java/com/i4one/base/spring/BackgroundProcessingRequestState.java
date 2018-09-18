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

import com.i4one.base.model.BaseStateful;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.user.User;
import com.i4one.base.web.HttpServletRequestWrapper;
import com.i4one.base.web.ModelManager;
import com.i4one.base.web.RequestState;
import com.i4one.base.web.controller.Model;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Hamid Badiozamani
 */
public class BackgroundProcessingRequestState extends BaseStateful implements RequestState
{
	private HttpServletRequestWrapper request;

	private Admin admin;
	private User user;

	private String language;
	private SingleClient singleClient;
	private Set<SiteGroup> siteGroups;

	private Model model;
	private ModelManager modelManager;

	public BackgroundProcessingRequestState()
	{
		// The background processing administrator should always have
		// a serial number of 1
		//
		admin = new Admin();
		admin.setSer(1);

		user = new User();

		request = new HttpServletRequestWrapper(new MockHttpServletRequest());

		getLogger().debug("Instantiating {} in {}", this, Thread.currentThread());
	}

	@PostConstruct
	@Override
	public void init()
	{
		super.init();

		model = new Model(getRequest());
		getModelManager().initRequestModel(model);
	}

	@Override
	public void reset()
	{
		user = new User();
		request = new HttpServletRequestWrapper(new MockHttpServletRequest());

		getLogger().debug("Resetting {} in {}", this, Thread.currentThread());
		init();
	}

	@Override
	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	@Override
	public SingleClient getSingleClient()
	{
		return singleClient;
	}

	@Override
	public void setSingleClient(SingleClient singleClient)
	{
		this.singleClient = singleClient;
	}

	@Override
	public Set<SiteGroup> getSiteGroups()
	{
		return siteGroups;
	}

	public void setSiteGroups(Set<SiteGroup> siteGroups)
	{
		this.siteGroups = siteGroups;
	}

	@Override
	public User getUser()
	{
		return user;
	}

	@Override
	public void setUser(User user)
	{
		this.user = user;
	}

	@Override
	public Admin getAdmin()
	{
		return admin;
	}

	@Override
	public void setAdmin(Admin admin)
	{
		this.admin = admin;
	}

	@Override
	public HttpServletRequestWrapper getRequest()
	{
		return request;
	}

	public void setRequest(HttpServletRequest request)
	{
		this.request = new HttpServletRequestWrapper(request);
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