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
package com.i4one.base.model.manager.pagination;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ClientType;
import com.i4one.base.model.client.SingleClient;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hamid Badiozamani
 */
public class ClientPagination extends SimplePaginationFilter implements PaginationFilter,ClientType
{
	private final Set<SingleClient> clients;

	public ClientPagination()
	{
		super();

		this.clients = new HashSet<>();
	}

	public ClientPagination(SingleClient client, PaginationFilter chain)
	{
		super(chain);

		//this.singleClient = singleClient;

		this.clients = new HashSet<>();
		this.clients.add(client);
	}

	public ClientPagination(Collection<SingleClient> clients, PaginationFilter chain)
	{
		super(chain);

		this.clients = new HashSet<>();
		this.clients.addAll(clients);
	}

	@Override
	protected void initQualifiers()
	{
		super.initQualifiers();

		getColumnQualifier().setQualifier("clientid", clients.size() > 0 ? getClientids() : null);
	}

	@Override
	protected String toStringInternal()
	{
		if (!clients.isEmpty())
		{
			if ( getLogger().isTraceEnabled() )
			{
				return "clients: [" + Utils.toCSV(clients, SingleClient::getName) + "], " + super.toStringInternal();
			}
			else
			{
				return "clients:[" + Utils.toCSV(clients, SingleClient::getName) + "]," + super.toStringInternal();
			}
		}
		else
		{
			return super.toStringInternal();
		}
	}

	@Override
	public SingleClient getClient()
	{
		if ( clients.isEmpty() )
		{
			return new SingleClient();
		}
		else
		{
			return clients.iterator().next();
		}
	}

	@Override
	public void setClient(SingleClient client)
	{
		clients.clear();
		clients.add(client);
	}

	public Set<SingleClient> getClients()
	{
		return Collections.unmodifiableSet(clients);
	}

	protected Set<Integer> getClientids()
	{
		return getClients()
			.stream()
			.map(SingleClient::getSer)
			.collect(Collectors.toSet());
	}
}
