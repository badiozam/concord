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

import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.model.balanceexpense.BalanceExpenseManager;
import com.i4one.base.model.balanceexpense.UserBalanceExpenseManager;
import com.i4one.base.model.manager.BaseDelegatingManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseExpendableManager<U extends ClientRecordType, T extends ExpendableClientType<U, ?>> extends BaseDelegatingManager<U,T> implements ExpendableManager<U,T>
{
	private BalanceExpenseManager balanceExpenseManager;
	private UserBalanceExpenseManager userBalanceExpenseManager;

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		ReturnType<T> retVal = getImplementationManager().create(item);
		updateBalanceExpenses(retVal.getPost());

		// Update the set of balance expenses after this call to ensure that the caller has
		// the latest information
		//
		retVal.getPost().setBalanceExpenses(getBalanceExpenseManager().getAllExpensesByFeature(retVal.getPost()));
		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		ReturnType<T> retVal = getImplementationManager().update(item);
		updateBalanceExpenses(retVal.getPost());

		// Update the set of balance expenses after this call to ensure that the caller has
		// the latest information
		//
		retVal.getPost().setBalanceExpenses(getBalanceExpenseManager().getAllExpensesByFeature(retVal.getPost()));
		return retVal;
	}

	protected void updateBalanceExpenses(T item)
	{
		getLogger().debug("Updating " + item.getBalanceExpenses().size() + " balance expenses for " + item);

		getBalanceExpenseManager().updateBalanceExpenses(item, item.getBalanceExpenses());
	}

	@Transactional(readOnly = false)
	@Override
	public T remove(T item)
	{
		// Dissociate all expenses having to do with this item since it's being
		// completely removed. 
		//
		Set<BalanceExpense> expenses = getBalanceExpenseManager().getAllExpensesByFeature(item);

		boolean hasActivity = false;
		for ( BalanceExpense expense : expenses )
		{
			if ( getUserBalanceExpenseManager().hasActivity(expense))
			{
				hasActivity = true;
				break;
			}
		}

		if ( hasActivity )
		{
			throw new Errors(getInterfaceName() + ".remove", new ErrorMessage("msg." + getInterfaceName() + ".remove.hasactivity", "Cannot remove an item that has activity, please revert first", new Object[] { "item", item }));
		}
		else
		{
			expenses.forEach( (expense) ->
			{
				getBalanceExpenseManager().dissociate(item, expense);
			});
		}

		return getImplementationManager().remove(item);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> clone(T item)
	{
		// First get the incoming item's expenses so we can clone them, too
		//
		Set<BalanceExpense> expenses = getBalanceExpenseManager().getAllExpensesByFeature(item);

		// Now we can clone the incoming item to get its serial no.
		//
		ReturnType<T> retVal = getImplementationManager().clone(item);

		// And clone the previous item's expenses and associated them with this one
		//
		List<ReturnType<BalanceExpense>> clonedExpenses = new ArrayList<>();
		expenses.forEach((expense) ->
		{
			// Need to create each one separately
			//
			expense.setSer(0);

			ReturnType<BalanceExpense> clonedExpense = getBalanceExpenseManager().create(expense);
			clonedExpenses.add(clonedExpense);

			getBalanceExpenseManager().associate(retVal.getPost(), clonedExpense.getPost());
		});

		//getBalanceExpenseManager().updateBalanceExpenses(item, clonedExpenses);
		initModelObject(retVal.getPost());

		retVal.addChain(getBalanceExpenseManager(), "clone", new ReturnType<>(clonedExpenses));
		return retVal;
	}

	@Override
	protected T initModelObject(T item)
	{
		item.setBalanceExpenses(getBalanceExpenseManager().getAllExpensesByFeature(item));

		return item;
	}

	public BalanceExpenseManager getBalanceExpenseManager()
	{
		return balanceExpenseManager;
	}

	@Autowired
	public void setBalanceExpenseManager(BalanceExpenseManager balanceExpenseManager)
	{
		this.balanceExpenseManager = balanceExpenseManager;
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