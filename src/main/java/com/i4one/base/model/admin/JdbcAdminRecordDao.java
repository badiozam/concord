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
package com.i4one.base.model.admin;

import com.i4one.base.dao.BasePaginableJdbcDao;
import com.i4one.base.dao.BaseRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcAdminRecordDao extends BasePaginableJdbcDao<AdminRecord> implements AdminRecordDao
{
	@Override
	protected BaseRowMapper<AdminRecord> initMapper()
	{
		return new AdminRecordMapper();
	}


	@Override
	public AdminRecord getAdmin(String username)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("username", username);

		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE username = :username", sqlParams);
	}

	@Override
	public String getPassword(int ser)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("ser", ser);

		return querySingleGeneric("SELECT password FROM " + getEmpty().getFullTableName() + " WHERE ser = :ser", sqlParams, SingleColumnRowMapper.newInstance(String.class));
	}

	@Override
	public void updatePassword(AdminRecord admin, String clearPassword)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("ser", admin.getSer());
		sqlParams.addValue("password", clearPassword);

		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET password = :password, forceupdate = false WHERE ser= :ser", sqlParams);

		// We've updated the record so the cache needs to be cleared. This is a special
		// case since we're excluding the password column from being updated normally
		//
		cacheEvict(admin.getSer());
	}
}
