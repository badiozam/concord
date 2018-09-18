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
package com.i4one.predict.model.event;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.categorizable.BaseCategorizableTerminableSiteGroupType;
import com.i4one.base.model.manager.categorizable.CategorizableTerminableClientType;
import com.i4one.base.model.manager.expendable.SimpleExpendable;
import com.i4one.base.model.manager.expendable.TerminableExpendableClientType;
import com.i4one.base.model.manager.terminable.SimpleParsingTerminable;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents a prediction event
 *
 * @author Hamid Badiozamani
 */
public class Event extends BaseCategorizableTerminableSiteGroupType<EventRecord, EventCategory> implements CategorizableTerminableClientType<EventRecord, EventCategory>, TerminableExpendableClientType<EventRecord, Event>
{
	static final long serialVersionUID = 42L;

	private transient EventOutcome actualOutcome;
	private transient Set<EventOutcome> possibleOutcomes;

	private final transient SimpleExpendable<EventRecord, Event> expendable;

	public Event()
	{
		super(new EventRecord());

		expendable = new SimpleExpendable<>(this);
	}

	protected Event(EventRecord delegate)
	{
		super(delegate);

		expendable = new SimpleExpendable<>(this);
	}

	@Override
	protected void init()
	{
		super.init();

		// We defer initialization here to prevent inifinte recursion loops when the actualOutcome
		// creates a parent Event which creates an actualOutcome, etc.
		//
		/*
		if ( actualOutcome == null )
		{
			actualOutcome = new EventOutcome();
		}
		actualOutcome.resetDelegateBySer(getDelegate().getActualoutcomeid());
		*/

		if ( possibleOutcomes == null )
		{
			possibleOutcomes = new LinkedHashSet<>();
		}
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getBrief().isBlank())
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".brief.empty", "Question (HTML) cannot be empty", new Object[]{"item", this}));
		}

		if ( getDelegate().getPromo().isBlank())
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".promo.empty", "Question (Plain) cannot be empty", new Object[]{"item", this}));
		}

		if ( getDelegate().getReference().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".reference.empty", "Source reference cannot be empty", new Object[]{"item", this}));
		}

		retVal.merge(expendable.validate());

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		expendable.setOverrides();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setActualOutcome(getActualOutcome());

		expendable.actualizeRelations();
	}

	@Override
	public Set<BalanceExpense> getBalanceExpenses()
	{
		return expendable.getBalanceExpenses();
	}

	@Override
	public void setBalanceExpenses(Collection<BalanceExpense> balanceExpenses)
	{
		expendable.setBalanceExpenses(balanceExpenses);
	}

	public boolean isPlayable(int asOf)
	{
		return exists() && isLive(asOf) && getClosesBySeconds() >= asOf;
	}

	public IString getDescr()
	{
		return getDelegate().getDescr();
	}

	public void setDescr(IString descr)
	{
		getDelegate().setDescr(descr);
	}

	public IString getReference()
	{
		return getDelegate().getReference();
	}

	public void setReference(IString reference)
	{
		getDelegate().setReference(reference);
	}

	public IString getBrief()
	{
		return getDelegate().getBrief();
	}

	public void setBrief(IString brief)
	{
		getDelegate().setBrief(brief);
	}

	public IString getPromo()
	{
		return getDelegate().getPromo();
	}

	public void setPromo(IString promo)
	{
		getDelegate().setPromo(promo);
	}

	public int getUsageCount()
	{
		return getDelegate().getUsagecount();
	}

	public void setUsageCount(int usageCount)
	{
		getDelegate().setUsagecount(usageCount);
	}

	public EventOutcome getActualOutcome()
	{
		return getActualOutcome(true);
	}

	public EventOutcome getActualOutcome(boolean doLoad)
	{
		if ( actualOutcome == null )
		{
			actualOutcome = new EventOutcome();
		}

		if ( getDelegate().getActualoutcomeid() != null )
		{
			actualOutcome.resetDelegateBySer(getDelegate().getActualoutcomeid());
		}

		if ( doLoad )
		{
			actualOutcome.loadedVersion();
		}

		return actualOutcome;
	}

	public boolean isActualized()
	{
		return getActualOutcome(false).exists();
	}

	public Set<EventOutcome> getPossibleOutcomes()
	{
		return possibleOutcomes;
	}

	public void setPossibleOutcomes(Collection<EventOutcome> possibleOutcomes)
	{
		this.possibleOutcomes.clear();
		this.possibleOutcomes.addAll(possibleOutcomes);
		this.possibleOutcomes.forEach((outcome) -> {outcome.setEvent(this);});
	}

	@Override
	protected void copyFromInternal(RecordTypeDelegator<EventRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyFromInternal(right);
		if ( right instanceof Event )
		{
			Event rightEvent = (Event)right;

			possibleOutcomes.clear();
			for ( EventOutcome outcome : rightEvent.getPossibleOutcomes() )
			{
				EventOutcome newOutcome = new EventOutcome();
				newOutcome.copyFrom(outcome);
				newOutcome.setSer(0);

				possibleOutcomes.add(newOutcome);
			}
		}
	}

	@Override
	protected void copyOverInternal(RecordTypeDelegator<EventRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyOverInternal(right);
		if ( right instanceof Event )
		{
			Event rightEvent = (Event)right;

			// If the right event's outcomes aren't empty, then regardless
			// of our outcomes we'll want to copy the right's outcomes
			//
			if ( !rightEvent.getPossibleOutcomes().isEmpty() )
			{
				copyFromInternal(right);
			}
		}
	}

	/**
	 * Get a random possible outcome for the current event. Priority is given to
	 * outcomes that have a likelihood higher than zero.
	 * 
	 * @return A random possible outcome
	 */
	public EventOutcome getRandomPossibleOutcome()
	{
		if ( possibleOutcomes.isEmpty() )
		{
			return new EventOutcome();
		}
		else
		{
			List<EventOutcome> outcomes = possibleOutcomes.stream()
				.filter( (eventOutcome) -> { return (eventOutcome.getLikelihood() > 0.0f); } )
				.collect(Collectors.toList());

			Collections.shuffle(outcomes);

			return outcomes.iterator().next();
		}
	}

	public void setActualOutcome(EventOutcome actualOutcome)
	{
		if ( actualOutcome.exists() )
		{
			this.actualOutcome = actualOutcome;
			getDelegate().setActualoutcomeid(actualOutcome.getSer());
			getDelegate().setPostedtm(Utils.currentTimeSeconds());
		}
		else
		{
			getDelegate().setActualoutcomeid( null );
		}
	}

	public int getMinBid()
	{
		return getDelegate().getMinbid();
	}

	public void setMinBid(int minbid)
	{
		getDelegate().setMinbid(minbid);
	}

	public int getMaxBid()
	{
		return getDelegate().getMaxbid();
	}

	public void setMaxBid(int maxbid)
	{
		getDelegate().setMaxbid(maxbid);
	}

	@Override
	protected EventCategory initCategory(int categoryid)
	{
		EventCategory retVal = new EventCategory();
		retVal.setSer(categoryid);
		retVal.loadedVersion();

		return retVal;
	}

	public Date getPostsBy()
	{
		return Utils.toDate(getPostsBySeconds());
	}

	public String getPostsByString()
	{
		return SimpleParsingTerminable.toDateString(getPostsBySeconds(), getParseLocale(), getClient().getTimeZone());
	}

	public void setPostsByString(String postsByStr) throws ParseException
	{
		setPostsBySeconds(SimpleParsingTerminable.parseToSeconds(postsByStr, getParseLocale(), getClient().getTimeZone()));
	}

	public int getPostsBySeconds()
	{
		return getDelegate().getPostsby();
	}

	public void setPostsBySeconds(int postsBy)
	{
		getDelegate().setPostsby(postsBy);
	}

	public Date getClosesBy()
	{
		//return Utils.toDate(getClosesBySeconds());
		return getEndTime();
	}

	public String getClosesByString()
	{
		//return SimpleParsingTerminable.toDateString(getClosesBySeconds(), getParseLocale(), getClient().getTimeZone());
		return getEndTimeString();
	}

	public void setClosesByString(String closesByStr) throws ParseException
	{
		//setClosesBySeconds(SimpleParsingTerminable.parseToSeconds(closesByStr, getParseLocale(), getClient().getTimeZone()));
	}

	public int getClosesBySeconds()
	{
		//return getDelegate().getClosesby();
		return getEndTimeSeconds();
	}

	public void setClosesBySeconds(int closesBy)
	{
		//getDelegate().setClosesby(closesBy);
	}

	public Date getPostedTime()
	{
		return Utils.toDate(getPostedTimeSeconds());
	}

	public int getPostedTimeSeconds()
	{
		return getDelegate().getPostedtm();
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle() + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
	}
}
