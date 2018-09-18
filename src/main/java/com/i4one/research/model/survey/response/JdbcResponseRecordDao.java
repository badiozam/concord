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

import com.i4one.base.dao.BasePaginableJdbcDao;
import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.ClassificationReportDriver;
import java.util.List;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcResponseRecordDao extends BasePaginableJdbcDao<ResponseRecord> implements ResponseRecordDao
{
	@Override
	protected BaseRowMapper<ResponseRecord> initMapper()
	{
		return new ResponseRecordMapper();
	}

	@Override
	public List<ResponseRecord> getAllResponses(int respondentid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("respondentid", respondentid);

		BaseRowMapper<ResponseRecord> mapper = initMapper();

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE respondentid = :respondentid", sqlParams);
	}

	@Override
	public List<ResponseRecord> getAllResponsesByQuestionid(int questionid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("questionid", questionid);

		BaseRowMapper<ResponseRecord> mapper = initMapper();

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE questionid = :questionid", sqlParams);
	}

	@Override
	public List<ResponseRecord> getAllResponsesByAnswerid(int answerid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("answerid", answerid);

		BaseRowMapper<ResponseRecord> mapper = initMapper();

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE answerid = :answerid", sqlParams);
	}

	@Override
	public List<ResponseRecord> getResponses(int questionid, int respondentid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("questionid", questionid);
		sqlParams.addValue("respondentid", respondentid);

		BaseRowMapper<ResponseRecord> mapper = initMapper();

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE respondentid = :respondentid AND questionid = :questionid", sqlParams);
	}

	@Override
	public boolean hasResponded(int answerid, int respondentid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("answerid", answerid);
		sqlParams.addValue("respondentid", respondentid);

		Integer ser = querySingleGeneric("SELECT ser FROM " + getEmpty().getFullTableName() + " WHERE respondentid = :respondentid AND answerid = :answerid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));

		return (ser != null);
	}

	@Override
	public void processReport(ResponseRecord sample, ClassificationReport<ResponseRecord, ?> report, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();

		sqlParams.addValue("itemid", sample.getQuestionid());

		getNamedParameterJdbcTemplate().query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE questionid IN (SELECT ser FROM research.surveyquestions WHERE surveyid = (SELECT surveyid FROM research.surveyquestions WHERE ser = :itemid))",
			sqlParams, new ClassificationReportDriver<>(report, getMapper()));
	}
}