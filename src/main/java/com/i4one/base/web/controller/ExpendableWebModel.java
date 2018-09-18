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
package com.i4one.base.web.controller;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.Errors;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.model.manager.expendable.ExpendableClientType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.AutoPopulatingList;
import org.springframework.util.AutoPopulatingList.ElementFactory;

/**
 * Provides implementation for a form backing object's expenses. Provides an
 * auto-populating list of exclusive balance expenses for the form backing
 * object so they can be edited easily.
 *  
 * Furthermore, the validateModel(..) method should be called within the form backing
 * object's validate(..) method. And the setModelOverrides() method should be called
 * within the form backing object's setOverrides() method.
 * 
 * @author Hamid Badiozamani
 * 
 * @param <U> The form backing object's database record type
 * @param <T> The form backing object's model type
 */
public class ExpendableWebModel<U extends ClientRecordType, T extends ExpendableClientType<U,T>> extends BaseLoggable implements ElementFactory<BalanceExpense>
{
	private transient List<BalanceExpense> expenses;
	private final transient T model;

	public ExpendableWebModel(T model)
	{
		this.model = model;

		expenses = new AutoPopulatingList<>(this);
	}

	public Errors validateModel(Errors errors)
	{
		Errors retVal = errors;

		// Note: The name of this field in the container class has to be "expendable"
		// in order for error messages to work properly
		//
		retVal.replaceFieldNames("balanceExpenses", "expendable.expenses");
		return retVal;
	}

	public void setModelOverrides()
	{
		// Filter out any non-existent and 0 amount expenses
		//
		List<BalanceExpense> filteredExpenses = getFilteredExpenses();

		// Set the overrides for each of the expenses
		//
		filteredExpenses.forEach( (expense) -> { expense.setOverrides(); });

		// After the overrides have been set, we reinitialize the model with
		// these since the references are unknown to the non-web model instance
		//
		model.setBalanceExpenses(filteredExpenses);
	}

	private List<BalanceExpense> getFilteredExpenses()
	{
		return getExpenses().stream()
			.filter( (expense) -> { return expense != null; } )
			.filter( (expense) -> { return expense.exists() || expense.getAmount() != 0; } )
			.collect(Collectors.toList());
	}

	public List<BalanceExpense> getExpenses()
	{
		if ( expenses == null || expenses.isEmpty() )
		{
			getLogger().debug("getExpenses() initializing expenses from " + Utils.toCSV(getModel().getBalanceExpenses()));

			Collection<BalanceExpense> modelExpenses = getModel().getBalanceExpenses();
			List<BalanceExpense> backingList = new ArrayList<>(modelExpenses);

			expenses = new AutoPopulatingList<>(backingList, this);
		}

		return expenses;
	}

	public void setExpenses(Collection<BalanceExpense> balanceExpenses)
	{
		// We can't trust the incoming balanceExpenses because it may have been set by us
		// in the setModelOverrides() method. So by clearing the expenses here, we force
		// a reload from the model when the getExpenses() method is called next.
		//
		expenses.clear();
	}

	@Override
	public BalanceExpense createElement(int i) throws AutoPopulatingList.ElementInstantiationException
	{
		BalanceExpense expense = new BalanceExpense();
		expense.setOwner(getModel());

		return expense;
	}

	public T getModel()
	{
		return model;
	}
}
