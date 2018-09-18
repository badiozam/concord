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
package com.i4one.base.model.balanceexpense;

import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ReturnType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("balanceExpenseManager")
public class CachedBalanceExpenseManager extends SimpleBalanceExpenseManager implements BalanceExpenseManager
{
	@Cacheable(value = "balanceExpenseManager", key = "target.makeKey('getAllExpensesByFeature', #item)")
	@Override
	public Set<BalanceExpense> getAllExpensesByFeature(SingleClientType<?> item)
	{
		return Collections.unmodifiableSet(super.getAllExpensesByFeature(item));
	}

	@CacheEvict(value = "balanceExpenseManager", allEntries = true)
	@Override
	public List<ReturnType<BalanceExpense>> updateBalanceExpenses(SingleClientType<?> item, Collection<BalanceExpense> expenses)
	{
		return super.updateBalanceExpenses(item, expenses);
	}

	@CacheEvict(value = "balanceExpenseManager", allEntries = true)
	@Override
	public boolean associate(SingleClientType<?> item, BalanceExpense expense)
	{
		return super.associate(item, expense);
	}

	@CacheEvict(value = "balanceExpenseManager", allEntries = true)
	@Override
	public boolean dissociate(SingleClientType<?> item, BalanceExpense expense)
	{
		return super.dissociate(item, expense);
	}

	@CacheEvict(value = "balanceExpenseManager", allEntries = true)
	@Override
	public ReturnType<BalanceExpense> clone(BalanceExpense item)
	{
		return super.clone(item);
	}

	@CacheEvict(value = "balanceExpenseManager", allEntries = true)
	@Override
	public ReturnType<BalanceExpense> create(BalanceExpense item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "balanceExpenseManager", allEntries = true)
	@Override
	public ReturnType<BalanceExpense> update(BalanceExpense item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "balanceExpenseManager", allEntries = true)
	@Override
	public BalanceExpense remove(BalanceExpense item)
	{
		return super.remove(item);
	}
}