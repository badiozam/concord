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
package com.i4one.promotion.model.trivia;

import com.i4one.base.dao.categorizable.BaseCategorizableSiteGroupJdbcDao;
import com.i4one.base.dao.categorizable.BaseCategorizableJdbcDao;
import com.i4one.base.model.report.ReportDriver;
import com.i4one.base.model.report.TopLevelReport;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcTriviaRecordDao extends BaseCategorizableSiteGroupJdbcDao<TriviaRecord> implements TriviaRecordDao
{
	@Override
	protected TriviaRecordMapper initMapper()
	{
		return new TriviaRecordMapper();
	}

	@Override
	public int getAnswerCount(int triviaid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("triviaid", triviaid);

		TriviaAnswerRecord questionRecord = new TriviaAnswerRecord();
		String answerTable = questionRecord.getFullTableName();

		return querySingleGeneric("SELECT COUNT(*) FROM " + answerTable + " WHERE triviaid = :triviaid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));
	}

	@Override
	public void updateAnswerCount(int triviaid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("triviaid", triviaid);

		TriviaAnswerRecord answerRecord = new TriviaAnswerRecord();
		String answerTable = answerRecord.getFullTableName();

		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET answercount = (SELECT COUNT(*) FROM " + answerTable + " WHERE triviaid = :triviaid) WHERE ser = :triviaid", sqlParams);
	}

	@Override
	public void setCorrectAnswer(int triviaid, int answerid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("triviaid", triviaid);
		sqlParams.addValue("answerid", answerid);

		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET correctanswerid = :answerid WHERE ser = :triviaid", sqlParams);
	}

	@Override
	public void processReport(TriviaRecord trivia, TopLevelReport report)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", trivia.getSer());

		report.initReport();
		getLogger().debug("Pre-processed report: " + report);

		TriviaResponseRecord triviaResponse = new TriviaResponseRecord();
		getNamedParameterJdbcTemplate().query("SELECT * FROM " + triviaResponse.getFullTableName() + " WHERE itemid = :itemid",
			sqlParams, new ReportDriver(report, new TriviaResponseRecordMapper()));
	}
}
