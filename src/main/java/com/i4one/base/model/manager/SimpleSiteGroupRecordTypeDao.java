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

import com.i4one.base.dao.BaseSiteGroupJdbcDao;
import com.i4one.base.dao.SiteGroupRecordRowMapper;
import com.i4one.base.dao.SiteGroupRecordType;
import com.i4one.base.dao.SiteGroupRecordTypeDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.TopLevelReport;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class SimpleSiteGroupRecordTypeDao<T extends SiteGroupRecordType> extends BaseSiteGroupJdbcDao<T> implements SiteGroupRecordTypeDao<T>
{
	private final SiteGroupRecordRowMapper<T> mapper;
	private final SiteGroupRecordTypeDao<T> delegate;

	public SimpleSiteGroupRecordTypeDao(SiteGroupRecordRowMapper<T> mapper, SiteGroupRecordTypeDao<T> delegate)
	{
		this.mapper = mapper;
		this.delegate = delegate;
	}

	@Override
	protected SiteGroupRecordRowMapper<T> initMapper()
	{
		return this.mapper;
	}

	@Override
	public T getBySer(int ser)
	{
		return getDelegate().getBySer(ser);
	}

	@Override
	public T getBySer(int ser, boolean forUpdate)
	{
		return getDelegate().getBySer(ser, forUpdate);
	}

	@Override
	public void insert(T t)
	{
		getDelegate().insert(t);
	}

	@Override
	public void updateBySer(T t)
	{
		getDelegate().updateBySer(t);
	}

	@Override
	public void updateBySer(T t, String... columnList)
	{
		getDelegate().updateBySer(t, columnList);
	}

	@Override
	public void deleteBySer(int ser)
	{
		getDelegate().deleteBySer(ser);
	}

	@Override
	public List<T> getAll(PaginationFilter pagination)
	{
		return getDelegate().getAll(pagination);
	}

	@Override
	public String getOrderBy()
	{
		return getDelegate().getOrderBy();
	}

	@Override
	public void processReport(T sample, TopLevelReport report)
	{
		getDelegate().processReport(sample, report);
	}

	public SiteGroupRecordTypeDao<T> getDelegate()
	{
		return delegate;
	}
}