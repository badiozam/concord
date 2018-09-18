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

import com.i4one.base.dao.BaseSiteGroupRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseCategoryRecord extends BaseSiteGroupRecordType implements CategoryRecord
{
	static final long serialVersionUID = 42L;

	private IString title;
	private String thumbnailurl;
	private String detailpicurl;
	private IString descr;

	public BaseCategoryRecord()
	{
		title = new IString();
		descr = new IString();

		thumbnailurl = "";
		detailpicurl = "";
	}

	@Override
	public String getTableName()
	{
		return "categories";
	}

	@Override
	public IString getTitle()
	{
		return title;
	}

	@Override
	public void setTitle(IString title)
	{
		this.title = title;
	}

	@Override
	public String getThumbnailurl()
	{
		return thumbnailurl;
	}

	@Override
	public void setThumbnailurl(String thumbnailurl)
	{
		this.thumbnailurl = thumbnailurl;
	}

	@Override
	public String getDetailpicurl()
	{
		return detailpicurl;
	}

	@Override
	public void setDetailpicurl(String detailpicurl)
	{
		this.detailpicurl = detailpicurl;
	}

	@Override
	public IString getDescr()
	{
		return descr;
	}

	@Override
	public void setDescr(IString descr)
	{
		this.descr = descr;
	}

}
