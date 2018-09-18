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

import com.i4one.base.model.ReturnType;
import java.util.Collections;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("base.SiteGroupManager")
public class CachedSiteGroupManager extends SimpleSiteGroupManager implements SiteGroupManager
{
	@CacheEvict(value = "siteGroupManager", allEntries = true)
	@Override
	public void init()
	{
		super.init();
	}

	@Cacheable(value = "siteGroupManager", key = "target.makeKey('getSiteGroup', #ser)")
	@Override
	public SiteGroup getSiteGroup(int ser)
	{
		return super.getSiteGroup(ser);
	}

	@Cacheable(value = "siteGroupManager", key = "target.makeKey('getSiteGroups', #client)")
	@Override
	public Set<SiteGroup> getSiteGroups(SingleClient client)
	{
		return Collections.unmodifiableSet(super.getSiteGroups(client));
	}

	@Cacheable(value = "siteGroupManager", key="target.makeKey('getSingleClients', #siteGroup')")
	@Override
	public Set<SingleClient> getSingleClients(SiteGroup siteGroup)
	{
		return Collections.unmodifiableSet(super.getSingleClients(siteGroup));
	}

	@CacheEvict(value = "siteGroupManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<SiteGroup> create(SiteGroup item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "siteGroupManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<SiteGroup> update(SiteGroup siteGroup)
	{
		return super.update(siteGroup);
	}

	@CacheEvict(value = "siteGroupManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public SiteGroup remove(SiteGroup siteGroup)
	{
		return super.remove(siteGroup);
	}

	@CacheEvict(value = "siteGroupManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public boolean associate(SiteGroup siteGroup, SingleClient client)
	{
		return super.associate(siteGroup, client);
	}

	@CacheEvict(value = "siteGroupManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public boolean dissociate(SiteGroup siteGroup, SingleClient client)
	{
		return super.dissociate(siteGroup, client);
	}

}
