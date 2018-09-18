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
package com.i4one.predict.model.term;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.model.client.ClientOption;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.terminable.BaseSimpleTerminableManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class retrieves prediction terms for a given client
 *
 * @author Hamid Badiozamani
 */
public class SimpleTermManager extends BaseSimpleTerminableManager<TermRecord, Term> implements TermManager
{
	private BalanceManager balanceManager;
	private MessageManager messageManager;

	private static final String NAMESINGLE_KEY = "predict.eventManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "predict.eventManager.namePlural";

	@Override
	public Term getTerm(SingleClient client, String title)
	{
		TermRecord record = getDao().getByTitle(title, new TerminablePagination(Utils.currentTimeSeconds(), new ClientPagination(client, SimplePaginationFilter.NONE)));

		Term retVal = new Term();
		if ( record != null )
		{
			retVal.setOwnedDelegate(record);
		}

		return retVal;
	}

	@Override
	public Term getLatestTerm(SingleClient client)
	{
		Term retVal = getLiveTerm(client);

		// Maybe the term has ended and there's results that have come in after the fact,
		// we may want to compute the top players chart, or display a user's past predictions
		// until the next term begins.
		//
		if (!retVal.exists())
		{
			TerminablePagination lastTermPagination = new TerminablePagination(Utils.currentTimeSeconds(), new ClientPagination(client, new SimplePaginationFilter()));
			lastTermPagination.setOrderBy("ser DESC");
			lastTermPagination.setPerPage(1);
			lastTermPagination.setPast();

			Set<Term> lastTermSet = getByRange(lastTermPagination);
			if ( !lastTermSet.isEmpty() )
			{
				retVal = lastTermSet.iterator().next();
			}
		}

		return retVal;
	}

	@Override
	public Term getLiveTerm(SingleClient client)
	{
		Set<Term> liveTerms = getLive(new TerminablePagination(Utils.currentTimeSeconds(), new ClientPagination(client, SimplePaginationFilter.NONE)));

		if ( liveTerms.isEmpty() )
		{
			Term retVal = new Term();
			retVal.setClient(client);

			return retVal;
		}
		else
		{
			return liveTerms.iterator().next();
		}
	}

	@Override
	public TermSettings getSettings(SingleClient client)
	{
		TermSettings retVal = new TermSettings();
		retVal.setClient(client);

		boolean enabled = Utils.defaultIfNaB(getClientOptionManager().getOption(client, getEnabledOptionKey()).getValue(), true);
		retVal.setEnabled(enabled);

		boolean injectPrivateMessages = Utils.defaultIfNaB(getClientOptionManager().getOption(client, getInjectPrivateMessagesKey()).getValue(), true);
		retVal.setInjectPrivateMessages(injectPrivateMessages);

		int minBid = Utils.defaultIfNaN(getClientOptionManager().getOption(client, getDefaultMinBidKey()).getValue(), 1);
		retVal.setDefaultMinBid(minBid);

		int maxBid = Utils.defaultIfNaN(getClientOptionManager().getOption(client, getDefaultMaxBidKey()).getValue(), 1000);
		retVal.setDefaultMaxBid(maxBid);

		List<Message> singularNames = getMessageManager().getAllMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = getMessageManager().getAllMessages(client, NAMEPLURAL_KEY);
		retVal.setNames(singularNames, pluralNames);

		String csvBalanceExpenses = Utils.forceEmptyStr(getClientOptionManager().getOption(client, getDefaultBalanceExpensesKey()).getValue());
		Set<BalanceExpense> expenses = new LinkedHashSet<>();
		Balance defaultBalance = getBalanceManager().getDefaultBalance(client);
		for (String balanceExpenseStr : csvBalanceExpenses.split("\n"))
		{
			BalanceExpense expense = new BalanceExpense();
			if ( !Utils.isEmpty(balanceExpenseStr) && expense.fromCSV(balanceExpenseStr) )
			{
				if ( !expense.getBalance(false).exists())
				{
					expense.setBalance(defaultBalance);
				}
				getLogger().debug("Adding expense {}", expense);
				expenses.add(expense);
			}
		}
		retVal.setDefaultExpenses(expenses);

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<TermSettings> updateSettings(TermSettings settings)
	{
		ReturnType<TermSettings> retVal = new ReturnType<>(settings);

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		// Enabled/Disabled option
		//
		updateOption(client, getEnabledOptionKey(), String.valueOf(settings.isEnabled()), retVal);

		// Inject private messages option
		//
		updateOption(client, getInjectPrivateMessagesKey(), String.valueOf(settings.isInjectPrivateMessages()), retVal);

		// Min bid option
		//
		updateOption(client, getDefaultMinBidKey(), String.valueOf(settings.getDefaultMinBid()), retVal);

		// Max bid option
		//
		updateOption(client, getDefaultMaxBidKey(), String.valueOf(settings.getDefaultMaxBid()), retVal);

		// Default expenses option
		//
		StringBuilder expensesCSV = new StringBuilder("");
		for ( BalanceExpense expense : settings.getFilteredExpenses() )
		{
			expensesCSV.append(expense.toCSV(false));
			expensesCSV.append('\n');
		}
		updateOption(client, getDefaultBalanceExpensesKey(), expensesCSV.toString(), retVal);

		List<Message> singularNames = settings.getNameSingleMessages(client, NAMESINGLE_KEY);
		singularNames.forEach( (message) -> { getMessageManager().update(message); } );

		List<Message> pluralNames = settings.getNamePluralMessages(client, NAMEPLURAL_KEY);
		pluralNames.forEach( (message) -> { getMessageManager().update(message); } );

		retVal.setPost(settings);

		return retVal;
	}

	protected String getDefaultBalanceExpensesKey()
	{
		return getFullInterfaceName() + ".defaultBalanceExpenses";
	}
	
	protected String getDefaultMinBidKey()
	{
		return getFullInterfaceName() + ".defaultMinBid";
	}
	
	protected String getDefaultMaxBidKey()
	{
		return getFullInterfaceName() + ".defaultMaxBid";
	}
	
	protected String getInjectPrivateMessagesKey()
	{
		return getFullInterfaceName() + ".injectPrivateMessages";
	}

	@Override
	public TermRecordDao getDao()
	{
		return (TermRecordDao) super.getDao();
	}

	@Override
	public Term emptyInstance()
	{
		return new Term();
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}
}