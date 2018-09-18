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

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.activity.BaseSiteGroupActivityPrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.User;
import java.util.Set;

/**
 * XXX: Not being used
 * @author Hamid Badiozamani
 */
public class PrivilegedPollResponseManager extends BaseSiteGroupActivityPrivilegedManager<PollResponseRecord, PollResponse, Poll> implements PollResponseManager
{
	private PollResponseManager pollResponseManager;

	@Override
	public User getUser(PollResponse item)
	{
		return item.getUser();
	}

	@Override
	public PollResponseManager getImplementationManager()
	{
		return getPollResponseManager();
	}

	@Override
	public SingleClient getClient(PollResponse item)
	{
		return item.getPoll().getClient();
	}

	@Override
	public Set<PollResponse> getAllPollResponses(Poll poll, PaginationFilter pagination)
	{
		// Everyone needs to be able to see poll results
		//
		return getPollResponseManager().getAllPollResponses(poll, pagination);
	}

	@Override
	public boolean hasActivity(Poll poll)
	{
		// Everyone needs to be able to see poll results
		//
		return getPollResponseManager().hasActivity(poll);
	}

	public PollResponseManager getPollResponseManager()
	{
		return pollResponseManager;
	}

	public void setPollResponseManager(PollResponseManager pollResponseManager)
	{
		this.pollResponseManager = pollResponseManager;
	}

	@Override
	public ActivityReport getReport(PollResponse item, TopLevelReport report, PaginationFilter pagination)
	{
		return getPollResponseManager().getReport(item, report, pagination);
	}

}
