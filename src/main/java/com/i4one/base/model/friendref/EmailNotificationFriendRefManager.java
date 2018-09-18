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
package com.i4one.base.model.friendref;

import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseEmailNotificationManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.HashMap;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("base.FriendRefManager")
public class EmailNotificationFriendRefManager extends BaseEmailNotificationManager<FriendRefRecord, FriendRef> implements FriendRefManager
{
	private FriendRefManager friendRefManager;

	@Override
	public Manager<FriendRefRecord, FriendRef> getImplementationManager()
	{
		return getFriendRefManager();
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<FriendRef> create(FriendRef item)
	{
		ReturnType<FriendRef> retVal = getFriendRefManager().create(item);

		FriendRef friendRef = retVal.getPost();
		User user = friendRef.makeOrGetFriend();

		try
		{
			user.setCanEmail(true);

			HashMap<String, Object> subs = new HashMap<>();
			subs.put("request", getRequestState().getRequest());
			subs.put("friendRef", friendRef);
			getEmailManager().sendEmail(getRequestState().getSingleClient(), user, "emails.base.friendref", subs);

			return retVal;
		}
		catch (Exception ex)
		{
			throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".create.emailError", "Could not send e-mail to $user.email", new Object[] { "user", user }, ex));
		}
	}

	@Override
	public Set<FriendRef> getFriendsByUser(User user, PaginationFilter pagination)
	{
		return getFriendRefManager().getFriendsByUser(user, pagination);
	}

	@Override
	public FriendRef getReferral(int id, User user)
	{
		return getFriendRefManager().getReferral(id, user);
	}

	@Override
	public FriendRef getReferrer(User user)
	{
		return getFriendRefManager().getReferrer(user);
	}

	@Override
	public ReturnType<FriendRef> processReferral(User newUser)
	{
		return getFriendRefManager().processReferral(newUser);
	}

	@Override
	public FriendRefSettings getSettings(SingleClient client)
	{
		return getFriendRefManager().getSettings(client);
	}

	@Override
	public ReturnType<FriendRefSettings> updateSettings(FriendRefSettings settings)
	{
		return getFriendRefManager().updateSettings(settings);
	}

	@Override
	public PaginableRecordTypeDao<FriendRefRecord> getDao()
	{
		return getFriendRefManager().getDao();
	}

	public FriendRefManager getFriendRefManager()
	{
		return friendRefManager;
	}

	@Autowired
	public void setFriendRefManager(FriendRefManager transactionalFriendRefManager)
	{
		this.friendRefManager = transactionalFriendRefManager;
	}
}
