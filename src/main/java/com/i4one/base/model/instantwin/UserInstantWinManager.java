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
package com.i4one.base.model.instantwin;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface UserInstantWinManager extends ActivityManager<UserInstantWinRecord, UserInstantWin, InstantWin>
{
	/**
	 * Determine whether a given user is eligible to process a given trigger
	 * 
	 * @param instantWin The instant win to look up
	 * @param user The user to look up
	 * 
	 * @return True if the user has won this opportunity, false otherwise
	 */
	public boolean hasWon(InstantWin instantWin, User user);

	/**
	 * Get all instantWins by user
	 * 
	 * @param user The user whose instantWins we are to look up
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return  The (potentially empty) list of instantWins for the user
	 */
	public Set<UserInstantWin> getByUser(User user, PaginationFilter pagination);

	/**
	 * Get users processed for a given instantWin
	 * 
	 * @param instantWin The instantWin to look up
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return All instances of users that processed this instantWin
	 */
	public Set<UserInstantWin> getByInstantWin(InstantWin instantWin, PaginationFilter pagination);

	/**
	 * Get all instances of the given instantWin processed by the given user
	 * 
	 * @param instantWin The instantWin to look up
	 * @param user The user to look up
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return All instances of this instantWin processed for this user
	 */
	public Set<UserInstantWin> getAllUserInstantWins(InstantWin instantWin, User user, PaginationFilter pagination);

	/**
	 * Process the instantWins associated with a given feature for a given user
	 * 
	 * @param user The user to process
	 * @param manager The manager whose instantWins we are to process
	 * @param method The method in the manager whose instantWins we are to process
	 * @param item The item that the manager's method is operating on
	 * @param pagination The pagination/sort ordering information to use for
	 * 	retrieving the balance instantWins
	 * 
	 * @return A (potentially empty) list of all of the instantWin instances that were processed
	 */
	public List<ReturnType<UserInstantWin>> processInstantWins(User user, Manager<?,?> manager, String method, RecordTypeDelegator<?> item, PaginationFilter pagination);
}
