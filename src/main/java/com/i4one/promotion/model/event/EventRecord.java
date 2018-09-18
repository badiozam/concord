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
package com.i4one.promotion.model.event;

import com.i4one.base.dao.categorizable.BaseCategorizableTerminableSiteGroupRecordType;
import com.i4one.base.dao.categorizable.CategorizableTerminableSiteGroupRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class EventRecord extends BaseCategorizableTerminableSiteGroupRecordType implements CategorizableTerminableSiteGroupRecordType
{
	static final long serialVersionUID = 42L;

	private IString title;
	private Integer orderweight;
	private String detailpicurl;
	private String sponsorurl;
	private IString intro;

	public EventRecord()
	{
		super();

		orderweight = 0;

		title = new IString();
		intro = new IString();

		detailpicurl = "";
		sponsorurl = "";
	}

	@Override
	public String getSchemaName()
	{
		return "promotion";
	}

	@Override
	public String getTableName()
	{
		return "events";
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

	public IString getIntro()
	{
		return intro;
	}

	public void setIntro(IString intro)
	{
		this.intro = intro;
	}

	public Integer getOrderweight()
	{
		return orderweight;
	}

	public void setOrderweight(Integer orderweight)
	{
		this.orderweight = orderweight;
	}

	public String getDetailpicurl()
	{
		return detailpicurl;
	}

	public void setDetailpicurl(String detailpicurl)
	{
		this.detailpicurl = detailpicurl;
	}

	public String getSponsorurl()
	{
		return sponsorurl;
	}

	public void setSponsorurl(String sponsorurl)
	{
		this.sponsorurl = sponsorurl;
	}

}
