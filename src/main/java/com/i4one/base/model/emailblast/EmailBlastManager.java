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
package com.i4one.base.model.emailblast;

import com.i4one.base.model.targeting.TargetListPagination;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface EmailBlastManager extends PaginableManager<EmailBlastRecord,EmailBlast>
{
	/**
	 * Get all e-mail blasts that have been scheduled to mature in the future.
	 * 
	 * @param client The client to which the e-mail blasts belong to
	 * @param asOf The time stamp to use as the current time
	 * @param pagination The pagination/sorting info
	 * 
	 * @return A (potentially empty) set of future items.
	 */
	public Set<EmailBlast> getFuture(SingleClient client, int asOf, PaginationFilter pagination);

	/**
	 * Get all e-mail blasts that are live.
	 * 
	 * @param client The client to which the e-mail blasts belong to
	 * @param asOf The time stamp to use as the current time
	 * @param pagination The pagination/sorting info
	 * 
	 * @return A (potentially empty) set of live items.
	 */
	public Set<EmailBlast> getLive(SingleClient client, int asOf, PaginationFilter pagination);

	/**
	 * Get all e-mail blasts that have finished sending.
	 * 
	 * @param client The client to which the e-mail blasts belong to
	 * @param asOf The time stamp to use as the current time
	 * @param pagination The pagination/sorting info
	 * 
	 * @return 
	 */
	public Set<EmailBlast> getCompleted(SingleClient client, int asOf, PaginationFilter pagination);

	/**
	 * Update the attributes of an e-mail blast but not its contents.
	 * 
	 * @param item The e-mail blast to update
	 * 
	 * @return The result of the update
	 */
	public ReturnType<EmailBlast> updateAttributes(EmailBlast item);

	/**
	 * Get the set of all users to whom this e-mail blast was targeted.
	 * 
	 * @param emailBlast The e-mail blast for which to get the users
	 * @param pagination The pagination/sorting info for the users
	 * 
	 * @return A list of targeted users.
	 */
	public Set<User> getTargetUsers(EmailBlast emailBlast, TargetListPagination pagination);
}
