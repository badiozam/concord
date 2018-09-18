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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ActivityType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.transaction.TransactionManager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("base.UserBalanceExpenseManager")
public class SimpleUserBalanceExpenseManager extends BaseActivityManager<UserBalanceExpenseRecord, UserBalanceExpense, BalanceExpense> implements UserBalanceExpenseManager
{
	private TransactionManager transactionManager;
	private UserBalanceManager userBalanceManager;
	private BalanceExpenseManager balanceExpenseManager;

	@Override
	public UserBalanceExpense emptyInstance()
	{
		return new UserBalanceExpense();
	}

	@Override
	public Set<UserBalanceExpense> getByUser(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByUser(user.getSer(), pagination));
	}

	@Override
	public Set<UserBalanceExpense> getAllUserBalanceExpenses(BalanceExpense balanceExpense, User user, PaginationFilter pagination)
	{
		//return getAllUserBalanceExpenses(balanceExpense, user, pagination);
		return convertDelegates(getDao().getAll(balanceExpense.getSer(), user.getSer(), pagination));
	}

	@Transactional(readOnly = false)
	@Override
	public <T extends SingleClientType<?>> List<ReturnType<UserBalanceExpense>> processExpenses(User user, T item, ActivityType<?, T> activity, PaginationFilter pagination)
	{
		// Then the specific item expenses
		//
		Set<BalanceExpense> expenses = getBalanceExpenseManager().getAllExpensesByFeature(item);

		int currTime = Utils.currentTimeSeconds();
		List<ReturnType<UserBalanceExpense>> retVal = new ArrayList<>();

		for ( BalanceExpense expense : expenses)
		{
			getLogger().debug("Considering expense " + expense);

			// It's more likely that there are one or two expenses associated with a given feature, so this
			// loop will be very short. If the loop was longer, it might be worth considering getting all
			// of the user's expenses and filtering the array in memory rather than hitting the database
			// on each call
			//
			UserBalanceExpense userBalanceExpense = new UserBalanceExpense();
			userBalanceExpense.setQuantity(activity.getQuantity());
			userBalanceExpense.setActivityId(activity.getSer());
			userBalanceExpense.setBalanceExpense(expense);
			userBalanceExpense.setUser(user);
			userBalanceExpense.setTimeStampSeconds(currTime);

			int amount = userBalanceExpense.getQuantity() * expense.getAmount();
			ReturnType<UserBalance> updateStatus = getUserBalanceManager().increment(
				new UserBalance(user, expense.getBalance(), currTime),
				-1 * amount);

			if ( updateStatus.getPost().exists() )
			{
				// See if we got a transaction record, and if we did we can update our record
				// with its serial number. Consider moving this to its own manager (i.e. 
				// TransactionalSimpleUserBalanceExpenseManager)
				//
				Transaction transaction = (Transaction)updateStatus.get("transaction");
				if ( transaction != null )
				{
					transaction.setDescr(new IString(""));

					getTransactionManager().update(transaction);
					userBalanceExpense.setTransaction(transaction);
				}

				// Record that we processed the expense
				//
				ReturnType<UserBalanceExpense> createVal = create(userBalanceExpense);
				createVal.addChain(getUserBalanceManager(), "increment", updateStatus);

				retVal.add(createVal);
			}
			else
			{
				throw new Errors(getInterfaceName() + ".processExpenses", new ErrorMessage("msg.rewards.userBalanceExpenseManager.create.nsf", "You do not have sufficient currency: You need $amount #IString($item.balance.formatName($amount)) to continue. Your current balance is $item.total #IString($item.balance.formatName($item.total))", new Object[] { "item", updateStatus.getPost(), "amount", amount }, null));
			}
		}

		getLogger().debug("Processed expenses = " + retVal);

		return retVal;
	}

	private <T extends SingleClientType<?>> Set<UserBalanceExpense> getUserBalanceExpenses(BalanceExpense expense, ActivityType<?, T> activity)
	{
		return convertDelegates(getDao().getByActivity(expense.getSer(), activity.getSer()));
	}

	@Transactional(readOnly = false)
	@Override
	public <T extends SingleClientType<?>> List<ReturnType<UserBalance>> revertExpenses(T item, ActivityType<?, T> activity)
	{
		getLogger().debug("Reverting expenses for {} activity record {}", item, activity);

		// Since expenses are exclusive and at the very least cannot be removed if they have activity,
		// we'll be sure to get all of the expenses associated with the given item here
		//
		Set<BalanceExpense> expenses = getBalanceExpenseManager().getAllExpensesByFeature(item);

		// We collect the credits due to a user at the end
		//
		Map<Balance, Integer> credits = expenses
			.stream()
			.collect(Collectors.toMap(
				(balanceExpense) -> { return balanceExpense.getBalance(); },
				(balanceExpense) -> { return 0; }));

		User user = new User();
		for ( BalanceExpense expense : expenses)
		{
			getLogger().debug("Considering expense " + expense);

			// NOTE: BalanceExpenses are always exclusive. As such reverting all records should
			// not result in a member having currency refunded for any expense incurred outside
			// of this item. 
			//
			Set<UserBalanceExpense> userBalanceExpenses = getUserBalanceExpenses(expense, activity);
			getLogger().debug("Got {} userbalance expenses ", userBalanceExpenses.size() );

			for ( UserBalanceExpense userExpense : userBalanceExpenses)
			{
				if ( user.exists() && !user.equals(userExpense.getUser(false)))
				{
					getLogger().error("User {} should be the same as {} but isn't", user, userExpense.getUser(false));

					throw new Errors(getInterfaceName() + ".revertExpenses", new ErrorMessage("msg.rewards.userBalanceExpenseManager.revertExpenses.error", "Unexpected error", new Object[] { "item", item, "activity", activity }, null));
				}
				else
				{
					user = userExpense.getUser(false);
					getLogger().debug("Set user to {}", user );
				}

				// The amount the user actually spent for these entries
				//
				int amount = 0;

				// We go off of the transaction log and not what the current balance expense is
				// indicating since balance expenses can be changed after users enter transactions
				//
				Transaction transaction = userExpense.getTransaction();
				if ( transaction.exists() )
				{
					// This is reflective of what the amount was at the time the transaction took place
					//
					amount = transaction.getAmountTransacted();
				}
				else
				{
					// As a back up, if there's no transaction record of how much was actually spent,
					// we can go by the current expense amount
					//
					amount += -1 * userExpense.getQuantity() * expense.getAmount();
				}

				// We add the amount the user is due for any existing amount
				//
				Integer expenseAmount =  credits.get(expense.getBalance());
				expenseAmount += amount;

				credits.put(expense.getBalance(), expenseAmount);

				// We're done with this expense, note that the transaction record
				// is left untouched for record keeping.
				//
				remove(userExpense);
			}
		}

		// At this point we should have a list of balances and amounts to be credited back to those
		// balances.
		//
		if ( user.exists() )
		{
			List<ReturnType<UserBalance>> retVal = new ArrayList<>();
			for ( Balance balance : credits.keySet() )
			{
				UserBalance userBalance = new UserBalance();
				userBalance.setUser(user);
				userBalance.setBalance(balance);

				userBalance = getUserBalanceManager().getUserBalanceForUpdate(userBalance);
				ReturnType<UserBalance> credited = getUserBalanceManager().increment(userBalance, -1 * credits.get(balance));
				retVal.add(credited);
			}

			getLogger().debug("Reverted balances = " + retVal);

			return retVal;
		}
		else
		{
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	public List<ReturnType<UserBalanceExpense>> revert(BalanceExpense item, PaginationFilter pagination)
	{
		throw new UnsupportedOperationException("Please use revertExpenses(..) instead.");
	}

	@Override
	public UserBalanceExpenseRecordDao getDao()
	{
		return (UserBalanceExpenseRecordDao) super.getDao();
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
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

	public TransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Autowired
	public void setTransactionManager(TransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}
}