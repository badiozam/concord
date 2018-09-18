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
package com.i4one.base.model.userlogin;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleUserLoginManager extends BaseActivityManager<UserLoginRecord, UserLogin, SingleClient> implements UserLoginManager
{
	/** Record granularity, 60 seconds for now. */
	private static final int MIN_COOLDOWN = 60;

	private SingleClientManager singleClientManager;

	@Override
	public UserLogin emptyInstance()
	{
		return new UserLogin();
	}

	@Transactional(readOnly = false)
	@Override
	protected ReturnType<UserLogin> createInternal(UserLogin userLogin)
	{
		UserLogin prevLogin = getUserLogin(userLogin.getClient(), userLogin.getUser(), getGranularTimeStamp(userLogin.getTimeStampSeconds()));

		if ( prevLogin.exists() )
		{
			// We want to make sure we have a lock on that record and go off of that locked item
			// for any checks. If the item didn't exist, there's no reason to lock.
			//
			UserLoginRecord lockedRecord = lock(prevLogin);
			prevLogin = new UserLogin(lockedRecord);

			// Update using the previous login's quantity and serial no.
			//
			userLogin.setSer(prevLogin.getSer());
			userLogin.setQuantity(prevLogin.getQuantity() + 1);

			// Note that at this point, since we haven't called userLogin.loadedVersion()
			// all of the previous data (i.e. timestamp) is still preserved

			return super.updateInternal(lockedRecord, userLogin);
		}
		else
		{
			userLogin.setQuantity(1);
			userLogin.setTimeStampSeconds(getGranularTimeStamp(userLogin.getTimeStampSeconds()));

			return super.createInternal(userLogin);
		}
	}

	@Override
	protected int getActivityTimeStampSeconds(UserLogin userLogin)
	{
		// The user login should have the correct time stamp set outside
		//
		return userLogin.getTimeStampSeconds();
	}

	/**
	 * Subdivides the current time into buckets that are MIN_COOLDOWN seconds
	 * wide and looks up the record using that time stamp. Therefore, the returned
	 * value may be MIN_COOLDOWN - 1 seconds before or after the input time stamp.
	 * 
	 * @return The granular time stamp value
	 */
	private int getGranularTimeStamp(int currTime)
	{
		int div = currTime / MIN_COOLDOWN;

		return MIN_COOLDOWN * div;
	}

	/**
	 * Gets the login record for the given user/client/timestamp combination.
	 * 
	 * @param client The client
	 * @param user The user
	 * @param timeStamp The time to look up
	 * 
	 * @return The user login record corresponding to the given parameters or an
	 * 	empty record if not found
	 */
	private UserLogin getUserLogin(SingleClient client, User user, int timeStamp)
	{
		// We're getting all records for the given time stamp, which is why we need
		// to limit it to just one return value. We expect there to only be one for
		// the given time stamp in the database.
		//
		List<UserLoginRecord> userLoginRecords = getDao().getAll(client.getSer(), user.getSer(), timeStamp, SimplePaginationFilter.SINGLE);

		if ( userLoginRecords.isEmpty() )
		{
			UserLogin retVal = new UserLogin();
			retVal.setUser(user);
			retVal.setClient(client);
			retVal.setTimeStampSeconds(timeStamp);

			return retVal;
		}
		else
		{
			return new UserLogin(userLoginRecords.iterator().next());
		}
	}

	@Override
	public ReturnType<UserLogin> update(UserLogin userLogin)
	{
		throw new UnsupportedOperationException("Can't update a user login.");
	}

	@Override
	public UserLoginRecordDao getDao()
	{
		return (UserLoginRecordDao) super.getDao();
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
}
