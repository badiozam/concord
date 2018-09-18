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
package com.i4one.base.model.instantwin;

import com.i4one.base.dao.RecordType;
import com.i4one.base.dao.RecordTypeRowMapper;
import com.i4one.base.dao.terminable.BaseTerminableClientJdbcDao;
import com.i4one.base.model.manager.GenericFeatureRecordMapper;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.ReportDriver;
import com.i4one.base.model.report.TopLevelReport;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcInstantWinRecordDao extends BaseTerminableClientJdbcDao<InstantWinRecord> implements InstantWinRecordDao
{
	private RecordTypeRowMapper<RecordType> recordTypeRowMapper;

	@Override
	public void init()
	{
		super.init();

		recordTypeRowMapper = new GenericFeatureRecordMapper();
	}

	@Override
	protected InstantWinRecordMapper initMapper()
	{
		return new InstantWinRecordMapper();
	}

	@Override
	public void updateBySer(InstantWinRecord item)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("instantwinid", item.getSer());

		// We update the winner count each time the item is updated by serial number to maintain
		// consistency.
		//
		Integer winnerCount = this.querySingleGeneric("SELECT COUNT(*) FROM base.instantwinners WHERE itemid = :instantwinid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));
		item.setWinnercount( winnerCount );

		super.updateBySer(item);
	}

	@CacheEvict(value = "daoCache", key = "target.makeKey(#ser)")
	@Override
	public void incrementWinnerCount(int iwid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("ser", iwid);

		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET winnercount = winnercount + 1 WHERE ser = :ser", sqlParams);
	}

	@Override
	public boolean hasAssociations(int iwid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("iwid", iwid);

		Integer firstSer = querySingleGeneric("SELECT ser FROM base.featureinstantwins WHERE iwid = :iwid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));

		return firstSer != null;
	}

	@Override
	public boolean isAssociated(String feature, int id, int iwid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("feature", feature);
		sqlParams.addValue("featureid", id);
		sqlParams.addValue("iwid", iwid);

		Integer firstSer = querySingleGeneric("SELECT ser FROM base.featureinstantwins WHERE feature = :feature AND featureid = :featureid AND iwid = :iwid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));

		return firstSer != null;
	}

	@Override
	public void associate(String feature, int id, int iwid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("feature", feature);
		sqlParams.addValue("featureid", id);
		sqlParams.addValue("iwid", iwid);

		GeneratedKeyHolder serHolder = new GeneratedKeyHolder();
		getNamedParameterJdbcTemplate().update("INSERT INTO base.featureinstantwins (feature, featureid, iwid) VALUES (:feature, :featureid, :iwid)", sqlParams, serHolder);
	}

	@Override
	public void dissociate(String feature, int id, int iwid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("feature", feature);
		sqlParams.addValue("featureid", id);
		sqlParams.addValue("iwid", iwid);

		getNamedParameterJdbcTemplate().update("DELETE FROM base.featureinstantwins WHERE feature=:feature AND featureid = :featureid AND iwid = :iwid", sqlParams);
	}

	@Override
	public List<InstantWinRecord> getByFeature(String feature, int id, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("feature", feature);
		sqlParams.addValue("id", id);

		// Joins are faster than subqueries
		//
		String tablename = getEmpty().getFullTableName();
		return query("SELECT " + tablename + ".* FROM " + tablename + ", base.featureinstantwins WHERE " + tablename + ".ser = base.featureinstantwins.iwid AND feature = :feature AND featureid = :id", sqlParams, pagination);
	}

	@Override
	public List<RecordType> getByInstantWin(int iwid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("iwid", iwid);

		return queryGeneric("SELECT * FROM base.featureinstantwins WHERE iwid = :iwid", sqlParams, pagination, recordTypeRowMapper);
	}
	
	@Transactional(readOnly = false)
	@Override
	public void deleteBySer(int ser)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", ser);

		// Delete any activity associated with this record first
		//
		UserInstantWinRecord userInstantWin = new UserInstantWinRecord();
		getNamedParameterJdbcTemplate().update("DELETE FROM " + userInstantWin.getFullTableName() + " WHERE itemid = :itemid", sqlParams);

		super.deleteBySer(ser);
	}

	@Override
	public void processReport(InstantWinRecord iw, TopLevelReport report)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", iw.getSer());

		report.initReport();
		getLogger().debug("Pre-processed report: " + report);

		UserInstantWinRecord iwRecord = new UserInstantWinRecord();
		getNamedParameterJdbcTemplate().query("SELECT * FROM " + iwRecord.getFullTableName() + " WHERE itemid = :itemid",
			sqlParams, new ReportDriver(report, new UserInstantWinRecordMapper()));
	}
}
