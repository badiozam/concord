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
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.BalanceTriggerManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseDelegatingManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class TriggerableUserManager extends BaseDelegatingManager<UserRecord, User> implements UserManager
{
	private BalanceTriggerManager balanceTriggerManager;
	private UserManager userManager;

	@Override
	public Manager<UserRecord, User> getImplementationManager()
	{
		return getUserManager();
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
	public boolean updatePassword(User user)
	{
		return getUserManager().updatePassword(user);
	}

	@Override
	public boolean optout(User user)
	{
		return getUserManager().optout(user);
	}

	@Override
	public ReturnType<User> resetPassword(User user) throws Exception
	{
		return getUserManager().resetPassword(user);
	}

	@Override
	public User lookupUser(User user)
	{
		return getUserManager().lookupUser(user);
	}

	@Override
	public boolean existsUser(User user)
	{
		return getUserManager().existsUser(user);
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
	public Set<User> search(UserSearchCriteria criteria)
	{
		return getUserManager().search(criteria);
	}

	@Override
	public ReturnType<User> processBirthday(User user, int forYear)
	{
		return getUserManager().processBirthday(user, forYear);
	}

	private void initSettings(UserSettings settings)
	{
		Set<BalanceTrigger> triggers = getBalanceTriggerManager().getAllTriggersByManager(this,
			"processBirthday",
			new ClientPagination(settings.getClient(), SimplePaginationFilter.NONE));

		getLogger().debug("Setting triggers to: " + triggers);

		settings.setBirthdayBalanceTriggers(triggers);
	}

	@Override
	public UserSettings getSettings(SingleClient client)
	{
		UserSettings settings = getUserManager().getSettings(client);
		initSettings(settings);

		return settings;
	}

	@Override
	public ReturnType<UserSettings> updateSettings(UserSettings settings)
	{
		ReturnType<UserSettings> retVal = getUserManager().updateSettings(settings);
		initSettings(retVal.getPost());

		return retVal;
	}

	@Override
	public PaginableRecordTypeDao<UserRecord> getDao()
	{
		return getUserManager().getDao();
	}

	@Override
	public User authenticate(User item, SingleClient client)
	{
		return getUserManager().authenticate(item, client);
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager triggeredUserManager)
	{
		this.userManager = triggeredUserManager;
	}

	public BalanceTriggerManager getBalanceTriggerManager()
	{
		return balanceTriggerManager;
	}

	@Autowired
	public void setBalanceTriggerManager(BalanceTriggerManager balanceTriggerManager)
	{
		this.balanceTriggerManager = balanceTriggerManager;
	}

}
