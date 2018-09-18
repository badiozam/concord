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
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.GenericFeature;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleBalanceManager extends BaseSimpleManager<BalanceRecord, Balance> implements BalanceManager
{
	Map<String, BalanceAttachmentResolver<?, ?>> attachmentResolvers;

	@Override
	public void init()
	{
		super.init();

		attachmentResolvers = new HashMap<>();
	}

	@Override
	public Balance getBalance(SingleClientType<?> type)
	{
		BalanceRecord balanceRecord = getDao().getBalance(type.getSer(), type.getDelegate().getFullTableName(), new ClientPagination(type.getClient(), SimplePaginationFilter.NONE));
		if ( balanceRecord != null )
		{
			return new Balance(balanceRecord);
		}
		else
		{
			return new Balance();
		}
	}

	@Override
	public Set<Balance> getAllBalances(SingleClient client, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAllBalances(new ClientPagination(client, pagination)));
	}

	@Override
	public Balance getDefaultBalance(SingleClient client)
	{
		BalanceRecord balanceRecord = getDao().getBalance(client.getSer(), client.getDelegate().getFullTableName(), SimplePaginationFilter.SINGLE);
		if ( balanceRecord != null )
		{
			return new Balance(balanceRecord);
		}
		else
		{
			return new Balance();
		}
	}

	@Override
	public RecordTypeDelegator<?> getAttached(Balance balance)
	{
		BalanceAttachmentResolver<?, ?> resolver = attachmentResolvers.get(balance.getDelegate().getFeature());
		if ( resolver != null )
		{
			return resolver.getAttached(balance);
		}
		else
		{
			return new GenericFeature(balance.getDelegate().getFeatureid(), balance.getDelegate().getFeature());
		}
	}

	@Override
	public void removeBalanceAttachmentResolver(BalanceAttachmentResolver<?, ?> resolver)
	{
		attachmentResolvers.remove(resolver.emptyInstance().getFeatureName());
	}

	@Override
	public void setBalanceAttachmentResolver(BalanceAttachmentResolver<?, ?> resolver)
	{
		attachmentResolvers.put(resolver.emptyInstance().getFeatureName(), resolver);
	}

	@Override
	public BalanceRecordDao getDao()
	{
		return (BalanceRecordDao) super.getDao();
	}

	@Override
	public Balance emptyInstance()
	{
		return new Balance();
	}
}
