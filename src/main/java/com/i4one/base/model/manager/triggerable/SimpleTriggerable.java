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
package com.i4one.base.model.manager.triggerable;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.BaseAttachableClientType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class SimpleTriggerable<U extends ClientRecordType, T extends SingleClientType<U>> extends BaseAttachableClientType<U,T> implements TriggerableClientType<U,T>
{
	private final Set<BalanceTrigger> triggers;
	private final Set<ExclusiveBalanceTrigger<T>> exclusiveBalanceTriggers;
	private final Set<BalanceTrigger> nonExclusiveBalanceTriggers;

	public SimpleTriggerable(T forward)
	{
		super(forward);

		// We should always have at least one trigger, consider
		// adding an ExtensibleSet<T> class which takes as its
		// argument a Set and expands that set by one item if
		// it's called upon to reference that nth element
		//
		triggers = new LinkedHashSet<>();
		exclusiveBalanceTriggers = new LinkedHashSet<>();
		nonExclusiveBalanceTriggers = new LinkedHashSet<>();

		initAttachables();
	}

	protected ExclusiveBalanceTrigger<T> newExclusiveBalanceTrigger()
	{
		return new ExclusiveBalanceTrigger<>(getForward());
	}

	private void initAttachables()
	{
		initBalanceTriggers();
	}

	private void initBalanceTriggers()
	{
		exclusiveBalanceTriggers.clear();
		nonExclusiveBalanceTriggers.clear();

		getBalanceTriggers().stream().forEach( (currTrigger) ->
		{
			if ( currTrigger.isExclusive() )
			{
				// We do this to ensure the right class behavior happens. For example, to
				// instantiate a TerminableExclusiveBalanceTrigger or just an
				// ExclusiveBalanceTrigger.
				//
				ExclusiveBalanceTrigger<T> exclusiveTrigger = newExclusiveBalanceTrigger();

				// If the types are the same, we can skip this step entirely
				//
				if ( !exclusiveTrigger.getClass().equals(currTrigger.getClass()) )
				{
					exclusiveTrigger.fromTrigger(currTrigger);
				}
				else
				{
					exclusiveTrigger = (ExclusiveBalanceTrigger<T>)currTrigger;
				}

				exclusiveBalanceTriggers.add(exclusiveTrigger);
			}
			else
			{
				nonExclusiveBalanceTriggers.add(currTrigger);
			}
		});

		triggers.clear();
		triggers.addAll(exclusiveBalanceTriggers);
		triggers.addAll(nonExclusiveBalanceTriggers);
	}

	@Override
	protected final Errors validateInternal()
	{
		Errors retVal = super.validateInternal();

		validateBalances(retVal);

		return retVal;
	}

	private void validateBalances(Errors retVal)
	{
		// Collect the balances in play in order to detect duplicates
		//
		Set<Balance> balances = new HashSet<>();

		initBalanceTriggers();

		// We're only concerend with the exclusive triggers since we can't update the
		// non-exclusive ones here
		//
		int i = 0;
		for ( BalanceTrigger trigger : getExclusiveBalanceTriggers() )
		{
			String currKey = "exclusiveBalanceTriggers[" + i + "]";
			if ( balances.add(trigger.getBalance()) )
			{
				// Include any errors with the trigger itself
				//
				retVal.merge(currKey, validateTrigger(trigger));
			}
			else if ( trigger.getAmount() != 0 )
			{
				// Only add this error if the trigger isn't being removed (by virtue of having a 0 amount)
				//
				retVal.addError(currKey, new ErrorMessage(currKey + ".balance", "msg.base.Triggerable.exclusiveBalanceTriggers.collision", "There is already a trigger for the Balance $trigger.balance defined", new Object[]{"item", this, "trigger", trigger}));
			}
			
			i++;
		}

	}

	protected Errors validateTrigger(BalanceTrigger trigger)
	{
		Errors retVal = trigger.validate();

		// Make sure the trigger's clients belong to this item's
		//
		if ( !belongsTo(trigger.getClient()) )
		{
			retVal.addError(new ErrorMessage("client", "msg.base.Triggerable.exclusiveBalanceTriggers.clientMismatch", "The trigger $trigger does not belong to the same group of clients as this item", new Object[]{"item", this, "trigger", trigger}));
		}

		return retVal;

	}

	@Override
	public void setOverridesInternal()
	{
		super.setOverridesInternal();

		// Exclusive balances are to be created and removed alongside their owner
		// which is why their overrides must take effect
		//
		getExclusiveBalanceTriggers().forEach( (trigger) -> { trigger.setOverrides(); });
	}

	@Override
	public boolean fromCSVInternal(List<String> csv)
	{
		if ( super.fromCSVInternal(csv) )
		{
			triggers.clear();

			int numTriggers = Utils.defaultIfNaN(csv.get(0), 0); csv.remove(0);

			boolean retVal = true;
			for ( int i = 0; i < numTriggers && retVal; i++ )
			{
				// Note that this ignores whether the item is terminable or
				// whether the trigger is exclusive!
				//
				BalanceTrigger trigger = new BalanceTrigger();
				retVal &= trigger.fromCSVList(csv);

				triggers.add(trigger);
			}

			initBalanceTriggers();

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

		if (header)
		{
			// XXX: Needs to be i18n
			retVal.append("# of Triggers").append(",");
		}
		else
		{
			retVal.append(triggers.size()).append(",");
		}


		for ( BalanceTrigger currTrigger : triggers )
		{
			retVal.append(currTrigger.toCSV(header));
		}

		return retVal;
	}

	@Override
	public Set<BalanceTrigger> getBalanceTriggers()
	{
		return Collections.unmodifiableSet(triggers);
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> balanceTriggers)
	{
		triggers.clear();
		triggers.addAll(balanceTriggers);

		initBalanceTriggers();
	}

	@Override
	public Set<ExclusiveBalanceTrigger<T>> getExclusiveBalanceTriggers()
	{
		return Collections.unmodifiableSet(exclusiveBalanceTriggers);
	}

	@Override
	public Set<BalanceTrigger> getNonExclusiveBalanceTriggers()
	{
		return Collections.unmodifiableSet(nonExclusiveBalanceTriggers);
	}
}
