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

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.user.User;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A target resolver that combines the results of two other target resolvers and
 * returns their intersection.
 * 
 * @author Hamid Badiozamani
 */
@Service
public class IntersectionTargetListResolver extends BaseTargetListResolver
{

	@Override
	protected Target emptyInstance()
	{
		AllUsersTarget allUsers = new AllUsersTarget();
		return new IntersectionTarget("base.intersect:" + allUsers.getKey() + "&" + allUsers.getKey(), getTargetResolver());
	}

	@Override
	protected Target resolveKeyInternal(String key)
	{
		IntersectionTarget retVal = new IntersectionTarget(key, getTargetResolver());

		SingleClient client = getRequestState().getSingleClient();
		String lang = getRequestState().getLanguage();

		String title = getMessageManager().buildMessage(client,
			"msg." + getName(),
			lang,
				"firstTarget", getTargetResolver().getTitle(retVal.getFirstTarget()),
				"secondTarget", getTargetResolver().getTitle(retVal.getSecondTarget()));

		retVal.setTitle(title);

		return retVal;
	}

	@Override
	public boolean matches(Target target)
	{
		return (target instanceof IntersectionTarget);
	}

	@Override
	public IString getTitle(Target target)
	{
		IString retVal = null;
		if ( matches(target) )
		{
			IntersectionTarget intersectionTarget = (IntersectionTarget)target;
			SingleClient client = getRequestState().getSingleClient();
			retVal = new IString();

			for (String lang : client.getLanguageList() )
			{
				retVal.put(lang,
					getMessageManager().buildMessage(client,
						"msg." + getName(),
						lang,
							"firstTarget", getTargetResolver().getTitle(intersectionTarget.getFirstTarget()),
							"secondTarget", getTargetResolver().getTitle(intersectionTarget.getSecondTarget())));
			}
		}

		return retVal;
	}

	@Override
	public boolean resolve(Target target, Set<User> users, int offset, int limit)
	{
		if ( matches(target) )
		{
			IntersectionTarget intersectionTarget = (IntersectionTarget)target;

			// We get the first set of users
			//
			Set<User> firstSet = getTargetResolver().resolveUsers(intersectionTarget.getFirstTarget(), offset, limit);

			Set<User> secondSet = Collections.EMPTY_SET;
			if ( intersectionTarget.isHeavyContains() )
			{
				secondSet = getTargetResolver().resolveUsers(intersectionTarget.getSecondTarget(), offset, limit);
			}

			// Here we check each user against the second set and if any don't belong
			// to the second set, we remove them. This works well when the logic is
			// self-contained (i.e. testing a user's age or gender) but for cases where
			// a database query is involved, it can become bogged down as each comparison
			// invokes a round-trip to the database.
			//
			Iterator<User> it = firstSet.iterator();
			while ( it.hasNext() )
			{
				User user = it.next();
				if ( intersectionTarget.isHeavyContains() )
				{
					// Test off of the pre-loaded second set which we loaded earlier
					//
					if ( !secondSet.contains(user) )
					{
						it.remove();
					}
				}
				else
				{
					// Test individually
					//
					if ( !getTargetResolver().contains(intersectionTarget.getSecondTarget(), user) )
					{
						it.remove();
					}
				}
			}

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
			IntersectionTarget intersectionTarget = (IntersectionTarget)target;

			// The critical AND semantic
			//
			return getTargetResolver().contains(intersectionTarget.getFirstTarget(), user)
				&& getTargetResolver().contains(intersectionTarget.getSecondTarget(), user);
		}

		return false;
	}

	@Autowired
	public void setAllUsersTargetListResolver(TargetListResolver allUsersTargetListResolver)
	{
		// This method ensures we're initialized after the AllUsersTargetListResolver
		// since we depend on it when initializing our empty instances
		//
	}
}
