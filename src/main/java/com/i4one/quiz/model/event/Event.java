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
package com.i4one.quiz.model.event;

import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.manager.categorizable.BaseCategorizableTerminableSiteGroupType;
import com.i4one.base.model.manager.instantwinnable.SimpleTerminableInstantWinnable;
import com.i4one.base.model.manager.instantwinnable.TerminableInstantWinnableClientType;
import com.i4one.base.model.manager.triggerable.SimpleTerminableTriggerable;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import com.i4one.quiz.model.category.QuizCategory;
import com.i4one.quiz.model.player.questionbank.QuestionBank;
import java.util.Collection;
import java.util.Set;

/**
 * This class represents a prediction event
 *
 * @author Hamid Badiozamani
 */
public class Event extends BaseCategorizableTerminableSiteGroupType<EventRecord, QuizCategory> implements TerminableTriggerableClientType<EventRecord, Event>, TerminableInstantWinnableClientType<EventRecord, Event>
{
	static final long serialVersionUID = 42L;

	private transient QuestionBank questionBank;

	private final transient SimpleTerminableTriggerable<EventRecord, Event> triggerable;
	private final transient SimpleTerminableInstantWinnable<EventRecord, Event> instantWinnable;

	public Event()
	{
		super(new EventRecord());

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}

	protected Event(EventRecord delegate)
	{
		super(delegate);

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}

	@Override
	protected void init()
	{
		super.init();
	
		if ( questionBank == null )
		{
			questionBank = new QuestionBank();
		}
		questionBank.resetDelegateBySer(getDelegate().getQuestionbankid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();
	}

	public boolean isPlayable(int asOf)
	{
		return exists() && isLive(asOf);
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

	public void setOutro(IString outro)
	{
		getDelegate().setOutro(outro);
	}

	public QuestionBank getQuestionBank()
	{
		return questionBank;
	}

	public void setQuestionBank(QuestionBank questionBank)
	{
		this.questionBank = questionBank;
	}

	@Override
	protected QuizCategory initCategory(int categoryid)
	{
		QuizCategory retVal = new QuizCategory();
		retVal.setSer(categoryid);
		retVal.loadedVersion();

		return retVal;
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle() + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
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
	public Set<ExclusiveBalanceTrigger<Event>> getExclusiveBalanceTriggers()
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
	public Set<ExclusiveInstantWin<Event>> getExclusiveInstantWins()
	{
		return instantWinnable.getExclusiveInstantWins();
	}

	@Override
	public Set<InstantWin> getNonExclusiveInstantWins()
	{
		return instantWinnable.getNonExclusiveInstantWins();
	}
}
