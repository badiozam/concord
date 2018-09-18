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

import com.i4one.base.model.RecordTypeDelegator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamid Badiozamani
 */
public class TypeRegistrar extends BaseLoggable
{
	private transient final Map<String, RecordTypeDelegator<?>> types;

	public TypeRegistrar(Collection<Object> types)
	{
		this.types = new HashMap<>();

		types.stream()
			.filter( (item) -> { return item instanceof RecordTypeDelegator; } )
			.map( (item) -> { return (RecordTypeDelegator<?>)item; }) 
			.forEach((currType) ->  register(currType));
	}

	public void init()
	{
	}

	public final void register(RecordTypeDelegator<?> item)
	{
		register(item.getModelName(), item);
	}

	public final void register(String type, RecordTypeDelegator<?> item)
	{
		getLogger().debug("Registering " + type + " => " +  item);
		types.put(type, item);
	}

	public RecordTypeDelegator<?> get(String type)
	{
		RecordTypeDelegator<?> item = types.get(type);

		if ( item != null )
		{
			try
			{
				return item.getClass().newInstance();
			}
			catch (InstantiationException | IllegalAccessException ex)
			{
				getLogger().debug("Could not instantiate " + type, ex);
				return null;
			}
		}
		else
		{
			getLogger().trace("Could not find item for " + type);
			return null;
		}
	}
}
