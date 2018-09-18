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

import com.i4one.base.core.AgingLRUMap;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleAdminPrivilegeManager extends BaseSimpleManager<ClientAdminPrivilegeRecord,ClientAdminPrivilege> implements AdminPrivilegeManager
{
	private Privilege READ_ALL;
	private Privilege WRITE_ALL;

	private PrivilegeManager privilegeManager;
	private SingleClientManager singleClientManager;

	private AgingLRUMap<Privilege, Privilege> schemaPrivileges;

	@Override
	public void init()
	{
		super.init();

		// Initialize the wildcard privileges for recurrent use, saving it
		// to the database if it doesn't exist already
		//
		READ_ALL = new Privilege();
		READ_ALL.setFeature("*.*");
		READ_ALL.setReadWrite(false);
		Privilege readAll = getPrivilegeManager().lookupPrivilege(READ_ALL);
		if ( !readAll.exists() )
		{
			ReturnType<Privilege> readAllCreate = getPrivilegeManager().create(READ_ALL);
			READ_ALL = readAllCreate.getPost();
		}
		else
		{
			READ_ALL = readAll;
		}

		WRITE_ALL = new Privilege();
		WRITE_ALL.setFeature("*.*");
		WRITE_ALL.setReadWrite(true);
		Privilege writeAll = getPrivilegeManager().lookupPrivilege(WRITE_ALL);
		if ( !writeAll.exists() )
		{
			ReturnType<Privilege> writeAllCreate = getPrivilegeManager().create(WRITE_ALL);
			WRITE_ALL = writeAllCreate.getPost();
		}
		else
		{
			WRITE_ALL = writeAll;
		}

		schemaPrivileges = new AgingLRUMap<>(100, 1000);
	}

	@Override
	public Set<ClientAdminPrivilege> getAdminPrivileges(Admin admin, SingleClient client, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getClientAdminPrivilegeRecordByClient(admin.getSer(), client.getSer(), pagination));
	}

	@Override
	public Set<ClientAdminPrivilege> getAdminPrivileges(Admin admin, Privilege privilege, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getClientAdminPrivilegeRecordByPrivilege(admin.getSer(), privilege.getSer(), pagination));
	}

	@Override
	public Set<ClientAdminPrivilege> getAdminPrivileges(Admin admin, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getClientAdminPrivilegeRecord(admin.getSer(), pagination));
	}

	@Override
	public Set<ClientAdminPrivilege> getAdminPrivileges(SingleClient client, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getClientAdminPrivilegeRecordByClient(client.getSer(), pagination));
	}

	@Override
	public boolean hasAdminPrivilege(ClientAdminPrivilege priv)
	{
		boolean retVal = getAdminPrivilege(priv).exists();
		getLogger().trace("hasAdminPrivilege {} returning {}", priv.uniqueKey(), retVal);

		return retVal;
	}

	@Override
	public ReturnType<ClientAdminPrivilege> create(ClientAdminPrivilege item)
	{
		throw new UnsupportedOperationException("Use grant instead");
	}

	@Override
	public ClientAdminPrivilege remove(ClientAdminPrivilege item)
	{
		throw new UnsupportedOperationException("Use revoke instead");
	}

	/**
	 * Check to see if the given admin has the given privilege.
	 *
	 * @param priv The privilege to check
	 *
	 * @return The specific privilege that grants the administrator access to the
	 * 	requested privilege. This may or may not be the same as the incoming
	 * 	privilege object. If the admin does not have access, a non-existent
	 * 	privilege object is returned.
	 */
	protected Privilege getAdminPrivilege(ClientAdminPrivilege priv)
	{
		// Look up the privilege requested if it doesn't exist
		//
		Privilege privilege = getPrivilegeManager().lookupPrivilege(priv.getPrivilege());

		if ( !privilege.exists() )
		{
			// The admin couldn't possibly have a non-existent privilege
			//
			return privilege;
		}
		else if ( privilege.exists() &&
			priv.getAdmin().exists() &&
			priv.getClient().exists() )
		{
			ClientAdminPrivilegeRecord currRecord = getDao().getClientAdminPrivilegeRecord(priv.getAdmin(false).getSer(),
				priv.getClient(false).getSer(),
				privilege.getSer());

			if ( currRecord != null )
			{
				// We found a direct match
				//
				//priv.setOwnedDelegate(currRecord);
				getLogger().trace("Direct match found for " + currRecord);

				Privilege retVal = new Privilege();
				retVal.resetDelegateBySer(currRecord.getPrivid());

				return retVal;
			}
			else
			{
				// Attempt to match the wildcard privilege.
				//
				Privilege allPrivilege = priv.getPrivilege().getReadWrite() ? WRITE_ALL : READ_ALL;

				ClientAdminPrivilege allPriv = new ClientAdminPrivilege();
				//allPriv.setDelegate(priv.getDelegate());
				allPriv.setAdmin(priv.getAdmin());
				allPriv.setClient(priv.getClient(false));
				allPriv.setPrivilege(allPrivilege);

				// Test for the wildcard privilege (only if we're not already doing so through recursion)
				//
				if ( !allPrivilege.equals(priv.getPrivilege()) && hasAdminPrivilege(allPriv) )
				{
					getLogger().trace("Matched the wildcard privilege " + allPrivilege + " instead of " + priv.getPrivilege());
					return allPrivilege;
				}

				// Test for the schema based wildcard privilege
				//
				Privilege schemaAllPrivilege = buildSchemaPrivilege(priv.getPrivilege());
				allPriv.setPrivilege(schemaAllPrivilege);

				// Test for the wildcard privilege (only if we're not already doing so through recursion)
				//
				getLogger().trace("Testing privilege {} vs {} = {}", schemaAllPrivilege, priv.getPrivilege(), schemaAllPrivilege.equals(priv.getPrivilege()));
				if ( schemaAllPrivilege.exists() &&
					!schemaAllPrivilege.equals(allPrivilege) &&
					!schemaAllPrivilege.equals(priv.getPrivilege()) &&
					hasAdminPrivilege(allPriv) )
				{
					getLogger().trace("Matched the schema wildcard privilege " + allPrivilege + " instead of " + priv.getPrivilege());
					return schemaAllPrivilege;
				}

				// We didn't match the wild card at this level, we can move up by one level if we're looking up by single
				// client heirarchy. Otherwise, we're done since we couldn't match the client group privilege
				//
				if ( priv.getClient(false).exists() && !priv.getClient(false).isRoot() )
				{
					SingleClient parentClient = getSingleClientManager().getParent(priv.getClient(false));

					// Couldn't find it for this client, maybe the administrator has a higher up
					// privilege level that allows them access to this one as well
					//
					ClientAdminPrivilege parentPrivilege = new ClientAdminPrivilege();
					parentPrivilege.setAdmin(priv.getAdmin());
					parentPrivilege.setPrivilege(privilege);
					parentPrivilege.setClient(parentClient);

					return getAdminPrivilege(parentPrivilege);
				}
				else
				{
					getLogger().trace("No privilege record for " + priv + " found");
					return new Privilege();
				}
			}
		}
		else
		{
			Errors errors = new Errors();

			/*
			if ( !privilege.exists() )
			{
				errors.addError(new ErrorMessage("msg.adminPrivilegeManager.privilege.dne", "The privilege $priv.privilege does not exist.", new Object[] {"priv", priv}));
			}
			*/

			if ( !priv.getAdmin().exists() )
			{
				errors.addError(new ErrorMessage("msg.adminPrivilegeManager.admin.dne", "The administrator $priv.admin does not exist.", new Object[] { "priv", priv }));
			}

			if ( !priv.getClient().exists() )
			{
				errors.addError(new ErrorMessage("msg.adminPrivilegeManager.client.dne", "The client $priv.client does not exist (requested $priv.privilege).", new Object[] { "priv", priv }));
			}

			// Non-existant client/sitegroup/admin/privilege
			//
			throw errors;
		}
	}


	protected Privilege buildSchemaPrivilege(Privilege priv)
	{
		if ( priv.equals(READ_ALL) || priv.equals(WRITE_ALL))
		{
			return priv;
		}
		else
		{
			Privilege retVal = schemaPrivileges.get(priv);

			if ( retVal == null )
			{
				if ( priv.getFeature().contains("."))
				{
					getLogger().trace("Building schema privilege against {}", priv.getFeature());
	
					// Converts <schema>.<feature> to <schema>.*
					//
					String feature = priv.getFeature().substring(0, priv.getFeature().indexOf('.')) + ".*";
	
					retVal = getPrivilegeManager().lookupPrivilege(feature, priv.getReadWrite());
					if ( retVal.exists() )
					{
						schemaPrivileges.put(priv, retVal);
					}
				}
				else
				{
					// Not a valid privilege feature 
					//
					getLogger().error("Invalid privilege {}", priv.getFeature(), new Throwable());
					return new Privilege();
				}
			}
			else
			{
				getLogger().trace("Matched internal cache for {} => {}", priv, retVal);
			}

			return retVal;
		}
	}

	@Override
	public ReturnType<ClientAdminPrivilege> grant(ClientAdminPrivilege priv)
	{
		// We don't use the hasAdminPrivilege here because we want to use
		// the existing privilege in the error message
		//
		Privilege existingPriv = getAdminPrivilege(priv);
		if ( !existingPriv.exists() )
		{
			getLogger().trace("Granting " + priv);

			// We create the privilege record
			//
			return super.create(priv);
		}
		else
		{
			// The administrator already has this privilege
			//
			throw new Errors("adminManager.grant", new ErrorMessage("msg.adminManager.grant.duplicate", "This administrator $priv.Admin already has the privilege $priv.Privilege in the form of $existing", new Object[] { "priv", priv, "existing", existingPriv}));
		}
	}

	@Override
	public ReturnType<ClientAdminPrivilege> revoke(ClientAdminPrivilege priv)
	{
		// We only do direct revocations, no recursive look up of clients
		//
		ClientAdminPrivilegeRecord currRecord = getDao().getClientAdminPrivilegeRecord(priv.getAdmin(false).getSer(),
				priv.getClient(false).getSer(),
				priv.getPrivilege(false).getSer());

		ClientAdminPrivilege dbPriv = new ClientAdminPrivilege(currRecord);
		if ( currRecord.exists() )
		{
			getLogger().debug("Revoking " + dbPriv);

			return new ReturnType<>(super.remove(dbPriv));
		}
		else
		{
			// The administrator already doesn't have this privlege
			//
			throw new Errors("adminManager.revoke", new ErrorMessage("msg.adminManager.revoke.dne", "This administrator $priv.Admin does not have the privilege $priv.Privilege", new Object[] { "priv", priv }));
		}
	}

	@Override
	public ClientAdminPrivilegeRecordDao getDao()
	{
		return (ClientAdminPrivilegeRecordDao) super.getDao();
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
	public void setSingleClientManager(SingleClientManager readOnlySingleClientManager)
	{
		this.singleClientManager = readOnlySingleClientManager;
	}

	@Override
	public ClientAdminPrivilege emptyInstance()
	{
		return new ClientAdminPrivilege();
	}
}