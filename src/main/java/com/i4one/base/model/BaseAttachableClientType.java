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
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseAttachableClientType<U extends ClientRecordType, T extends SingleClientType<U>> extends BaseRecordTypeDelegatorForwarder<U, T> implements AttachableClientType<U,T>
{

	public BaseAttachableClientType(T forward)
	{
		super(forward);
	}

	@Override
	public boolean fromCSVList(List<String> csv)
	{
		return fromCSVInternal(csv);
	}

	protected boolean fromCSVInternal(List<String> csv)
	{
		return true;
	}

	@Override
	public String toCSV(boolean header)
	{
		StringBuilder retVal = new StringBuilder();
		if ( header )
		{
			retVal.append(toCSVInternal(true));

			return retVal.toString();
		}
		else
		{
			return toCSVInternal(false).toString();
		}
	}

	protected StringBuilder toCSVInternal(boolean header)
	{
		return new StringBuilder();
	}


	@Override
	public boolean belongsTo(Client client)
	{
		return getForward().belongsTo(client);
	}

	@Override
	public SingleClient getClient()
	{
		return getForward().getClient();
	}

	@Override
	public void setClient(SingleClient client)
	{
		getForward().setClient(client);
	}
}
