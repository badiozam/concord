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
package com.i4one.base.web.controller;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.SortableType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.velocity.tools.generic.EscapeTool;

/**
 * @author Hamid Badiozamani
 */
public abstract class TerminableSortableListingPagination<T extends RecordTypeDelegator<?> & SortableType<?>> extends TerminableListingPagination<T>
{
	private final List<T> ordered;
	private final List<T> unordered;

	public TerminableSortableListingPagination()
	{
		super();
		
		ordered = new ArrayList();
		unordered = new ArrayList();
	}

	public List<T> getOrdered()
	{
		return ordered;

	}

	public void setOrdered(List<T> ordered)
	{
		this.ordered.clear();
		this.ordered.addAll(ordered);
	}

	public List<T> getUnordered()
	{
		return unordered;
	}

	public void setUnordered(List<T> unordered)
	{
		this.unordered.clear();
		this.unordered.addAll(unordered);
	}

	public Map<Integer, String> getOrderedMap(String lang)
	{
		LinkedHashMap<Integer, String> retVal = new LinkedHashMap<>();
		getItems().stream()
			.filter((item) -> ( item.getOrderWeight() != 0 ))
			.forEach((item) ->
			{
				EscapeTool esc = new EscapeTool();
				retVal.put(item.getSer(), esc.html(item.getTitle().get(lang)));
			});

		return retVal;
	}

	public Map<Integer, String> getUnorderedMap(String lang)
	{
		LinkedHashMap<Integer, String> retVal = new LinkedHashMap<>();
		getItems().stream()
			.filter((item) -> ( item.getOrderWeight() == 0 ))
			.forEach((item) ->
			{
				EscapeTool esc = new EscapeTool();
				retVal.put(item.getSer(), esc.html(item.getTitle().get(lang)));
			});

		return retVal;
	}

	@Override
	public void setItems(Collection<T> items)
	{
		super.setItems(items);

		ordered.clear();
		unordered.clear();

		for (T item : items )
		{
			if ( item.getOrderWeight() == 0 )
			{
			}
			else
			{
			}
		}
	}
}
