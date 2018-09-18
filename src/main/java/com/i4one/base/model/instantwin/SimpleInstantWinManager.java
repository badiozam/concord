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
package com.i4one.base.model.instantwin;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.BaseSimpleTerminableManager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleInstantWinManager extends BaseSimpleTerminableManager<InstantWinRecord, InstantWin> implements InstantWinManager
{
	private UserManager userManager;

	@Override
	public InstantWin emptyInstance()
	{
		return new InstantWin();
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<InstantWin> clone(InstantWin item)
	{
		try
		{
			if ( item.exists() )
			{
				String currTimeStamp = String.valueOf(Utils.currentDateTime());
				IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				InstantWin retVal = new InstantWin();
				retVal.copyFrom(item);
	
				retVal.setSer(0);
				retVal.setTitle(workingTitle);
				retVal.setWinnerCount(0);
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

	@Override
	protected ReturnType<InstantWin> createInternal(InstantWin item)
	{
		updateUser(item);
		return super.createInternal(item);
	}

	@Override
	protected ReturnType<InstantWin> updateInternal(InstantWinRecord lockedRecord, InstantWin item)
	{
		// We can reduce the winner limit below what it currently is only if it doesn't
		// go below the total number of winners we have. We can also set the winner limit
		// to infinite (which is represented by 0).
		//
		if ( !item.isInfiniteWinners() &&
			item.getWinnerLimit() < lockedRecord.getWinnerlimit() &&
			item.getWinnerLimit() < lockedRecord.getWinnercount() )
		{
			throw new Errors(getInterfaceName() + ".update", new ErrorMessage("winnerLimit", "msg." + getInterfaceName() + ".update.invalidlimit", "The winner limit $item.winnerLimit is less than the existing winner count $winnerCount", new Object[] { "item", item, "winnerCount", lockedRecord.getWinnercount() }));
		}
		else
		{
			updateUser(item);
			return super.updateInternal(lockedRecord, item);
		}
	}

	private void updateUser(InstantWin item)
	{
		User user = item.getUser(false);
		if ( !Utils.isEmpty(user.getUsername()) )
		{
			user.setClient(item.getClient(false));
			User cherryPicked = getUserManager().lookupUser(user);
			if ( !cherryPicked.exists())
			{
				throw new Errors(getInterfaceName() + ".create", new ErrorMessage("userdne", "msg." + getInterfaceName() + ".update.userdne", "No user with the username $item.user.username was found", new Object[] { "item", item }));
			}
			else
			{
				item.setUser(cherryPicked);
			}
		}
	}

	@Override
	public Set<InstantWin> getAllInstantWinsByManager(Manager<?, ?> manager, String method, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByFeature(manager.getInterfaceName() + "." + method, 0, pagination));
	}

	@Override
	public Set<InstantWin> getAllInstantWinsByFeature(RecordTypeDelegator<?> item, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByFeature(item.getDelegate().getFullTableName(), item.getSer(), pagination));
	}

	@Override
	public List<ReturnType<InstantWin>> updateExclusiveInstantWins(RecordTypeDelegator<?> item, Collection<InstantWin> instantWins)
	{
		List<ReturnType<InstantWin>> retVal = new ArrayList<>();

		instantWins.stream()
			.filter((instantWin) -> { return instantWin.isExclusive(); } )
			.forEach( (instantWin) -> 
		{
			// First update or (or if non-existent, create) any details about the instantWin
			//
			boolean doAssociate;
			if ( instantWin.exists() )
			{
				if ( instantWin.getPercentWin() > 0.0f || instantWin.isCherryPick() )
				{
					ReturnType<InstantWin> updatedInstantWin = update(instantWin);
					retVal.add(updatedInstantWin);

					doAssociate = true;
				}
				else
				{
					remove(instantWin);
					doAssociate = false;
				}
			}
			else if ( instantWin.getPercentWin() > 0.0f || instantWin.isCherryPick() )
			{
				ReturnType<InstantWin> createdInstantWin = create(instantWin);
				retVal.add(createdInstantWin);

				doAssociate = true;
			}
			else
			{
				// Ignore impossible instant wins
				//
				doAssociate = false;
			}

			if ( doAssociate )
			{
				associate(item, instantWin);
			}
		});

		return retVal;
	}

	@Override
	public boolean associate(RecordTypeDelegator<?> item, InstantWin instantWin)
	{
		if ( !getDao().isAssociated(item.getFeatureName(), item.getSer(), instantWin.getSer()))
		{
			getDao().associate(item.getFeatureName(), item.getSer(), instantWin.getSer());
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean associate(Manager<?, ?> manager, String method, InstantWin instantWin)
	{
		if ( !getDao().isAssociated(buildManagerFeatureName(manager, method), 0, instantWin.getSer()))
		{
			getDao().associate(buildManagerFeatureName(manager, method), 0, instantWin.getSer());
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean dissociate(RecordTypeDelegator<?> item, InstantWin instantWin)
	{
		if ( getDao().isAssociated(item.getFeatureName(), item.getSer(), instantWin.getSer()))
		{
			getDao().dissociate(item.getFeatureName(), item.getSer(), instantWin.getSer());

			// If the instantWin no longer has any associations and is exclusive, we can remove it.
			//
			if ( instantWin.isExclusive() && !getDao().hasAssociations(instantWin.getSer()) )
			{
				getDao().deleteBySer(instantWin.getSer());
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean dissociate(Manager<?, ?> manager, String method, InstantWin instantWin)
	{
		if ( getDao().isAssociated(buildManagerFeatureName(manager, method), 0, instantWin.getSer()))
		{
			getDao().dissociate(buildManagerFeatureName(manager, method), 0, instantWin.getSer());
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public Set<RecordTypeDelegator<?>> getAllFeatureInstantWins(InstantWin instantWin)
	{
		return convertDelegatesToGenericFeatures(getDao().getByInstantWin(instantWin.getSer(), SimplePaginationFilter.NONE));
	}

	@Override
	public InstantWin initModelObject(InstantWin item)
	{
		getUserManager().loadById(item.getUser(), item.getUser(false).getSer());
		return item;
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager readOnlyUserManager)
	{
		// XXX: This needs better security
		//
		this.userManager = readOnlyUserManager;
	}

	protected String buildManagerFeatureName(Manager<?,?> manager, String method)
	{
		return manager.getInterfaceName() + "." + method;
	}

	@Override
	public InstantWinRecordDao getDao()
	{
		return (InstantWinRecordDao) super.getDao();
	}
}