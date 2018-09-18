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
import com.i4one.base.model.AttachableClientType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import java.util.Collection;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface ExpendableClientType<U extends ClientRecordType, T extends SingleClientType<U>> extends AttachableClientType<U,T>
{
	/**
	 * Get the set of all expenses associated with this item.
	 * 
	 * @return The (potentially empty) set of expenses
	 */
	public Set<BalanceExpense> getBalanceExpenses();

	/**
	 * Set the expenses associated with this item.
	 *
	 * @param balanceExpenses The new set of expenses, duplicates are automatically
	 * 	removed
	 */
	public void setBalanceExpenses(Collection<BalanceExpense> balanceExpenses);
}
