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
package com.i4one.promotion.model.clickthru;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedClickThruResponseManager extends SimpleClickThruResponseManager implements ClickThruResponseManager
{
	@Cacheable(value = "clickThruResponseManager", key = "target.makeKey('hasResponses', #clickThru)")
	@Override
	public boolean hasActivity(ClickThru clickThru)
	{
		return super.hasActivity(clickThru);
	}
	
	@Cacheable(value = "clickThruResponseManager", key = "target.makeKey('getClickThruResponse', #clickThru, #user)")
	@Override
	public ClickThruResponse getActivity(ClickThru clickThru, User user)
	{
		return super.getActivity(clickThru, user);
	}

	@CacheEvict(value = "clickThruResponseManager", allEntries = true )
	@Override
	public ReturnType<ClickThruResponse> create(ClickThruResponse clickThruResponse)
	{
		return super.create(clickThruResponse);
	}

	@CacheEvict(value = "clickThruResponseManager", allEntries = true )
	@Override
	public ReturnType<ClickThruResponse> update(ClickThruResponse respondent)
	{
		return super.update(respondent);
	}

	@CacheEvict(value = "clickThruResponseManager", allEntries = true)
	@Override
	public ClickThruResponse remove(ClickThruResponse respondent)
	{
		return super.remove(respondent);
	}
}
