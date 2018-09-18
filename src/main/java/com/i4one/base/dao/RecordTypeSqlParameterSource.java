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

import com.i4one.base.model.i18n.IString;
import org.apache.commons.logging.Log;
import static org.apache.commons.logging.LogFactory.getLog;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * MapSqlParameterSource that does not accept null values
 *
 * @author Hamid Badiozamani
 */
public class RecordTypeSqlParameterSource extends MapSqlParameterSource
{
	private final Log logger;

	public RecordTypeSqlParameterSource()
	{
		logger = getLog(getClass());
	}

	@Override
	public MapSqlParameterSource addValue(String key, Object value, int sqlType)
	{
		if ( value != null )
		{
			super.addValue(key, value, sqlType);
		}
		else
		{
			getLogger().trace("Skipping the key " + key + " due to null value");
		}

		return this;
	}

	@Override
	public MapSqlParameterSource addValue(String key, Object value)
	{
		if ( value != null )
		{
			super.addValue(key, value);
		}
		else
		{
			getLogger().trace("Skipping the key " + key + " due to null value");
		}

		return this;
	}

	public MapSqlParameterSource addNullableValue(String key, Object value)
	{
		super.addValue(key, value);
		return this;
	}

	public MapSqlParameterSource addValue(String key, IString value)
	{
		if ( value != null )
		{
			return addValue(key, value.toString());
		}
		else
		{
			getLogger().trace("Skipping the key " + key + " due to null value");
		}

		return this;
	}

	public Log getLogger()
	{
		return logger;
	}

	@Override
	public String toString()
	{
		StringBuilder retVal = new StringBuilder();
		retVal.append("[");

		this.getValues().forEach( (name, value) ->
		{
			retVal.append(name).append("=>").append(value).append(",");
		});

		retVal.deleteCharAt(retVal.length() - 1);
		retVal.append("]").append(super.toString());

		return retVal.toString();
	}
}
