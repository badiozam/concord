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
package com.i4one.base.model.adminprivilege;

import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimplePrivilegeManager extends BaseSimpleManager<PrivilegeRecord, Privilege> implements PrivilegeManager
{
	@Override
	public Set<Privilege> getAllPrivileges(PaginationFilter pagination)
	{
		return convertDelegates(getPrivilegeRecordDao().getAll(pagination));
	}

	@Override
	public Privilege lookupPrivilege(Privilege privilege)
	{
		Privilege retVal = new Privilege();

		// This determines if the input parameter is a search request by feature or by serial number
		//
		if ( privilege.exists() )
		{
			// If the record does exist, we attempt to load the latest version from the database
			//
			retVal.resetDelegateBySer(privilege.getSer());
			retVal.loadedVersion();

			return retVal;
		}
		else
		{
			return lookupPrivilege(privilege.getFeature(), privilege.getReadWrite());
		}
	}

	@Override
	public Privilege lookupPrivilege(String featureName, boolean hasWrite)
	{
		Privilege retVal = new Privilege();

		// Lookup the features (we might get 1 or 2 depending on whether there are readwrite/readonly options)
		//
		List<PrivilegeRecord> privs = getPrivilegeRecordDao().getAdminPrivilege(featureName);
		for ( PrivilegeRecord currPriv : privs )
		{
			if ( currPriv.getReadWrite() == hasWrite )
			{
				retVal.setOwnedDelegate(currPriv);
				break;
			}
		}

		getLogger().debug("Lookup privilege for " + featureName + " rw = " + hasWrite + " returning " + retVal);

		return retVal;
	}

	private PrivilegeRecordDao getPrivilegeRecordDao()
	{
		return (PrivilegeRecordDao)super.getDao();
	}

	@Override
	public Privilege emptyInstance()
	{
		return new Privilege();
	}
}
