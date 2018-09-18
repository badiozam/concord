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
package com.i4one.promotion.model.code;

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
public class JdbcCodeRecordDao extends BaseTerminableSiteGroupJdbcDao<CodeRecord> implements CodeRecordDao
{
	@Override
	protected BaseTerminableSiteGroupRecordRowMapper<CodeRecord> initMapper()
	{
		return new CodeRecordMapper();
	}

	@Override
	public List<CodeRecord> getLiveCodes(String code, TerminablePagination pagination)
	{
		// We get the live codes and use the pagination-set value to retrieve
		// live codes based on a particular time
		//
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("code", code);
		sqlParams.addValue("currtime", pagination.getCurrentTime());

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE LOWER(code) = LOWER(:code) AND starttm <= :currtime AND endtm > :currtime", sqlParams, pagination);
	}

	@Override
	public void processReport(CodeRecord code, TopLevelReport report)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", code.getSer());

		report.initReport();
		getLogger().debug("Pre-processed report: " + report);

		CodeResponseRecord codeResponse = new CodeResponseRecord();
		getNamedParameterJdbcTemplate().query("SELECT * FROM " + codeResponse.getFullTableName() + " WHERE itemid = :itemid",
			sqlParams, new ReportDriver(report, new CodeResponseRecordMapper()));
	}

}
