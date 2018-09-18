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
package com.i4one.research.model.poll;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedPollResponseManager extends SimplePollResponseManager implements PollResponseManager
{
	@Cacheable(value = "pollResponseManager", key = "target.makeKey('hasResponses', #poll)")
	@Override
	public boolean hasActivity(Poll poll)
	{
		return super.hasActivity(poll);
	}
	
	@Cacheable(value = "pollResponseManager", key = "target.makeKey('getPollResponse', #poll, #user)")
	@Override
	public PollResponse getActivity(Poll poll, User user)
	{
		return super.getActivity(poll, user);
	}

	@CacheEvict(value = "pollResponseManager", allEntries = true )
	@Override
	public ReturnType<PollResponse> create(PollResponse pollResponse)
	{
		return super.create(pollResponse);
	}

	@CacheEvict(value = "pollResponseManager", allEntries = true )
	@Override
	public ReturnType<PollResponse> update(PollResponse respondent)
	{
		return super.update(respondent);
	}

	@CacheEvict(value = "pollResponseManager", allEntries = true)
	@Override
	public PollResponse remove(PollResponse respondent)
	{
		return super.remove(respondent);
	}
}
