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
package com.i4one.base.model.instantwin;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.terminable.BaseTerminablePrivilegedManager;
import com.i4one.base.model.manager.terminable.TerminableManager;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedInstantWinManager extends BaseTerminablePrivilegedManager<InstantWinRecord, InstantWin> implements InstantWinManager
{
	private InstantWinManager instantWinManager;

	@Override
	public TerminableManager<InstantWinRecord, InstantWin> getImplementationManager()
	{
		return getInstantWinManager();
	}

	@Override
	public SingleClient getClient(InstantWin item)
	{
		SingleClient retVal = item.getClient();

		// Sometimes the items aren't loaded from the database
		//
		if ( !retVal.exists() )
		{
			item.loadedVersion();
			return item.getClient();
		}
		else
		{
			return retVal;
		}
	}

	@Override
	public List<ReturnType<InstantWin>> updateExclusiveInstantWins(RecordTypeDelegator<?> item, Collection<InstantWin> instantWins)
	{
		instantWins.forEach( (instantWin) -> { checkWrite(getClient(instantWin), "update"); } );
		return getInstantWinManager().updateExclusiveInstantWins(item, instantWins);
	}

	@Override
	public boolean associate(RecordTypeDelegator<?> item, InstantWin instantWin)
	{
		checkWrite(getClient(instantWin), "associate");
		return getInstantWinManager().associate(item, instantWin);
	}

	@Override
	public boolean associate(Manager<?, ?> manager, String method, InstantWin instantWin)
	{
		checkWrite(getClient(instantWin), "associate");
		return getInstantWinManager().associate(manager, method, instantWin);
	}

	@Override
	public boolean dissociate(RecordTypeDelegator<?> item, InstantWin instantWin)
	{
		checkWrite(getClient(instantWin), "dissociate");
		return getInstantWinManager().dissociate(item, instantWin);
	}

	@Override
	public boolean dissociate(Manager<?, ?> manager, String method, InstantWin instantWin)
	{
		checkWrite(getClient(instantWin), "dissociate");
		return getInstantWinManager().dissociate(manager, method, instantWin);
	}

	@Override
	public Set<InstantWin> getAllInstantWinsByFeature(RecordTypeDelegator<?> item, PaginationFilter pagination)
	{
		return getInstantWinManager().getAllInstantWinsByFeature(item, pagination);
	}

	@Override
	public Set<InstantWin> getAllInstantWinsByManager(Manager<?, ?> manager, String method, PaginationFilter pagination)
	{
		return getInstantWinManager().getAllInstantWinsByManager(manager, method, pagination);
	}

	@Override
	public Set<RecordTypeDelegator<?>> getAllFeatureInstantWins(InstantWin instantWin)
	{
		return getInstantWinManager().getAllFeatureInstantWins(instantWin);
	}

	public InstantWinManager getInstantWinManager()
	{
		return instantWinManager;
	}

	@Autowired
	public void setInstantWinManager(InstantWinManager triggerableInstantWinManager)
	{
		this.instantWinManager = triggerableInstantWinManager;
	}
}
