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
package com.i4one.base.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A map that creates and injects elements if they do not exist in the map.
 * 
 * @author Hamid Badiozamani
 */
public class AutoPopulatingMap<K extends Object, V extends Object> extends BaseLoggable implements Map<K,V>
{
	private final Function<K, V> instantiator;
	private final Map<K, V> impl;

	/**
	 * Constructs an AutoPopulatingMap with the given function to use when
	 * instantiating non-existent elements. Uses an empty HashMap as the
	 * underlying implementation.
	 * 
	 * @param instantiator The method to call to instantiate a non-existent element
	 */
	public AutoPopulatingMap(Function<K, V> instantiator)
	{
		this.impl = new HashMap<>();
		this.instantiator = instantiator;
	}

	/**
	 * Constructs an AutoPopulatingMap with the given function to use when
	 * instantiating non-existent elements for the given map.
	 * 
	 * @param instantiator The method to call to instantiate a non-existent element
	 * @param map The underlying map
	 */
	public AutoPopulatingMap(Function<K, V> instantiator, Map<K, V> map)
	{
		this.impl = map;
		this.instantiator = instantiator;
	}

	@Override
	public int size()
	{
		return impl.size();
	}

	@Override
	public boolean isEmpty()
	{
		return impl.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return impl.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return impl.containsValue(value);
	}

	@Override
	public V get(Object key)
	{
		V retVal = impl.get(key);
		if ( retVal == null )
		{
			K castKey = (K)key;
			retVal = instantiator.apply(castKey);

			impl.put(castKey, retVal);
		}

		return impl.get(key);
	}

	@Override
	public V put(K key, V value)
	{
		return impl.put(key, value);
	}

	@Override
	public V remove(Object key)
	{
		return impl.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		impl.putAll(m);
	}

	@Override
	public void clear()
	{
		impl.clear();
	}

	@Override
	public Set<K> keySet()
	{
		return impl.keySet();
	}

	@Override
	public Collection<V> values()
	{
		return impl.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet()
	{
		return impl.entrySet();
	}
}
