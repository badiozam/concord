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
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface FriendRefRecordDao extends PaginableRecordTypeDao<FriendRefRecord>
{
	/**
	 * Returns a list of all friend referral's for the given user
	 * 
	 * @param userid The user for which to retrieve all past friend referrals
	 * @param pagination The qualifier/sorting info
	 * 
	 * @return A (potentially empty) list of friend referral requests.
	 */
	public List<FriendRefRecord> getByUserid(int userid, PaginationFilter pagination);

	/**
	 * Get a user's friend referral record.
	 * 
	 * @param friendid The user id to look up
	 * 
	 * @return The friend referral record of the user (or null if not found)
	 */
	public FriendRefRecord getByFriendid(int friendid);

	/**
	 * Get the friend referral request for the given e-mail.
	 * 
	 * @param email The e-mail to look up
	 * 
	 * @return  The friend referral request for the given e-mail or null if
	 * 	not found.
	 */
	public FriendRefRecord getByEmail(String email);
}
