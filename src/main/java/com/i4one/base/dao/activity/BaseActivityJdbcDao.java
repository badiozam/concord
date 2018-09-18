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
package com.i4one.base.dao.activity;

import com.i4one.base.core.Pair;
import com.i4one.base.dao.BasePaginableJdbcDao;
import com.i4one.base.model.manager.activity.ActivityPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.report.ReportDriver;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.ClassificationReportDriver;
import java.util.List;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseActivityJdbcDao<U extends ActivityRecordType> extends BasePaginableJdbcDao<U> implements ActivityRecordTypeDao<U>
{
	@Override
	public List<U> getByItem(int itemid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid", sqlParams, pagination);
	}

	@Override
	public List<U> getByUser(int userid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("userid", userid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE userid = :userid", sqlParams, pagination);
	}

	@Override
	public List<U> getAll(int itemid, int userid, int timestamp, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);
		sqlParams.addValue("userid", userid);
		sqlParams.addValue("timestamp", timestamp);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid AND userid = :userid AND timestamp = :timestamp", sqlParams, pagination);
	}
	@Override
	public List<U> getAll(int itemid, int userid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);
		sqlParams.addValue("userid", userid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid AND userid = :userid", sqlParams, pagination);
	}

	@Override
	public int getCountByItem(int itemid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);

		return querySingleGeneric("SELECT COUNT(*) FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));
	}

	@Override
	public U get(int itemid, int userid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);
		sqlParams.addValue("userid", userid);

		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid AND userid = :userid", sqlParams, SimplePaginationFilter.DESC);
	}

	@Override
	public boolean hasActivity(int itemid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);

		Integer purchaseSer = querySingleGeneric("SELECT ser FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));

		return purchaseSer != null;
	}


	@Transactional(readOnly = false)
	@Override
	public void removeAll(int itemid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);

		getNamedParameterJdbcTemplate().update("DELETE FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid", sqlParams);
	}

	@Override
	public void removeAll(int itemid, int userid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);
		sqlParams.addValue("userid", userid);

		getNamedParameterJdbcTemplate().update("DELETE FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid AND userid = :userid", sqlParams);
	}

	@Override
	public void processReport(U sample, TopLevelReport report)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();

		report.initReport();
		getLogger().debug("Pre-processed report: " + report);

		sqlParams.addValue("itemid", sample.getItemid());

		getNamedParameterJdbcTemplate().query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid",
			sqlParams, new ReportDriver(report, getMapper()));
	}

	@Override
	public void processReport(U sample, ClassificationReport<U, ?> report, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();

		sqlParams.addValue("itemid", sample.getItemid());

		Pair<String, MapSqlParameterSource> exec = pagination.configureSQL("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid", sqlParams);

		if ( pagination instanceof ActivityPagination )
		{
			ActivityPagination activityPagination = (ActivityPagination)pagination;
			if ( activityPagination.isUniqueUsers() )
			{
				exec = pagination.configureSQL("SELECT o.* FROM " + getEmpty().getFullTableName() + " o INNER JOIN (SELECT max(ser) ser, userid FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid GROUP BY userid) AS i ON o.ser=i.ser WHERE o.itemid = :itemid", sqlParams);
			}
		}

		getNamedParameterJdbcTemplate().query(exec.getKey(), exec.getValue(), new ClassificationReportDriver<>(report, getMapper()));
	}
}
