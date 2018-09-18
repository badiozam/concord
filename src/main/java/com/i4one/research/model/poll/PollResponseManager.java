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
package com.i4one.research.model.poll;

import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface PollResponseManager extends ActivityManager<PollResponseRecord, PollResponse, Poll>
{
	/**
	 * Determines whether a given poll item has any activity associated
	 * with it or not.
	 * 
	 * @param poll The poll whose responses we're to look up
	 * 
	 * @return True if there is any activity, false otherwise
	 */
	@Override
	public boolean hasActivity(Poll poll);

	/**
	 * Gets all of the responses associated with a given poll.
	 * 
	 * @param poll The poll for which all responses are to be returned
	 * @param pagination The pagination filtering for retrieving the responses
	 * 
	 * @return A (potentially empty) set of all responses for the given poll.
	 */
	public Set<PollResponse> getAllPollResponses(Poll poll, PaginationFilter pagination);
}
