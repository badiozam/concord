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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Implements an Aging Least Recently Used Map. The map removes entries if they expire
 * or if the list reaches a certain capacity. Although this implementation's methods
 * have the same complexity as the super-class the performance will be slightly slower
 * due to timestamp checks and age lookup
 *
 * @author Hamid Badiozamani
 * 
 * @param <K> The key type
 * @param <V> The value type
 */
public class AgingLRUMap<K, V> implements Map<K, V>
{
	private final LRUMap<K, AgingObject<V> > map;
	private long maxAge;

	/**
	 * @param maxSize The maximum size of the map
	 * @param maxAge The maximum age of any given entry in milliseconds
	 */
	public AgingLRUMap(int maxSize, long maxAge)
	{
		map = new LRUMap <> ( maxSize );
		this.maxAge = maxAge;
	}

	/**
	 * Looks up an object by key.
	 *
	 * Complexity: {@code O(log n)}
	 *
	 * @param key The key
	 *
	 * @return The object associated with the key or null if the object has
	 * either expired or is not in the map
	 */
	@Override
	public V get(Object key)
	{
		AgingObject<V> agedObject = map.get(key);
		if ( agedObject != null )
		{
			// Automatically remove an object once it's expired
			//
			V value = agedObject.getObject();
			if ( value == null )
			{
				remove(key);
			}

			return value;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Clears the map.
	 *
	 * Complexity: {@code O(1)}
	 */
	@Override
	public void clear()
	{
		map.clear();
	}

	/**
	 * Adds a value to the map using current time as the time stamp.
	 *
	 * Complexity: {@code O(log n)}
	 *
	 * @param k The key
	 * @param v The value to associate with the key
	 *
	 * @return The old value associated with the key or null if non existed
	 */
	@Override
	public V put(K k, V v)
	{
		AgingObject<V> entry = new AgingObject<>(v, maxAge);
		AgingObject<V> oldEntry = map.put(k, entry);
		if ( oldEntry != null )
		{
			return oldEntry.getObject();
		}
		else
		{
			return null;
		}
	}

	/**
	 * Gets the current size of the map. Does NOT take into
	 * account expired objects
	 *
	 * Complexity: {@code O(1)}
	 *
	 * @return The current size
	 */
	@Override
	public int size()
	{
		return map.size();
	}

	/**
	 * Whether the map is empty or not. Does NOT take into account
	 * expired objects.
	 *
	 * Complexity: {@code O(1)}
	 *
	 * @return True if the map is empty, false otherwise
	 */
	@Override
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Whether the map contains the given key. Note that the object
	 * associated with the key might expire by the time it is
	 * returned so this method does.
	 *
	 * Complexity: {@code O(log n)}
	 *
	 * @param key The key to look up
	 *
	 * @return True if an object associated with that key exists
	 */
	@Override
	public boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}

	/**
	 * Whether the map contains the value.
	 *
	 * Complexity: {@code O(n)}
	 *
	 * @param value The value to look up
	 *
	 * @return True if the value has a key associated with it in the map
	 *
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean containsValue(Object value)
	{
		return map.containsValue(new AgingObject<>((V)value, maxAge));
	}

	/**
	 * Removes an object from the map and returns the old value.
	 *
	 * Complexity: {@code O(log n)}
	 *
	 * @param key The key of the object to remove
	 *
	 * @return The old object or null if the object did not exist
	 */
	@Override
	public V remove(Object key)
	{
		AgingObject<V> retVal = map.remove(key);
		if ( retVal != null )
		{
			return retVal.getObject();
		}
		else
		{
			return null;
		}
	}

	/**
	 * Adds all of the values of the given map to this one.
	 *
	 * Complexity: {@code O(m log n)}
	 *
	 * @param m The map to import
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		m.entrySet().stream().forEach((entry) ->
		{
			put(entry.getKey(), entry.getValue());
		});
	}

	/**
	 * Gets all of the keys in this map checking the age of each before
	 * returning the elements.
	 *
	 * Complexity: {@code O(n)}
	 *
	 * @return The keys
	 */
	@Override
	public Set<K> keySet()
	{
		Set < Entry < K, V >> entries = entrySet();
		HashSet<K> keys = new HashSet<>(entries.size());

		entries.stream().forEach((currEntry) ->
		{
			keys.add(currEntry.getKey());
		});

		return keys;
	}

	/**
	 * Gets all of the values in this map that are still fresh.
	 *
	 * Complexity: {@code O(n)}
	 *
	 * @return The non-expired values in the map
	 */
	@Override
	public Collection<V> values()
	{
		Set < Entry < K, V >> entries = entrySet();
		ArrayList<V> values = new ArrayList<>(entries.size());

		entries.stream().forEach((currEntry) ->
		{
			values.add(currEntry.getValue());
		});

		return values;
	}

	/**
	 * Gets all of the entries in this map that are still fresh.
	 *
	 * Complexity: {@code O(n)}
	 *
	 * @return All of the entries in the map
	 */
	@Override
	public Set < Entry < K, V >> entrySet()
	{
		// Use the same time stamp for all objects for consistency
		//
		long currTime = System.currentTimeMillis();

		Set < Entry < K, V >> retVal = new HashSet <> ( );
		map.entrySet().stream().forEach((currEntry) ->
		{
			V value = currEntry.getValue().getObject(currTime);

			retVal.add(new Pair<>(currEntry.getKey(), value));
		});

		return retVal;
	}

	public long getMaxAge()
	{
		return maxAge;
	}

	public void setMaxAge(long maxAge)
	{
		this.maxAge = maxAge;
	}

	public int getMaxSize()
	{
		return map.getMaxSize();
	}

	public void setMaxSize(int maxSize)
	{
		map.setMaxSize(maxSize);
	}
}