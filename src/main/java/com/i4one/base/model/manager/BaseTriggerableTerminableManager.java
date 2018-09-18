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
package com.i4one.base.model.manager;

import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.dao.terminable.TerminableClientRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.triggerable.BaseTriggerableManager;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for managers that have triggerable and terminable objects. The terminable is handled by
 * an implementation manager, while the triggerable functionality is managed by the super-class.
 * 
 * @author Hamid Badiozamani
 */
public abstract class BaseTriggerableTerminableManager<U extends TerminableClientRecordType, T extends TerminableTriggerableClientType<U, ?>> extends BaseTriggerableManager<U, T> implements TriggerableTerminableManager<U, T>
{
	@Override
	public abstract TerminableManager<U, T> getImplementationManager();

	private BalanceManager balanceManager;

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		ReturnType<T> retVal = super.create(item);

		// Handled by TerminableExclusiveBalanceTrigger.setOverrides
		//
		//syncExclusiveBalanceTriggerTimes(retVal.getPost());

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		ReturnType<T> retVal = super.update(item);

		// Handled by TerminableExclusiveBalanceTrigger.setOverrides
		//
		//syncExclusiveBalanceTriggerTimes(retVal.getPost());

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> clone(T item)
	{
		ReturnType<T> retVal = super.clone(item);
		T cloned = retVal.getPost();

		Set<BalanceTrigger> triggers = getBalanceTriggerManager().getAllTriggersByFeature(cloned, SimplePaginationFilter.NONE);
		triggers.forEach( (trigger) ->
		{
			// We only alter the triggers that we know were cloned by us
			//
			if ( trigger.isExclusive() )
			{
				trigger.setStartTimeSeconds(item.getStartTimeSeconds());
				trigger.setEndTimeSeconds(item.getEndTimeSeconds());

				getBalanceTriggerManager().update(trigger);
			}
		});

		return retVal;
	}

	@Override
	public Set<T> getLive(TerminablePagination pagination)
	{
		return initSet(getImplementationManager().getLive(pagination));
	}

	@Override
	public Set<T> getByRange(TerminablePagination pagination)
	{
		return initSet(getImplementationManager().getByRange(pagination));
	}

	@Override
	public List<ReturnType<T>> importCSV(InputStream stream, Supplier<T> instantiator, Function<T,Boolean> preprocessor, Consumer<ReturnType<T>> postprocessor)
	{
		List<ReturnType<T>> imported = super.importCSV(stream, instantiator,
			(item) ->
				{
					if ( preprocessor.apply(item) )
					{
						// We use the default balance for a client if a given
						// trigger's balance ID is not specified.
						//
						Balance defaultBalance = getBalanceManager().getDefaultBalance(item.getClient());
						for ( BalanceTrigger currTrigger : item.getBalanceTriggers() )
						{
							if ( !currTrigger.getBalance().exists() )
							{
								currTrigger.setBalance(defaultBalance);
							}
						}

						return true;
					}
					else
					{
						return false;
					}
				},
			postprocessor.andThen( (item) ->
				{
					T createdItem = item.getPost();

					updateExclusiveBalanceTriggers(createdItem);
					initModelObject(createdItem);
				}
			));

		return imported;
	}

	@Override
	public TerminableClientRecordTypeDao<U> getDao()
	{
		return getImplementationManager().getDao();
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

}
