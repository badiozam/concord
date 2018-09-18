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
package com.i4one.base.model.friendref;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedFriendRefManager extends SimpleFriendRefManager implements FriendRefManager
{
	/*
	 * Can't cache this since user registrations through userManager would invalidate the 'pending' flag
	 * in objects in this cache
	 *
	@Cacheable(value = "friendRefManager", key = "target.makeKey('getFriendsByUser', #user, #pagination)")
	@Override
	public Set<FriendRef> getFriendsByUser(User user, PaginationFilter pagination)
	{
		return super.getFriendsByUser(user, pagination);
	}
	*/

	/*
	 * No reason to cache this
	 *
	@Cacheable(value = "friendRefManager", key = "target.makeKey('getReferral', #id, #user)")
	@Override
	public FriendRef getReferral(int id, User user)
	{
		return super.getReferral(id, user);
	}
	*/

	@Cacheable(value = "friendRefManager", key = "target.makeKey('getSettings', #client)")
	@Override
	public FriendRefSettings getSettings(SingleClient client)
	{
		return super.getSettings(client);
	}

	@CacheEvict(value = "friendRefManager", key = "target.makeKey('getSettings', #settings.client)")
	@Override
	public ReturnType<FriendRefSettings> updateSettings(FriendRefSettings settings)
	{
		return super.updateSettings(settings);
	}

	@CacheEvict(value = "friendRefManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<FriendRef> processReferral(User newUser)
	{
		return super.processReferral(newUser);
	}

	@CacheEvict(value = "friendRefManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<FriendRef> create(FriendRef item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "friendRefManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<FriendRef> update(FriendRef item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "friendRefManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public FriendRef remove(FriendRef item)
	{
		return super.remove(item);
	}
}
