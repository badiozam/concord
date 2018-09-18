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
package com.i4one.base.model.manager.targetable;

import com.i4one.base.dao.activity.ActivityRecordType;
import com.i4one.base.model.ActivityType;
import com.i4one.base.model.Titled;
import com.i4one.base.model.targeting.BaseTargetListResolver;
import com.i4one.base.model.targeting.TargetListResolver;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.targeting.Target;
import com.i4one.base.model.user.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public abstract class BaseActivityTargetListResolver<U extends ActivityRecordType, T extends ActivityType<U, V>, V extends Titled<?>, AT extends ActivityTarget<T>> extends BaseTargetListResolver implements TargetListResolver
{
	@Override
	protected AT emptyInstance()
	{
		return emptyInstance(":0");
	}

	protected abstract AT emptyInstance(String key);

	protected abstract Manager<?, V> getParentManager();

	protected abstract ActivityManager<U, T, V> getActivityManager();

	@Override
	public boolean matches(Target target)
	{
		return (emptyInstance().getClass().isInstance(target));
	}

	@Override
	protected Target resolveKeyInternal(String key)
	{
		getLogger().debug("Resolving target w/ key {}", key);
		AT retVal = emptyInstance(key);
		Titled<?> parent = getParentManager().getById(retVal.getItemid());

		String lang = getRequestState().getLanguage();
		if ( parent.exists())
		{
			retVal.setTitle(parent.getTitle().get(lang));
		}
		else
		{
			retVal.setTitle(key);
		}

		return retVal;
	}

	@Override
	public IString getTitle(Target target)
	{
		IString retVal;
		if ( matches(target) )
		{
			AT targ = (AT)target;
			Titled<?> parent = getParentManager().getById(targ.getItemid());

			if ( parent.exists() )
			{
				retVal = parent.getTitle();
			}
			else
			{
				retVal = new IString("en", target.getKey());
			}
		}
		else
		{
			retVal = new IString("en", target.getKey());
		}

		return retVal;
	}

	@Override
	public boolean resolve(Target target, Set<User> users, int offset, int limit)
	{
		if ( matches(target) )
		{
			AT activityTarget = (AT)target;

			PaginationFilter pagination = new SimplePaginationFilter();

			pagination.setOffset(offset);
			pagination.setLimit(limit);

			V parent = getParentManager().getById(activityTarget.getItemid());
			users.addAll(getActivityManager().getAllActivity(parent, pagination)
				.stream()
				.map(ActivityType::getUser)
				.collect(Collectors.toSet()));

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean contains(Target target, User user)
	{
		if ( matches(target))
		{
			AT codeResponseTarget = (AT)target;
			V parent = getParentManager().getById(codeResponseTarget.getItemid());

			return getActivityManager().getActivity(parent, user).exists();
		}
		else
		{
			return false;
		}
	}
}
