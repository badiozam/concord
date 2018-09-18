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
package com.i4one.base.web.controller.admin.targeting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.i4one.base.core.Base;
import com.i4one.base.model.targeting.GenericTarget;
import com.i4one.base.model.targeting.Target;

/**
 * @author Hamid Badiozamani
 */
public class WebModelTarget implements Target
{
	private Target delegate;

	public WebModelTarget(Target delegate)
	{
		this.delegate = delegate;
	}

	@Override
	public boolean matches(String key)
	{
		return delegate.matches(key);
	}

	@JsonIgnore
	@Override
	public boolean isDefault()
	{
		return delegate.isDefault();
	}

	@JsonIgnore
	@Override
	public String getKey()
	{
		return delegate.getKey();
	}

	@JsonIgnore
	@Override
	public String getName()
	{
		return delegate.getName();
	}

	@JsonIgnore
	@Override
	public String getTitle()
	{
		return delegate.getTitle();
	}

	@JsonIgnore
	@Override
	public void setTitle(String title)
	{
		delegate.setTitle(title);
	}

	public String getLabel()
	{
		return getTitle();
	}

	public String getValue()
	{
		return getKey();
	}

	public void setValue(String key)
	{
		delegate = new GenericTarget(key);
	}

	@Override
	public String toString()
	{
		try
		{
			return Base.getInstance().getJacksonObjectMapper().writeValueAsString(this);
		}
		catch (JsonProcessingException ex)
		{
			return ex.getLocalizedMessage();
		}
	}
}
