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
package com.i4one.base.model.accesscode;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.terminable.BaseTerminablePrivilegedManager;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedAccessCodeManager extends BaseTerminablePrivilegedManager<AccessCodeRecord, AccessCode> implements AccessCodeManager
{
	private AccessCodeManager accessCodeManager;

	@Override
	public TerminableManager<AccessCodeRecord, AccessCode> getImplementationManager()
	{
		return getAccessCodeManager();
	}

	@Override
	public SingleClient getClient(AccessCode item)
	{
		SingleClient retVal = item.getClient();

		// Sometimes the items aren't loaded from the database
		//
		if ( !retVal.exists() )
		{
			item.loadedVersion();
			return item.getClient();
		}
		else
		{
			return retVal;
		}
	}

	@Override
	public Set<AccessCode> getLiveCodes(String code, TerminablePagination pagination)
	{
		return getAccessCodeManager().getLiveCodes(code, pagination);
	}

	@Override
	public AccessCodeSettings getSettings(SingleClient client)
	{
		// Anyone can read the current settings because we need to know
		// whether the feature is enabled or not.
		//
		//checkRead(client, "updateSettings");
		return getAccessCodeManager().getSettings(client);
	}

	@Override
	public ReturnType<AccessCodeSettings> updateSettings(AccessCodeSettings settings)
	{
		checkWrite(settings.getClient(), "updateSettings");
		return getAccessCodeManager().updateSettings(settings);
	}

	public AccessCodeManager getAccessCodeManager()
	{
		return accessCodeManager;
	}

	@Autowired
	public void setAccessCodeManager(AccessCodeManager instantWinnableAccessCodeManager)
	{
		this.accessCodeManager = instantWinnableAccessCodeManager;
	}
}