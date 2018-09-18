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
package com.i4one.base.model.targeting;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.web.RequestState;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseTargetListResolver extends BaseLoggable implements TargetListResolver
{
	private RequestState requestState;
	private MessageManager messageManager;
	private TargetResolver targetResolver;
	private Target empty;

	@PostConstruct
	public void init()
	{
		empty = emptyInstance();
		getTargetResolver().registerResolver(this);
	}

	protected abstract Target emptyInstance();

	@Override
	public String getName()
	{
		return empty.getName();
	}

	@Override
	public Target resolveKey(String key)
	{
		if ( key.startsWith(getName()) )
		{
			return resolveKeyInternal(key);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Instantiate a new target from the given key.
	 * 
	 * @param key The key to instantiate off of
	 * 
	 * @return The newly resolved target.
	 */
	protected abstract Target resolveKeyInternal(String key);

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}

	public TargetResolver getTargetResolver()
	{
		return targetResolver;
	}

	@Autowired
	public void setTargetResolver(TargetResolver targetResolver)
	{
		this.targetResolver = targetResolver;
	}

}
