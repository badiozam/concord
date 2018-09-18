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

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class SimpleModelManager extends BaseLoggable implements ModelManager
{
	private List<ModelInterceptor> interceptors;
	private MessageManager messageManager;

	public void SimpleModelManager()
	{
	}

	public void init()
	{
		getLogger().debug("Initializing SimpleModelManager");
	}

	@Override
	public Model initRequestModel(Model model)
	{
		model.setMessageManager(getMessageManager());

		for (ModelInterceptor interceptor : interceptors )
		{
			model.putAll(interceptor.initRequestModel(model));
		}

		return model;
	}

	@Override
	public Model initResponseModel(Model model)
	{
		for (ModelInterceptor interceptor : interceptors )
		{
			model.putAll(interceptor.initResponseModel(model));
		}

		return model;
	}

	@Override
	public void setModelInterceptors(List<ModelInterceptor> modelInterceptors)
	{
		if ( interceptors != null )
		{
			interceptors.clear();
		}
		else
		{
			interceptors = new ArrayList<>();
		}

		if ( modelInterceptors != null )
		{
			getLogger().debug("Adding interceptors: " + modelInterceptors);
			interceptors.addAll(modelInterceptors);
		}
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

}
