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
package com.i4one.base.model.adminhistory;

import com.i4one.base.dao.BaseClientRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class AdminHistoryRecordMapper extends BaseClientRecordRowMapper<AdminHistoryRecord>
{
	@Override
	protected AdminHistoryRecord mapRowInternal(ResultSet res) throws SQLException
	{
		AdminHistoryRecord retVal = new AdminHistoryRecord();

		retVal.setAction(res.getString("action"));
		retVal.setAdminid(getInteger(res, "adminid"));
		retVal.setParentid(getInteger(res, "parentid"));
		retVal.setDescr(res.getString("descr"));
		retVal.setFeature(res.getString("feature"));
		retVal.setFeatureid(getInteger(res, "featureid"));
		retVal.setSourceip(res.getString("srcip"));
		retVal.setTimestamp(getInteger(res, "timestamp"));
		retVal.setBefore(res.getString("before"));
		retVal.setAfter(res.getString("after"));

		return retVal;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(AdminHistoryRecord ahr)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("action", ahr.getAction());
		sqlParams.addValue("adminid", ahr.getAdminid());
		sqlParams.addValue("clientid", ahr.getClientid());
		sqlParams.addValue("descr", ahr.getDescr());
		sqlParams.addValue("feature", ahr.getFeature());
		sqlParams.addValue("featureid", ahr.getFeatureid());
		sqlParams.addValue("srcip", ahr.getSourceip());
		sqlParams.addValue("timestamp", ahr.getTimestamp());
		sqlParams.addValue("before", ahr.getBefore());
		sqlParams.addValue("after", ahr.getAfter());

		addForeignKey(sqlParams, "parentid", ahr.getParentid());

		return sqlParams;
	}

	@Override
	public AdminHistoryRecord emptyInstance()
	{
		return new AdminHistoryRecord();
	}

}
