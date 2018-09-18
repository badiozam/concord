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
package com.i4one.base.model.manager;

import com.i4one.base.core.Utils;
import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.dao.ClientRecordTypeDao;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.dao.Dao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.client.ClientOption;
import com.i4one.base.model.client.ClientOptionManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.report.NullTopLevelReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;
import java.lang.reflect.InvocationTargetException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseSimpleClientTypeManager<U extends ClientRecordType, T extends SingleClientType<U>> extends BasePaginableManager<U, T>
{
	private ClientOptionManager clientOptionManager;

	public BaseSimpleClientTypeManager()
	{
	}

	protected String getEnabledOptionKey()
	{
		return getFullInterfaceName() + ".enabled";
	}
	
	/**
	 * Convenience method for updating a client option that's part of a settings object.
	 * Adds the results of the update as part of the incoming chain.
	 * 
	 * @param client The client for which to update the option
	 * @param key The key of the option
	 * @param value The value of the option
	 * @param chain The return chain where all updates are to be stored
	 */
	protected void updateOption(SingleClient client, String key, String value, ReturnType<?> chain)
	{
		ClientOption forUpdateOption = new ClientOption();
		forUpdateOption.setKey(key);
		forUpdateOption.setValue(value);
		forUpdateOption.setClient(client);

		ReturnType<ClientOption> updateResult = getClientOptionManager().update(forUpdateOption);
		chain.addChain(getClientOptionManager(), "update", updateResult);
	}

	@Override
	protected T cloneInternal(T item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		T retVal = emptyInstance();

		retVal.copyFrom(item);
		retVal.setSer(0);

		return retVal;
	}

	@Override
	public ClientRecordTypeDao<U> getDao()
	{
		Dao<U> baseDao = super.getDao();
		if (baseDao instanceof ClientRecordTypeDao)
		{
			ClientRecordTypeDao<U> clientFilterDao = (ClientRecordTypeDao<U>) baseDao;
			return clientFilterDao;
		}
		else
		{
			return null;
		}
	}

	@Override
	public TopLevelReport getReport(T item, TopLevelReport report, PaginationFilter pagination)
	{
		TopLevelReport dbReport = getReportManager().loadReport(report);

		if ( dbReport instanceof NullTopLevelReport )
		{
			if ( report.getSubReports().isEmpty())
			{
				// If the report has no demographics, it's useless. We allow
				// the caller to set custom sub-reports, demographics, etc.
				// but otherwise, we'll load the default demographics for the
				// client before beginning processing
				//
				getReportManager().populateReport(report);
				getLogger().debug("Populated report with: " + Utils.toCSV(report.getSubReports()));
			}
			else
			{
				getLogger().debug("Incoming report has the following demographics: " + Utils.toCSV(report.getSubReports()));
			}

			getDao().processReport(item.getDelegate(), report);
			getReportManager().saveReport(report);

			return report;
		}
		else
		{
			getLogger().debug("Loaded manager report successfully!");
			return dbReport;
		}
	}

	@Override
	public <R extends Object> ClassificationReport<U, R> getReport(T item, ClassificationReport<U, R> report, PaginationFilter pagination)
	{
		getDao().processReport(item.getDelegate(), report, pagination);
		return report;
	}

	@Override
	protected T initModelObject(T item)
	{
		return item;
	}

	public ClientOptionManager getClientOptionManager()
	{
		return clientOptionManager;
	}

	@Autowired
	public void setClientOptionManager(ClientOptionManager clientOptionManager)
	{
		this.clientOptionManager = clientOptionManager;
	}
}
