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
package com.i4one.base.model.client;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedClientOptionManager extends SimpleClientOptionManager implements ClientOptionManager
{
	@Cacheable(value = "clientOptionManager", key="target.makeKey('getOptionValue', #client, #key)")
	@Override
	public String getOptionValue(SingleClient client, String key)
	{
		return super.getOptionValue(client, key);
	}

	@Cacheable(value = "clientOptionManager", key="target.makeKey('getOption', #client, #key)")
	@Override
	public ClientOption getOption(SingleClient client, String key)
	{
		return super.getOption(client, key);
	}

	@Cacheable(value = "clientOptionManager", key="target.makeKey('getOptions', #client, #startsWithKey, #pagination)")
	@Override
	public List<ClientOption> getOptions(SingleClient client, String startsWithKey, PaginationFilter pagination)
	{
		return super.getOptions(client, startsWithKey, pagination);
	}

	@CacheEvict(value = "clientOptionManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClientOption> create(ClientOption item)
	{
		return super.create(item);
	}

	// Doesn't work
	//@CacheEvict(value = "clientOptionManager", key="target.makeKey('getOption', #item.client, #item.key)")
	@CacheEvict(value = "clientOptionManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClientOption> update(ClientOption item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "clientOptionManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ClientOption remove(ClientOption item)
	{
		return super.remove(item);
	}
}
