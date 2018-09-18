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

import com.i4one.base.model.MessageKeySettings;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.Message;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.util.AutoPopulatingList;
import org.springframework.util.AutoPopulatingList.ElementFactory;

/**
 * @author Hamid Badiozamani
 */
public class TermSettings extends MessageKeySettings implements ElementFactory<BalanceExpense>
{
	private SingleClient client;

	private IString nameSingle;
	private IString namePlural;

	private int defaultMinBid;
	private int defaultMaxBid;

	public boolean injectPrivateMessages;

	private final List<BalanceExpense> defaultExpenses;

	public TermSettings()
	{
		enabled = true;
		injectPrivateMessages = false;

		defaultExpenses = new AutoPopulatingList<>(this);

		defaultMinBid = 1;
		defaultMaxBid = 1000;
	}

	public SingleClient getClient()
	{
		return client;
	}

	public void setClient(SingleClient client)
	{
		this.client = client;
	}

	public List<Message> getNameSingleMessages(SingleClient client, String key)
	{
		return getMessages(client, key, nameSingle);
	}

	public List<Message> getNamePluralMessages(SingleClient client, String key)
	{
		return getMessages(client, key, namePlural);
	}

	public void setNames(List<Message> singleNames, List<Message> pluralNames)
	{
		setNameSingle(messagesToIString(singleNames));
		setNamePlural(messagesToIString(pluralNames));
	}

	@Override
	public IString getNameSingle()
	{
		return nameSingle;
	}

	public void setNameSingle(IString nameSingle)
	{
		this.nameSingle = nameSingle;
	}

	@Override
	public IString getNamePlural()
	{
		return namePlural;
	}

	public void setNamePlural(IString namePlural)
	{
		this.namePlural = namePlural;
	}

	public List<BalanceExpense> getDefaultExpenses()
	{
		return defaultExpenses;
	}

	public List<BalanceExpense> getFilteredExpenses()
	{
		List<BalanceExpense> retVal = new ArrayList<>();
		for ( BalanceExpense expense : defaultExpenses )
		{
			if ( expense != null && expense.getAmount() > 0 )
			{
				retVal.add(expense);
			}
		}

		return retVal;
	}

	public void setDefaultExpenses(Collection<BalanceExpense> newExpenses)
	{
		defaultExpenses.clear();
		for( BalanceExpense expense : newExpenses)
		{
			defaultExpenses.add(expense);
		}
	}

	// Aliases to allow for easier code reuse between settings and
	// expendableCrud in the settings page
	//
	public List<BalanceExpense> getExpenses()
	{
		return getDefaultExpenses();
	}

	public void setExpenses(List<BalanceExpense> expenses)
	{
		setDefaultExpenses(expenses);
	}

	public int getDefaultMinBid()
	{
		return defaultMinBid;
	}

	public void setDefaultMinBid(int defaultMinBid)
	{
		this.defaultMinBid = defaultMinBid;
	}

	public int getDefaultMaxBid()
	{
		return defaultMaxBid;
	}

	public void setDefaultMaxBid(int defaultMaxBid)
	{
		this.defaultMaxBid = defaultMaxBid;
	}

	public boolean getInjectPrivateMessages()
	{
		return isInjectPrivateMessages();
	}

	public boolean isInjectPrivateMessages()
	{
		return injectPrivateMessages;
	}

	public void setInjectPrivateMessages(boolean injectPrivateMessages)
	{
		this.injectPrivateMessages = injectPrivateMessages;
	}

	@Override
	public BalanceExpense createElement(int i) throws AutoPopulatingList.ElementInstantiationException
	{
		BalanceExpense expense = new BalanceExpense();

		return expense;
	}
}
