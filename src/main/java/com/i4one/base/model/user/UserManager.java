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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.AuthenticationManager;
import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * This interface handles authorization, registration and queries on users
 * in the system.
 *
 * @author Hamid Badiozamani
 */
public interface UserManager extends PaginableManager<UserRecord,User>, AuthenticationManager<User>
{
	/**
	 * Generates and/or sends a verification code to the given user
	 * 
	 * @param user The user to generate the verification code for
	 * 
	 * @return The verification code generated and/or sent
	 */
	public String generateVerificationCode(User user);

	/**
	 * Verifies a user's account by matching the given code with the one
	 * that was generated and/or sent to the user.
	 * 
	 * @param user The user's account to be verified 
	 * @param code The verification code to use
	 * 
	 * @return True if the verification was successful, false otherwise
	 */
	public ReturnType<Boolean> verify(User user, String code);

	/**
	 * Update a user's password
	 * 
	 * @param user The user with the new password
	 * 
	 * @return Whether the update was successful or not
	 */
	public boolean updatePassword(User user);

	/**
	 * Opt out the user from receiving e-mails
	 * 
	 * @param user The user
	 * 
	 * @return Whether the optout was successful or not
	 */
	public boolean optout(User user);

	/**
	 * Send the user his/her password
	 * 
	 * @param user The user whose password to send/reset
	 * 
	 * @return The user record as retrieved from the database as well as a
	 * 	"resetPassword" mapping to the newly generated password
	 * 
	 * @throws java.lang.Exception 
	 */
	public ReturnType<User> resetPassword(User user) throws java.lang.Exception;

	/**
	 * Look up a user in the database using the information given in
	 * the parameter. Either the serial number is needed or the (client
	 * along with username and/or e-mail address.
	 *
	 * @param user The user information to use for look up.
	 *
	 * @return A new user object with the information of the user loaded or
	 * 	an empty user object if the user was not found
	 */
	public User lookupUser(User user);

	/**
	 * Test to see if a user exists in the database using the information given
	 * the parameter. Either the serial number is needed or the client
	 * along with username  and/or e-mail address
	 * 
	 * @param user The user information to use for look up.
	 * 
	 * @return True if the user exists, false otherwise
	 */
	public boolean existsUser(User user);

	/**
	 * Get all of the users that have signed up through a given client.
	 * Note that this method is different from getAllMembers(..) as this
	 * method is concerned with which client a user has signed up on, and
	 * getAllMembers(..) is concerned with which client has a user interacted
	 * with and is thus a member of.
	 * 
	 * @param client The client whose users we are to retrieve
	 * @param pagination The pagination/ordering information
	 * 
	 * @return All users that have signed up through a given client
	 */
	public Set<User> getAllUsers(SingleClient client, PaginationFilter pagination);

	/**
	 * Get all of the users that are members of a given client. A user is
	 * considered to be a member of a particular client if that user has a
	 * balance amount that references that client's default balance.
	 * 
	 * @param client The client whose users we are to retrieve
	 * @param pagination The pagination/ordering information
	 * 
	 * @return All users that belong to the given client.
	 */
	public Set<User> getAllMembers(SingleClient client, PaginationFilter pagination);

	/**
	 * Search for a set of users given a search criteria.
	 * 
	 * @param criteria The criteria to use when searching for users
	 * 
	 * @return A (potentially empty) set of users matching the criteria.
	 */
	public Set<User> search(UserSearchCriteria criteria);

	/**
	 * Process a given user's birthday. The user's birth day is
	 * double checked before processing happens. This method is
	 * idempotent on an annual basis.
	 * 
	 * @param user The user for which to process his/her birthday.
	 * @param forYear The year for which to process this user's birthday.
	 * 
	 * @return The result of having processed the user's birthday.
	 */
	public ReturnType<User> processBirthday(User user, int forYear);

	/**
	 * Get the settings for a given client.
	 * 
	 * @param client The client for which to retrieve the settings
	 * 
	 * @return The settings for the client
	 */
	public UserSettings getSettings(SingleClient client);

	/**
	 * Update the user settings.
	 * 
	 * @param settings The new settings to update
	 * 
	 * @return The result of the updated settings
	 */
	public ReturnType<UserSettings> updateSettings(UserSettings settings);
}
