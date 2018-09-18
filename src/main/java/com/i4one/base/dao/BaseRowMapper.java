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

import static com.i4one.base.core.Utils.isEmpty;
import com.i4one.base.core.BaseLoggable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Base class row mapper for all BaseRecordType objects.
 * 
 * @author Hamid Badiozamani
 * 
 * @param <T> The type of object being mapped
 */
public abstract class BaseRowMapper<T extends RecordType> extends BaseLoggable implements RecordTypeRowMapper<T>
{
	public BaseRowMapper()
	{
	}

	/**
	 * This method returns an Integer object from a given column and correctly
	 * detects and returns NULL values in the database as null java references
	 *
	 * @param res The ResultSet containing the data
	 * @param columnName The column name of the result set
	 *
	 * @return The proper integer value or null if the record contains a null value
	 *
	 * @throws SQLException
	 */
	protected Integer getInteger(ResultSet res, String columnName) throws SQLException
	{
		Integer retVal = res.getInt(columnName);
		return nullCheck(res, retVal);
	}

	protected Integer getInteger(ResultSet res, int columnIndex) throws SQLException
	{
		Integer retVal = res.getInt(columnIndex);
		return nullCheck(res, retVal);
	}

	protected Long getLong(ResultSet res, String columnName) throws SQLException
	{
		Long retVal = res.getLong(columnName);
		return nullCheck(res, retVal);
	}

	protected Long getLong(ResultSet res, int columnIndex) throws SQLException
	{
		Long retVal = res.getLong(columnIndex);
		return nullCheck(res, retVal);
	}

	protected Float getFloat(ResultSet res, String columnName) throws SQLException
	{
		Float retVal = res.getFloat(columnName);
		return nullCheck(res, retVal);
	}

	protected Float getFloat(ResultSet res, int columnIndex) throws SQLException
	{
		Float retVal = res.getFloat(columnIndex);
		return nullCheck(res, retVal);
	}
	protected Boolean getBoolean(ResultSet res, String columnName) throws SQLException
	{
		Boolean retVal = res.getBoolean(columnName);
		return nullCheck(res, retVal);
	}

	protected Boolean getBoolean(ResultSet res, int columnIndex) throws SQLException
	{
		Boolean retVal = res.getBoolean(columnIndex);
		return nullCheck(res, retVal);
	}

	protected Character getCharacter(ResultSet res, String columnName) throws SQLException
	{
		String retVal = res.getString(columnName);
		if ( isEmpty(retVal) )
		{
			return null;
		}
		else
		{
			return retVal.charAt(0);
		}
	}

	protected void addForeignKey(MapSqlParameterSource map, String name, Integer value)
	{
		if ( value != null && value > 0 )
		{
			map.addValue(name, value, Types.INTEGER);
		}
	}

	protected void addNullableForeignKey(RecordTypeSqlParameterSource map, String name, Integer value)
	{
		if ( value != null && value > 0 )
		{
			map.addValue(name, value, Types.INTEGER);
		}
		else
		{
			map.addNullableValue(name, null);
		}
	}

	private <T extends Object> T nullCheck(ResultSet res, T notNull) throws SQLException
	{
		if ( res.wasNull() )
		{
			return null;
		}
		else
		{
			return notNull;
		}
	}

	@Override
	public T mapRow(ResultSet res, int rowNum) throws java.sql.SQLException
	{
		T retVal = mapRowInternal(res, rowNum);
		retVal.setSer(res.getInt("ser"));
		retVal.setLoaded(true);

		return retVal;
	}

	/**
	 * Map a given record in the result set onto the specific object
	 *
	 * @param res The result set containing the record values
	 * @param rowNum The row number in a multi-record query
	 *
	 * @return The mapped object populated with the data contained in the ResultSet
	 * @throws java.sql.SQLException
	 */
	protected T mapRowInternal(ResultSet res, int rowNum) throws java.sql.SQLException
	{
		return mapRowInternal(res);
	}

	/**
	 * Map a given record in the result set onto the specific object
	 *
	 * @param res The result set containing the record values
	 *
	 * @return The mapped object populated with the data contained in the ResultSet
	 * @throws java.sql.SQLException
	 */
	protected abstract T mapRowInternal(ResultSet res) throws java.sql.SQLException;

	@Override
	public MapSqlParameterSource getSqlParameterSource(T o)
	{
		MapSqlParameterSource retVal = getSqlParameterSourceInternal(o);
		if ( o.getSer() > 0 )
		{
			// We only add the serial number if it's a real item in the database
			//
			retVal.addValue("ser", o.getSer(), Types.INTEGER);
		}

		return retVal;
	}

	/**
	 * Get the column name-value pairs for this mapper
	 *
	 * @param o The object to map columns and values to
	 *
	 * @return An SqlParameterSource containing the name-value pairs
	 */
	protected abstract RecordTypeSqlParameterSource getSqlParameterSourceInternal(T o);
}
