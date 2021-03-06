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
package com.i4one.base.model.balance;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedBalanceManager extends SimpleBalanceManager implements BalanceManager
{
	@Cacheable(value = "balanceManager", key="target.makeKey('getBalance', #type)")
	@Override
	public Balance getBalance(SingleClientType<?> type)
	{
		return super.getBalance(type);
	}

	@Cacheable(value = "balanceManager", key="target.makeKey('getAllBalances', #client, #pagination)")
	@Override
	public Set<Balance> getAllBalances(SingleClient client, PaginationFilter pagination)
	{
		return super.getAllBalances(client, pagination);
	}
	
	@Cacheable(value = "balanceManager", key="target.makeKey('getDefaultBalance', #client)")
	@Override
	public Balance getDefaultBalance(SingleClient client)
	{
		return super.getDefaultBalance(client);
	}

	@CacheEvict(value = "balanceManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Balance> create(Balance item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "balanceManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Balance> update(Balance item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "balanceManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public Balance remove(Balance item)
	{
		return super.remove(item);
	}
}
