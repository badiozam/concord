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
package com.i4one.base.model.emailblast.target;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Utils;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseSQLListBuilder extends BaseLoggable implements SQLListBuilder
{
	private Set<SQLListBuilder> ands;
	private Set<SQLListBuilder> ors;

	@Override
	public Set<String> getTables()
	{
		Set<String> retVal = getTablesInternal();

		ands.forEach( (builder) -> { retVal.addAll(builder.getTables()); });
		ors.forEach( (builder) -> { retVal.addAll(builder.getTables()); });

		return retVal;
	}

	/**
	 * Get the set of tables for just this builder.
	 * 
	 * @return The set of tables for this builder.
	 */
	protected abstract Set<String> getTablesInternal();

	@Override
	public String buildSQL()
	{
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ").append(getColumnName()).append(" FROM ");
		sql.append(Utils.toCSV(getTables()));

		sql.append(" WHERE ");

		sql.append("(");
		for ( String andCondition : getAndConditions() )
		{
			sql.append(" AND (").append( andCondition ).append(")");
		}

		for ( String orCondition : getOrConditions() )
		{
			sql.append(" OR (").append( orCondition ).append(")");
		}
		sql.append(")");

		for ( SQLListBuilder and : ands )
		{
			sql.append(" AND ").append(getColumnName()).append(" IN (");
			sql.append(and.buildSQL());
			sql.append(")");
		}

		for ( SQLListBuilder or : ors )
		{
			sql.append(" OR ").append(getColumnName()).append(" IN (");
			sql.append(or.buildSQL());
			sql.append(")");
		}

		return sql.toString();
	}

	@Override
	public Set<Integer> build()
	{
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public boolean belongs(Integer item)
	{
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void andWithList(SQLListBuilder list)
	{
		ands.add(list);
	}

	@Override
	public void orWithList(SQLListBuilder list)
	{
		ors.add(list);
	}

	@Override
	public String uniqueKey()
	{
		return getAndConditions() + ":" + getOrConditions() + ands.hashCode() + ors.hashCode();
	}
}
