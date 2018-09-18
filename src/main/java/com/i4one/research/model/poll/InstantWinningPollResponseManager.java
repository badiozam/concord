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

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.instantwinnable.BaseInstantWinningManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class InstantWinningPollResponseManager extends BaseInstantWinningManager<PollResponseRecord, PollResponse, Poll> implements PollResponseManager
{
	private PollResponseManager pollResponseManager;

	@Override
	public User getUser(PollResponse item)
	{
		return item.getUser();
	}

	@Override
	public RecordTypeDelegator<?> getAttachee(PollResponse pollResponse)
	{
		return pollResponse.getPoll();
	}

	@Override
	public PollResponseManager getImplementationManager()
	{
		return getPollResponseManager();
	}

	@Override
	public Set<PollResponse> getAllPollResponses(Poll poll, PaginationFilter pagination)
	{
		return getPollResponseManager().getAllPollResponses(poll, pagination);
	}

	@Override
	protected void processAttached(ReturnType<PollResponse> retPollResponse, String methodName)
	{
		// We only process the instant wins if the poll was successfully processed for the first time
		//
		PollResponse pre = retPollResponse.getPre();
		PollResponse post = retPollResponse.getPost();

		if (
			(post.exists() && pre.equals(post)) 	||	// Previously played
			(!post.exists())				// Failed/Expired
		)
		{
			getLogger().debug("Instant wins not processed for " + retPollResponse.getPost().getPoll() + " and user " + retPollResponse.getPost().getUser(false) + " since the poll was previously played or expired");
		}
		else
		{
			super.processAttached(retPollResponse, methodName);
		}

	}

	public PollResponseManager getPollResponseManager()
	{
		return pollResponseManager;
	}

	@Autowired
	@Qualifier("research.TriggeredPollResponseManager")
	public void setPollResponseManager(PollResponseManager pollResponseManager)
	{
		this.pollResponseManager = pollResponseManager;
	}

}
