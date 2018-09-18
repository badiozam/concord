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
package com.i4one.base.model.manager.expendable;

import com.i4one.base.dao.activity.ActivityRecordType;
import com.i4one.base.dao.activity.ActivityRecordTypeDao;
import com.i4one.base.model.ActivityType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balanceexpense.UserBalanceExpense;
import com.i4one.base.model.balanceexpense.UserBalanceExpenseManager;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.attachable.BaseAttachedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseExpensiveManager<U extends ActivityRecordType, T extends ActivityType<U, V>, V extends SingleClientType<?>> extends BaseAttachedManager<U, T> implements ExpensiveManager<U,T>, ActivityManager<U, T, V>
{
	private UserBalanceExpenseManager userBalanceExpenseManager;

	@Override
	public abstract ActivityManager<U, T, V> getImplementationManager();

	@Override
	public abstract V getAttachee(T item);

	@Override
	public boolean hasActivity(V parent)
	{
		return getImplementationManager().hasActivity(parent);
	}

	@Override
	public T getActivity(V parent, User user)
	{
		return getImplementationManager().getActivity(parent, user);
	}

	@Override
	public ActivityReport getReport(T item, TopLevelReport report, PaginationFilter pagination)
	{
		return getImplementationManager().getReport(item, report, pagination);
	}

	@Override
	public Set<T> getAllActivity(V parent, PaginationFilter pagination)
	{
		return getImplementationManager().getAllActivity(parent, pagination);
	}

	@Override
	public ActivityRecordTypeDao<U> getDao()
	{
		return getImplementationManager().getDao();
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<T>> revert(V parent, PaginationFilter pagination)
	{
		List<ReturnType<T>> reversions = getImplementationManager().revert(parent, pagination);
		for ( ReturnType<T> reverted : reversions )
		{
			T activityItem = reverted.getPre();
			List<ReturnType<UserBalance>> revertedExpenses = getUserBalanceExpenseManager().revertExpenses(parent, activityItem);

			reverted.addChain(getUserBalanceExpenseManager(), "revertExpenses", revertedExpenses);
		}
		return reversions;
	}

	@Override
	protected void processAttached(ReturnType<T> processedItem, String method)
	{
		T item = processedItem.getPost();
		User expenseUser = getUser(item);
		if ( expenseUser.exists() )
		{
			List<ReturnType<UserBalanceExpense>> processedExpenses = getUserBalanceExpenseManager().processExpenses(expenseUser,
				getAttachee(item),
				item,
				SimplePaginationFilter.NONE);

			processedItem.put("processedExpenses", processedExpenses);
			getLogger().debug("Processed expenses = " + processedExpenses);
		}
	}

	public UserBalanceExpenseManager getUserBalanceExpenseManager()
	{
		return userBalanceExpenseManager;
	}

	@Autowired
	public void setUserBalanceExpenseManager(UserBalanceExpenseManager userBalanceExpenseManager)
	{
		this.userBalanceExpenseManager = userBalanceExpenseManager;
	}
}