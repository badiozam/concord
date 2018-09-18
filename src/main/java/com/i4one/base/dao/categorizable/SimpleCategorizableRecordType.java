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

import com.i4one.base.dao.SimpleClientRecordType;
import com.i4one.base.dao.categorizable.CategorizableRecordType;

/**
 * @author Hamid Badiozamani
 */
public class SimpleCategorizableRecordType<T extends CategorizableRecordType> extends SimpleClientRecordType<T> implements CategorizableRecordType
{
	private Integer categoryid;

	public SimpleCategorizableRecordType(T forward)
	{
		super(forward);

		categoryid = 0;
	}

	@Override
	public Integer getCategoryid()
	{
		return categoryid;
	}

	@Override
	public void setCategoryid(Integer categoryid)
	{
		this.categoryid = categoryid;
	}
}
