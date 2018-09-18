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
import com.i4one.base.model.manager.BaseTriggerableTerminableManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.triggerable.TriggerableManager;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class TriggerableInstantWinManager extends BaseTriggerableTerminableManager<InstantWinRecord, InstantWin> implements TriggerableManager<InstantWinRecord, InstantWin>, InstantWinManager
{
	private InstantWinManager instantWinManager;

	@Override
	public List<ReturnType<InstantWin>> updateExclusiveInstantWins(RecordTypeDelegator<?> item, Collection<InstantWin> instantWins)
	{
		return getInstantWinManager().updateExclusiveInstantWins(item, instantWins);
	}

	@Override
	public boolean associate(RecordTypeDelegator<?> item, InstantWin instantWin)
	{
		return getInstantWinManager().associate(item, instantWin);
	}

	@Override
	public boolean associate(Manager<?, ?> manager, String method, InstantWin instantWin)
	{
		return getInstantWinManager().associate(manager, method, instantWin);
	}

	@Override
	public boolean dissociate(RecordTypeDelegator<?> item, InstantWin instantWin)
	{
		return getInstantWinManager().dissociate(item, instantWin);
	}

	@Override
	public boolean dissociate(Manager<?, ?> manager, String method, InstantWin instantWin)
	{
		return getInstantWinManager().dissociate(instantWin, instantWin);
	}

	@Override
	public Set<InstantWin> getAllInstantWinsByFeature(RecordTypeDelegator<?> item, PaginationFilter pagination)
	{
		return initSet(getInstantWinManager().getAllInstantWinsByFeature(item, pagination));
	}

	@Override
	public Set<InstantWin> getAllInstantWinsByManager(Manager<?, ?> manager, String method, PaginationFilter pagination)
	{
		return initSet(getInstantWinManager().getAllInstantWinsByManager(manager, method, pagination));
	}

	@Override
	public Set<RecordTypeDelegator<?>> getAllFeatureInstantWins(InstantWin instantWin)
	{
		return getInstantWinManager().getAllFeatureInstantWins(instantWin);
	}

	@Override
	public TerminableManager<InstantWinRecord, InstantWin> getImplementationManager()
	{
		return getInstantWinManager();
	}

	public InstantWinManager getInstantWinManager()
	{
		return instantWinManager;
	}

	@Autowired
	public void setInstantWinManager(InstantWinManager cachedInstantWinManager)
	{
		this.instantWinManager = cachedInstantWinManager;
	}
}
