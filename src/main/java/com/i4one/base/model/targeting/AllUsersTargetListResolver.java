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
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class AllUsersTargetListResolver extends BaseTargetListResolver
{
	private final Target allUsers;

	public AllUsersTargetListResolver()
	{
		allUsers = new AllUsersTarget();
	}

	@Override
	protected Target emptyInstance()
	{
		return allUsers;
	}

	@Override
	protected Target resolveKeyInternal(String key)
	{
		return allUsers;
	}

	@Override
	public boolean matches(Target target)
	{
		return ( target instanceof AllUsersTarget );
	}

	@Override
	public IString getTitle(Target target)
	{
		IString retVal = null;
		if ( matches(target) )
		{
			SingleClient client = getRequestState().getSingleClient();

			retVal = new IString();
			for (String lang : client.getLanguageList() )
			{
				retVal.put(lang, getMessageManager().buildMessage(client, "msg." + getName(), lang));
			}
		}
		return retVal;
	}

	@Override
	public boolean resolve(Target target, Set<User> users, int offset, int limit)
	{
		// We never resolve anything
		//
		return false;
	}

	@Override
	public boolean contains(Target target, User user)
	{
		return matches(target);
	}
}
