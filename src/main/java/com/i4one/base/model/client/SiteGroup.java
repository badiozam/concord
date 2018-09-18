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

import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.SingleClientType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamid Badiozamani
 */
public class SiteGroup extends BaseSingleClientType<SiteGroupRecord> implements SingleClientType<SiteGroupRecord>
{
	private transient Map<Integer,SingleClient> clients;

	public SiteGroup()
	{
		super(new SiteGroupRecord());
	}

	public SiteGroup(SiteGroupRecord delegate)
	{
		super(delegate);
	}

	@Override
	public void init()
	{
		super.init();

		if ( clients == null )
		{
			clients = new HashMap<>();
		}
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle();
	}

	public String getTitle()
	{
		return getDelegate().getTitle();
	}

	public void setTitle(String title)
	{
		getDelegate().setTitle(title);
	}

	public String getName()
	{
		return getTitle();
	}

	/**
	 * Determines whether a given client belongs to this site group or not.
	 * This method returns true if the input client or any of its ancestors
	 * are members of this site group.
	 * 
	 * @param client The client to test
	 * 
	 * @return True if the client is a member of this SiteGroup, false otherwise.
	 */
	public boolean contains(SingleClient client)
	{
		boolean containsClient = clients.containsKey(client.getSer());
		if ( !containsClient && !client.isRoot() )
		{
			// If we contain this client's parent, we can say that we contain
			// this client as well
			//
			containsClient = contains(client.getParent(true));
		}

		return containsClient;
	}

	public Collection<SingleClient> getClients()
	{
		return this.clients.values();
	}

	public void setClients(Collection<SingleClient> clients)
	{
		clients.stream().forEach( (currClient) -> { this.clients.putIfAbsent(currClient.getSer(), currClient); });
	}

	/**
	 * Determines whether this client group is a member of an incoming client.
	 * If the incoming client is a single client, then this group belongs to it
	 * only if the group's owning client belongs to it. If the incoming client
	 * is a client group, then the group belongs to it only if they're equal.
	 * 
	 * @param client The incoming client to test
	 * 
	 * @return True if this client group is a member of that client, false otherwise.
	 */
	public boolean isMemberOf(Client client)
	{
		if ( client instanceof SingleClient )
		{
			// If we belong to the incoming client, we are a member of that client
			//
			return getClient().belongsTo((SingleClient)client);
		}
		else if ( client instanceof SiteGroup )
		{
			// We're only a member of another SiteGroup if we're equal
			return equals((SiteGroup)client);
		}
		else
		{
			// Consider throwing an exception here since the type is
			// not handled
			//
			return false;
		}
	}
}
