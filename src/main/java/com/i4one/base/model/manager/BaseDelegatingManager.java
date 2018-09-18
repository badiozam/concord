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

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.dao.Dao;
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.RecordTypeDelegator;
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
import javax.annotation.PostConstruct;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseDelegatingManager<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseLoggable implements DelegatingManager<U, T>
{
	protected void initInternal(Map<String, Object> options)
	{
	}

	@PostConstruct
	@Override
	public void init()
	{
		getLogger().debug("Created manager " + getClass().getSimpleName() + ": " + this);
		initInternal(Collections.emptyMap());
	}

	@Override
	public void init(Map<String, Object> options)
	{
		getImplementationManager().init(options);

		initInternal(options);
	}


	@Override
	public U lock(T item)
	{
		return getImplementationManager().lock(item);
	}

	@Override
	public T getById(int id)
	{
		return initModelObject(getImplementationManager().getById(id));
	}

	@Override
	public void loadById(T item, int id)
	{
		getImplementationManager().loadById(item, id);
		initModelObject(item);
	}

	@Override
	public ReturnType<T> create(T item)
	{
		ReturnType<T> retVal = getImplementationManager().create(item);
		initModelObject(retVal.getPost());

		return retVal;
	}

	@Override
	public T remove(T item)
	{
		return getImplementationManager().remove(item);
	}

	@Override
	public ReturnType<T> update(T item)
	{
		ReturnType<T> retVal = getImplementationManager().update(item);
		initModelObject(retVal.getPost());

		return retVal;
	}

	@Override
	public ReturnType<T> clone(T item)
	{
		ReturnType<T> retVal = getImplementationManager().clone(item);
		initModelObject(retVal.getPost());

		return retVal;
	}

	@Override
	public TopLevelReport getReport(T item, TopLevelReport report, PaginationFilter pagination)
	{
		return getImplementationManager().getReport(item, report, pagination);
	}

	@Override
	public <R extends Object> ClassificationReport<U, R> getReport(T item, ClassificationReport<U, R> report, PaginationFilter pagination)
	{
		return getImplementationManager().getReport(item, report, pagination);
	}

	@Override
	public List<ReturnType<T>> importCSV(InputStream importStream, Supplier<T> instantiator, Function<T,Boolean> preprocessor, Consumer<ReturnType<T>> postprocessor)
	{
		return getImplementationManager().importCSV(importStream, instantiator, preprocessor, postprocessor);
	}

	@Override
	public OutputStream exportCSV(Set<T> items, OutputStream out)
	{
		return getImplementationManager().exportCSV(items, out);
	}

	@Override
	public String getInterfaceName()
	{
		return getImplementationManager().getInterfaceName();
	}

	@Override
	public T emptyInstance()
	{
		return getImplementationManager().emptyInstance();
	}

	@Override
	public Dao<U> getDao()
	{
		return getImplementationManager().getDao();
	}

	/**
	 * Initialize a model after it has been loaded but before it is returned.
	 * The implementation manager has its own version, ours allows any new
	 * functionality to be added.
	 * 
	 * @param item The item to initialize
	 * 
	 * @return The input parameter that is now newly
	 */
	protected T initModelObject(T item)
	{
		return item;
	}

	protected Set<T> initSet(Set<T> implSet)
	{
		implSet.forEach( (item) -> { initModelObject(item); });

		return implSet;
	}

}
