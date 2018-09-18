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
import com.i4one.base.dao.BaseClientJdbcDao;
import com.i4one.base.model.balance.BalanceRecord;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.report.ReportDriver;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.ClassificationReportDriver;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcUserRecordDao extends BaseClientJdbcDao<UserRecord> implements UserRecordDao
{
	@Override
	protected UserRecordMapper initMapper()
	{
		return new UserRecordMapper();
	}

	@Override
	public void updateLastLoginTime(UserRecord user, int currentTimeSeconds)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("ser", user.getSer());
		sqlParams.addValue("lastlogintime", currentTimeSeconds);

		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET lastlogintime = :lastlogintime WHERE ser= :ser", sqlParams);

		// We've updated the record so the cache needs to be cleared. This is a special
		// case since we're excluding the lastlogintime column from being updated normally
		//
		cacheEvict(user.getSer());
	}

	@Override
	public void updatePassword(UserRecord user, String clearPassword)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("ser", user.getSer());
		sqlParams.addValue("password", clearPassword);

		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET password = :password, forceupdate = false WHERE ser= :ser", sqlParams);

		// We've updated the record so the cache needs to be cleared. This is a special
		// case since we're excluding the password column from being updated normally
		//
		cacheEvict(user.getSer());
	}

	@Override
	public List<UserRecord> getAllUsers(PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();

		return query("SELECT * FROM " + getEmpty().getFullTableName(), sqlParams, pagination);
	}

	@Override
	public List<UserRecord> getAllMembers(SingleClient client, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("clientid", client.getSer());
		sqlParams.addValue("clientfeature", client.getFeatureName());

		// It's better to do a one level subquery as that allows the pagination
		// qualifiers to work properly
		//
		//return query("SELECT * FROM " + getEmpty().getFullTableName() + "u, base.balances b, " + client.getDelegate().getFullTableName() + " c, base.userbalances ub WHERE u.ser=ub.userid AND ub.balid=b.ser AND b.featureid= :clientid AND b.featurename = :clientfeature", sqlParams, pagination);

		BalanceRecord b = new BalanceRecord();
		UserBalanceRecord ub = new UserBalanceRecord();

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE ser IN "
			+ "(SELECT ub.userid FROM " +
				b.getFullTableName() + " b, "
				+ client.getDelegate().getFullTableName() + " c, "
				+ ub.getFullTableName() + " ub "
				+ "WHERE ub.balid=b.ser AND "
					+ "b.featureid= :clientid AND "
					+ "b.feature = :clientfeature"
					+ " ORDER BY userid)",
			sqlParams, pagination);
	}

	@Override
	public void processReport(UserRecord sample, TopLevelReport report)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("clientid", sample.getClientid());

		report.initReport();
		getLogger().debug("Pre-processed report: " + report);

		getNamedParameterJdbcTemplate().query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE clientid = :clientid",  sqlParams, new ReportDriver(report, getMapper()));
	}

	@Override
	public void processReport(UserRecord sample, ClassificationReport<UserRecord, ?> report, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();

		sqlParams.addValue("clientid", sample.getClientid());

		Pair<String, MapSqlParameterSource> exec = pagination.configureSQL("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE clientid = :clientid", sqlParams);
		getNamedParameterJdbcTemplate().query(exec.getKey(), exec.getValue(),
			 new ClassificationReportDriver<>(report, getMapper()));
	}

	@Override
	public UserRecord getUserByUsername(String username)
	{
		// Create a map of name-value pairs for the query
		//
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("username", username);

		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE LOWER(username) = :username", sqlParams, SimplePaginationFilter.SINGLE);
	}

	@Override
	public UserRecord getUserByEmail(String email)
	{
		// Create a map of name-value pairs for the query
		//
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("email", email);

		// Look for the user
		//
		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE LOWER(email) = :email", sqlParams, SimplePaginationFilter.SINGLE);
	}

	@Override
	public UserRecord getUserByCellphone(String cellphone)
	{
		// Create a map of name-value pairs for the query
		//
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("cellphone", cellphone);

		// Look for the user
		//
		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE cellphone = :cellphone", sqlParams, SimplePaginationFilter.SINGLE);
	}
}
