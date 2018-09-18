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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.BaseTransactionalActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.user.User;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("research.PollResponseManager")
public class TransactionalPollResponseManager extends BaseTransactionalActivityManager<PollResponseRecord, PollResponse, Poll> implements PollResponseManager
{
	private PollResponseManager pollResponseManager;

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

	protected void processPollTransaction(ReturnType<PollResponse> retPollResponse)
	{
		PollResponse pre = retPollResponse.getPre();
		PollResponse post = retPollResponse.getPost();

		Transaction t = newTransaction(post.getUser());

		if ( post.exists() && pre.equals(post))
		{
			// Previously played
			//
			t.setStatus(Transaction.FAILED);
		}

		// The message is responsible for displaying the proper previously played status
		//
		setTransactionDescr(t, "msg.pollResponseManager.create.xaction.descr", "pollResponse", post);

		// See if we can locate a user balance update transaction and set us
		// as the the parent transaction
		//
		t = createTransaction(retPollResponse, t);

		// Set our master transaction record since we've created a new transaction
		//
		retPollResponse.put("transaction", t);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<PollResponse> create(PollResponse pollResponse)
	{
		ReturnType<PollResponse> retVal = getPollResponseManager().create(pollResponse);

		processPollTransaction(retVal);

		return retVal;
	}

	public PollResponseManager getPollResponseManager()
	{
		return pollResponseManager;
	}

	@Autowired
	@Qualifier("research.InstantWinningPollResponseManager")
	public void setPollResponseManager(PollResponseManager pollResponseManager)
	{
		this.pollResponseManager = pollResponseManager;
	}
}
