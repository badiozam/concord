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
package com.i4one.base.model.admin;

import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import com.i4one.base.model.adminprivilege.PrivilegeManager;
import com.i4one.base.model.adminprivilege.Privilege;
import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleAdminManager extends BasePaginableManager<AdminRecord,Admin> implements AdminManager
{
	private SingleClientManager singleClientManager;
	private PrivilegeManager privilegeManager;
	private AdminPrivilegeManager adminPrivilegeManager;

	@Override
	public Set<Admin> getAdmins(Admin supervisor, PaginationFilter pagination)
	{
		Set<Admin> retVal = new HashSet<>();
		Privilege clientAdminsRO = getPrivilegeManager().lookupPrivilege(supervisor.getFeatureName(), false);
		Privilege allRO = getPrivilegeManager().lookupPrivilege("*.*", false);

		retVal.addAll(getAdmins(supervisor, clientAdminsRO, pagination));
		retVal.addAll(getAdmins(supervisor, allRO, pagination));

		return retVal;
	}

	protected Set<Admin> getAdmins(Admin supervisor, Privilege priv, PaginationFilter pagination)
	{
		getLogger().debug("getAdmins(..) called with admin " + supervisor + " and privilege " + priv);

		// We will likely need to convert this method into a single query in order to support proper pagination
		//
		Collection<ClientAdminPrivilege> clientAdminPrivs = getAdminPrivilegeManager().getAdminPrivileges(supervisor, priv, pagination);

		// Gather all of the individual clients that the administrator has base.admins privileges for
		//
		Set<SingleClient> singleClients = clientAdminPrivs.stream()
			.map( (cap) -> { return cap.getClient(false); } )
			.filter( (singleClient) -> { return singleClient.exists(); })
			.collect(Collectors.toSet());

		// In addition to all of the explicitly set admin privilege's client, any
		// child clients are considered since those would also fall under the supervisor
		//
		singleClients.forEach( (singleClient) ->
		{
			SingleClient currClient = singleClient;
			singleClients.addAll(getSingleClientManager().getAllClients(currClient, SimplePaginationFilter.NONE));
		});

		Set<Admin> retVal = new HashSet<>();

		// Now, for each client, look up all of the administrators that have any access rights
		// to that client
		//
		singleClients.stream().forEach( (singleClient) ->
		{
			Collection<ClientAdminPrivilege> privs = getAdminPrivilegeManager().getAdminPrivileges(singleClient, pagination);
			retVal.addAll(privs.stream().map( (cap) -> { return cap.getAdmin(); }).collect(Collectors.toList()));
		});

		getLogger().debug("getAdmins(..) returning " + Utils.toCSV(retVal) + " for " + supervisor);

		// Since this method is an aggregator of several different queries we'll need to apply
		// the pagination filter's limit before we return
		//
		return pagination.apply(retVal);
	}

	@Override
	public Admin authenticate(Admin admin, SingleClient client)
	{
		AdminRecord dbAdminRecord = getAdminRecord(admin, false);

		if ( dbAdminRecord != null )
		{
			Admin dbAdmin = new Admin(dbAdminRecord);
			String password = getDao().getPassword(dbAdminRecord.getSer());

			String md5Password = "x";
			String dbMD5Password = "y";
			try
			{
				md5Password = admin.getMD5Password();
				dbMD5Password = dbAdmin.getMD5Password();
			}
			catch (NoSuchAlgorithmException ex)
			{
				getLogger().debug("No MD5 algorithm found", ex);
			}

			if ( admin.getPassword().equals(password)
				|| md5Password.equals(dbMD5Password))
			{
				getLogger().debug("Password match: {} = {} OR MD5 {} = {}", dbAdmin.getPassword(), password, md5Password, dbMD5Password);
				return dbAdmin;
			}
			else
			{
				getLogger().debug("No password match: {} = {} NOR MD5 {} = {}", dbAdmin.getPassword(), password, md5Password, dbMD5Password);

				// Non-password match
				//
				return new Admin();
			}
		}
		else
		{
			getLogger().debug("The administrator doesn't exist by username {} or serial number {}", admin.getUsername(), admin.getSer());

			// The administrator doesn't exist by username nor serial number
			//
			return new Admin();
		}
	}

	@Transactional(readOnly=false)
	@Override
	public ReturnType<Admin> clone(Admin item)
	{
		if ( item.exists() )
		{
			Admin admin = new Admin();
			admin.setEmail("clone-" + Utils.currentTimeSeconds() + "-" + item.getEmail());
			admin.setName("Cloned from " + item.getName());
			//admin.setPassword(item.getDelegate().getPassword()); // They have to reset it via e-mail
			admin.setUsername("clone-" + Utils.currentTimeSeconds() + "-" + item.getUsername());

			ReturnType<Admin> retVal = create(admin);

			List<ReturnType<ClientAdminPrivilege>> grantedPrivs = new ArrayList<>();
			for ( ClientAdminPrivilege cap : getAdminPrivilegeManager().getAdminPrivileges(item, SimplePaginationFilter.NONE))
			{
				Privilege priv = cap.getPrivilege(false);

				ClientAdminPrivilege clonedPriv = new ClientAdminPrivilege();
				clonedPriv.setAdmin(retVal.getPost());
				clonedPriv.setClient(cap.getClient(false));
				clonedPriv.setPrivilege(priv);

				ReturnType<ClientAdminPrivilege> clonedCap = getAdminPrivilegeManager().grant(clonedPriv);
				grantedPrivs.add(clonedCap);
			}

			retVal.addChain(getAdminPrivilegeManager(), "grant", new ReturnType<>(grantedPrivs));

			return retVal;
		}
		else
		{
			throw new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.dne", "You are attempting to clone a non-existent item: $item", new Object[] { "item", item }));
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Admin> resetPassword(Admin admin) throws Exception
	{
		ReturnType<Admin> retVal = new ReturnType<>();

		// Attempt to look up the admin by username, and then e-mail address
		//
		AdminRecord adminRecord = getAdminRecord(admin, true);

		if ( adminRecord == null || !adminRecord.exists() )
		{
			// Couldn't find the admin
			//
			retVal.setPost(new Admin());
		}
		else
		{
			Admin dbAdmin = new Admin(adminRecord);

			// Need the previous version of the record for historical reasons
			//
			Admin oldAdmin = new Admin();
			oldAdmin.resetDelegateBySer(dbAdmin.getSer());
			oldAdmin.loadedVersion();

			// Set the password to the MD5 hex version with the time stamp appended and send that
			// to the admin for their next login and also force them to update their password
			//
			String newPwd = Utils.getMD5Hex(Utils.abbrStr(adminRecord.getPassword(), 32) + Utils.currentTimeSeconds());
			dbAdmin.setPassword(newPwd);
			dbAdmin.setForceUpdate(true);

			// The order here matters since update password sets the forceupdate field to false, and we must set it
			// back to true if the password was successfully updated
			//
			getDao().updatePassword(adminRecord, dbAdmin.getPassword());
			getDao().updateBySer(adminRecord); // Need to update the force update field here

			// Convenient to have the serial number so callers won't have to
			// look up the admin again
			//
			admin.setSer(adminRecord.getSer());

			retVal.setPre(oldAdmin);
			retVal.setPost(dbAdmin);

			retVal.put("resetPassword", newPwd);
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Admin> updatePassword(Admin admin)
	{
		ReturnType<Admin> retVal = new ReturnType<>();

		try
		{
			Errors errors = admin.validate();
			if ( errors.hasErrors() )
			{
				throw errors;
			}
			else
			{
				// Lock the record to prevent other updates
				//
				AdminRecord dbAdminRecord = getAdminRecord(admin, true);
	
				if ( dbAdminRecord != null && dbAdminRecord.exists() )
				{
					// Need the previous version of the record for historical reasons
					//
					Admin oldAdmin = new Admin();
					oldAdmin.resetDelegateBySer(dbAdminRecord.getSer());
					oldAdmin.loadedVersion();

					getDao().updatePassword(dbAdminRecord, admin.getPassword());
					Admin dbAdmin = new Admin(dbAdminRecord);

					retVal.setPre(oldAdmin);
					retVal.setPost(dbAdmin);

				}
				else
				{
					retVal.setPost(new Admin());
				}

				return retVal;
			}
		}
		catch (Exception ex)
		{
			getLogger().error("Update password failed", ex);

			Errors errors = new Errors(getInterfaceName() + ".updatePassword", new ErrorMessage("msg." + getInterfaceName() + ".updatePassword.failed", "Could not update administrator password: $item: $ex.message", new Object[] { "item", admin, "ex", ex }, ex));
			throw errors;
		}
	}

	private AdminRecord getAdminRecord(Admin admin, boolean forUpdate)
	{
		AdminRecord dbAdminRecord = null;
		if ( admin.exists() )
		{
			getLogger().trace("Lookup by serial number");
			dbAdminRecord = getDao().getBySer(admin.getSer(), forUpdate);
		}

		// Look up by username if serial number failed
		//
		if ( dbAdminRecord == null )
		{
			getLogger().trace("Lookup by username");
			dbAdminRecord = getDao().getAdmin(admin.getUsername());
		}

		return dbAdminRecord;
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

	public SingleClientManager getSingleClientManager()
	{
		return singleClientManager;
	}

	@Autowired
	public void setSingleClientManager(SingleClientManager singleClientManager)
	{
		this.singleClientManager = singleClientManager;
	}

	@Override
	public AdminRecordDao getDao()
	{
		return (AdminRecordDao) super.getDao();
	}

	@Override
	public Admin emptyInstance()
	{
		return new Admin();
	}
}