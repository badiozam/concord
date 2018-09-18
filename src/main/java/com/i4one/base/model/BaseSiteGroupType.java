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
package com.i4one.base.model;

import com.i4one.base.dao.SiteGroupRecordType;
import com.i4one.base.model.client.SiteGroup;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseSiteGroupType<U extends SiteGroupRecordType> extends BaseSingleClientType<U> implements SingleClientType<U>, SiteGroupType<U>
{
	private transient SimpleSiteGroupType<U, SiteGroupType<U>> simpleSiteGroupType;

	public BaseSiteGroupType(U delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( simpleSiteGroupType == null )
		{
			simpleSiteGroupType = new SimpleSiteGroupType(this);
		}
		simpleSiteGroupType.init();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		retVal.merge(simpleSiteGroupType.validate());
		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		simpleSiteGroupType.setOverrides();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		simpleSiteGroupType.actualizeRelations();
	}

	@Override
	public String uniqueKey()
	{
		return super.uniqueKey() + "-" + simpleSiteGroupType.uniqueKey();
	}

	@Override
	public SiteGroup getSiteGroup()
	{
		return simpleSiteGroupType.getSiteGroup();
	}

	public SiteGroup getSiteGroup(boolean doLoad)
	{
		return simpleSiteGroupType.getSiteGroup(doLoad);
	}


	@Override
	public void setSiteGroup(SiteGroup siteGroup)
	{
		simpleSiteGroupType.setSiteGroup(siteGroup);
	}
}