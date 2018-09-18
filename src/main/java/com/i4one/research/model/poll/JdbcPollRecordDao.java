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
package com.i4one.research.model.poll;

import com.i4one.base.dao.categorizable.BaseCategorizableSiteGroupJdbcDao;
import com.i4one.base.model.report.ReportDriver;
import com.i4one.base.model.report.TopLevelReport;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcPollRecordDao extends BaseCategorizableSiteGroupJdbcDao<PollRecord> implements PollRecordDao
{
	@Override
	protected PollRecordMapper initMapper()
	{
		return new PollRecordMapper();
	}

	@Override
	public void updateAnswerCount(int pollid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("pollid", pollid);

		PollAnswerRecord answerRecord = new PollAnswerRecord();
		String answerTable = answerRecord.getFullTableName();

		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET answercount = (SELECT COUNT(*) FROM " + answerTable + " WHERE pollid = :pollid) WHERE ser = :pollid", sqlParams);
	}

	@Override
	public void processReport(PollRecord poll, TopLevelReport report)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", poll.getSer());

		report.initReport();
		getLogger().debug("Pre-processed report: " + report);

		PollResponseRecord pollResponse = new PollResponseRecord();
		getNamedParameterJdbcTemplate().query("SELECT * FROM " + pollResponse.getFullTableName() + " WHERE itemid = :itemid",
			sqlParams, new ReportDriver(report, new PollResponseRecordMapper()));
	}
}
