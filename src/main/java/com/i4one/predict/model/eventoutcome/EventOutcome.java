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
package com.i4one.predict.model.eventoutcome;

import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.i18n.IString;
import com.i4one.predict.model.event.Event;

/**
 * @author Hamid Badiozamani
 */
public class EventOutcome extends BaseRecordTypeDelegator<EventOutcomeRecord>
{
	static final long serialVersionUID = 42L;

	private transient Event event;

	public EventOutcome()
	{
		super(new EventOutcomeRecord());
	}

	protected EventOutcome(EventOutcomeRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		if ( event == null )
		{
			event = new Event();
		}
		event.resetDelegateBySer(getDelegate().getEventid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setEvent(getEvent());
	}

	public Event getEvent()
	{
		return getEvent(true);
	}

	public Event getEvent(boolean doLoad)
	{
		if ( doLoad )
		{
			event.loadedVersion();
		}

		return event;
	}

	public void setEvent(Event event)
	{
		this.event = event;
		getDelegate().setEventid(event.getSer());
	}

	public float getLikelihood()
	{
		return getDelegate().getLikelihood();
	}

	public void setLikelihood(float likelihood)
	{
		getDelegate().setLikelihood(likelihood);
	}

	public float getBaseline()
	{
		return getDelegate().getBaseline();
	}

	public void setBaseline(float baseline)
	{
		getDelegate().setBaseline(baseline);
	}

	public int getUpdateTimeSeconds() 
	{
		return getDelegate().getUpdatetime();
	}
	
	public void setUpdateTimeSeconds(int updateTime)
	{
		getDelegate().setUpdatetime(updateTime);
	}

	public int getUsageCount()
	{
		return getDelegate().getUsagecount();
	}

	public void setUsageCount(int usageCount)
	{
		getDelegate().setUsagecount(usageCount);
	}

	public boolean isLockLikelihood()
	{
		return getDelegate().getLocklikelihood();
	}

	public void setLockLikelihood(boolean lockLikelihood)
	{
		getDelegate().setLocklikelihood(lockLikelihood);
	}

	public Boolean isActualized()
	{
		return getDelegate().getActualized();
	}

	public void setAcutalized(Boolean actualized)
	{
		getDelegate().setActualized(actualized);
	}

	public float getRoi()
	{
		if ( getLikelihood() > 0 )
		{
			return 1 / getLikelihood();
		}
		else
		{
			return 0.0f;
		}
	}

	public IString getDescr()
	{
		return getDelegate().getDescr();
	}

	public void setDescr(IString descr)
	{
		getDelegate().setDescr(descr);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getEventid() + "-" + getDescr();
	}
}
