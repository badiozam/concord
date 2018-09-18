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
package com.i4one.research.model.survey.question;

import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class QuestionRecordMapper extends BaseRowMapper<QuestionRecord> implements RowMapper<QuestionRecord>
{

	@Override
	public QuestionRecord emptyInstance()
	{
		return new QuestionRecord();
	}

	@Override
	protected QuestionRecord mapRowInternal(ResultSet res) throws SQLException
	{
		QuestionRecord sr = new QuestionRecord();

		sr.setMaxresponses(getInteger(res, "maxresponses"));
		sr.setMinresponses(getInteger(res, "minresponses"));
		sr.setAnswercount(getInteger(res, "answercount"));
		sr.setOrderweight(getInteger(res, "orderweight"));
		sr.setQuestion(new IString(res.getString("question")));
		sr.setQuestiontype(getInteger(res, "questiontype"));
		sr.setValidanswer(res.getString("validanswer"));

		sr.setSurveyid(getInteger(res, "surveyid"));

		return sr;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(QuestionRecord sr)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("maxresponses", sr.getMaxresponses());
		sqlParams.addValue("minresponses", sr.getMinresponses());
		sqlParams.addValue("answercount", sr.getAnswercount());
		sqlParams.addValue("orderweight", sr.getOrderweight());
		sqlParams.addValue("question", sr.getQuestion());
		sqlParams.addValue("questiontype", sr.getQuestiontype());
		sqlParams.addValue("validanswer", sr.getValidanswer());

		addForeignKey(sqlParams, "surveyid", sr.getSurveyid());

		return sqlParams;
	}
}
