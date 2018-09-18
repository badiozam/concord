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

import com.i4one.base.model.BaseActivityType;
import com.i4one.base.model.SiteGroupActivityType;
import com.i4one.base.model.UsageType;

/**
 * @author Hamid Badiozamani
 */
public class PollResponse extends BaseActivityType<PollResponseRecord, PollRecord, Poll> implements UsageType<PollResponseRecord>,SiteGroupActivityType<PollResponseRecord, Poll>
{
	static final long serialVersionUID = 42L;

	protected transient PollAnswer pollAnswer;

	public PollResponse()
	{
		super(new PollResponseRecord());
	}

	protected PollResponse(PollResponseRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( pollAnswer == null )
		{
			pollAnswer = new PollAnswer();
		}
		pollAnswer.resetDelegateBySer(getDelegate().getAnswerid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setPollAnswer(getPollAnswer());
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getItemid() + "-" + getDelegate().getUserid();
	}

	public Poll getPoll()
	{
		return getActionItem();
	}

	public Poll getPoll(boolean doLoad)
	{
		return getActionItem(doLoad);
	}

	public void setPoll(Poll poll)
	{
		setActionItem(poll);
	}

	public PollAnswer getPollAnswer()
	{
		return getPollAnswer(true);
	}

	public PollAnswer getPollAnswer(boolean doLoad)
	{
		if ( doLoad )
		{
			pollAnswer.loadedVersion();
		}

		return pollAnswer;
	}

	public void setPollAnswer(PollAnswer pollAnswer)
	{
		this.pollAnswer = pollAnswer;
		getDelegate().setAnswerid(pollAnswer.getSer());
	}

	@Override
	protected Poll newActionItem()
	{
		return new Poll();
	}
}