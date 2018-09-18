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
package com.i4one.base.model.preference;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedUserPreferenceManager extends SimpleUserPreferenceManager implements UserPreferenceManager
{
	@Cacheable(value = "userPreferenceManager", key = "target.makeKey('hasResponses', #preference)")
	@Override
	public boolean hasUserPreferences(Preference preference)
	{
		return super.hasUserPreferences(preference);
	}
	
	@Cacheable(value = "userPreferenceManager", key = "target.makeKey('getUserPreference', #preference, #user)")
	@Override
	public UserPreference getUserPreference(Preference preference, User user)
	{
		return super.getUserPreference(preference, user);
	}

	@CacheEvict(value = "userPreferenceManager", allEntries = true )
	@Override
	public ReturnType<UserPreference> create(UserPreference userPreference)
	{
		return super.create(userPreference);
	}

	@CacheEvict(value = "userPreferenceManager", allEntries = true )
	@Override
	public ReturnType<UserPreference> update(UserPreference respondent)
	{
		return super.update(respondent);
	}

	@CacheEvict(value = "userPreferenceManager", allEntries = true)
	@Override
	public UserPreference remove(UserPreference respondent)
	{
		return super.remove(respondent);
	}
}
