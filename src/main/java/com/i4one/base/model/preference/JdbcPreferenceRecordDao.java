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
package com.i4one.base.model.preference;

import com.i4one.base.dao.BaseSiteGroupJdbcDao;
import com.i4one.base.model.report.ReportDriver;
import com.i4one.base.model.report.TopLevelReport;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcPreferenceRecordDao extends BaseSiteGroupJdbcDao<PreferenceRecord> implements PreferenceRecordDao
{
	@Override
	protected PreferenceRecordMapper initMapper()
	{
		return new PreferenceRecordMapper();
	}

	@Override
	public void updateAnswerCount(int preferenceid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("preferenceid", preferenceid);

		PreferenceAnswerRecord answerRecord = new PreferenceAnswerRecord();
		String answerTable = answerRecord.getFullTableName();

		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET answercount = (SELECT COUNT(*) FROM " + answerTable + " WHERE preferenceid = :preferenceid) WHERE ser = :preferenceid", sqlParams);
	}

	@Override
	public void processReport(PreferenceRecord preference, TopLevelReport report)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", preference.getSer());

		report.initReport();
		getLogger().debug("Pre-processed report: " + report);

		UserPreferenceRecord userPreference = new UserPreferenceRecord();
		getNamedParameterJdbcTemplate().query("SELECT * FROM " + userPreference.getFullTableName() + " WHERE itemid = :itemid",
			sqlParams, new ReportDriver(report, new UserPreferenceRecordMapper()));
	}
}
