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
package com.i4one.base.model.balancetrigger;

import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.dao.terminable.BaseTerminableClientRecordRowMapper;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class BalanceTriggerRecordMapper extends BaseTerminableClientRecordRowMapper<BalanceTriggerRecord> implements RowMapper<BalanceTriggerRecord>
{

	@Override
	public BalanceTriggerRecord emptyInstance()
	{
		return new BalanceTriggerRecord();
	}

	@Override
	protected BalanceTriggerRecord mapRowInternal(ResultSet res) throws SQLException
	{
		BalanceTriggerRecord retVal = new BalanceTriggerRecord();

		retVal.setTitle(new IString(res.getString("title")));
		retVal.setAmount(getInteger(res, "amount"));
		retVal.setSynced(getBoolean(res, "synced"));
		retVal.setExclusive(getBoolean(res, "exclusive"));
		retVal.setFrequency(getInteger(res, "frequency"));
		retVal.setMaxglobalusage(getInteger(res, "maxglobalusage"));
		retVal.setMaxuserusage(getInteger(res, "maxuserusage"));

		retVal.setBalid(getInteger(res, "balid"));

		return retVal;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(BalanceTriggerRecord item)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("title", item.getTitle());
		sqlParams.addValue("amount", item.getAmount());
		sqlParams.addValue("synced", item.getSynced());
		sqlParams.addValue("frequency", item.getFrequency());
		sqlParams.addValue("maxglobalusage", item.getMaxglobalusage());
		sqlParams.addValue("maxuserusage", item.getMaxuserusage());
		sqlParams.addValue("exclusive", item.getExclusive());

		addForeignKey(sqlParams, "balid", item.getBalid());

		return sqlParams;
	}
	
}
