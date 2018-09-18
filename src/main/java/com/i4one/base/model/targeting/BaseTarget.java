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

import java.util.Objects;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseTarget implements Target
{
	private String title;

	public BaseTarget(String title)
	{
		this.title = title;
	}

	@Override
	public String getTitle()
	{
		return title;
	}

	@Override
	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public boolean matches(String key)
	{
		return key.startsWith(getName());
	}

	@Override
	public boolean equals(Object right)
	{
		if ( right.getClass().isAssignableFrom(getClass()))
		{
			return Objects.equals(this.getKey(), ((BaseTarget)right).getKey());
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 59 * hash + Objects.hashCode(getKey());
		return hash;
	}
}
