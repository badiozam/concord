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
package com.i4one.base.dao.terminable;

import com.i4one.base.dao.BaseClientJdbcDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * These DAOs provide functionality for terminable types (i.e. those that have a start and end time)
 * 
 * @author Hamid Badiozamani
 * 
 * @param <T> The specific terminable type to be retrieved via JDBC
 */
public abstract class BaseTerminableClientJdbcDao<T extends TerminableClientRecordType> extends BaseClientJdbcDao<T> implements TerminableClientRecordTypeDao<T>
{
	public BaseTerminableClientJdbcDao()
	{
	}

	@Override
	protected abstract TerminableClientRecordRowMapper<T> initMapper();

	@Override
	public List<T> getByRange(Integer startSeconds, Integer endSeconds, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("starttm", startSeconds);
		sqlParams.addValue("endtm", endSeconds);

		StringBuilder conditions = new StringBuilder();
		if ( startSeconds != null )
		{
			conditions.append("starttm > :starttm");
			if ( endSeconds != null )
			{
				conditions.append(" AND ");
			}
		}

		if ( endSeconds != null )
		{
			conditions.append("endtm < :endtm");
		}

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE " + conditions.toString(), sqlParams, pagination);
	}

	@Override
	public List<T> getByRange(TerminablePagination pagination)
	{
		return getByRange(pagination.getStartTime(), pagination.getEndTime(), pagination);
	}

	@Override
	public List<T> getLive(int asOf, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("currtime", asOf);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE starttm <= :currtime AND endtm >= :currtime", sqlParams, pagination);
	}

	@Override
	public List<T> getLive(TerminablePagination pagination)
	{
		return getLive(pagination.getCurrentTime(), pagination);
	}
}
