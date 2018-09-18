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

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class SimplePollResultsManager extends BaseLoggable implements PollResultsManager
{
	private PollManager pollManager;
	private PollResponseManager pollResponseManager;

	private static final int PROCESS_N_RESPONSES = 500;

	@Override
	public PollResults computeResults(Poll poll)
	{
		PollResults results = new PollResults();

		if ( poll.exists() )
		{
			Poll dbPoll = getPollManager().getById(poll.getSer());
			results.setPoll(dbPoll);

			PaginationFilter pagination = new SimplePaginationFilter();
			pagination.setPerPage(PROCESS_N_RESPONSES);
			pagination.setCurrentPage(0);

			boolean hasMore = true;
			while (hasMore)
			{
				Set<PollResponse> responses = getPollResponseManager().getAllPollResponses(dbPoll, pagination);
				hasMore = !responses.isEmpty();
				pagination.setNextPage(true);

				// We can benefit from parallel processing here
				//
				responses.parallelStream().forEach((response) ->
				{
					results.tally(response);
				});
			}
		}

		return results;
	}

	public PollManager getPollManager()
	{
		return pollManager;
	}

	@Autowired
	public void setPollManager(PollManager pollManager)
	{
		this.pollManager = pollManager;
	}

	public PollResponseManager getPollResponseManager()
	{
		return pollResponseManager;
	}

	@Autowired
	public void setPollResponseManager(PollResponseManager pollResponseManager)
	{
		this.pollResponseManager = pollResponseManager;
	}

}
