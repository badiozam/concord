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
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Pair;
import com.i4one.base.core.Utils;
import com.i4one.base.dao.qualifier.ColumnQualifier;
import com.i4one.base.dao.qualifier.SimpleColumnQualifier;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * @author Hamid Badiozamani
 */
public abstract class BasePaginationFilter extends BaseLoggable implements PaginationFilter
{
	private String orderBy;

	public BasePaginationFilter()
	{
		orderBy = "ser";
	}

	@Override
	public Pair<String, MapSqlParameterSource> configureSQL(String sql, MapSqlParameterSource sqlParams)
	{
		//String execQuery = Utils.makeSQLwithOffset(buildColumnQualifierSQL(query, qualifier), offset, limit, orderBy, true);
		String query = Utils.makeSQLwithOffset(sql, 0, 0, getOrderBy(), true);

		return new Pair<>(query, sqlParams);
	}

	@Override
	public ColumnQualifier getColumnQualifier()
	{
		return SimpleColumnQualifier.NONE;
	}

	@Override
	public String getOrderBy()
	{
		return orderBy;
	}

	@Override
	public void setOrderBy(String orderBy)
	{
		this.orderBy = orderBy;
	}

	@Override
	public <T extends Collection<?>> T apply(T collection)
	{
		return collection;
	}

	@Override
	public String toString()
	{
		if ( getLogger().isTraceEnabled() )
		{
			return getClass().getSimpleName() + ", orderBy = " + getOrderBy() + ", " + toStringInternal();
		}
		else
		{
			return getClass().getSimpleName() + ",o:" + getOrderBy() + "," + toStringInternal();
		}
	}

	protected abstract String toStringInternal();

	@Override
	public void fromJSONString(String json) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		PropertyUtils.copyProperties(this, Base.getInstance().getJacksonObjectMapper().readValue(json, this.getClass()));
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
