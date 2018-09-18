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
import com.i4one.base.model.BaseAttachableClientType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class SimpleExpendable<U extends ClientRecordType, T extends SingleClientType<U>> extends BaseAttachableClientType<U,T> implements ExpendableClientType<U,T>
{
	private final Set<BalanceExpense> expenses;

	public SimpleExpendable(T forward)
	{
		super(forward);

		expenses = new LinkedHashSet<>();
	}

	@Override
	protected final Errors validateInternal()
	{
		Errors retVal = super.validateInternal();

		validateBalances(retVal);

		return retVal;
	}

	private void validateBalances(Errors retVal)
	{
		// Collect the balances in play in order to detect duplicates
		//
		Set<Balance> balances = new HashSet<>();

		// We're only concerend with the exclusive expenses since we can't update the
		// non-exclusive ones here
		//
		int i = 0;
		for ( BalanceExpense expense : getBalanceExpenses() )
		{
			String currKey = "balanceExpenses[" + i + "]";
			if ( balances.add(expense.getBalance()) )
			{
				// Include any errors with the expense itself
				//
				retVal.merge(currKey, expense.validate());
			}
			else if ( expense.getAmount() != 0 )
			{
				// Only add this error if the expense isn't being removed (by virtue of having a 0 amount)
				//
				retVal.addError(currKey, new ErrorMessage(currKey + ".balance", "msg.base.Expendable.balanceExpenses.collision", "There is already a expense for the Balance $expense.balance defined", new Object[]{"item", this, "expense", expense}));
			}
			
			i++;
		}

	}

	@Override
	public void setOverridesInternal()
	{
		super.setOverridesInternal();

		getBalanceExpenses().forEach( (expense) -> { expense.setOverrides(); });
	}


	@Override
	public Set<BalanceExpense> getBalanceExpenses()
	{
		return Collections.unmodifiableSet(expenses);
	}

	@Override
	public void setBalanceExpenses(Collection<BalanceExpense> balanceExpenses)
	{
		expenses.clear();
		expenses.addAll(balanceExpenses);
	}
}
