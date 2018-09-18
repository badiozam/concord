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
package com.i4one.base.model.manager;

import static com.i4one.base.core.Base.getInstance;
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.PermissionDeniedException;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import com.i4one.base.model.adminprivilege.Privilege;
import com.i4one.base.model.adminprivilege.PrivilegeManager;
import com.i4one.base.model.adminprivilege.PrivilegeRecordDao;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.web.RequestState;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BasePrivilegedManager<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseDelegatingManager<U,T> implements PrivilegedManager<U, T>
{
	// The privilege manager to look up privileges
	//
	private PrivilegeManager privilegeManager;
	private AdminPrivilegeManager adminPrivilegeManager;

	// The request scope state
	//
	private RequestState requestState;

	private Privilege readPrivilege;
	private Privilege writePrivilege;

	private transient String featureName;

	public BasePrivilegedManager()
	{
		super();

		getLogger().debug("Created privileged manager " + getClass().getSimpleName());
	}

	@Override
	protected void initInternal(Map<String, Object> options)
	{
		// Cache the feature name here
		//
		featureName = emptyInstance().getDelegate().getFullTableName();

		readPrivilege = new Privilege();
		writePrivilege = new Privilege();

		readPrivilege.setReadWrite(false);
		writePrivilege.setReadWrite(true);

		readPrivilege.setFeature(getFeatureName());
		writePrivilege.setFeature(getFeatureName());

		Privilege dbReadPrivilege = getPrivilegeManager().lookupPrivilege(readPrivilege);
		if ( !dbReadPrivilege.exists() )
		{
			getLogger().debug("Could not find read privilege: " + readPrivilege);
			getPrivilegeManager().create(readPrivilege);
		}
		else
		{
			// Use the database version
			//
			readPrivilege = dbReadPrivilege;
		}

		Privilege dbWritePrivilege = getPrivilegeManager().lookupPrivilege(writePrivilege);
		if ( !dbWritePrivilege.exists() )
		{
			getLogger().debug("Could not find write privilege: " + writePrivilege);
			getPrivilegeManager().create(writePrivilege);
		}
		else
		{
			// Use the database version
			//
			writePrivilege = dbWritePrivilege;
		}
	}

	//private static boolean longDelay = false;

	/**
	 * Check whether the given administrator has write privileges for the
	 * current feature and the given client
	 *
	 * @param singleClient The client for which to check the privilege
	 * @param methodName The name of the method making the request (used for error reporting)
	 *
	 * @throws Errors if the administrator doesn't have sufficient privileges. The contained
	 * 	message will have a key in the form of "msg.[interfaceName].[methodName].denied"
	 */
	public void checkWrite(SingleClient singleClient, String methodName)
	{
		/*
		 * This test coupled with two browser requests coming in, tests to make sure
		 * each request maintains its own admin and that requests are not crossed over.
		 * 
		longDelay = !longDelay;
		getLogger().debug("Long delay is now " + longDelay);
		getLogger().debug("Admin is " + getAdmin().getUsername() + " in " + Thread.currentThread().toString() + " sleeping for " + (longDelay ? 10000 : 5000));

		try
		{
			Thread.sleep(longDelay ? 10000 : 5000);
		}
		catch (Exception e)
		{
			getLogger().debug("Caught exception while sleeping ", e);
		}

		getLogger().debug("Admin is " + getAdmin().getUsername() + " in " + Thread.currentThread().toString());
		*/

		ClientAdminPrivilege writeAdminPrivilege = new ClientAdminPrivilege();
		writeAdminPrivilege.setClient(singleClient);
		writeAdminPrivilege.setAdmin(getAdmin());
		writeAdminPrivilege.setPrivilege(getWritePrivilege());

		if ( !getAdminPrivilegeManager().hasAdminPrivilege(writeAdminPrivilege))
		{
			throw new PermissionDeniedException(new ErrorMessage("msg." + getInterfaceName() + "." + methodName + ".denied", "The administrator $priv.Admin does not have write privileges for client $priv.client feature $featureName.", new Object[] { "priv", writeAdminPrivilege, "featureName", getFeatureName() }, null));
		}
	}

	/**
	 * Check whether the given administrator has read privileges for the
	 * current feature and the given client
	 *
	 * @param singleClient The client for which to check the privilege
	 * @param methodName The name of the method making the request (used for error reporting)
	 *
	 * @throws Errors if the administrator doesn't have sufficient privileges. The contained
	 * 	message will have a key in the form of "msg.[interfaceName].[methodName].denied"
	 */
	public void checkRead(SingleClient singleClient, String methodName)
	{
		ClientAdminPrivilege readAdminPrivilege = new ClientAdminPrivilege();
		readAdminPrivilege.setClient(singleClient);
		readAdminPrivilege.setAdmin(getAdmin());
		readAdminPrivilege.setPrivilege(getReadPrivilege());

		if ( !getAdminPrivilegeManager().hasAdminPrivilege(readAdminPrivilege))
		{
			throw new PermissionDeniedException(new ErrorMessage("msg." + getInterfaceName() + "." + methodName + ".denied", "The administrator $priv.Admin does not have read privileges for client $priv.client feature $featureName.", new Object[] { "priv", readAdminPrivilege, "featureName", getFeatureName() }, null));
		}
	}

	@Override
	public U lock(T item)
	{
		return getImplementationManager().lock(item);
	}

	@Override
	public ReturnType<T> create(T item)
	{
		checkWrite(getClient(item), "create");
		return getImplementationManager().create(item);
	}

	@Override
	public T remove(T item)
	{
		checkWrite(getClient(item), "remove");
		return getImplementationManager().remove(item);
	}

	@Override
	public ReturnType<T> update(T item)
	{
		checkWrite(getClient(item), "update");

		getLogger().debug("Updating item " + item);
		return getImplementationManager().update(item);
	}

	@Override
	public ReturnType<T> clone(T item)
	{
		checkWrite(getClient(item), "clone");
		return getImplementationManager().clone(item);
	}

	@Override
	public TopLevelReport getReport(T item, TopLevelReport report, PaginationFilter pagination)
	{
		checkRead(getClient(item), "report");
		return getImplementationManager().getReport(item, report, pagination);
	}

	@Override
	public String getFeatureName()
	{
		return featureName;
	}

	public AdminPrivilegeManager getAdminPrivilegeManager()
	{
		return adminPrivilegeManager;
	}

	@Autowired
	public void setAdminPrivilegeManager(AdminPrivilegeManager adminPrivilegeManager)
	{
		this.adminPrivilegeManager = adminPrivilegeManager;
	}

	public PrivilegeManager getPrivilegeManager()
	{
		return privilegeManager;
	}

	@Autowired
	public void setPrivilegeManager(PrivilegeManager privilegeManager)
	{
		this.privilegeManager = privilegeManager;
	}

	@Override
	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	@Override
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
		//this.setRequestState(requestState);
	}

	public PrivilegeRecordDao getPrivilegeRecordDao()
	{
		return (PrivilegeRecordDao) getInstance().getDaoManager().getNewDao("privilegeDao");
	}

	@Override
	public Admin getAdmin()
	{
		return getRequestState().getAdmin();
	}

	@Override
	public Privilege getReadPrivilege()
	{
		return readPrivilege;
	}

	public void setReadPrivilege(Privilege readPrivilege)
	{
		this.readPrivilege = readPrivilege;
	}

	@Override
	public Privilege getWritePrivilege()
	{
		return writePrivilege;
	}

	public void setWritePrivilege(Privilege writePrivilege)
	{
		this.writePrivilege = writePrivilege;
	}

}
