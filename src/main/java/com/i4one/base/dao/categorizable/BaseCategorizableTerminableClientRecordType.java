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
package com.i4one.base.dao.categorizable;

import com.i4one.base.dao.terminable.BaseTerminableClientRecordType;

/**
 * A terminable record type that can belong to a category
 * 
 * @author Hamid Badiozamani
 */
public abstract class BaseCategorizableTerminableClientRecordType extends BaseTerminableClientRecordType implements CategorizableTerminableClientRecordType
{
	private Integer categoryid;

	public BaseCategorizableTerminableClientRecordType()
	{
		super();

		categoryid = 0;
	}

	@Override
	public final Integer getCategoryid()
	{
		return categoryid;
	}

	@Override
	public final void setCategoryid(Integer categoryid)
	{
		this.categoryid = categoryid;
	}
}
