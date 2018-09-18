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

import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.BalanceTriggerManager;
import com.i4one.base.model.manager.BaseDelegatingManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseTriggerableManager<U extends ClientRecordType, T extends TriggerableClientType<U, ? extends SingleClientType>> extends BaseDelegatingManager<U,T> implements TriggerableManager<U,T>
{
	private BalanceTriggerManager balanceTriggerManager;

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		ReturnType<T> retVal = getImplementationManager().create(item);
		updateExclusiveBalanceTriggers(item);

		// Update the set of balance triggers after this call to ensure that the caller has
		// the latest information
		//
		initModelObject(retVal.getPost());
		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		ReturnType<T> retVal = getImplementationManager().update(item);
		updateExclusiveBalanceTriggers(item);

		// Update the set of balance triggers after this call to ensure that the caller has
		// the latest information
		//
		initModelObject(retVal.getPost());
		return retVal;
	}

	protected void updateExclusiveBalanceTriggers(T item)
	{
		getLogger().debug("Updating " + item.getBalanceTriggers().size() + " balance triggers for " + item);

		getBalanceTriggerManager().updateExclusiveBalanceTriggers(item, item.getBalanceTriggers());
	}

	@Transactional(readOnly = false)
	@Override
	public T remove(T item)
	{
		// Dissociate all triggers having to do with this item since it's being
		// completely removed. The trigger itself may or may not be removed by the
		// trigger manager depending on whether or not there are other associations
		//
		Set<BalanceTrigger> triggers = getBalanceTriggerManager().getAllTriggersByFeature(item, SimplePaginationFilter.NONE);
		triggers.forEach( (trigger) ->
		{
			getBalanceTriggerManager().dissociate(item, trigger);
		});

		return initModelObject(getImplementationManager().remove(item));
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> clone(T item)
	{
		// First get the incoming item's triggers so we can clone them, too
		//
		Set<BalanceTrigger> triggers = getBalanceTriggerManager().getAllTriggersByFeature(item, SimplePaginationFilter.NONE);

		// Now we can clone the incoming item to get its serial no.
		//
		ReturnType<T> retVal = getImplementationManager().clone(item);

		// And clone the previous item's triggers and associated them with this one
		//
		List<ReturnType<BalanceTrigger>> clonedTriggers = new ArrayList<>();
		triggers.forEach((trigger) ->
		{
			BalanceTrigger toAssociate = trigger;

			// We have to clone any exclusive triggers since these belong to each
			// invidual item.
			//
			if ( trigger.isExclusive() )
			{
				// Make sure we have our serial number as part of the title instead of the item we're a clone of
				//
				trigger.setTitle(new IString("en", retVal.getPost().getFeatureName() + ":" + retVal.getPost().getSer()));

				ReturnType<BalanceTrigger> clonedTrigger = getBalanceTriggerManager().clone(trigger);
				clonedTriggers.add(clonedTrigger);

				toAssociate = clonedTrigger.getPost();
			}

			getBalanceTriggerManager().associate(retVal.getPost(), toAssociate);
		});

		//getBalanceTriggerManager().updateBalanceTriggers(item, clonedTriggers);

		retVal.addChain(getBalanceTriggerManager(), "clone", new ReturnType<>(clonedTriggers));
		return retVal;
	}

	@Override
	protected T initModelObject(T item)
	{
		item.setBalanceTriggers(getBalanceTriggerManager().getAllTriggersByFeature(item, SimplePaginationFilter.NONE));

		return item;
	}

	public BalanceTriggerManager getBalanceTriggerManager()
	{
		return balanceTriggerManager;
	}

	@Autowired
	public void setBalanceTriggerManager(BalanceTriggerManager balanceTriggerManager)
	{
		this.balanceTriggerManager = balanceTriggerManager;
	}
}