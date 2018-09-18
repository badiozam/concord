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
package com.i4one.base.model.report;

import com.i4one.base.dao.RecordTypeRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.dao.terminable.BaseTerminableRecordRowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class ReportRecordMapper extends BaseTerminableRecordRowMapper<ReportRecord> implements RecordTypeRowMapper<ReportRecord>
{
	@Override
	protected ReportRecord mapRowInternal(ResultSet res) throws SQLException
	{
		ReportRecord retVal = new ReportRecord();

		retVal.setTitle( res.getString("title"));
		retVal.setTotal( getInteger(res, "total"));
		retVal.setProperties( res.getString("properties") );

		retVal.setLastser( getInteger(res, "lastser") );
		retVal.setTimestamp( getInteger(res, "timestamp") );

		retVal.setParentid( getInteger(res, "parentid"));

		return retVal;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(ReportRecord item)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("title", item.getTitle());
		sqlParams.addValue("total", item.getTotal());
		sqlParams.addValue("properties", item.getProperties());

		sqlParams.addValue("lastser", item.getLastser());
		sqlParams.addValue("timestamp", item.getTimestamp());

		addForeignKey(sqlParams, "parentid", item.getParentid());

		return sqlParams;
	}

	@Override
	public ReportRecord emptyInstance()
	{
		return new ReportRecord();
	}
}
