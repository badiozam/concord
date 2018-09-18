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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedSingleClientManager extends SimpleSingleClientManager implements SingleClientManager
{
	@CacheEvict(value = "singleClientManager", allEntries = true)
	@Override
	public void init()
	{
		super.init();
	}

	@Cacheable(value = "singleClientManager", key = "target.makeKey('byName', #name)")
	@Override
	public SingleClient getClient(String name)
	{
		return super.getClient(name);
	}

	@Cacheable(value = "singleClientManager", key = "target.makeKey('bySer', #ser)")
	@Override
	public SingleClient getClient(int ser)
	{
		return super.getClient(ser);
	}

	@Cacheable(value = "singleClientManager", key = "target.makeKey('byDomain', #domain)")
	@Override
	public SingleClient getClientByDomain(String domain)
	{
		return super.getClientByDomain(domain);
	}

	@CacheEvict(value = "singleClientManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<SingleClient> create(SingleClient item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "singleClientManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<SingleClient> update(SingleClient client)
	{
		return super.update(client);
	}

	@CacheEvict(value = "singleClientManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public SingleClient remove(SingleClient client)
	{
		return super.remove(client);
	}

	@Override
	public ClientSettings getSettings(SingleClient client)
	{
		ClientSettings retVal = super.getSettings(client);

		// We get a separate copy since we may want to perform edits which may affect other
		// requests that use the cached client reference
		//
		retVal.setClient(super.getClient(client.getSer()));

		return retVal;
	}

	@CacheEvict(value = "singleClientManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClientSettings> updateSettings(ClientSettings settings)
	{
		return super.updateSettings(settings);
	}
}