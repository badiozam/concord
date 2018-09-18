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
package com.i4one.base.web.interceptor.model;

import com.i4one.base.model.category.Category;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseCategoriesModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	protected <T extends Category> Set<T> filterOutEmptyCategories(Collection<T> categories, Function<T, Set> getItems)
	{
		Set<T> retVal = new LinkedHashSet<>(categories.size());
		// See which ones have at least one item in them
		//
		categories.stream().forEach((T currCategory) ->
		{
			Set categoryContents = getItems.apply(currCategory);
			if (!categoryContents.isEmpty())
			{
				retVal.add(currCategory);
			}
			else
			{
				getLogger().debug(currCategory + " is empty, skipping");
			}
		});
		return retVal;
	}
}
