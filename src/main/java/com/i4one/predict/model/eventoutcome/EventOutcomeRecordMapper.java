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
package com.i4one.predict.model.eventoutcome;

import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class EventOutcomeRecordMapper extends BaseRowMapper<EventOutcomeRecord>
{
	@Override
	protected EventOutcomeRecord mapRowInternal(ResultSet res) throws SQLException
	{
		EventOutcomeRecord er = new EventOutcomeRecord();

		er.setDescr(new IString(res.getString("descr")));

		er.setLikelihood(getFloat(res, "likelihood"));
		er.setBaseline(getFloat(res, "baseline"));
		er.setUsagecount(getInteger(res, "usagecount"));
		er.setLocklikelihood(getBoolean(res, "locklikelihood"));
		er.setUpdatetime(getInteger(res, "updatetime"));
		er.setActualized(getBoolean(res, "actualized"));

		er.setEventid(getInteger(res, "eventid"));

		return er;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(EventOutcomeRecord er)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("descr", er.getDescr());

		sqlParams.addValue("likelihood", er.getLikelihood());
		sqlParams.addValue("baseline", er.getBaseline());
		sqlParams.addValue("usagecount", er.getUsagecount());
		sqlParams.addValue("locklikelihood", er.getLocklikelihood());
		sqlParams.addValue("updatetime", er.getUpdatetime());
		sqlParams.addValue("actualized", er.getActualized());

		sqlParams.addValue("eventid", er.getEventid());

		return sqlParams;
	}

	@Override
	public EventOutcomeRecord emptyInstance()
	{
		return new EventOutcomeRecord();
	}
}
