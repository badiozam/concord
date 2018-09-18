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
package com.i4one.predict.model.eventprediction;

import com.i4one.base.model.BaseQuantifiedActivityType;
import com.i4one.base.model.SiteGroupActivityType;
import com.i4one.base.model.UsageType;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventRecord;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.term.Term;

/**
 * @author Hamid Badiozamani
 */
public class EventPrediction extends BaseQuantifiedActivityType<EventPredictionRecord, EventRecord, Event> implements UsageType<EventPredictionRecord>,SiteGroupActivityType<EventPredictionRecord, Event>
{
	static final long serialVersionUID = 42L;

	private transient Term term;
	private transient EventOutcome eventOutcome;

	public EventPrediction()
	{
		super(new EventPredictionRecord());
	}

	protected EventPrediction(EventPredictionRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( eventOutcome == null )
		{
			eventOutcome = new EventOutcome();
		}
		eventOutcome.resetDelegateBySer(getDelegate().getEventoutcomeid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setEventOutcome(getEventOutcome());
	}

	public int getPostedTimeSeconds()
	{
		return getDelegate().getPostedtm();
	}

	public void setPostedTimeSeconds(int postedTimeSeconds)
	{
		getDelegate().setPostedtm(postedTimeSeconds);
	}

	public float getPayout()
	{
		return getDelegate().getPayout();
	}

	public void setPayout(float payout)
	{
		getDelegate().setPayout(payout);
	}

	public Event getEvent()
	{
		return getActionItem();
	}

	public Event getEvent(boolean doLoad)
	{
		return getActionItem(doLoad);
	}

	public void setEvent(Event event)
	{
		setActionItem(event);
	}

	public EventOutcome getEventOutcome()
	{
		return getEventOutcome(true);
	}

	public EventOutcome getEventOutcome(boolean doLoad)
	{
		if ( doLoad )
		{
			eventOutcome.loadedVersion();
		}

		return eventOutcome;
	}

	public void setEventOutcome(EventOutcome eventOutcome)
	{
		this.eventOutcome = eventOutcome;
		getDelegate().setEventoutcomeid(eventOutcome.getSer());
	}

	public Term getTerm()
	{
		return getTerm(true);
	}

	public Term getTerm(boolean doLoad)
	{
		if ( doLoad )
		{
			term.loadedVersion();
		}

		return term;
	}

	public void setTerm(Term term)
	{
		this.term = term;
		getDelegate().setTermid(term.getSer());
	}

	public Boolean getCorrect()
	{
		if ( getDelegate().getCorrect() == null )
		{
			return null;
		}
		else
		{
			return getDelegate().getCorrect() != 0;
		}
	}

	public void setCorrect(boolean correct)
	{
		getDelegate().setCorrect( correct ? 1 : 0);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getUserid() + "-" + getDelegate().getEventoutcomeid() + "-" + getDelegate().getTimestamp();
	}

	@Override
	protected Event newActionItem()
	{
		return new Event();
	}
}