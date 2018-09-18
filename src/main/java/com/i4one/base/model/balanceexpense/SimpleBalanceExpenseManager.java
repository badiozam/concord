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
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.Manager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleBalanceExpenseManager extends BaseSimpleManager<BalanceExpenseRecord, BalanceExpense> implements BalanceExpenseManager
{
	@Override
	public BalanceExpense emptyInstance()
	{
		return new BalanceExpense();
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<BalanceExpense> clone(BalanceExpense item)
	{
		try
		{
			if ( item.exists() )
			{
				BalanceExpense retVal = new BalanceExpense();
				retVal.copyFrom(item);
	
				retVal.setSer(0);
		
				return create(retVal);
			}
			else
			{
				throw new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.dne", "You are attempting to clone a non-existent item: $item", new Object[] { "item", item }));
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
		{
			Errors errors = new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.collision", "An item with the same qualifiers already exists: $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));

			throw errors;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<BalanceExpense> createInternal(BalanceExpense item)
	{
		// When we create a balance expense, we don't associate it with anything
		//
		item.getDelegate().setFeature("");
		item.getDelegate().setFeatureid(0);

		return super.createInternal(item);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<BalanceExpense> updateInternal(BalanceExpenseRecord lockedItem, BalanceExpense item)
	{
		if ( !Objects.equals(lockedItem.getFeatureid(), item.getOwner().getSer()) || !lockedItem.getFeature().equals(item.getOwner().getFeatureName()))
		{
			throw new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".update.ownerswitch", "This expense item already belongs to another feature.", new Object[] { "item", item}));
		}
		else
		{
			return super.updateInternal(lockedItem, item);
		}
	}


	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<BalanceExpense>> updateBalanceExpenses(SingleClientType<?> item, Collection<BalanceExpense> expenses)
	{
		List<ReturnType<BalanceExpense>> retVal = new ArrayList<>();
		expenses.forEach( (expense) -> 
		{
			// Force the expense to have the incoming item as its owner since we know
			// balance expenses are always exclusive to their owners
			//
			expense.setOwner(item);

			// First update or (or if non-existent, create) any details about the expense
			//
			boolean doAssociate;
			if ( expense.exists() && expense.getAmount() > 0 )
			{
				ReturnType<BalanceExpense> updatedExpense = update(expense);
				retVal.add(updatedExpense);
				doAssociate = true;
			}
			else if ( expense.getAmount() > 0 )
			{
				ReturnType<BalanceExpense> createdExpense = create(expense);
				retVal.add(createdExpense);
				doAssociate = true;
			}
			else if ( expense.exists() && expense.getAmount() <= 0 )
			{
				remove(expense);
				doAssociate = false;
			}
			else
			{
				// Don't create a expense with a zero amount
				//
				doAssociate = false;
			}

			if ( doAssociate )
			{
				associate(item, expense);
			}
		});

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public boolean dissociate(SingleClientType<?> item, BalanceExpense expense)
	{
		BalanceExpenseRecord balanceExpenseRecord = expense.getDelegate();
		if ( !expense.exists() )
		{
			// Look up the expense by balance serial number
			//
			BalanceExpenseRecord dbBalanceExpenseRecord = getDao().getByFeature(item.getFeatureName(), item.getSer(), expense.getBalance(false).getSer());
			if ( dbBalanceExpenseRecord != null )
			{
				balanceExpenseRecord = dbBalanceExpenseRecord;
			}
		}

		if ( balanceExpenseRecord.exists() )
		{
			getDao().deleteBySer(expense.getSer());
			return true;
		}
		else
		{
			return false;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public boolean associate(SingleClientType<?> item, BalanceExpense expense)
	{
		BalanceExpenseRecord balanceExpenseRecord = expense.getDelegate();
		if ( !expense.exists() )
		{
			// Look up the expense by balance serial number
			//
			BalanceExpenseRecord dbBalanceExpenseRecord = getDao().getByFeature(item.getFeatureName(), item.getSer(), expense.getBalance(false).getSer());
			if ( dbBalanceExpenseRecord != null )
			{
				balanceExpenseRecord = dbBalanceExpenseRecord;
			}
		}

		if ( !balanceExpenseRecord.exists() )
		{
			throw new Errors(getInterfaceName() + ".associate", new ErrorMessage("msg." + getInterfaceName() + ".associate.dne", "You are attempting to associate a non-existent balance expense item: $expense", new Object[] { "item", item, "expense", expense }));

			// We can only create new records not update existing ones
			//
			//return false;
		}
		else if ( !item.exists() )
		{
			throw new Errors(getInterfaceName() + ".associate", new ErrorMessage("msg." + getInterfaceName() + ".associate.dne", "You are attempting to associate this balance expense with a non-existent item: $item", new Object[] { "item", item, "expense", expense}));

			// We can only associate with existing items
			//
			//return false;
		}
		else
		{
			balanceExpenseRecord.setFeature(item.getFeatureName());
			balanceExpenseRecord.setFeatureid(item.getSer());

			getDao().updateBySer(balanceExpenseRecord);
			return true;
		}
	}

	@Override
	public Set<BalanceExpense> getAllExpensesByFeature(SingleClientType<?> item)
	{
		getLogger().debug("getAllExpensesByFeature(..) called for " + item);
		return convertDelegates(getDao().getByFeature(item.getDelegate().getFullTableName(), item.getSer()));
	}

	protected String buildManagerFeatureName(Manager<?,?> manager, String method)
	{
		return manager.getInterfaceName() + "." + method;
	}

	@Override
	public BalanceExpenseRecordDao getDao()
	{
		return (BalanceExpenseRecordDao) super.getDao();
	}
}
