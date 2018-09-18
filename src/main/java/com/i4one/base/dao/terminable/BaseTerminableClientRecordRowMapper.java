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

import com.i4one.base.dao.BaseClientRecordRowMapper;
import java.sql.ResultSet;
import java.sql.Types;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Maps terminable client types to/form a given parameter source.
 * 
 * @author Hamid Badiozamani
 * 
 * @param <T> The specific terminable type to map
 */
public abstract class BaseTerminableClientRecordRowMapper<T extends TerminableClientRecordType> extends BaseClientRecordRowMapper<T> implements TerminableClientRecordRowMapper<T>
{
	@Override
	public T mapRow(ResultSet res, int rowNum) throws java.sql.SQLException
	{
		T retVal = super.mapRow(res, rowNum);
		retVal.setStarttm(res.getInt("starttm"));
		retVal.setEndtm(res.getInt("endtm"));

		return retVal;
	}

	@Override
	public MapSqlParameterSource getSqlParameterSource(T o)
	{
		MapSqlParameterSource retVal = super.getSqlParameterSource(o);
		retVal.addValue("starttm", o.getStarttm(), Types.INTEGER);
		retVal.addValue("endtm", o.getEndtm(), Types.INTEGER);

		return retVal;
	}
}
