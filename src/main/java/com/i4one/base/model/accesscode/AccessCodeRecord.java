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
package com.i4one.base.model.accesscode;

import com.i4one.base.dao.terminable.BaseTerminableClientRecordType;
import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class AccessCodeRecord extends BaseTerminableClientRecordType implements TerminableClientRecordType
{
	static final long serialVersionUID = 42L;

	private String code;
	private String pages;

	private IString title;
	private IString descr;
	private IString accessdenied;

	public AccessCodeRecord()
	{
		super();

		code = "";
		pages = "";

		title = new IString();
		descr = new IString();
		accessdenied = new IString();
	}

	@Override
	public String getTableName()
	{
		return "accesscodes";
	}

	public IString getTitle()
	{
		return title;
	}

	public void setTitle(IString title)
	{
		this.title = title;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public IString getDescr()
	{
		return descr;
	}

	public void setDescr(IString descr)
	{
		this.descr = descr;
	}

	public IString getAccessdenied()
	{
		return accessdenied;
	}

	public void setAccessdenied(IString accessdenied)
	{
		this.accessdenied = accessdenied;
	}

	public String getPages()
	{
		return pages;
	}

	public void setPages(String pages)
	{
		this.pages = pages;
	}
}
