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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface FriendRefManager extends PaginableManager<FriendRefRecord, FriendRef>
{
	/**
	 * Get all of a given user's friends.
	 * 
	 * @param user The user whose friends to look up
	 * @param pagination The pagination/sorting to apply
	 * 
	 * @return A (potentially empty) set of friends belonging to the user
	 */
	public Set<FriendRef> getFriendsByUser(User user, PaginationFilter pagination);

	/**
	 * Get a specific friend referral request by e-mail or id. If the ID is
	 * unknown 0 can be used, if the e-mail is unknown null may be used.
	 * 
	 * @param id The serial number of the referral to look up
	 * @param user The referee to look up (based on e-mail, or name).
	 * 
	 * @return The referral for the given user or a non-existent object
	 * 	if not found
	 */
	public FriendRef getReferral(int id, User user);

	/**
	 * Get a particular user's referrer.
	 * 
	 * @param user The user whose referrer to look up.
	 * 
	 * @return The referral record containing the referrer of the user or
	 * 	a non-existent record
	 */
	public FriendRef getReferrer(User user);

	/**
	 * Process a referral. Any time a new user is registered, this method
	 * must be called to process any potential referrals. The following
	 * conditions must be satisfied for the referral to be processed:
	 * 
	 * <ol>
	 * 	<li>The feature is enabled</li>
	 * 	<li>The new user's e-mail address matches the referrer record</li>
	 * 	<li>The referral was not previously processed.</li>
	 * </ol>
	 * 
	 * @param newUser The new user that may have been referred by another user.
	 * 
	 * @return The update results of a processed referral or non-existent return
	 * 	value if no processing took place.
	 */
	public ReturnType<FriendRef> processReferral(User newUser);

	/**
	 * Get the settings for a given client.
	 * 
	 * @param client The client for which to retrieve the friend referral settings
	 * 
	 * @return The friend referral settings for the client
	 */
	public FriendRefSettings getSettings(SingleClient client);

	/**
	 * Update the friend referral settings.
	 * 
	 * @param settings The new settings to update
	 * 
	 * @return The result of the updated settings
	 */
	public ReturnType<FriendRefSettings> updateSettings(FriendRefSettings settings);
}
