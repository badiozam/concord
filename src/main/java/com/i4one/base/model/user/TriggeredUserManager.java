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
import com.i4one.base.model.manager.triggerable.BaseTriggeredManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.triggerable.TriggeredManager;
import com.i4one.base.model.balancetrigger.UserBalanceTrigger;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class TriggeredUserManager extends BaseTriggeredManager<UserRecord, User> implements UserManager,TriggeredManager<UserRecord, User>
{
	private UserManager userManager;

	@Transactional(readOnly = false)
	@Override
	public User authenticate(User user, SingleClient client)
	{
		User retVal = getUserManager().authenticate(user, client);

		// We only process if we were successfull at authenticating the user
		//
		if ( retVal.exists() )
		{
			getRequestState().setUser(retVal);
			List<ReturnType<UserBalanceTrigger>> processedTriggers = getUserBalanceTriggerManager().processTriggers(retVal,
				client,
				this,
				"authenticate",
				emptyInstance(),
				SimplePaginationFilter.NONE);
			//retVal.put("processedTriggers", processedTriggers);
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public String generateVerificationCode(User user)
	{
		return getUserManager().generateVerificationCode(user);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Boolean> verify(User user, String code)
	{
		ReturnType<Boolean> retVal = getUserManager().verify(user, code);

		// We only process if we were successfull at verifying the user
		//
		if ( retVal.getPost() )
		{
			List<ReturnType<UserBalanceTrigger>> processedTriggers = getUserBalanceTriggerManager().processTriggers(user,
				user.getClient(),
				this,
				"verify",
				emptyInstance(), SimplePaginationFilter.NONE);

			retVal.put("processedTriggers", processedTriggers);
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<User> processBirthday(User user, int forYear)
	{
		ReturnType<User> retVal = getUserManager().processBirthday(user, forYear);

		// We only process if we were successfull at verifying the user
		//
		if ( retVal.getPost().exists() )
		{
			List<ReturnType<UserBalanceTrigger>> processedTriggers = getUserBalanceTriggerManager().processTriggers(user,
				user.getClient(),
				this,
				"processBirthday",
				emptyInstance(), SimplePaginationFilter.NONE);

			retVal.put("processedTriggers", processedTriggers);
		}

		return retVal;
	}

	@Override
	public ReturnType<User> resetPassword(User user) throws Exception
	{
		return getUserManager().resetPassword(user);
	}

	@Override
	public Set<User> search(UserSearchCriteria criteria)
	{
		return getUserManager().search(criteria);
	}

	@Override
	public User lookupUser(User user)
	{
		return getUserManager().lookupUser(user);
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
		return getUserManager().optout(user);
	}

	@Override
	public boolean updatePassword(User user)
	{
		return getUserManager().updatePassword(user);
	}

	@Override
	public Set<User> getAllUsers(SingleClient client, PaginationFilter pagination)
	{
		return getUserManager().getAllUsers(client, pagination);
	}

	@Override
	public Set<User> getAllMembers(SingleClient client, PaginationFilter pagination)
	{
		return getUserManager().getAllMembers(client, pagination);
	}

	@Override
	public UserSettings getSettings(SingleClient client)
	{
		return getUserManager().getSettings(client);
	}

	@Override
	public ReturnType<UserSettings> updateSettings(UserSettings settings)
	{
		return getUserManager().updateSettings(settings);
	}

	@Override
	public User getUser(User user)
	{
		return user;
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	@Qualifier("base.SMSNotificationUserManager")
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	@Override
	public Manager<UserRecord, User> getImplementationManager()
	{
		return userManager;
	}

	@Override
	public PaginableRecordTypeDao<UserRecord> getDao()
	{
		return getUserManager().getDao();
	}
}
