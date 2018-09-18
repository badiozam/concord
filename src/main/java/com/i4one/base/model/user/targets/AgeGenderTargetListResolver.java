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
package com.i4one.base.model.user.targets;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.targeting.BaseTargetListResolver;
import com.i4one.base.model.targeting.TargetListResolver;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.targeting.Target;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import java.util.Calendar;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class AgeGenderTargetListResolver extends BaseTargetListResolver implements TargetListResolver
{
	private UserManager userManager;

	@Override
	protected Target emptyInstance()
	{
		return new AgeGenderTarget("a", 0, 100);
	}

	@Override
	public boolean matches(Target target)
	{
		return (target instanceof AgeGenderTarget);
	}

	@Override
	protected Target resolveKeyInternal(String key)
	{
		AgeGenderTarget retVal = new AgeGenderTarget(key);

		SingleClient client = getRequestState().getSingleClient();
		String lang = getRequestState().getLanguage();

		String title = getMessageManager().buildMessage(client,
			"msg." + getName(),
			lang,
				"gender", retVal.getGender(),
				"minAge", retVal.getMinAge(),
				"maxAge", retVal.getMaxAge());

		retVal.setTitle(title);

		return retVal;
	}

	@Override
	public IString getTitle(Target target)
	{
		IString retVal = null;
		if ( matches(target) )
		{
			AgeGenderTarget ageGenderTarget = (AgeGenderTarget)target;
			SingleClient client = getRequestState().getSingleClient();
			retVal = new IString();

			for (String lang : client.getLanguageList() )
			{
				retVal.put(lang,
					getMessageManager().buildMessage(client,
						"msg." + getName(),
						lang,
							"gender", ageGenderTarget.getGender(),
							"minAge", ageGenderTarget.getMinAge(),
							"maxAge", ageGenderTarget.getMaxAge()));
			}
		}

		return retVal;
	}

	@Override
	public boolean resolve(Target target, Set<User> users, int offset, int limit)
	{
		if ( matches(target) )
		{
			SingleClient client = getRequestState().getSingleClient();
			AgeGenderTarget ageGenderTarget = (AgeGenderTarget)target;

			Calendar cal = client.getCalendar();
			cal.setTimeInMillis(getRequestState().getRequest().getTimeInMillis());
			int currYear = cal.get(Calendar.YEAR);

			int minBirthyyyy = cal.get(currYear) - ageGenderTarget.getMinAge();
			int maxBirthyyyy = cal.get(currYear) - ageGenderTarget.getMaxAge();

			PaginationFilter pagination = new AgeGenderPagination(ageGenderTarget.getGender(),
				minBirthyyyy, maxBirthyyyy);

			pagination.setOffset(offset);
			pagination.setLimit(limit);

			users.addAll(getUserManager().getAllMembers(client, pagination));
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
			AgeGenderTarget ageGenderTarget = (AgeGenderTarget)target;
			SingleClient client = getRequestState().getSingleClient();

			Calendar cal = client.getCalendar();
			cal.setTimeInMillis(getRequestState().getRequest().getTimeInMillis());
			int currYear = cal.get(Calendar.YEAR);

			int minBirthyyyy = cal.get(currYear) - ageGenderTarget.getMaxAge();
			int maxBirthyyyy = cal.get(currYear) - ageGenderTarget.getMaxAge();

			return minBirthyyyy < user.getBirthYYYY() && user.getBirthYYYY() < maxBirthyyyy &&
				(ageGenderTarget.getGender().equalsIgnoreCase("a") || user.getGender().equalsIgnoreCase(ageGenderTarget.getGender()));
		}
		else
		{
			return false;
		}
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

}
