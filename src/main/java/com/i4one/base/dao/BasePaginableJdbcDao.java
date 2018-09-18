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

import com.i4one.base.core.Pair;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * A DAO that provides convenience methods for applying PaginationFilters to queries.
 * Subclasses must be instantiated as singletons and must not maintain any stateful
 * attributes.
 * 
 * @author Hamid Badiozamani
 * 
 * @param <T> The record type to retrieve via JDBC
 */
public abstract class BasePaginableJdbcDao<T extends RecordType> extends BaseJdbcDao<T> implements PaginableRecordTypeDao<T>
{
	public BasePaginableJdbcDao()
	{
		super();

		getLogger().debug("Instantiated " + getClass().getSimpleName());
	}

	@Override
	public void init()
	{
		super.init();
	}

	/**
	 * Queries for a set of objects using the given query and parameters
	 *
	 * @param query The query to execute.
	 * @param sqlParams The parameters to use in the query.
	 * @param pagination The pagination filter to use for sorting and limits.
	 *
	 * @return A (potentially) empty list of items retrieved from the database
	 */
	public List<T> query(String query, MapSqlParameterSource sqlParams, PaginationFilter pagination)
	{
		return queryGeneric(query, sqlParams, pagination, getMapper());
	}

	/**
	 * Queries for a generic single object using the given query and parameters.
	 *
	 * @param query The query to execute
	 * @param sqlParams The parameters to use in the query
	 * @param pagination The pagination filter to use when retrieving the item
	 *
	 * @return The first entry of items retrieved from the database or null if the query
	 * 	returned no objects
	 */
	public T querySingle(String query, MapSqlParameterSource sqlParams, PaginationFilter pagination)
	{
		// Force a limit of 1 since we're only looking for one object to
		// increase performance.
		//
		List<T> retVal = queryGeneric(query, sqlParams, new SimplePaginationFilter(0, 1, pagination), getMapper());

		// See if there were any results returned
		//
		if ( !retVal.isEmpty())
		{
			// Return the first element even if there were multiple results returned
			//
			return retVal.get(0);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Queries for a set of objects using the given query and pagination filter
	 *
	 * @param <U> The type of list to return
	 * @param query The query to execute
	 * @param sqlParams The parameters to use in the query
	 * @param pagination The pagination filter to use when building the query
	 * @param mapper The mapper object that converts the ResultSet values to objects
	 *
	 * @return A (potentially) empty list of items retrieved from the database
	 */
	public <U extends Object> List<U> queryGeneric(String query, MapSqlParameterSource sqlParams, PaginationFilter pagination, RowMapper<U> mapper)
	{
		Pair<String, MapSqlParameterSource> exec = pagination.configureSQL(query, sqlParams);

		String execQuery = exec.getKey();
		MapSqlParameterSource execParams = exec.getValue();

		getLogger().trace("Executing query '" + execQuery + "' with params: " + execParams.getValues());
		return super.directQuery(execQuery, execParams, mapper);
	}

	@Override
	public List<T> getAll(PaginationFilter pagination)
	{
		return query("SELECT * FROM " + getEmpty().getFullTableName(), new MapSqlParameterSource(), pagination);
	}
}
