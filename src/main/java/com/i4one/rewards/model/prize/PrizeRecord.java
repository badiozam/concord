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
package com.i4one.rewards.model.prize;

import com.i4one.base.dao.BaseSiteGroupRecordType;
import com.i4one.base.dao.SiteGroupRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class PrizeRecord extends BaseSiteGroupRecordType implements SiteGroupRecordType
{
	private Integer initinventory;
	private Integer currinventory;

	private IString title;
	private IString descr;

	private String thumbnailurl;
	private String detailpicurl;

	public PrizeRecord()
	{
		title = new IString();
		descr = new IString();

		thumbnailurl = "";
		detailpicurl = "";

		initinventory = 0;
		currinventory = 0;
	}

	@Override
	public String getSchemaName()
	{
		return "rewards";
	}

	@Override
	public String getTableName()
	{
		return "prizes";
	}

	public IString getTitle()
	{
		return title;
	}

	public void setTitle(IString title)
	{
		this.title = title;
	}

	public IString getDescr()
	{
		return descr;
	}

	public void setDescr(IString descr)
	{
		this.descr = descr;
	}

	public String getThumbnailurl()
	{
		return thumbnailurl;
	}

	public void setThumbnailurl(String thumbnailurl)
	{
		this.thumbnailurl = thumbnailurl;
	}

	public String getDetailpicurl()
	{
		return detailpicurl;
	}

	public void setDetailpicurl(String detailpicurl)
	{
		this.detailpicurl = detailpicurl;
	}

	public Integer getInitinventory()
	{
		return initinventory;
	}

	public void setInitinventory(Integer initinventory)
	{
		this.initinventory = initinventory;
	}

	public Integer getCurrinventory()
	{
		return currinventory;
	}

	public void setCurrinventory(Integer currinventory)
	{
		this.currinventory = currinventory;
	}
}
