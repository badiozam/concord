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
package com.i4one.promotion.model.event;

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
import com.i4one.promotion.model.event.category.EventCategory;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class Event extends BaseCategorizableTerminableSiteGroupType<EventRecord, EventCategory> implements TerminableTriggerableClientType<EventRecord, Event>, TerminableInstantWinnableClientType<EventRecord, Event>
{
	static final long serialVersionUID = 42L;

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
	public void init()
	{
		super.init();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getIntro().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".intro.empty", "Intro cannot be empty", new Object[]{"item", this}));
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
	}

	public IString getIntro()
	{
		return getDelegate().getIntro();
	}

	public void setIntro(IString intro)
	{
		getDelegate().setIntro(intro);
	}

	public int getOrderWeight()
	{
		return getDelegate().getOrderweight();
	}

	public void setOrderWeight(int orderweight)
	{
		getDelegate().setOrderweight(orderweight);
	}

	public String getDetailPicURL()
	{
		return getDelegate().getDetailpicurl();
	}

	public void setDetailPicURL(String detailpicurl)
	{
		getDelegate().setDetailpicurl(detailpicurl);
	}

	public String getSponsorURL()
	{
		return getDelegate().getSponsorurl();
	}

	public void setSponsorURL(String sponsorurl)
	{
		getDelegate().setSponsorurl(sponsorurl);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle() + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
	}

	@Override
	protected boolean equalsInternal(RecordTypeDelegator<EventRecord> right)
	{
		if ( right instanceof Event )
		{
			Event rightEvent = (Event)right;
			return getStartTimeSeconds() == rightEvent.getStartTimeSeconds() &&
				getEndTimeSeconds() == rightEvent.getEndTimeSeconds() &&
				getTitle().equals(rightEvent.getTitle());
		}
		else
		{
			return false;
		}
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		if  ( super.fromCSVInternal(csv) )
		{
			String intro = csv.get(0); csv.remove(0);

			setIntro(new IString(intro));

			boolean retVal = true;

			retVal &= triggerable.fromCSVList(csv);
			retVal &= instantWinnable.fromCSVList(csv);

			return retVal;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected StringBuilder toCSVInternal(boolean header)
	{
		StringBuilder retVal = super.toCSVInternal(header);

		if ( header )
		{
			// XXX: Needs to be i18n
			retVal.append(Utils.csvEscape("Intro")).append(",");
		}
		else
		{
			retVal.append("\"").append(Utils.csvEscape(getIntro().toString())).append("\",");
		}

		retVal.append(triggerable.toCSV(header));
		retVal.append(instantWinnable.toCSV(header));

		return retVal;
	}

	@Override
	protected EventCategory initCategory(int categoryid)
	{
		EventCategory retVal = new EventCategory();
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
