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
package com.i4one.base.dao;

import com.i4one.base.core.BaseLoggable;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.cache.Cache;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * @author Hamid Badiozamani
 */
public class CachedRowMapper<T extends RecordType> extends BaseLoggable implements DelegatingRecordTypeRowMapper<T>
{
	private RecordTypeRowMapper<T> mapper;
	private Cache cache;

	public CachedRowMapper()
	{
	}

	@Override
	public T emptyInstance()
	{
		return getMapper().emptyInstance();
	}

	@Override
	public MapSqlParameterSource getSqlParameterSource(T o)
	{
		return getMapper().getSqlParameterSource(o);
	}

	@Override
	public T mapRow(ResultSet rs, int i) throws SQLException
	{
		T retVal = getMapper().mapRow(rs, i);

		// This caches the first row that comes out of any given query.
		//
		if ( i < 1 )
		{
			getCache().put(retVal.makeKey(), retVal);
		}

		return retVal;
	}

	@Override
	public RecordTypeRowMapper<T> getMapper()
	{
		return mapper;
	}

	@Override
	public void setMapper(RecordTypeRowMapper<T> mapper)
	{
		this.mapper = mapper;
	}

	public Cache getCache()
	{
		return cache;
	}

	public void setCache(Cache cache)
	{
		this.cache = cache;
	}

}
