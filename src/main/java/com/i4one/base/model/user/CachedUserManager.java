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
package com.i4one.base.model.user;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author Hamid Badiozamani
 */
public class CachedUserManager extends SimpleUserManager implements UserManager
{
	@Cacheable(value = "userManager", key="target.makeKey(#user)")
	@Override
	public boolean existsUser(User user)
	{
		// Anyone can see if another user exists or not
		//
		return super.existsUser(user);
	}

	@Override
	public Set<User> search(UserSearchCriteria criteria)
	{
		return super.search(criteria);
	}

	@CacheEvict(value = "userManager", key = "target.makeKey(#user)")
	@Override
	public ReturnType<User> resetPassword(User user) throws Exception
	{
		return super.resetPassword(user);
	}

	@CacheEvict(value = "userManager", key = "target.makeKey(#user)")
	@Override
	public boolean optout(User user)
	{
		return super.optout(user);
	}

	@CacheEvict(value = "userManager", key = "target.makeKey(#user)")
	@Override
	public User authenticate(User user, SingleClient client)
	{
		return super.authenticate(user, client);
	}

	@CacheEvict(value = "userManager", key = "target.makeKey(#user)")
	@Override
	public ReturnType<User> create(User user)
	{
		return super.create(user);
	}

	@CacheEvict(value = "userManager", key = "target.makeKey(#user)")
	@Override
	public ReturnType<User> update(User user)
	{
		return super.update(user);
	}

}
