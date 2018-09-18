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
package com.i4one.base.model.preference;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseTransactionalActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.user.User;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("base.UserPreferenceManager")
public class TransactionalUserPreferenceManager extends BaseTransactionalActivityManager<UserPreferenceRecord, UserPreference, Preference> implements UserPreferenceManager
{
	private UserPreferenceManager userPreferenceManager;

	@Override
	public UserPreferenceManager getImplementationManager()
	{
		return getUserPreferenceManager();
	}

	@Override
	public Set<UserPreference> getAllUserPreferences(Preference preference, PaginationFilter pagination)
	{
		return getUserPreferenceManager().getAllUserPreferences(preference, pagination);
	}

	@Override
	public boolean hasUserPreferences(Preference preference)
	{
		return getUserPreferenceManager().hasUserPreferences(preference);
	}

	@Override
	public UserPreference getUserPreference(Preference preference, User user)
	{
		return getUserPreferenceManager().getUserPreference(preference, user);
	}

	@Override
	public Set<UserPreference> getAllUserPreferences(User user, PaginationFilter pagination)
	{
		return getUserPreferenceManager().getAllUserPreferences(user, pagination);
	}

	@Override
	public Set<UserPreference> getAllUserPreferences(Preference preference, User user, PaginationFilter pagination)
	{
		return getUserPreferenceManager().getAllUserPreferences(preference, user, pagination);
	}

	protected void processUserPreferenceTransaction(ReturnType<UserPreference> retUserPreference)
	{
		UserPreference pre = retUserPreference.getPre();
		UserPreference post = retUserPreference.getPost();

		getLogger().debug("Considering return type w/ pre = {} and post = {}", pre, post);

		// Only record a transaction if successful and if the item being
		// changed is different
		//
		if ( pre.exists() || post.exists() )
		{
			if ( pre.exists() && pre.getPreference().isOpenAnswer()
				&& Utils.forceEmptyStr(pre.getOpenAnswer()).equalsIgnoreCase(Utils.forceEmptyStr(post.getOpenAnswer())) )
			{
				getLogger().debug("The open answer for {} is the same as what's recorded, skipping transaction", pre.getPreference());
			}
			else if ( pre.exists() && !pre.getPreference().isOpenAnswer()
				&& post.exists()
				&& pre.getPreferenceAnswer(false).equals(post.getPreferenceAnswer(false)))
			{
				getLogger().debug("The single answer for {} is the same as what's recorded, skipping transaction", pre.getPreference());
			}
			else
			{
				User user = post.exists() ? post.getUser() : pre.getUser();
				Transaction t = newTransaction(user);

				// The message is responsible for displaying the proper previously played status
				//
				setTransactionDescr(t, "msg.userPreferenceManager.create.xaction.descr", "pre", pre, "post", post);

				// See if we can locate a user balance update transaction and set us
				// as the the parent transaction
				//
				t = createTransaction(retUserPreference, t);

				// Set our master transaction record since we've created a new transaction
				//
				retUserPreference.put("transaction", t);
			}
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<UserPreference> create(UserPreference userPreference)
	{
		ReturnType<UserPreference> retVal = getUserPreferenceManager().create(userPreference);

		processUserPreferenceTransaction(retVal);

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<UserPreference> update(UserPreference userPreference)
	{
		ReturnType<UserPreference> retVal = getUserPreferenceManager().update(userPreference);

		processUserPreferenceTransaction(retVal);

		return retVal;
	}

	@Override
	public ReturnType<List<ReturnType<UserPreference>>> setUserPreferences(User user, SingleClient client, Map<Preference, List<UserPreference>> userPreferences)
	{
		ReturnType<List<ReturnType<UserPreference>>> retVal = getUserPreferenceManager().setUserPreferences(user, client, userPreferences);

		List<ReturnType<UserPreference>> posts = retVal.getPost();

		Transaction t = newTransaction(user);
		if ( retVal.getPost().size() > 0 )
		{
			for ( ReturnType<UserPreference> post : posts )
			{
				processUserPreferenceTransaction(post);
			}

			// The message is responsible for displaying the proper previously played status
			//
			setTransactionDescr(t, "msg.userPreferenceManager.setuserpreferences.xaction.descr", "userPreferences", retVal.getPost());

			// Create the transaction setting this transaction
			// as the parent for all of the listed items
			//
			t = createTransaction(retVal, t);

			// Set our master transaction record since we've created a new transaction
			//
			retVal.put("transaction", t);
		}

		return retVal;
	}

	public UserPreferenceManager getUserPreferenceManager()
	{
		return userPreferenceManager;
	}

	@Autowired
	@Qualifier("base.CachedUserPreferenceManager")
	public void setUserPreferenceManager(UserPreferenceManager userPreferenceManager)
	{
		this.userPreferenceManager = userPreferenceManager;
	}
}
