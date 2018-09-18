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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.activity.BaseSiteGroupActivityPrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.User;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * XXX: Not being used
 * @author Hamid Badiozamani
 */
public class PrivilegedUserPreferenceManager extends BaseSiteGroupActivityPrivilegedManager<UserPreferenceRecord, UserPreference, Preference> implements UserPreferenceManager
{
	private UserPreferenceManager userPreferenceManager;

	@Override
	public User getUser(UserPreference item)
	{
		return item.getUser();
	}

	@Override
	public UserPreferenceManager getImplementationManager()
	{
		return getUserPreferenceManager();
	}

	@Override
	public SingleClient getClient(UserPreference item)
	{
		return item.getPreference().getClient();
	}

	@Override
	public Set<UserPreference> getAllUserPreferences(Preference preference, PaginationFilter pagination)
	{
		checkAdminRead(preference.getClient(), "getAll");
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
		UserPreference retVal = getUserPreferenceManager().getUserPreference(preference, user);

		// Make sure the incoming user had access rights to look up this preference
		//
		checkRead(retVal, getUser(retVal), "get");

		return retVal;
	}

	@Override
	public Set<UserPreference> getAllUserPreferences(User user, PaginationFilter pagination)
	{
		Set<UserPreference> retVal = getUserPreferenceManager().getAllUserPreferences(user, pagination);
		if ( !retVal.isEmpty() )
		{
			checkRead(retVal.iterator().next(), user, "get");
		}

		return retVal;
	}

	@Override
	public Set<UserPreference> getAllUserPreferences(Preference preference, User user, PaginationFilter pagination)
	{
		return getUserPreferenceManager().getAllUserPreferences(preference, user, pagination);
	}

	@Override
	public ReturnType<List<ReturnType<UserPreference>>> setUserPreferences(User user, SingleClient client, Map<Preference, List<UserPreference>> userPreferences)
	{
		return getUserPreferenceManager().setUserPreferences(user, client, userPreferences);
	}

	public UserPreferenceManager getUserPreferenceManager()
	{
		return userPreferenceManager;
	}

	@Autowired
	public void setUserPreferenceManager(UserPreferenceManager userPreferenceManager)
	{
		this.userPreferenceManager = userPreferenceManager;
	}

	@Override
	public ActivityReport getReport(UserPreference item, TopLevelReport report, PaginationFilter pagination)
	{
		return getUserPreferenceManager().getReport(item, report, pagination);
	}
}
