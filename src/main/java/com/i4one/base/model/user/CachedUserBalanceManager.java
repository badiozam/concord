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
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedUserBalanceManager extends SimpleUserBalanceManager implements UserBalanceManager
{
	@Caching( evict = {
		@CacheEvict(value = "userBalanceManager", key = "target.makeKey('getUserBalances', #userBalance.user)"),
		@CacheEvict(value = "userBalanceManager", key = "target.makeKey('getUserBalance', #userBalance.user, #userBalance.balance)")
		})
	@Override
	public UserBalance getUserBalanceForUpdate(UserBalance userBalance)
	{
		return super.getUserBalanceForUpdate(userBalance);
	}

	@Cacheable(value = "userBalanceManager", key = "target.makeKey('getUserBalance', #user, #balance)")
	@Override
	public UserBalance getUserBalance(User user, Balance balance)
	{
		return super.getUserBalance(user, balance);
	}

	@Cacheable(value = "userBalanceManager", key = "target.makeKey('getUserBalances', #user)")
	@Override
	public Set<UserBalance> getUserBalances(User user, PaginationFilter pagination)
	{
		return super.getUserBalances(user, pagination);
	}

	@Caching( evict = {
		@CacheEvict(value = "userBalanceManager", key = "target.makeKey('getUserBalances', #userBalance.user)"),
		@CacheEvict(value = "userBalanceManager", key = "target.makeKey('getUserBalance', #userBalance.user, #userBalance.balance)")
		})
	@Transactional(readOnly = false)
	@Override
	public ReturnType<UserBalance> increment(UserBalance userBalance, int amount)
	{
		return super.increment(userBalance, amount);
	}

	@Caching( evict = {
		@CacheEvict(value = "userBalanceManager", key = "target.makeKey('getUserBalances', #item.user)"),
		@CacheEvict(value = "userBalanceManager", key = "target.makeKey('getUserBalance', #item.user, #item.balance)")
		})
	@Transactional(readOnly = false)
	@Override
	public ReturnType<UserBalance> clone(UserBalance item)
	{
		return super.clone(item);
	}

	@Caching( evict = {
		@CacheEvict(value = "userBalanceManager", key = "target.makeKey('getUserBalances', #item.user)"),
		@CacheEvict(value = "userBalanceManager", key = "target.makeKey('getUserBalance', #item.user, #item.balance)")
		})
	@Transactional(readOnly = false)
	@Override
	public UserBalance remove(UserBalance item)
	{
		return super.remove(item);
	}
}