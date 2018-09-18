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
package com.i4one.base.model.user;

import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedUserManager extends BaseUserPrivilegedManager<UserRecord, User> implements PrivilegedManager<UserRecord, User>,UserManager
{
	private UserManager userManager;

	@Override
	public User authenticate(User user, SingleClient client)
	{
		return getUserManager().authenticate(user, client);
	}

	@Override
	public String generateVerificationCode(User user)
	{
		return getUserManager().generateVerificationCode(user);
	}

	@Override
	public ReturnType<Boolean> verify(User user, String code)
	{
		return getUserManager().verify(user, code);
	}

	@Override
	public ReturnType<User> resetPassword(User user) throws Exception
	{
		// Anyone can send anyone else their password via e-mail
		//
		// checkRead(user, user, "resetPassword");
		return getUserManager().resetPassword(user);
	}

	@Override
	public Set<User> search(UserSearchCriteria criteria)
	{
		checkAdminRead(getRequestState().getSingleClient(), "search");
		return getUserManager().search(criteria);
	}

	@Override
	public User lookupUser(User user)
	{
		checkRead(user, user, "lookupUser");
		return getUserManager().lookupUser(user);
	}

	@Override
	public ReturnType<User> processBirthday(User user, int forYear)
	{
		checkAdminWrite(getRequestState().getSingleClient(), "processBirthday");
		return getUserManager().processBirthday(user, forYear);
	}

	@Override
	public boolean existsUser(User user)
	{
		// Anyone can see if another user exists or not
		//
		return getUserManager().existsUser(user);
	}

	@Override
	public boolean optout(User user)
	{
		// Anyone can opt out another user if they know their e-mail/username or serial number
		//
		return getUserManager().optout(user);
	}

	@Override
	public boolean updatePassword(User user)
	{
		checkWrite(user, getRequestState().getUser(), "updatePassword");
		return getUserManager().updatePassword(user);
	}

	@Override
	public ReturnType<User> create(User user)
	{
		getLogger().debug("Logging user in " + user + " for " + System.identityHashCode(getRequestState()) );
		getRequestState().setUser(user);

		// Anyone can create users because registration is free!
		//
		//checkWrite(item.getClient(), "create");
		ReturnType<User> retVal = getUserManager().create(user);

		/*
		 * This has to be set prior to our delegates create(..) since
		 * friend referral trickles will not have a valid user logged in.
		 *
		getLogger().debug("Setting user to " + retVal.getPost() + " for " + System.identityHashCode(getRequestState()) );
		getRequestState().setUser(retVal.getPost());
		*/

		return retVal;
	}

	@Override
	public ReturnType<User> update(User user)
	{
		// Updating a user requires admin privileges and/or being
		// the user himself
		//
		checkWrite(user, getUser(), "update");
		getLogger().debug("Updating " + user);

		return getUserManager().update(user);
	}

	@Override
	public User remove(User user)
	{
		checkWrite(user, getUser(), "remove");
		return getUserManager().remove(user);
	}

	@Override
	public Set<User> getAllUsers(SingleClient client, PaginationFilter pagination)
	{
		checkAdminRead(client, "getAllUsers");
		return getUserManager().getAllUsers(client, pagination);
	}

	@Override
	public Set<User> getAllMembers(SingleClient client, PaginationFilter pagination)
	{
		checkAdminRead(client, "getAllMembers");
		return getUserManager().getAllMembers(client, pagination);
	}

	@Override
	public UserSettings getSettings(SingleClient client)
	{
		checkAdminRead(client, "getSettings");
		return getUserManager().getSettings(client);
	}

	@Override
	public ReturnType<UserSettings> updateSettings(UserSettings settings)
	{
		checkAdminWrite(settings.getClient(), "updateSettings");
		return getUserManager().updateSettings(settings);
	}

	@Override
	public PaginableRecordTypeDao<UserRecord> getDao()
	{
		return getUserManager().getDao();
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager triggerableUserManager)
	{
		this.userManager = triggerableUserManager;
	}

	@Override
	public Manager<UserRecord, User> getImplementationManager()
	{
		return getUserManager();
	}

	@Override
	public SingleClient getClient(User user)
	{
		return getRequestState().getSingleClient();
	}

	@Override
	public User getUser(User item)
	{
		return item;
	}
}
