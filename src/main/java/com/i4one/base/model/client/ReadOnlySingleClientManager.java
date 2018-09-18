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
import com.i4one.base.model.manager.BaseReadOnlyManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class ReadOnlySingleClientManager extends BaseReadOnlyManager<SingleClientRecord, SingleClient> implements SingleClientManager
{
	private SingleClientManager singleClientManager;

	public ReadOnlySingleClientManager()
	{
		singleClientManager = new NullSingleClientManager();
	}

	@Override
	public Manager<SingleClientRecord, SingleClient> getImplementationManager()
	{
		return getSingleClientManager();
	}

	@Override
	public SingleClient getClient(String name)
	{
		return getSingleClientManager().getClient(name);
	}

	@Override
	public SingleClient getClientByDomain(String domain)
	{
		return getSingleClientManager().getClientByDomain(domain);
	}

	@Override
	public SingleClient getClient(int ser)
	{
		return getSingleClientManager().getClient(ser);
	}

	@Override
	public SingleClient getParent(SingleClient client)
	{
		return getSingleClientManager().getParent(client);
	}

	@Override
	public List<SingleClient> getAllClients(SingleClient parent, PaginationFilter pagination)
	{
		return getSingleClientManager().getAllClients(parent, pagination);
	}

	@Override
	public ClientSettings getSettings(SingleClient client)
	{
		return getSingleClientManager().getSettings(client);
	}

	@Override
	public ReturnType<ClientSettings> updateSettings(ClientSettings settings)
	{
		throw new UnsupportedOperationException("Read-only access.");
	}

	public SingleClientManager getSingleClientManager()
	{
		return singleClientManager;
	}

	@Autowired
	@Qualifier("base.CachedSingleClientManager")
	public void setSingleClientManager(SingleClientManager singleClientManager)
	{
		this.singleClientManager = singleClientManager;
	}

}
