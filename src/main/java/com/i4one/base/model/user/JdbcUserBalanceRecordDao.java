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
package com.i4one.base.model.user;

import com.i4one.base.core.Pair;
import com.i4one.base.dao.BasePaginableJdbcDao;
import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.ClassificationReportDriver;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcUserBalanceRecordDao extends BasePaginableJdbcDao<UserBalanceRecord> implements UserBalanceRecordDao
{

	@Override
	protected BaseRowMapper<UserBalanceRecord> initMapper()
	{
		return new UserBalanceRecordMapper();
	}

	@Override
	public List<UserBalanceRecord> weightedRandoms(int balid, int count)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("balid", balid);

		Integer totalEntries = querySingleGeneric("SELECT SUM(total) FROM base.userbalances WHERE balid = :balid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));

		if ( totalEntries != null && totalEntries > 0 )
		{
			WeightedRandomUserBalanceRecordExtractor extractor = new WeightedRandomUserBalanceRecordExtractor(count, totalEntries);
			Set<Integer> ids = getNamedParameterJdbcTemplate().query("SELECT * FROM base.userbalances WHERE balid = :balid", sqlParams, extractor);

			sqlParams.addValue("sers", ids);
			return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE ser IN ( :sers )", sqlParams);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	@Override
	public List<UserBalanceRecord> getUserBalancesByUserid(int userid, PaginationFilter pagination)
	{
		// Create a map of name-value pairs for the query
		//
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("userid", userid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE userid = :userid", sqlParams, pagination);
	}

	@Override
	public List<UserBalanceRecord> getUserBalancesByBalid(int balid, PaginationFilter pagination)
	{
		// Create a map of name-value pairs for the query
		//
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("balid", balid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE balid = :balid", sqlParams, pagination);
	}

	@Override
	public UserBalanceRecord getUserBalance(int balid, int userid)
	{
		// Create a map of name-value pairs for the query
		//
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("balid", balid);
		sqlParams.addValue("userid", userid);

		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE balid = :balid AND userid = :userid", sqlParams);
	}

	@Override
	public UserBalanceRecord random(int balid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("balid", balid);

		// Postgres specific
		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE balid = :balid ORDER BY random()", sqlParams);

		/*
		int count = querySingleGeneric("SELECT COUNT(*) FROM " + getEmpty().getFullTableName() + " WHERE balid = :balid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class)); ;
		int randOffset = (int)(Math.random() * count);

		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE balid = :balid OFFSET " + randOffset, sqlParams);
		*/
	}

	@Override
	public UserBalanceRecord increment(int ser, int amount, int timestamp)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("ser", ser);
		sqlParams.addValue("amount", amount);
		sqlParams.addValue("timestamp", timestamp);

		List<UserBalanceRecord> items = queryInternal("UPDATE " + getEmpty().getFullTableName() + " SET total = total + :amount, updatetime = :timestamp WHERE ser = :ser RETURNING *", 0, 0, null, sqlParams, getMapper());

		if ( items.isEmpty() )
		{
			return null;
		}
		else
		{
			return items.get(0);
		}
	}

	@Override
	public void processReport(UserBalanceRecord sample, ClassificationReport<UserBalanceRecord, ?> report, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();

		sqlParams.addValue("balid", sample.getBalid());

		Pair<String, MapSqlParameterSource> exec = pagination.configureSQL("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE balid = :balid", sqlParams);

		getNamedParameterJdbcTemplate().query(exec.getKey(), exec.getValue(), new ClassificationReportDriver<>(report, getMapper()));
	}
}
