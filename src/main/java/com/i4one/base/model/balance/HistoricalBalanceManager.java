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
package com.i4one.base.model.balance;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.PrivilegedManager;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("base.BalanceManager")
public class HistoricalBalanceManager extends BaseHistoricalManager<BalanceRecord, Balance> implements BalanceManager
{
	private BalanceManager privilegedBalanceManager;

	@Override
	public PrivilegedManager<BalanceRecord, Balance> getImplementationManager()
	{
		return (PrivilegedManager<BalanceRecord, Balance>) getPrivilegedBalanceManager();
	}

	@Override
	public Balance getBalance(SingleClientType<?> type)
	{
		return getBalanceManager().getBalance(type);
	}

	@Override
	public Set<Balance> getAllBalances(SingleClient client, PaginationFilter pagination)
	{
		return getBalanceManager().getAllBalances(client, pagination);
	}

	@Override
	public Balance getDefaultBalance(SingleClient client)
	{
		return getBalanceManager().getDefaultBalance(client);
	}

	@Override
	public RecordTypeDelegator<?> getAttached(Balance balance)
	{
		return getBalanceManager().getAttached(balance);
	}

	@Override
	public void removeBalanceAttachmentResolver(BalanceAttachmentResolver<?, ?> resolver)
	{
		getBalanceManager().removeBalanceAttachmentResolver(resolver);
	}

	@Override
	public void setBalanceAttachmentResolver(BalanceAttachmentResolver<?, ?> resolver)
	{
		getBalanceManager().setBalanceAttachmentResolver(resolver);
	}

	public BalanceManager getBalanceManager()
	{
		return getPrivilegedBalanceManager();
	}

	public BalanceManager getPrivilegedBalanceManager()
	{
		return privilegedBalanceManager;
	}

	@Autowired
	public <P extends BalanceManager & PrivilegedManager<BalanceRecord, Balance>>
	 void setPrivilegedBalanceManager(P privilegedBalanceManager)
	{
		this.privilegedBalanceManager = privilegedBalanceManager;
	}
}
