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
package com.i4one.base.model.balancetrigger;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface UserBalanceTriggerManager extends ActivityManager<UserBalanceTriggerRecord, UserBalanceTrigger, BalanceTrigger>
{
	/**
	 * Get all triggers by user
	 * 
	 * @param user The user whose triggers we are to look up
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return  The (potentially empty) list of triggers for the user
	 */
	public Set<UserBalanceTrigger> getByUser(User user, PaginationFilter pagination);

	/**
	 * Get users processed for a given trigger
	 * 
	 * @param balanceTrigger The trigger to look up
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return All instances of users that processed this trigger
	 */
	public Set<UserBalanceTrigger> getByBalanceTrigger(BalanceTrigger balanceTrigger, PaginationFilter pagination);

	/**
	 * Get all instances of the given trigger processed by the given user
	 * 
	 * @param balanceTrigger The trigger to look up
	 * @param user The user to look up
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return All instances of this trigger processed for this user
	 */
	public Set<UserBalanceTrigger> getAllUserBalanceTriggers(BalanceTrigger balanceTrigger, User user, PaginationFilter pagination);

	/**
	 * Determine whether a given user is eligible to process a given trigger
	 * 
	 * @param balanceTrigger The trigger to look up
	 * @param user The user to look up
	 * 
	 * @return True if the user is eligible, false otherwise
	 */
	public boolean isEligible(BalanceTrigger balanceTrigger, User user);

	/**
	 * Determine whether a given user is eligible to process a given trigger at a given time.
	 * 
	 * @param balanceTrigger The trigger to look up
	 * @param user The user to look up
	 * @param timestamp The time to use when determining eligibility
	 * 
	 * @return True if the user is eligible, false otherwise
	 */
	public boolean isEligibleAt(BalanceTrigger balanceTrigger, User user, int timestamp);

	/**
	 * Determine the number of seconds before a user is eligible to process a given trigger.
	 * Note that this method does not factor the usage limits of the given trigger.
	 * 
	 * @param balanceTrigger The trigger to look up
	 * @param user The user to look up
	 * 
	 * @return The number of seconds the user must wait to process the trigger, 0 or negative
	 * 	values indicate that the user is eligible.
	 */
	public int eligibleInSeconds(BalanceTrigger balanceTrigger, User user);

	/**
	 * Determine the number of seconds before a user is eligible to process a given trigger.
	 * Note that this method does not factor the usage limits of the given trigger.
	 * 
	 * @param balanceTrigger The trigger to look up
	 * @param user The user to look up
	 * @param timestamp The time stamp to use as the reference point
	 * 
	 * @return The number of seconds the user must wait to process the trigger, 0 or negative
	 * 	values indicate that the user is eligible.
	 */
	public int eligibleInSeconds(BalanceTrigger balanceTrigger, User user, int timestamp);

	/**
	 * Process the triggers associated with a given feature for a given user
	 * 
	 * @param user The user to process
	 * @param client The client making the trigger request
	 * @param manager The manager whose triggers we are to process
	 * @param method The method in the manager whose triggers we are to process
	 * @param item The item that the manager's method is operating on
	 * @param pagination The pagination/sort ordering information to use for
	 * 	retrieving the balance triggers
	 * 
	 * @return A (potentially empty) list of all of the trigger instances that were processed
	 */
	public List<ReturnType<UserBalanceTrigger>> processTriggers(User user, SingleClient client, Manager<?,?> manager, String method, RecordTypeDelegator<?> item, PaginationFilter pagination);
}
