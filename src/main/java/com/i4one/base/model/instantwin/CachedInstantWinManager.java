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
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedInstantWinManager extends SimpleInstantWinManager implements InstantWinManager
{
	@Cacheable(value = "instantWinManager", key = "target.makeKey('getInstantWinsByManager', #manager, #method, #pagination)")
	@Override
	public Set<InstantWin> getAllInstantWinsByManager(Manager<?, ?> manager, String method, PaginationFilter pagination)
	{
		return Collections.unmodifiableSet(super.getAllInstantWinsByManager(manager, method, pagination));
	}

	@Cacheable(value = "instantWinManager", key = "target.makeKey('getInstantWinsByFeature', #item, #pagination)")
	@Override
	public Set<InstantWin> getAllInstantWinsByFeature(RecordTypeDelegator<?> item, PaginationFilter pagination)
	{
		return Collections.unmodifiableSet(super.getAllInstantWinsByFeature(item, pagination));
	}

	@Cacheable(value = "instantWinManager", key = "target.makeKey('getLive', #pagination)")
	@Override
	public Set<InstantWin> getLive(TerminablePagination pagination)
	{
		return Collections.unmodifiableSet(super.getLive(pagination));
	}

	@Cacheable(value = "instantWinManager", key = "target.makeKey('getByRange', #pagination)")
	@Override
	public Set<InstantWin> getByRange(TerminablePagination pagination)
	{
		return Collections.unmodifiableSet(super.getByRange(pagination));
	}

	@CacheEvict(value = "instantWinManager", allEntries = true)
	@Override
	public List<ReturnType<InstantWin>> updateExclusiveInstantWins(RecordTypeDelegator<?> item, Collection<InstantWin> instantWins)
	{
		return super.updateExclusiveInstantWins(item, instantWins);
	}

	@CacheEvict(value = "instantWinManager", allEntries = true)
	@Override
	public boolean associate(RecordTypeDelegator<?> item, InstantWin trigger)
	{
		return super.associate(item, trigger);
	}

	@CacheEvict(value = "instantWinManager", allEntries = true)
	@Override
	public boolean associate(Manager<?, ?> manager, String method, InstantWin trigger)
	{
		return super.associate(manager, method, trigger);
	}

	@CacheEvict(value = "instantWinManager", allEntries = true)
	@Override
	public boolean dissociate(RecordTypeDelegator<?> item, InstantWin trigger)
	{
		return super.dissociate(item, trigger);
	}

	@CacheEvict(value = "instantWinManager", allEntries = true)
	@Override
	public boolean dissociate(Manager<?, ?> manager, String method, InstantWin trigger)
	{
		return super.dissociate(manager, method, trigger);
	}

	@CacheEvict(value = "instantWinManager", allEntries = true)
	@Override
	public ReturnType<InstantWin> clone(InstantWin item)
	{
		return super.clone(item);
	}

	@CacheEvict(value = "instantWinManager", allEntries = true)
	@Override
	public ReturnType<InstantWin> create(InstantWin item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "instantWinManager", allEntries = true)
	@Override
	public ReturnType<InstantWin> update(InstantWin item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "instantWinManager", allEntries = true)
	@Override
	public InstantWin remove(InstantWin item)
	{
		return super.remove(item);
	}
}