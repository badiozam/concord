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
package com.i4one.base.model.targeting;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.user.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleTargetResolver extends BaseLoggable implements TargetResolver
{
	private final Map<String, TargetListResolver> resolvers;

	public SimpleTargetResolver()
	{
		resolvers = new HashMap<>();
	}

	@Override
	public boolean registerResolver(TargetListResolver listResolver)
	{
		if ( resolvers.containsKey(listResolver.getName()))
		{
			getLogger().debug("Skipping registration of target list resolver {} due to duplicate", listResolver.getName());
			return false;
		}
		else
		{
			getLogger().debug("Adding target list resolver {}", listResolver.getName());
			resolvers.put(listResolver.getName(), listResolver);
			return true;
		}
	}

	@Override
	public boolean unregisterResolver(TargetListResolver listResolver)
	{
		return resolvers.remove(listResolver.getName()) != null;
	}

	@Override
	public Map<Target, IString> getTitles(Set<Target> targets)
	{
		Map<Target, IString> retVal = new HashMap<>();

		for ( Target target : targets)
		{
			IString title = getTitle(target);
			if ( title != null )
			{
				retVal.put(target, getTitle(target));
			}
		}

		return retVal;
	}

	@Override
	public Set<User> resolveUsers(Target target, int offset, int limit)
	{
		return resolveUsersInternal(new HashSet<>(), target, offset, limit);
	}

	protected Set<User> resolveUsersInternal(Set<User> users, Target target, int offset, int limit)
	{
		for ( TargetListResolver resolver : resolvers.values() )
		{
			if ( resolver.resolve(target, users, offset, limit))
			{
				// This resolver handled the target
				//
				break;
			}
		}

		return users;
	}

	@Override
	public Set<User> resolveUsers(Set<Target> targets, int offset, int limit)
	{
		Set<User> retVal = new HashSet<>();

		for (Target target : targets)
		{
			resolveUsersInternal(retVal, target, offset, limit);
		}

		return retVal;
	}

	@Override
	public Set<Target> resolveTargets(Collection<Target> targets)
	{
		Set<Target> retVal = new HashSet<>();

		for ( Target target : targets )
		{
			for ( TargetListResolver resolver : resolvers.values() )
			{
				Target resolvedTarget = resolver.resolveKey(target.getKey());
				if ( resolvedTarget != null )
				{
					retVal.add(resolvedTarget);
				}
			}
		}

		return retVal;
	}

	@Override
	public boolean contains(Target target, User user)
	{
		for ( TargetListResolver resolver : resolvers.values() )
		{
			if ( resolver.contains(target, user) )
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean contains(Set<Target> targets, User user)
	{
		boolean doesContain = false;
		for (Target target : targets)
		{
			doesContain |= contains(target, user);
			if ( doesContain )
			{
				break;
			}
		}

		return doesContain;
	}

	@Override
	public Target resolveKey(String key)
	{
		for ( TargetListResolver resolver : resolvers.values() )
		{
			Target resolvedTarget = resolver.resolveKey(key);
			if ( resolvedTarget != null )
			{
				return resolvedTarget;
			}
		}
		return null;
	}

	@Override
	public IString getTitle(Target target)
	{
		for ( TargetListResolver resolver : resolvers.values() )
		{
			IString title = resolver.getTitle(target);
			if ( title != null )
			{
				return title;
			}
		}

		return null;
	}
}
