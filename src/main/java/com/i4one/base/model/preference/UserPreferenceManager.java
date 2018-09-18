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
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface UserPreferenceManager extends ActivityManager<UserPreferenceRecord, UserPreference, Preference>
{
	/**
	 * Determines whether a given preference item has any activity associated
	 * with it or not.
	 * 
	 * @param preference The preference whose responses we're to look up
	 * 
	 * @return True if there is any activity, false otherwise
	 */
	public boolean hasUserPreferences(Preference preference);

	/**
	 * Retrieves the preference response for the given preference and user. If the user
	 * has not responded to the given preference an empty object is returned
	 * 
	 * @param preference The preference
	 * @param user The user
	 * 
	 * @return The (potentially empty) preference response object.
	 */
	public UserPreference getUserPreference(Preference preference, User user);

	/**
	 * Gets all of the responses associated with a given preference.
	 * 
	 * @param preference The preference for which all responses are to be returned
	 * @param pagination The pagination filtering for retrieving the responses
	 * 
	 * @return A (potentially empty) set of all responses for the given preference.
	 */
	public Set<UserPreference> getAllUserPreferences(Preference preference, PaginationFilter pagination);

	/**
	 * Gets all of the responses associated with a given user.
	 * 
	 * @param user The user for which all responses are to be returned
	 * @param pagination The pagination filtering for retrieving the responses
	 * 
	 * @return A (potentially empty) set of all responses for the given preference.
	 */
	public Set<UserPreference> getAllUserPreferences(User user, PaginationFilter pagination);

	/**
	 * Gets all of the responses associated with a given preference for a given user.
	 * 
	 * @param preference The preference for which all responses are to be returned,
	 * 	this should be a multi response question type.
	 * @param user The user for which all responses are to be returned
	 * @param pagination The pagination filtering for retrieving the responses
	 * 
	 * @return A (potentially empty) set of all responses for the given preference for the given user.
	 */
	public Set<UserPreference> getAllUserPreferences(Preference preference, User user, PaginationFilter pagination);

	/**
	 * Set a multitude of user preferences for a particular user at once.
	 * 
	 * @param user The user for who the preferences are to be set
	 * @param client The specific client for which the user's preference is
	 * 	being updated.
	 * @param userPreferences A mapping of preferences to the user's response
	 * 
	 * @return A list of the creation status of the given user preferences.
	 */
	public ReturnType<List<ReturnType<UserPreference>>> setUserPreferences(User user, SingleClient client, Map<Preference, List<UserPreference>> userPreferences);
}
