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
package com.i4one.base.model;

import com.i4one.base.core.BaseLoggable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class that stores and retrieves state information and can be chained to
 * other Stateful objects in order to reflect that chained object's state
 * 
 * @author Hamid Badiozamani
 */
public abstract class BaseStateful extends BaseLoggable implements Stateful
{
	private Map<String, Object> state;

	public BaseStateful()
	{
	}

	public void init()
	{
		state = new HashMap<>();
	}

	@Override
	public Object get(String name)
	{
		getLogger().trace("Getting property " + name + " for " + this);
		return state.get(name);
	}

	@Override
	public void set(String name, Object value)
	{
		getLogger().trace("Setting property " + name + " for " + this);
		state.put(name, value);
	}
}
