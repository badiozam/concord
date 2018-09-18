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
package com.i4one.base.dao.activity;

import com.i4one.base.dao.BaseUsageRecordRowMapper;
import com.i4one.base.dao.RecordTypeRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseActivityRecordRowMapper<T extends ActivityRecordType> extends BaseUsageRecordRowMapper<T> implements RecordTypeRowMapper<T>
{
	@Override
	public T mapRow(ResultSet res, int rowNum) throws java.sql.SQLException
	{
		T retVal = super.mapRow(res, rowNum);
		retVal.setItemid(getInteger(res, "itemid"));

		return retVal;
	}

	@Override
	protected T mapRowInternal(ResultSet res) throws SQLException
	{
		// We provide empty functionality here for subclasses because many
		// activity records will only have the basic columns and not need
		// any additional mapping.
		//
		return emptyInstance();
	}

	@Override
	public MapSqlParameterSource getSqlParameterSource(T o)
	{
		MapSqlParameterSource retVal = super.getSqlParameterSource(o);

		addForeignKey(retVal, "itemid", o.getItemid());

		return retVal;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(T item)
	{
		// We provide empty functionality here for subclasses because many
		// activity records will only have the basic columns and not need
		// any additional mapping.
		//
		return new RecordTypeSqlParameterSource();
	}
}
