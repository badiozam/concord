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
package com.i4one.base.model.balancetrigger;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.terminable.BaseSimpleTerminableManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleBalanceTriggerManager extends BaseSimpleTerminableManager<BalanceTriggerRecord, BalanceTrigger> implements BalanceTriggerManager
{
	@Override
	public BalanceTrigger emptyInstance()
	{
		return new BalanceTrigger();
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<BalanceTrigger> clone(BalanceTrigger item)
	{
		try
		{
			if ( item.exists() )
			{
				String currTimeStamp = String.valueOf(Utils.currentDateTime());
				IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				BalanceTrigger retVal = new BalanceTrigger();
				retVal.copyFrom(item);
	
				retVal.setSer(0);
				retVal.setTitle(workingTitle);
				retVal.setStartTimeSeconds(Utils.currentTimeSeconds() + (86400 * 365));
				retVal.setEndTimeSeconds(Utils.currentTimeSeconds() + (86400 * 366));
		
				return create(retVal);
			}
			else
			{
				throw new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.dne", "You are attempting to clone a non-existent item: $item", new Object[] { "item", item }));
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
		{
			Errors errors = new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.collision", "An item with the same qualifiers already exists: $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));

			throw errors;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<BalanceTrigger>> updateExclusiveBalanceTriggers(RecordTypeDelegator<?> item, Collection<BalanceTrigger> triggers)
	{
		List<ReturnType<BalanceTrigger>> retVal = new ArrayList<>();
		triggers.stream()
			.filter( (trigger) -> { return trigger.isExclusive(); } )
			.forEach( (trigger) ->
		{
			// First update or (or if non-existent, create) any details about the trigger
			//
			boolean doAssociate;
			if ( trigger.exists() && trigger.getAmount() > 0 )
			{
				ReturnType<BalanceTrigger> updatedTrigger = update(trigger);
				retVal.add(updatedTrigger);
				doAssociate = true;
			}
			else if ( trigger.getAmount() > 0 )
			{
				ReturnType<BalanceTrigger> createdTrigger = create(trigger);
				retVal.add(createdTrigger);
				doAssociate = true;
			}
			else if ( trigger.exists() && trigger.getAmount() <= 0 )
			{
				remove(trigger);
				doAssociate = false;
			}
			else
			{
				// Don't create a trigger with a zero amount
				//
				doAssociate = false;
			}

			if ( doAssociate )
			{
				associate(item, trigger);
			}
		});

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public boolean dissociate(RecordTypeDelegator<?> item, BalanceTrigger trigger)
	{
		if ( getDao().isAssociated(item.getFeatureName(), item.getSer(), trigger.getSer()))
		{
			getDao().dissociate(item.getFeatureName(), item.getSer(), trigger.getSer());

			// If the trigger no longer has any associations and is exclusive, we can remove it.
			//
			if ( trigger.isExclusive() && !getDao().hasAssociations(trigger.getSer()) )
			{
				getDao().deleteBySer(trigger.getSer());
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public boolean dissociate(Manager<?, ?> manager, String method, BalanceTrigger trigger)
	{
		if ( getDao().isAssociated(buildManagerFeatureName(manager, method), 0, trigger.getSer()))
		{
			getDao().dissociate(buildManagerFeatureName(manager, method), 0, trigger.getSer());
			return true;
		}
		else
		{
			return false;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public boolean associate(RecordTypeDelegator<?> item, BalanceTrigger trigger)
	{
		if ( !getDao().isAssociated(item.getFeatureName(), item.getSer(), trigger.getSer()))
		{
			getDao().associate(item.getFeatureName(), item.getSer(), trigger.getSer());
			return true;
		}
		else
		{
			return false;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public boolean associate(Manager<?, ?> manager, String method, BalanceTrigger trigger)
	{
		if ( !getDao().isAssociated(buildManagerFeatureName(manager, method), 0, trigger.getSer()))
		{
			getDao().associate(buildManagerFeatureName(manager, method), 0, trigger.getSer());
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public Set<BalanceTrigger> getAllTriggersByFeature(RecordTypeDelegator<?> item, PaginationFilter pagination)
	{
		getLogger().debug("getAllTriggersByFeature(..) called for " + item);
		return convertDelegates(getDao().getByFeature(item.getDelegate().getFullTableName(), item.getSer(), pagination));
	}

	@Override
	public Set<BalanceTrigger> getAllTriggersByManager(Manager<?, ?> manager, String method, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByFeature(manager.getInterfaceName() + "." + method, 0, pagination));
	}

	@Override
	public Set<RecordTypeDelegator<?>> getAllFeatureTriggers(BalanceTrigger trigger)
	{
		return convertDelegatesToGenericFeatures(getDao().getByTrigger(trigger.getSer(), SimplePaginationFilter.NONE));
	}

	protected String buildManagerFeatureName(Manager<?,?> manager, String method)
	{
		return manager.getInterfaceName() + "." + method;
	}

	@Override
	public BalanceTriggerRecordDao getDao()
	{
		return (BalanceTriggerRecordDao) super.getDao();
	}
}
