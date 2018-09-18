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
package com.i4one.base.model.manager.pagination;

import com.i4one.base.core.Base;
import com.i4one.base.core.Pair;
import com.i4one.base.dao.qualifier.ColumnQualifier;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * @author Hamid Badiozamani
 */
public class UnmodifiablePaginationFilter implements PaginationFilter, ChainingPaginationFilter
{
	private final PaginationFilter pagination;

	public UnmodifiablePaginationFilter(PaginationFilter delegate)
	{
		this.pagination = delegate;
	}

	@Override
	public Pair<String, MapSqlParameterSource> configureSQL(String sql, MapSqlParameterSource sqlParams)
	{
		return pagination.configureSQL(sql, sqlParams);
	}

	@Override
	public int getCurrentPage()
	{
		return pagination.getCurrentPage();
	}

	@Override
	public void setCurrentPage(int currentPage)
	{
		throw new UnsupportedOperationException("Unmodifiable pagination.");
	}

	@Override
	public int getPerPage()
	{
		return pagination.getPerPage();
	}

	@Override
	public void setPerPage(int perPage)
	{
		throw new UnsupportedOperationException("Unmodifiable pagination.");
	}

	@Override
	public int getTotalPages()
	{
		return pagination.getTotalPages();
	}

	@Override
	public void setTotalPages(int totalPages)
	{
		throw new UnsupportedOperationException("Unmodifiable pagination.");
	}

	@Override
	public String getOrderBy()
	{
		return pagination.getOrderBy();
	}

	@Override
	public void setOrderBy(String orderBy)
	{
		throw new UnsupportedOperationException("Unmodifiable pagination.");
	}

	@Override
	public boolean getNextPage()
	{
		return pagination.getNextPage();
	}

	@Override
	public void setNextPage(boolean nextPage)
	{
		throw new UnsupportedOperationException("Unmodifiable pagination.");
	}

	@Override
	public boolean getPrevPage()
	{
		return pagination.getPrevPage();
	}

	@Override
	public void setPrevPage(boolean prevPage)
	{
		throw new UnsupportedOperationException("Unmodifiable pagination.");
	}

	@Override
	public ColumnQualifier getColumnQualifier()
	{
		return pagination.getColumnQualifier();
	}

	@Override
	public <T extends Collection<?>> T apply(T collection)
	{
		return pagination.apply(collection);
	}

	@Override
	public int getOffset()
	{
		return pagination.getOffset();
	}

	@Override
	public void setOffset(int offset)
	{
		throw new UnsupportedOperationException("Unmodifiable pagination.");
	}

	@Override
	public int getLimit()
	{
		return pagination.getLimit();
	}

	@Override
	public void setLimit(int limit)
	{
		throw new UnsupportedOperationException("Unmodifiable pagination.");
	}

	private ChainingPaginationFilter getChainingPaginationFilter()
	{
		if ( pagination instanceof ChainingPaginationFilter )
		{
			return (ChainingPaginationFilter)pagination;
		}
		else
		{
			throw new UnsupportedOperationException("Underlying pagination is not of type ChainingPaginationFilter");
		}
	}

	@Override
	public StringBuilder buildColumnQualifierSQLChain(StringBuilder query)
	{
		return getChainingPaginationFilter().buildColumnQualifierSQLChain(query);
	}

	@Override
	public MapSqlParameterSource configureSQLParams(MapSqlParameterSource sqlParams)
	{
		return getChainingPaginationFilter().configureSQLParams(sqlParams);
	}

	@Override
	public StringBuilder buildColumnQualifierSQL(String query)
	{
		return getChainingPaginationFilter().buildColumnQualifierSQL(query);
	}

	@Override
	public void fromJSONString(String json) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		throw new UnsupportedOperationException("Unmodifiable pagination.");
	}

	@Override
	public String toJSONString()
	{
		try
		{
			return Base.getInstance().getJacksonObjectMapper().writeValueAsString(this);
		}
		catch (IOException ex)
		{
			return ex.getLocalizedMessage();
		}
	}
}
