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
 * Basic implementation for a single client type
 *
 * @author Hamid Badiozamani
 * @param <U>
 */
public abstract class BaseSingleClientType<U extends ClientRecordType> extends BaseRecordTypeDelegator<U> implements SingleClientType<U>
{
	private transient SimpleSingleClientType<U, SingleClientType<U>> simpleSingleClientType;

	protected BaseSingleClientType(U delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		if ( simpleSingleClientType == null )
		{
			simpleSingleClientType = new SimpleSingleClientType(this);
		}

		simpleSingleClientType.init();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		simpleSingleClientType.actualizeRelations();
	}

	@Override
	public SingleClient getClient()
	{
		return simpleSingleClientType.getClient();
	}

	public SingleClient getClient(boolean doLoad)
	{
		return simpleSingleClientType.getClient(doLoad);
	}

	protected void setClientInternal(SingleClient client)
	{
		simpleSingleClientType.setClientInternal(client);
	}

	/**
	 * @param client The client this object belongs to
	 */
	@Override
	public void setClient(SingleClient client)
	{
		simpleSingleClientType.setClient(client);
	}

	/**
	 * Generates a key that uniquely identifies this object
	 *
	 * @return The unique key
	 */
	@Override
	public String uniqueKey()
	{
		return super.uniqueKey() + "-" + simpleSingleClientType.uniqueKey();
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
		return simpleSingleClientType.belongsTo(client);
	}
}
