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
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedPreferenceManager extends SimplePreferenceManager implements PreferenceManager
{
	@Cacheable(value = "preferenceManager", key = "target.makeKey('getAllPreferences', #client, #pagination)")
	@Override
	public Set<Preference> getAllPreferences(SingleClient client, PaginationFilter pagination)
	{
		return super.getAllPreferences(client, pagination);
	}

	@CacheEvict(value = "preferenceManager", allEntries = true)
	@Override
	public ReturnType<Preference> clone(Preference item)
	{
		return super.clone(item);
	}

	@CacheEvict(value = "preferenceManager", allEntries = true)
	@Override
	public ReturnType<Preference> create(Preference item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "preferenceManager", allEntries = true)
	@Override
	public ReturnType<Preference> update(Preference item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "preferenceManager", allEntries = true)
	@Override
	public Preference remove(Preference item)
	{
		return super.remove(item);
	}
}
