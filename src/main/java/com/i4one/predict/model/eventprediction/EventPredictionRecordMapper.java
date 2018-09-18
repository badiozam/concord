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
package com.i4one.predict.model.eventprediction;

import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.dao.activity.BaseQuantifiedActivityRecordRowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class EventPredictionRecordMapper extends BaseQuantifiedActivityRecordRowMapper<EventPredictionRecord>
{
	@Override
	protected EventPredictionRecord mapRowInternal(ResultSet res) throws SQLException
	{
		EventPredictionRecord er = new EventPredictionRecord();

		er.setTermid(getInteger(res, "termid"));
		er.setEventoutcomeid(getInteger(res, "eventoutcomeid"));

		er.setPayout(getFloat(res, "payout"));
		er.setPostedtm(getInteger(res, "postedtm"));
		er.setCorrect(getInteger(res, "correct"));

		return er;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(EventPredictionRecord er)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		addForeignKey(sqlParams, "termid", er.getTermid());
		addForeignKey(sqlParams, "eventoutcomeid", er.getEventoutcomeid());

		sqlParams.addValue("postedtm", er.getPostedtm());
		sqlParams.addValue("correct", er.getCorrect());

		sqlParams.addValue("payout", er.getPayout());


		return sqlParams;
	}

	@Override
	public EventPredictionRecord emptyInstance()
	{
		return new EventPredictionRecord();
	}

}
