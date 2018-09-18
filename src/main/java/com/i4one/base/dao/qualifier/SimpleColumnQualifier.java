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
package com.i4one.base.dao.qualifier;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Pair;
import com.i4one.base.core.Utils;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Hamid Badiozamani
 */
public class SimpleColumnQualifier extends BaseLoggable implements ColumnQualifier
{
	private boolean and;
	private Map<String, Object> columnQualifiers;

	public static final ColumnQualifier NONE;

	static
	{
		NONE = new SimpleColumnQualifier(Collections.EMPTY_MAP);
	}

	public SimpleColumnQualifier()
	{
		init();
	}

	public SimpleColumnQualifier(Map<String, Object> qualifiers)
	{
		init();
		columnQualifiers.putAll(qualifiers);
	}

	public SimpleColumnQualifier(Object... args)
	{
		init();
		columnQualifiers.putAll(Utils.makeMap(args));
	}

	private void init()
	{
		columnQualifiers = new LinkedHashMap<>();
		and = true;
	}

	@Override
	public String buildSQL()
	{
		StringBuilder retVal = new StringBuilder();

		columnQualifiers.entrySet().stream().forEach( (qualifier) ->
		{
			if ( isAnd() )
			{
				retVal.append(" AND ");
			}
			else
			{
				retVal.append(" OR ");
			}

			if ( qualifier.getValue() instanceof NullValue )
			{
				retVal.append(qualifier.getKey());
				retVal.append(" IS NULL");
			}
			else if ( qualifier.getValue() instanceof NotNullValue )
			{
				retVal.append(qualifier.getKey());
				retVal.append(" IS NOT NULL");
			}
			else if ( qualifier.getValue() instanceof LikeString )
			{
				LikeString likeString = (LikeString)qualifier.getValue();
				retVal.append("LOWER(").append(qualifier.getKey()).append(")");
				retVal.append(" LIKE '");
				retVal.append(likeString.getSQLValue());
				retVal.append("'");
			}
			else if ( qualifier.getValue() instanceof Collection )
			{
				Collection values = (Collection)qualifier.getValue();
	
				retVal.append(qualifier.getKey());
				retVal.append(" IN (");
				retVal.append(Utils.sqlEscape(Utils.toCSV(values)));
				retVal.append(")");
			}
			else if ( qualifier.getValue() instanceof ColumnQualifier )
			{
				ColumnQualifier condition = (ColumnQualifier)qualifier.getValue();
				retVal.append("(");
				retVal.append(condition.buildSQL());
				retVal.append(")");
			}
			else if ( qualifier.getValue() instanceof ComparisonQualifier )
			{
				ComparisonQualifier comparisonQualifier = (ComparisonQualifier)qualifier.getValue();
				retVal.append(comparisonQualifier.getColumn());
				retVal.append(comparisonQualifier.getOperator());
				retVal.append(":"); retVal.append(qualifier.getKey());
			}
			else
			{
				// Catch all
				//
				retVal.append(qualifier.getKey());
				retVal.append("= :");
				retVal.append(qualifier.getKey());
			}
		});

		return retVal.toString();
	}

	@Override
	public Object getQualifier(String name)
	{
		return columnQualifiers.get(name);
	}

	@Override
	public void setQualifier(String key, Object value)
	{
		if ( value == null )
		{
			columnQualifiers.remove(key);
		}
		else
		{
			columnQualifiers.put(key, value);
		}
	}

	@Override
	public Map<String, Object> getAllQualifiers()
	{
		// We filter out any qualifiers that don't hold actual values themselves
		// since these are handled in the buildSQL() method
		//
		return columnQualifiers
			.entrySet()
			.stream()
			.filter( (entry) -> !(entry.getValue() instanceof SemanticQualifier))
			.filter( (entry) -> !(entry.getValue() instanceof Collection))
			.map( (entry) -> (entry.getValue() instanceof ComparisonQualifier ?
				new Pair<String, Object>(entry.getKey(), ((ComparisonQualifier)entry.getValue()).getValue())
				: new Pair<String, Object>(entry.getKey(), entry.getValue()) ) )
			.collect(Collectors.toMap( (entry) -> entry.getKey(), (entry) -> entry.getValue()));
	}

	public boolean isAnd()
	{
		return and;
	}

	public void setAnd(boolean and)
	{
		this.and = and;
	}

}
