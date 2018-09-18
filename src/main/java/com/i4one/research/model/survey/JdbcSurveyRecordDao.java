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
package com.i4one.research.model.survey;

import com.i4one.base.dao.categorizable.BaseCategorizableSiteGroupJdbcDao;
import com.i4one.research.model.survey.question.QuestionRecord;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcSurveyRecordDao extends BaseCategorizableSiteGroupJdbcDao<SurveyRecord> implements SurveyRecordDao
{
	@Override
	protected SurveyRecordMapper initMapper()
	{
		return new SurveyRecordMapper();
	}

	@Override
	public int getQuestionCount(int surveyid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("surveyid", surveyid);

		QuestionRecord questionRecord = new QuestionRecord();
		String questionTable = questionRecord.getFullTableName();

		return querySingleGeneric("SELECT COUNT(*) FROM " + questionTable + " WHERE surveyid = :surveyid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));
	}
}
