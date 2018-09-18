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
package com.i4one.base.model.client;

import com.i4one.base.dao.Dao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A client manager that does nothing. This is needed mainly for bootstrapping purposes
 * as there is a circular dependency with ReadOnlySingleClientManager:
 * 
 * 	ReadOnlySingleClientManager &gt; CachedSingleClientManager &gt;
 * HistoricalClientOptionManager &gt; ReadOnlyMessageManager &gt; CachedMessageManager &gt;
 * VelocityConfig &gt; MessageResourceLoader &gt; ReadOnlySingleClientManager
 * 
 * @author Hamid Badiozamani
 */
public class NullSingleClientManager implements SingleClientManager
{

	@Override
	public SingleClient getClient(String name)
	{
		return emptyInstance();
	}

	@Override
	public SingleClient getClientByDomain(String domain)
	{
		return emptyInstance();
	}

	@Override
	public SingleClient getClient(int ser)
	{
		return emptyInstance();
	}

	@Override
	public SingleClient getParent(SingleClient client)
	{
		return emptyInstance();
	}

	@Override
	public List<SingleClient> getAllClients(SingleClient parent, PaginationFilter pagination)
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public ClientSettings getSettings(SingleClient client)
	{
		return new ClientSettings();
	}

	@Override
	public ReturnType<ClientSettings> updateSettings(ClientSettings settings)
	{
		return new ReturnType<>();
	}

	@Override
	public void init()
	{
	}

	@Override
	public void init(Map<String, Object> options)
	{
	}

	@Override
	public SingleClient emptyInstance()
	{
		return new SingleClient();
	}

	@Override
	public SingleClientRecord lock(SingleClient item)
	{
		return null;
	}

	@Override
	public SingleClient getById(int id)
	{
		return emptyInstance();
	}

	@Override
	public void loadById(SingleClient item, int id)
	{
	}

	@Override
	public ReturnType<SingleClient> create(SingleClient item)
	{
		return new ReturnType<>();
	}

	@Override
	public ReturnType<SingleClient> update(SingleClient item)
	{
		return new ReturnType<>();
	}

	@Override
	public SingleClient remove(SingleClient item)
	{
		return emptyInstance();
	}

	@Override
	public ReturnType<SingleClient> clone(SingleClient item)
	{
		return new ReturnType<>();
	}

	@Override
	public TopLevelReport getReport(SingleClient item, TopLevelReport report, PaginationFilter pagination)
	{
		throw new UnsupportedOperationException("Null client manager");
	}

	@Override
	public <R extends Object> ClassificationReport<SingleClientRecord, R> getReport(SingleClient item, ClassificationReport<SingleClientRecord, R> report, PaginationFilter pagination)
	{
		throw new UnsupportedOperationException("Null client manager.");
	}

	@Override
	public List<ReturnType<SingleClient>> importCSV(InputStream importStream, Supplier<SingleClient> instantiator, Function<SingleClient,Boolean> preprocessor, Consumer<ReturnType<SingleClient>> postprocessor)
	{
		throw new UnsupportedOperationException("Null client manager");
	}

	@Override
	public OutputStream exportCSV(Set<SingleClient> items, OutputStream out)
	{
		throw new UnsupportedOperationException("Null client manager");
	}

	@Override
	public String getInterfaceName()
	{
		return "";
	}

	@Override
	public Dao<SingleClientRecord> getDao()
	{
		return null;
	}
}
