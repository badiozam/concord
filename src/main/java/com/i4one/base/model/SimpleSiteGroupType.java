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
import com.i4one.base.model.client.Client;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.client.SingleClient;

/**
 * @author Hamid Badiozamani
 */
public class SimpleSiteGroupType<U extends SiteGroupRecordType, T extends SiteGroupType<U>> extends BaseRecordTypeDelegatorForwarder<U, T> implements SiteGroupType<U>
{
	private SiteGroup siteGroup;

	public SimpleSiteGroupType(T forward)
	{
		super(forward);
	}
	
	@Override
	protected void initInternal()
	{
		super.initInternal();

		if ( siteGroup == null )
		{
			siteGroup = new SiteGroup();
		}
		siteGroup.resetDelegateBySer(getDelegate().getSitegroupid());
	}

	@Override
	public Errors validateInternal()
	{
		Errors retVal = super.validateInternal();

		if ( !getSiteGroup().belongsTo(getClient()) )
		{
			retVal.addError("siteGroup", new ErrorMessage("msg.base.SiteGroup.clientMismatch", "This client (${item.client.name}) did not create (and therefore does not own) the group ${item.siteGroup.name}. Only the owner of the group (${item.siteGroup.client.name}) can add items to it.", new Object[]{"item", this}));
		}

		return retVal;
	}

	@Override
	public void setOverridesInternal()
	{
		super.setOverridesInternal();

		getSiteGroup().setOverrides();
	}

	@Override
	public void actualizeRelationsInternal()
	{
		super.actualizeRelationsInternal();

		setSiteGroup(getSiteGroup());
	}


	@Override
	public SiteGroup getSiteGroup()
	{
		return getSiteGroup(true);
	}

	public SiteGroup getSiteGroup(boolean doLoad)
	{
		if ( doLoad )
		{
			siteGroup.loadedVersion();
		}

		return siteGroup;
	}


	@Override
	public void setSiteGroup(SiteGroup siteGroup)
	{
		this.siteGroup = siteGroup;
		getDelegate().setSitegroupid(siteGroup.getSer());
	}

	@Override
	public SingleClient getClient()
	{
		return getForward().getClient();
	}

	@Override
	public void setClient(SingleClient client)
	{
		getForward().setClient(client);
	}

	@Override
	public boolean belongsTo(Client client)
	{
		return getForward().belongsTo(client);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return String.valueOf(getSiteGroup(false).getSer());
	}
}
