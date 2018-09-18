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

import com.i4one.base.core.Pair;
import com.i4one.base.core.Utils;
import com.i4one.base.dao.qualifier.ColumnQualifier;
import com.i4one.base.dao.qualifier.SimpleColumnQualifier;
import java.util.Collection;
import java.util.Iterator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * @author Hamid Badiozamani
 */
public class SimplePaginationFilter extends BasePaginationFilter implements PaginationFilter, ChainingPaginationFilter
{
	static final long serialVersionUID = 42L;

	private int offset;
	private int totalPages;
	private int limit;

	private ColumnQualifier columnQualifier;

	private final PaginationFilter chain;

	public static final PaginationFilter SINGLE;
	public static final PaginationFilter NONE;
	public static final PaginationFilter DESC;

	public static final PaginationFilter ORDERWEIGHT;
	
	static
	{
		// The "NONE" pagination orders by ascending
		// serial number
		//
		PaginationFilter none = new SimplePaginationFilter();

		none.setCurrentPage(0);
		none.setPerPage(0);
		none.setTotalPages(0);
		none.setOrderBy("ser");

		NONE = new UnmodifiablePaginationFilter(none);

		// The "DESC" pagination orders by descending
		//serial number
		//
		PaginationFilter desc = new SimplePaginationFilter();

		desc.setCurrentPage(0);
		desc.setPerPage(0);
		desc.setTotalPages(0);
		desc.setOrderBy("ser DESC");

		DESC = new UnmodifiablePaginationFilter(desc);

		// The "DESC" pagination orders by descending
		//serial number
		//
		PaginationFilter orderWeight = new SimplePaginationFilter();

		orderWeight.setCurrentPage(0);
		orderWeight.setPerPage(0);
		orderWeight.setTotalPages(0);
		orderWeight.setOrderBy("orderweight DESC, ser");

		ORDERWEIGHT = new UnmodifiablePaginationFilter(orderWeight);

		// The "SINGLE" pagination orders by ascending
		// serial number but only returns a single item
		// at a time
		//
		PaginationFilter single = new SimplePaginationFilter();

		single.setCurrentPage(0);
		single.setPerPage(1);
		single.setTotalPages(0);
		single.setOrderBy("ser");

		SINGLE = new UnmodifiablePaginationFilter(single);
	}

	public SimplePaginationFilter()
	{
		this.chain = null;

		offset = 0;
		limit = 50;
		totalPages = 0;

		columnQualifier = new SimpleColumnQualifier();
	}

	public SimplePaginationFilter(int offset, int limit, PaginationFilter chain)
	{
		if ( chain == this )
		{
			throw new IllegalArgumentException("Can't use self referential chain");
		}
		else
		{
			this.offset = offset;
			this.limit = limit;

			this.chain = chain;
			columnQualifier = new SimpleColumnQualifier();
		}
	}

	public SimplePaginationFilter(PaginationFilter chain)
	{
		super();

		if ( chain == this )
		{
			throw new IllegalArgumentException("Can't use self referential chain");
		}
		else
		{
			this.chain = chain;

			offset = chain.getOffset();
			limit = chain.getLimit();
			setOrderBy(chain.getOrderBy());

			totalPages = 0;

			columnQualifier = new SimpleColumnQualifier();
		}
	}

	public Object getQualifier(String columnName)
	{
		return getColumnQualifier().getQualifier(columnName);
	}

	public void setQualifier(String columnName, Object columnValue)
	{
		getColumnQualifier().setQualifier(columnName, columnValue);
	}

	@Override
	public ColumnQualifier getColumnQualifier()
	{
		return columnQualifier;
	}

	@Override
	public Pair<String, MapSqlParameterSource> configureSQL(String sql, MapSqlParameterSource sqlParams)
	{
		String query = Utils.makeSQLwithOffset(buildColumnQualifierSQL(sql).toString(), getOffset(), getLimit(), getOrderBy(), true);
		MapSqlParameterSource params = configureSQLParams(sqlParams);

		return new Pair<>(query, params);
	}

	@Override
	public MapSqlParameterSource configureSQLParams(MapSqlParameterSource sqlParams)
	{
		if ( chain != null )
		{
			if ( chain instanceof ChainingPaginationFilter )
			{
				ChainingPaginationFilter simpleChain = (ChainingPaginationFilter)chain;
				simpleChain.configureSQLParams(sqlParams);
			}
			else
			{
				throw new UnsupportedOperationException("No support for chains that do not implement ChainingPaginationFilter");
			}
			// If we have a chain add those parameters too
			//
		}

		// Add all of the column parameter values we may have added to the query
		//
		sqlParams.addValues(getColumnQualifier().getAllQualifiers());

		return sqlParams;
	}

	protected void initQualifiers()
	{
	}

	/**
	 * Build a given query by adding any custom column qualifier
	 * 
	 * @param query The incoming query
	 * 
	 * @return The query with any column qualifier appended as part of the conditions
	 */
	@Override
	public StringBuilder buildColumnQualifierSQL(String query)
	{
		StringBuilder retVal = new StringBuilder(query);
		if ( !query.toLowerCase().contains("where") )
		{
			retVal.append(" WHERE 0 = 0");
		}

		return buildColumnQualifierSQLChain(retVal);
	}

	@Override
	public StringBuilder buildColumnQualifierSQLChain(StringBuilder query)
	{
		if ( chain != null )
		{
			if ( chain instanceof ChainingPaginationFilter )
			{
				ChainingPaginationFilter simpleChain = (ChainingPaginationFilter)chain;
				simpleChain.buildColumnQualifierSQLChain(query);
			}
			else
			{
				throw new UnsupportedOperationException("No support for chains that do not implement ChainingPaginationFilter");
			}
		}

		initQualifiers();
		return query.append(getColumnQualifier().buildSQL());
	}

	/**
	 * Initialize the structure after updating any of the internal
	 * state items
	 */
	protected void init()
	{
	}

	@Override
	public int getCurrentPage()
	{
		if ( chain != null )
		{
			return chain.getCurrentPage();
		}
		else
		{
			if ( limit == 0 )
			{
				// No limit means we're always on the first page
				//
				return 0;
			}
			else
			{
				return offset / limit;
			}
		}
	}

	@Override
	public void setCurrentPage(int currentPage)
	{
		if ( chain != null )
		{
			chain.setCurrentPage(currentPage);
		}
		else
		{
			this.offset = currentPage * limit;
			init();
		}
	}

	@Override
	public boolean getNextPage()
	{
		if ( chain != null )
		{
			return chain.getNextPage();
		}
		else
		{
			return false;
		}
	}

	@Override
	public void setNextPage(boolean nextPage)
	{
		if ( chain != null )
		{
			chain.setNextPage(nextPage);
		}
		else
		{
			if ( nextPage )
			{
				setCurrentPage(getCurrentPage() + 1);
				init();
			}
		}
	}

	@Override
	public boolean getPrevPage()
	{
		if ( chain != null )
		{
			return chain.getPrevPage();
		}
		else
		{
			return false;
		}
	}

	@Override
	public void setPrevPage(boolean prevPage)
	{
		if ( chain != null )
		{
			setPrevPage(prevPage); 
		}
		else
		{
			if ( prevPage && getCurrentPage() > 0 )
			{
				setCurrentPage(getCurrentPage() - 1);
				init();
			}
		}
	}

	@Override
	public int getPerPage()
	{
		if ( chain != null )
		{
			return chain.getPerPage();
		}
		else
		{
			return limit;
		}
	}

	@Override
	public void setPerPage(int perPage)
	{
		if ( chain != null )
		{
			chain.setPerPage(perPage);
		}
		else
		{
			int currentPage = getCurrentPage();

			this.limit = perPage;
			this.offset = currentPage * perPage;

			init();
		}
	}

	@Override
	public int getOffset()
	{
		if ( chain != null )
		{
			return chain.getOffset();
		}
		else
		{
			return offset;
		}
	}

	@Override
	public void setOffset(int offset)
	{
		if ( chain != null )
		{
			chain.setOffset(offset);
		}
		else
		{
			this.offset = offset;
			init();
		}
	}

	@Override
	public int getLimit()
	{
		if ( chain != null )
		{
			return chain.getLimit();
		}
		else
		{
			return limit;
		}
	}

	@Override
	public void setLimit(int limit)
	{
		if ( chain != null )
		{
			chain.setLimit(limit);
		}
		else
		{
			this.limit = limit;
			init();
		}
	}

	@Override
	public int getTotalPages()
	{
		if ( chain != null )
		{
			return chain.getTotalPages();
		}
		else
		{
			return totalPages;
		}
	}

	@Override
	public void setTotalPages(int totalPages)
	{
		if ( chain != null )
		{
			chain.setTotalPages(totalPages);
		}
		else
		{
			this.totalPages = totalPages;
			init();
		}
	}

	@Override
	public <T extends Collection<?>> T apply(T collection)
	{
		int max = getPerPage();
		if ( collection.size() > max )
		{
			Iterator<?> it = collection.iterator();
			for (int i = 0; i < max; i++ )
			{
				it.next();
			}

			// Remove everything that comes after the limit has been
			// reached
			//
			while ( it.hasNext() )
			{
				it.next();
				it.remove();
			}

		}

		return collection;
	}

	@Override
	protected String toStringInternal()
	{
		if ( getLogger().isTraceEnabled() )
		{
			return "currentPage:" + getCurrentPage() + ", perPage:" + getPerPage() + ", chain:" + chain;
		}
		else
		{
			return "c:" + getCurrentPage() + ",p:" + getPerPage() + ",ch:" + chain;
		}
	}
}
