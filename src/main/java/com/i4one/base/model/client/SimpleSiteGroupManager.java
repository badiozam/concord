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
package com.i4one.base.model.client;

import com.i4one.base.dao.Dao;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleSiteGroupManager extends BaseSimpleManager<SiteGroupRecord, SiteGroup> implements SiteGroupManager
{
	@Override
	public SiteGroup getSiteGroup(int ser)
	{
		SiteGroup retVal;
		SiteGroupRecord siteGroupRecord = getDao().getBySer(ser);

		if ( siteGroupRecord != null )
		{
			retVal = initModelObject(new SiteGroup(siteGroupRecord));
			getLogger().debug("Lookup of site group with serial number " + ser + ": " + retVal);
		}
		else
		{
			retVal = new SiteGroup();
		}

		return retVal;
	}

	@Override
	public Set<SiteGroup> getSiteGroups(SingleClient client)
	{
		if ( client.isRoot() )
		{
			// The root client is able to view all site groups
			//
			return convertDelegates(getDao().getAll(SimplePaginationFilter.NONE));
		}
		else
		{
			return convertDelegates(getDao().getSiteGroups(client.getSer()));
		}
	}

	@Override
	public Set<SingleClient> getSingleClients(SiteGroup siteGroup)
	{
		List<SingleClientRecord> clientRecords = getDao().getSingleClients(siteGroup.getSer());

		return convertGenericDelegates(clientRecords, (singleClient) -> {}, SingleClient::new );
	}


	@Transactional(readOnly = false)
	@Override
	public boolean associate(SiteGroup siteGroup, SingleClient client)
	{
		if ( !getDao().isAssociated(siteGroup.getSer(), client.getSer()))
		{
			getDao().associate(siteGroup.getSer(), client.getSer());
			return true;
		}
		else
		{
			return false;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public boolean dissociate(SiteGroup siteGroup, SingleClient client)
	{
		if ( getDao().isAssociated(siteGroup.getSer(), client.getSer()))
		{
			getDao().dissociate(siteGroup.getSer(), client.getSer());
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected SiteGroup initModelObject(SiteGroup siteGroup) 
	{
		siteGroup.setClients(getSingleClients(siteGroup));

		return siteGroup;
	}

	@Override
	public SiteGroupRecordDao getDao()
	{
		return (SiteGroupRecordDao) super.getDao();
	}

	public SingleClientRecordDao getSingleClientDao()
	{
		Dao<SingleClientRecord> retVal = getDaoManager().getNewDao(SingleClient.class);
		return (SingleClientRecordDao) retVal;
	}

	@Override
	public SiteGroup emptyInstance()
	{
		return new SiteGroup();
	}
}
