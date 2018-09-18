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
package com.i4one.base.web.interceptor.model.menu;

import com.i4one.base.core.BaseLoggable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class UserMenu extends BaseLoggable implements Map<String, Object>
{
	private boolean leftAligned;
	private final Map<String, Object> items;

	public UserMenu()
	{
		items = new LinkedHashMap<>();
		leftAligned = true;
	}

	public UserMenu(UserMenu forward)
	{
		items = new LinkedHashMap<>();
		items.putAll(forward);

		leftAligned = forward.getLeftAligned();

	}

	@Override
	public Object get(Object key)
	{
		return items.get(key);
	}

	@Override
	public Object put(String key, Object value)
	{
		return items.put(key, value); 
	}

	@Override
	public int size()
	{
		return items.size();
	}

	@Override
	public boolean isEmpty()
	{
		return items.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return items.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return items.containsValue(value);
	}

	@Override
	public Object remove(Object key)
	{
		return items.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m)
	{
		items.putAll(m);
	}

	@Override
	public void clear()
	{
		items.clear();
	}

	@Override
	public Set<String> keySet()
	{
		return items.keySet();
	}

	@Override
	public Collection<Object> values()
	{
		return items.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet()
	{
		return items.entrySet();
	}

	public boolean isLeftAligned()
	{
		return leftAligned;
	}

	public boolean getLeftAligned()
	{
		return isLeftAligned();
	}

	public void setLeftAligned(boolean leftAligned)
	{
		this.leftAligned = leftAligned;
	}
}
