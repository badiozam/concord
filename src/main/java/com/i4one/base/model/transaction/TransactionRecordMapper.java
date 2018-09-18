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
package com.i4one.base.model.transaction;

import com.i4one.base.dao.BaseClientRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class TransactionRecordMapper extends BaseClientRecordRowMapper<TransactionRecord>
{
	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(TransactionRecord t)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("descr", t.getDescr());
		sqlParams.addValue("balxacted", t.getBalxacted());
		sqlParams.addValue("newbal", t.getNewbal());
		sqlParams.addValue("prevbal", t.getPrevbal());
		sqlParams.addValue("status", t.getStatus());
		sqlParams.addValue("timestamp", t.getTimestamp());
		sqlParams.addValue("srcip", t.getSourceip());

		addForeignKey(sqlParams, "parentid", t.getParentid());
		addForeignKey(sqlParams, "userid", t.getUserid());
		addForeignKey(sqlParams, "balid", t.getBalid());

		return sqlParams;
	}

	@Override
	protected TransactionRecord mapRowInternal(ResultSet res) throws SQLException
	{
		TransactionRecord t = new TransactionRecord();

		t.setBalxacted(getInteger(res, "balxacted"));
		t.setNewbal(getInteger(res, "newbal"));
		t.setPrevbal(getInteger(res, "prevbal"));
		t.setStatus(res.getString("status"));
		t.setTimestamp(getInteger(res, "timestamp"));
		t.setSourceip(res.getString("srcip"));
		t.setDescr(new IString(res.getString("descr")));

		t.setParentid(getInteger(res, "parentid"));
		t.setUserid(getInteger(res, "userid"));
		t.setBalid(getInteger(res, "balid"));

		return t;
	}

	@Override
	public TransactionRecord emptyInstance()
	{
		return new TransactionRecord();
	}
}
