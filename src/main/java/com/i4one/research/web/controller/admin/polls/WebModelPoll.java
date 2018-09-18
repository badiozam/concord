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
package com.i4one.research.web.controller.admin.polls;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.web.controller.TerminableInstantWinnableWebModel;
import com.i4one.base.web.controller.TerminableTriggerableWebModel;
import com.i4one.research.model.poll.Poll;
import com.i4one.research.model.poll.PollAnswer;
import com.i4one.research.model.poll.PollRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.AutoPopulatingList;
import org.springframework.util.AutoPopulatingList.ElementFactory;

/**
 * @author Hamid Badiozamani
 */
public class WebModelPoll extends Poll implements ElementFactory<PollAnswer>
{
	public static final int DEFAULT_POLL_ANSWER_SIZE = 6;

	private final transient TerminableTriggerableWebModel<PollRecord,Poll> triggerable;
	private final transient TerminableInstantWinnableWebModel<PollRecord,Poll> instantWinning;

	private transient List<PollAnswer> answers;

	public WebModelPoll()
	{
		super();

		triggerable = new TerminableTriggerableWebModel<>(this);
		instantWinning = new TerminableInstantWinnableWebModel<>(this);

		answers = new AutoPopulatingList<>(this);
	}

	protected WebModelPoll(PollRecord delegate)
	{
		super(delegate);

		triggerable = new TerminableTriggerableWebModel<>(this);
		instantWinning = new TerminableInstantWinnableWebModel<>(this);

		answers = new AutoPopulatingList<>(this);
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		retVal = triggerable.validateModel(retVal);
		retVal = instantWinning.validateModel(retVal);

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		triggerable.setModelOverrides();
		instantWinning.setModelOverrides();

		// Since we're managing the answers as well we need to ensure that
		// their overrides are also set
		//
		List<PollAnswer> filteredAnswers = getFilteredAnswers();
		filteredAnswers.forEach( (pollAnswer) ->
		{
			pollAnswer.setPoll(this);

			// No need to set the overrides of the answers here since
			// that's taken care of by the manager when it creates/updates
			// each answer
			//
			// pollAnswer.setOverrides();
		});

		setPollAnswers(filteredAnswers);

		// We have to set the overrides here after the balance triggers have been
		// set to our filtered results
		//
		super.setOverrides();
	}

	protected List<PollAnswer> getFilteredAnswers()
	{
		return getAnswers().stream()
			.filter( (answer) -> { return answer != null; } )

			// Blank answers are to be handled by the manager (i.e. either removed or ignored)
			//
			//.filter( (answer) -> { return answer.exists() || !answer.getAnswer().isBlank(); } )
			.collect(Collectors.toList());
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> newTriggers)
	{
		super.setBalanceTriggers(newTriggers);

		triggerable.setBalanceTriggers(newTriggers);
	}

	@Override
	public void setInstantWins(Collection<InstantWin> newInstantWins)
	{
		super.setInstantWins(newInstantWins);

		instantWinning.setInstantWins(newInstantWins);
	}

	@Override
	public void setPollAnswers(Collection<PollAnswer> newAnswers)
	{
		super.setPollAnswers(newAnswers);

		// We have to force a reload of the answers since they may have
		// changed in the database
		//
		answers.clear();
	}

	public TerminableTriggerableWebModel<PollRecord, Poll> getTriggerable()
	{
		return triggerable;
	}

	public TerminableInstantWinnableWebModel<PollRecord, Poll> getInstantWinning()
	{
		return instantWinning;
	}

	public List<PollAnswer> getAnswers()
	{
		if ( answers == null || answers.isEmpty() )
		{
			getLogger().debug("getAnswers() initializing answers from " + Utils.toCSV(getPollAnswers()));

			Collection<PollAnswer> pollAnswers = getPollAnswers();
			List<PollAnswer> backingList = new ArrayList<>(pollAnswers);

			answers = new AutoPopulatingList<>(backingList, this);

			// This ensures there is always one element that can be added to a poll
			//
			answers.get(backingList.size());
		}

		// This creates the elements up to this point if they didn't already exist
		//
		answers.get(DEFAULT_POLL_ANSWER_SIZE - 1);

		return answers;
	}

	public void setAnswers(List<PollAnswer> answers)
	{
		this.answers = answers;
	}

	@Override
	public PollAnswer createElement(int index) throws AutoPopulatingList.ElementInstantiationException
	{
		return new WebModelPollAnswer();
	}
}