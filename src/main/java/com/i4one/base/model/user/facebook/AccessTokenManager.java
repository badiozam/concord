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
package com.i4one.base.model.user.facebook;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface AccessTokenManager
{
	/**
	 * Retrieve an access token from Facebook given a code from
	 * Facebook's OAuth interface
	 * 
	 * @param token The token with the code, redir and state parameters
	 * @param state A parameter returned by FB to verify the integrity of the request
	 * 
	 * @return A new access token from Facebook
	 */
	public AccessToken getByCode(AccessToken token, String state);

	/**
	 * Retrieve an access token given a Facebook user's ID
	 * 
	 * @param client The client which hosts the Facebook App
	 * @param fbId The Facebook user's ID
	 * 
	 * @return The previously stored (and potentially expired or empty) token
	 */
	public AccessToken getById(SingleClient client, String fbId);

	/**
	 * Retrieve an access token that matches the given user
	 * 
	 * @param client The client which hosts the Facebook App
	 * @param user The user to look up
	 * 
	 * @return The previously stored (and potentially expired or empty) token
	 */
	public AccessToken getByUser(SingleClient client, User user);

	/**
	 * Get a list of all access tokens for a given client that do not expire
	 * until the given time stamp.
	 * 
	 * @param client The client for which to check users
	 * @param expirationTimestamp The expiration time stamp cut off.
	 * @param pagination The ordering/pagination information
	 * 
	 * @return A (potentially empty) list of matching access tokens.
	 */
	public Set<AccessToken> getAllByClient(SingleClient client, int expirationTimestamp, PaginationFilter pagination);

	/**
	 * Save the given token in the database, thus connecting the facebook user ID
	 * with our internal ID. If the ID already exists, updates the value with the
	 * more recent access token. 
	 * 
	 * @param token The token to save
	 */
	public void connect(AccessToken token);
}
