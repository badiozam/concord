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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.manager.instantwinnable.SimpleTerminableInstantWinnable;
import com.i4one.base.model.manager.triggerable.SimpleTerminableTriggerable;
import com.i4one.base.model.manager.instantwinnable.TerminableInstantWinnableClientType;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import com.i4one.base.model.manager.categorizable.BaseCategorizableTerminableSiteGroupType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.research.model.poll.category.PollCategory;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class Poll extends BaseCategorizableTerminableSiteGroupType<PollRecord, PollCategory> implements TerminableTriggerableClientType<PollRecord, Poll>, TerminableInstantWinnableClientType<PollRecord, Poll>
{
	static final long serialVersionUID = 42L;

	public static final int TYPE_SINGLEANSWER_RADIO = 0;
	public static final int TYPE_SINGLEANSWER_SELECT = 1;

	private transient Set<PollAnswer> pollAnswers;

	private final transient SimpleTerminableTriggerable<PollRecord, Poll> triggerable;
	private final transient SimpleTerminableInstantWinnable<PollRecord, Poll> instantWinnable;

	public Poll()
	{
		super(new PollRecord());

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}

	protected Poll(PollRecord delegate)
	{
		super(delegate);

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}

	@Override
	public void init()
	{
		super.init();

		if ( pollAnswers == null )
		{
			pollAnswers = new LinkedHashSet<>();
		}
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getIntro().isBlank() )
		{
			retVal.addError(new ErrorMessage("intro", "msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".intro.empty", "Intro cannot be empty", new Object[]{"item", this}));
		}

		if ( getDelegate().getOutro().isBlank() )
		{
			retVal.addError(new ErrorMessage("outro", "msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".outro.empty", "Outro cannot be empty", new Object[]{"item", this}));
		}

		if (getPollingStartTimeSeconds() > getPollingEndTimeSeconds() )
		{
			retVal.addError("pollingStartTimeSeconds", new ErrorMessage("pollingStartTimeSeconds", "msg.research.Poll.pollingTimeMismatch", "The polling start time ($item.pollingStartTimeSeconds) cannot be after the polling end time ($item.pollingEndTimeSeconds).", new Object[]{"item", this}));
		}

		if (getPollingStartTimeSeconds() < getStartTimeSeconds() )
		{
			retVal.addError("pollingStartTimeSeconds", new ErrorMessage("pollingStartTimeSeconds", "msg.research.Poll.pollingStartMismatch", "The polling start time ($item.pollingStartTimeSeconds) cannot be before the display start time ($item.startTimeSeconds).", new Object[]{"item", this}));
		}

		if (getPollingEndTimeSeconds() > getEndTimeSeconds() )
		{
			retVal.addError("pollingEndTimeSeconds", new ErrorMessage("pollingEndTimeSeconds", "msg.research.Poll.pollingEndMismatch", "The polling end time ($item.pollingEndTimeSeconds) cannot be after the display end time ($item.endTimeSeconds).", new Object[]{"item", this}));
		}

		retVal.merge(triggerable.validate());
		retVal.merge(instantWinnable.validate());

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		triggerable.setOverrides();
		instantWinnable.setOverrides();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		triggerable.actualizeRelations();
		instantWinnable.actualizeRelations();

		int answerCount = 0;
		for (PollAnswer answer : getPollAnswers() )
		{
			if ( !answer.getAnswer().isBlank() )
			{
				// The answer count is only incremented for those
				// answers that exist
				//
				answerCount++;
			}
		}

		setAnswerCount(answerCount);
	}

	public boolean isAvailableDuring(int startTimeSeconds, int endTimeSeconds)
	{
		return getPollingStartTimeSeconds() <= startTimeSeconds && getPollingEndTimeSeconds() >= endTimeSeconds;
	}

	public boolean isAvailableAt(int seconds)
	{
		return isAvailableDuring(seconds, seconds);
	}

	public boolean isAvailable()
	{
		int currTime = Utils.currentTimeSeconds();

		return isAvailableDuring(currTime, currTime);
	}

	public IString getIntro()
	{
		return getDelegate().getIntro();
	}

	public void setIntro(IString intro)
	{
		getDelegate().setIntro(intro);
	}

	public IString getOutro()
	{
		return getDelegate().getOutro();
	}

	public void setOutro(IString intro)
	{
		getDelegate().setOutro(intro);
	}

	public boolean getRandomize()
	{
		return getDelegate().getRandomize();
	}

	public void setRandomize(boolean randomize)
	{
		getDelegate().setRandomize(randomize);
	}

	public int getAnswerCount()
	{
		return getDelegate().getAnswercount();
	}

	public void setAnswerCount(int answerCount)
	{
		getDelegate().setAnswercount(answerCount);
	}

	public int getOrderWeight()
	{
		return getDelegate().getOrderweight();
	}

	public void setOrderWeight(int orderweight)
	{
		getDelegate().setOrderweight(orderweight);
	}

	public int getQuestionType()
	{
		return getDelegate().getQuestiontype();
	}

	public void setQuestionType(int questionType)
	{
		getDelegate().setQuestiontype(questionType);
	}

	public Date getPollingStartTime()
	{
		return Utils.toDate(getPollingStartTimeSeconds());
	}

	public String getPollingStartTimeString()
	{
		return getClient().toDateString(getPollingStartTimeSeconds(), getParseLocale());
	}

	public void setPollingStartTimeString(String startTimeStr) throws ParseException
	{
		int seconds = getClient().parseToSeconds(startTimeStr, getParseLocale());
		setPollingStartTimeSeconds(seconds);
	}

	public int getPollingStartTimeSeconds()
	{
		return getDelegate().getPollingstarttm();
	}

	public void setPollingStartTimeSeconds(int startTime)
	{
		getDelegate().setPollingstarttm(startTime);
	}

	public Date getPollingEndTime()
	{
		return Utils.toDate(getPollingEndTimeSeconds());
	}

	public String getPollingEndTimeString()
	{
		return getClient().toDateString(getPollingEndTimeSeconds(), getParseLocale());
	}

	public void setPollingEndTimeString(String endTimeStr) throws ParseException
	{
		int seconds = getClient().parseToSeconds(endTimeStr, getParseLocale());
		setPollingEndTimeSeconds(seconds);
	}

	public int getPollingEndTimeSeconds()
	{
		return getDelegate().getPollingendtm();
	}

	public void setPollingEndTimeSeconds(int endTime)
	{
		getDelegate().setPollingendtm(endTime);
	}

	public Set<PollAnswer> getPollAnswers()
	{
		return Collections.unmodifiableSet(pollAnswers);
	}

	public void setPollAnswers(Collection<PollAnswer> answers)
	{
		this.pollAnswers.clear();
		this.pollAnswers.addAll(answers);
	}

	@Override
	protected void copyFromInternal(RecordTypeDelegator<PollRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyFromInternal(right);
		if ( right instanceof Poll )
		{
			Poll rightPoll = (Poll)right;

			pollAnswers.clear();
			for ( PollAnswer pollAnswer : rightPoll.getPollAnswers() )
			{
				PollAnswer newAnswer = new PollAnswer();
				newAnswer.copyFrom(pollAnswer);
				newAnswer.setSer(0);

				pollAnswers.add(newAnswer);
			}
		}
	}

	@Override
	protected void copyOverInternal(RecordTypeDelegator<PollRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyOverInternal(right);
		if ( right instanceof Poll )
		{
			Poll rightPoll = (Poll)right;

			// If the right poll's answers aren't empty, then regardless
			// of our answers we'll want to copy the right's poll's answers
			//
			if ( !rightPoll.getPollAnswers().isEmpty() )
			{
				copyFromInternal(right);
			}
		}
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle() + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
	}

	@Override
	protected PollCategory initCategory(int categoryid)
	{
		PollCategory retVal = new PollCategory();
		retVal.setSer(categoryid);
		retVal.loadedVersion();

		return retVal;
	}

	@Override
	public Set<BalanceTrigger> getBalanceTriggers()
	{
		return triggerable.getBalanceTriggers();
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> balanceTriggers)
	{
		triggerable.setBalanceTriggers(balanceTriggers);
	}

	@Override
	public Set<ExclusiveBalanceTrigger<Poll>> getExclusiveBalanceTriggers()
	{
		return triggerable.getExclusiveBalanceTriggers();
	}

	@Override
	public Set<BalanceTrigger> getNonExclusiveBalanceTriggers()
	{
		return triggerable.getNonExclusiveBalanceTriggers();
	}

	@Override
	public Set<InstantWin> getInstantWins()
	{
		return instantWinnable.getInstantWins();
	}

	@Override
	public void setInstantWins(Collection<InstantWin> instantWins)
	{
		instantWinnable.setInstantWins(instantWins);
	}

	@Override
	public Set<ExclusiveInstantWin<Poll>> getExclusiveInstantWins()
	{
		return instantWinnable.getExclusiveInstantWins();
	}

	@Override
	public Set<InstantWin> getNonExclusiveInstantWins()
	{
		return instantWinnable.getNonExclusiveInstantWins();
	}
}
