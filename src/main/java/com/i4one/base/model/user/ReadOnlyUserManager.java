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
import com.i4one.base.model.manager.BaseReadOnlyManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class ReadOnlyUserManager extends BaseReadOnlyManager<UserRecord,User> implements UserManager
{
	private UserManager userManager;

	@Override
	public Manager<UserRecord, User> getImplementationManager()
	{
		return getUserManager();
	}

	@Override
	public User authenticate(User item, SingleClient client)
	{
		return getUserManager().authenticate(item, client);
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
	public UserSettings getSettings(SingleClient client)
	{
		return getUserManager().getSettings(client);
	}

	@Override
	public ReturnType<UserSettings> updateSettings(UserSettings settings)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String generateVerificationCode(User user)
	{
		throw new UnsupportedOperationException("Read-only access.");
	}

	@Override
	public ReturnType<Boolean> verify(User user, String code)
	{
		throw new UnsupportedOperationException("Read-only access.");
	}

	@Override
	public boolean updatePassword(User user)
	{
		throw new UnsupportedOperationException("Read-only access.");
	}

	@Override
	public boolean optout(User user)
	{
		throw new UnsupportedOperationException("Read-only access.");
	}

	@Override
	public ReturnType<User> resetPassword(User user) throws Exception
	{
		throw new UnsupportedOperationException("Read-only access.");
	}

	@Override
	public ReturnType<User> processBirthday(User user, int forYear)
	{
		throw new UnsupportedOperationException("Read-only access.");
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
	@Qualifier("base.TriggeredUserManager")
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

}
