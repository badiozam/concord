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

import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.client.Client;
import com.i4one.base.model.client.SingleClient;

/**
 * @author Hamid Badiozamani
 */
public class SimpleSingleClientType<U extends ClientRecordType, T extends SingleClientType<U>> extends BaseRecordTypeDelegatorForwarder<U, T> implements SingleClientType<U>
{
	private transient SingleClient client;

	public SimpleSingleClientType(T forward)
	{
		super(forward);
	}

	@Override
	protected void initInternal()
	{
		super.initInternal();

		if ( client == null )
		{
			client = new SingleClient();
		}

		// Clients are cached so this isn't a real performance hit as it's just a map lookup
		//
		client.setSer(getDelegate().getClientid());
	}

	@Override
	public void actualizeRelationsInternal()
	{
		super.actualizeRelationsInternal();

		// This is a special case because we specfically don't want certain
		// object's clients to be settable and we have the setClient(..)
		// method to throw an exception unless overridden by a subclass
		//
		setClientInternal(getClient());
	}

	@Override
	public SingleClient getClient()
	{
		return getClient(true);
	}

	public SingleClient getClient(boolean doLoad)
	{
		if (doLoad)
		{
			client.setSer(getDelegate().getClientid());
			client.loadedVersion();
		}
		return client;
	}

	protected void setClientInternal(SingleClient client)
	{
		this.client.setSer(client.getSer());
		getDelegate().setClientid(client.getSer());
	}

	/**
	 * @param client The client this object belongs to
	 */
	@Override
	public void setClient(SingleClient client)
	{
		throw new UnsupportedOperationException("Can't set client directly.");
	}

	/**
	 * Generates a key that uniquely identifies this object
	 *
	 * @return The unique key
	 */
	@Override
	protected String uniqueKeyInternal()
	{
		return String.valueOf(getClient(false).getSer());
	}

	/**
	 * Whether this object belongs to the given client or not
	 *
	 * @param client The client to test
	 *
	 * @return True if the object belongs to that client, false otherwise
	 */
	@Override
	public boolean belongsTo(Client client)
	{
		if (getClient() == null)
		{
			return false;
		}
		else
		{
			// If our client is a member of the incoming client
			// we belong to that client
			//
			return getClient().isMemberOf(client);
		}
	}

}
