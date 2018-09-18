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
package com.i4one.research.model.survey.response;

import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class ResponseRecordMapper extends BaseRowMapper<ResponseRecord>
{
	@Override
	protected ResponseRecord mapRowInternal(ResultSet res) throws SQLException
	{
		ResponseRecord er = new ResponseRecord();

		er.setRespondentid(getInteger(res, "respondentid"));
		er.setQuestionid(getInteger(res, "questionid"));

		er.setAnswerid(getInteger(res, "answerid"));
		er.setOpenanswer(res.getString("openanswer"));

		er.setTimestamp(getInteger(res, "timestamp"));

		return er;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(ResponseRecord er)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("openanswer", er.getOpenanswer());
		sqlParams.addValue("timestamp", er.getTimestamp());

		addForeignKey(sqlParams, "respondentid", er.getRespondentid());
		addForeignKey(sqlParams, "answerid", er.getAnswerid());
		addForeignKey(sqlParams, "questionid", er.getQuestionid());

		return sqlParams;
	}

	@Override
	public ResponseRecord emptyInstance()
	{
		return new ResponseRecord();
	}

}
