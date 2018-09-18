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
package com.i4one.base.model.emailblast.target;

import com.i4one.base.core.BaseLoggable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Base class for in-memory list builders.
 * 
 * @author Hamid Badiozamani
 */
public abstract class BaseListBuilder<T extends Object> extends BaseLoggable implements ListBuilder<T>
{
	Set<ListBuilder<T>> ands;
	Set<ListBuilder<T>> ors;

	public BaseListBuilder()
	{
		ands = new LinkedHashSet<>();
		ors = new LinkedHashSet<>();
	}

	@Override
	public Set<T> build()
	{
		Set<T> items = buildInternal();
		
		// Get our list and remove things that don't belong
		//
		for ( Iterator<T> it = items.iterator(); it.hasNext(); )
		{
			T item = it.next();
			for ( ListBuilder<T> and : ands)
			{
				if ( !and.belongs(item))
				{
					it.remove();
				}
			}
		}

		// Only after our own list has been built can we union with
		// all of the OR lists
		//
		for ( ListBuilder<T> or : ors )
		{
			items.addAll(or.build());
		}

		return items;
	}

	/**
	 * Build the list of items for just this builder ignoring any chains.
	 * 
	 * @return The (potentially empty) set of items.
	 */
	protected abstract Set<T> buildInternal();

	@Override
	public boolean belongs(T item)
	{
		boolean belongsToThis = belongsInternal(item);

		if ( !belongsToThis )
		{
			for ( ListBuilder<T> or : ors )
			{
				belongsToThis |= or.belongs(item);
			}
		}
		else
		{
			for ( ListBuilder<T> and : ands )
			{
				belongsToThis &= and.belongs(item);
			}
		}

		return belongsToThis;
	}

	/**
	 * Tests whether an item belongs to just this list builder or not.
	 * 
	 * @param item The item to test
	 * 
	 * @return True if the item belongs, false otherwise.
	 */
	protected abstract boolean belongsInternal(T item);

	@Override
	public void andWithList(ListBuilder<T> list)
	{
		ands.add(list);
	}

	@Override
	public void orWithList(ListBuilder<T> list)
	{
		ors.add(list);
	}

	@Override
	public String uniqueKey()
	{
		StringBuilder retVal = new StringBuilder();
		retVal.append(uniqueKeyInternal());
		
		retVal.append("AND:");
		for ( ListBuilder<T> and : ands )
		{
			retVal.append(and.uniqueKey());
			retVal.append(",");
		}

		retVal.append("OR:");
		for ( ListBuilder<T> or : ors )
		{
			retVal.append(or.uniqueKey());
			retVal.append(",");
		}

		return retVal.toString();
	}
	
	protected abstract String uniqueKeyInternal();
}
