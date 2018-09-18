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
package com.i4one.base.model.category;

import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;

/**
 * @author Hamid Badiozamani
 */
public class CategoryPagination extends TerminablePagination implements PaginationFilter
{
	static final long serialVersionUID = 42L;

	private int categoryId;

	public CategoryPagination(Category<?> category, int currentTime, PaginationFilter chain)
	{
		super(currentTime, chain);

		this.categoryId = (category == null) ? 0 : category.getSer();
		getLogger().trace("Instantiating " + getClass() + " with chain " + chain);
	}

	public int getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId(int categoryId)
	{
		this.categoryId = categoryId;
	}

	@Override
	protected void initQualifiers()
	{
		super.initQualifiers();

		getColumnQualifier().setQualifier("categoryid", categoryId > 0 ? categoryId : null);
	}
	
	@Override
	protected String toStringInternal()
	{
		if ( getLogger().isTraceEnabled() )
		{
			return "categoryId: " + categoryId + ", " + super.toStringInternal();
		}
		else
		{
			return "cats:" + categoryId + "," + super.toStringInternal();
		}
	}

}
