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
package com.i4one.predict.model.event;

import com.i4one.base.dao.categorizable.BaseCategorizableTerminableSiteGroupRecordType;
import com.i4one.base.dao.categorizable.CategorizableTerminableSiteGroupRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class EventRecord extends BaseCategorizableTerminableSiteGroupRecordType implements CategorizableTerminableSiteGroupRecordType
{
	static final long serialVersionUID = 42L;

	private Integer actualoutcomeid;

	private IString title;
	private IString brief;
	private IString promo;
	private IString descr;
	private IString reference;

	private Integer minbid;
	private Integer maxbid;
	private Integer postsby;
	private Integer closesby;
	private Integer postedtm;

	private Integer usagecount;

	public EventRecord()
	{
		super();

		minbid = 0;
		maxbid = 0;
		postsby = 0;
		closesby = 0;
		usagecount = 0;

		title = new IString();
		brief = new IString();
		promo = new IString();
		descr = new IString();
		reference = new IString();
	}

	@Override
	public String getSchemaName()
	{
		return "predict";
	}

	@Override
	public String getTableName()
	{
		return "events";
	}

	public IString getBrief()
	{
		return brief;
	}

	public void setBrief(IString brief)
	{
		this.brief = brief;
	}

	public IString getPromo()
	{
		return promo;
	}

	public void setPromo(IString promo)
	{
		this.promo = promo;
	}

	public IString getDescr()
	{
		return descr;
	}

	public void setDescr(IString descr)
	{
		this.descr = descr;
	}

	public IString getReference()
	{
		return reference;
	}

	public void setReference(IString reference)
	{
		this.reference = reference;
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

	public Integer getActualoutcomeid()
	{
		return actualoutcomeid;
	}

	public void setActualoutcomeid(Integer actualoutcomeid)
	{
		this.actualoutcomeid = actualoutcomeid;
	}

	public Integer getMaxbid()
	{
		return maxbid;
	}

	public void setMaxbid(Integer maxbid)
	{
		this.maxbid = maxbid;
	}

	public Integer getMinbid()
	{
		return minbid;
	}

	public void setMinbid(Integer minbid)
	{
		this.minbid = minbid;
	}

	public Integer getPostsby()
	{
		return postsby;
	}

	public void setPostsby(Integer postsby)
	{
		this.postsby = postsby;
	}

	public Integer getPostedtm()
	{
		return postedtm;
	}

	public void setPostedtm(Integer postedtm)
	{
		this.postedtm = postedtm;
	}

	public Integer getUsagecount()
	{
		return usagecount;
	}

	public void setUsagecount(Integer usagecount)
	{
		this.usagecount = usagecount;
	}

	public Integer getClosesby()
	{
		return closesby;
	}

	public void setClosesby(Integer closesby)
	{
		this.closesby = closesby;
	}

}
