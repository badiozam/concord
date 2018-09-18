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
package com.i4one.base.model.balancetrigger;

import com.i4one.base.dao.terminable.BaseTerminableClientJdbcDao;
import com.i4one.base.dao.RecordType;
import com.i4one.base.dao.RecordTypeRowMapper;
import com.i4one.base.model.manager.GenericFeatureRecordMapper;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.ReportDriver;
import com.i4one.base.model.report.TopLevelReport;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcBalanceTriggerRecordDao extends BaseTerminableClientJdbcDao<BalanceTriggerRecord> implements BalanceTriggerRecordDao
{
	private RecordTypeRowMapper<RecordType> recordTypeRowMapper;

	@Override
	public void init()
	{
		super.init();

		recordTypeRowMapper = new GenericFeatureRecordMapper();
	}

	@Override
	protected BalanceTriggerRecordMapper initMapper()
	{
		return new BalanceTriggerRecordMapper();
	}

	@Override
	public List<BalanceTriggerRecord> getByFeature(String feature, int id, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("feature", feature);
		sqlParams.addValue("id", id);

		//return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE ser IN (SELECT balancetriggerid FROM base.featuretriggers WHERE feature = :feature AND featureid = :id)", sqlParams, mapper);

		// Joins are faster than subqueries
		//
		String tablename = getEmpty().getFullTableName();
		return query("SELECT " + tablename + ".* FROM " + tablename + ", base.featuretriggers WHERE " + tablename + ".ser = base.featuretriggers.balancetriggerid AND feature = :feature AND (featureid = :id OR featureid = 0)", sqlParams, pagination);
	}

	@Override
	public List<RecordType> getByTrigger(int triggerid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("triggerid", triggerid);

		return queryGeneric("SELECT * FROM base.featuretriggers WHERE balancetriggerid = :triggerid", sqlParams, pagination, recordTypeRowMapper);
	}

	@Override
	public void associate(String feature, int id, int triggerid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("feature", feature);
		sqlParams.addValue("featureid", id);
		sqlParams.addValue("balancetriggerid", triggerid);

		GeneratedKeyHolder serHolder = new GeneratedKeyHolder();
		getNamedParameterJdbcTemplate().update("INSERT INTO base.featuretriggers (feature, featureid, balancetriggerid) VALUES (:feature, :featureid, :balancetriggerid)", sqlParams, serHolder);
	}

	@Override
	public void dissociate(String feature, int id, int triggerid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("feature", feature);
		sqlParams.addValue("featureid", id);
		sqlParams.addValue("balancetriggerid", triggerid);

		getNamedParameterJdbcTemplate().update("DELETE FROM base.featuretriggers WHERE feature=:feature AND featureid = :featureid AND balancetriggerid = :balancetriggerid", sqlParams);
	}

	@Override
	public boolean isAssociated(String feature, int id, int triggerid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("feature", feature);
		sqlParams.addValue("featureid", id);
		sqlParams.addValue("balancetriggerid", triggerid);

		Integer firstSer = querySingleGeneric("SELECT ser FROM base.featuretriggers WHERE feature = :feature AND featureid = :featureid AND balancetriggerid = :balancetriggerid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));

		return firstSer != null;
	}

	@Override
	public boolean hasAssociations(int triggerid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("balancetriggerid", triggerid);

		Integer firstSer = querySingleGeneric("SELECT ser FROM base.featuretriggers WHERE balancetriggerid = :balancetriggerid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));

		return firstSer != null;
	}

	@Transactional(readOnly = false)
	@Override
	public void deleteBySer(int ser)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", ser);

		// Delete any activity associated with this record first
		//
		UserBalanceTriggerRecord userBalanceTrigger = new UserBalanceTriggerRecord();
		getNamedParameterJdbcTemplate().update("DELETE FROM " + userBalanceTrigger.getFullTableName() + " WHERE itemid = :itemid", sqlParams);

		super.deleteBySer(ser);
	}

	@Override
	public void processReport(BalanceTriggerRecord trigger, TopLevelReport report)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", trigger.getSer());

		report.initReport();
		getLogger().debug("Pre-processed report: " + report);

		UserBalanceTriggerRecord triggerRecord = new UserBalanceTriggerRecord();
		getNamedParameterJdbcTemplate().query("SELECT * FROM " + triggerRecord.getFullTableName() + " WHERE itemid = :itemid",
			sqlParams, new ReportDriver(report, new UserBalanceTriggerRecordMapper()));
	}
}
