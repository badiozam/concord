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
package com.i4one.promotion.model.socialphrase;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.terminable.BaseTerminableSiteGroupJdbcDao;
import com.i4one.base.dao.terminable.BaseTerminableSiteGroupRecordRowMapper;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.report.ReportDriver;
import com.i4one.base.model.report.TopLevelReport;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcSocialPhraseRecordDao extends BaseTerminableSiteGroupJdbcDao<SocialPhraseRecord> implements SocialPhraseRecordDao
{
	@Override
	protected BaseTerminableSiteGroupRecordRowMapper<SocialPhraseRecord> initMapper()
	{
		return new SocialPhraseRecordMapper();
	}

	@Override
	public List<SocialPhraseRecord> getLiveSocialPhrases(String phrase, TerminablePagination pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("phrase", phrase);
		sqlParams.addValue("currtime", pagination.getCurrentTime());

		// The phrases are stored as a list of comma separated double-quote escaped strings
		// so if there is a phrase that is matched in its entirety it'll have to be between
		// double-quotes and potentially somewhere in the middle of the string.
		//
		String sqlEscapedPhrase = Utils.sqlEscape(phrase);
		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE (phrases=:phrase OR " // Single token
			+ "phrases ILIKE '" + sqlEscapedPhrase + ",%' OR "	// Beginning
			+ "phrases ILIKE '%," + sqlEscapedPhrase + ",%' OR "	// Middle
			+ "phrases ILIKE '%," + sqlEscapedPhrase + "')"		// End
			+ " AND starttm <= :currtime AND endtm > :currtime", sqlParams, pagination);
	}

	@Override
	public void processReport(SocialPhraseRecord socialPhrase, TopLevelReport report)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", socialPhrase.getSer());

		report.initReport();
		getLogger().debug("Pre-processed report: " + report);

		SocialPhraseResponseRecord socialPhraseResponse = new SocialPhraseResponseRecord();
		getNamedParameterJdbcTemplate().query("SELECT * FROM " + socialPhraseResponse.getFullTableName() + " WHERE itemid = :itemid",
			sqlParams, new ReportDriver(report, new SocialPhraseResponseRecordMapper()));
	}

}
