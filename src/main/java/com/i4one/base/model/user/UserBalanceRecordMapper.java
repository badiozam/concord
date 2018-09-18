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

import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;

/**
 * @author Hamid Badiozamani
 */
public class UserBalanceRecordMapper extends BaseRowMapper<UserBalanceRecord>
{
	@Override
	protected UserBalanceRecord mapRowInternal(ResultSet res) throws java.sql.SQLException
	{
		UserBalanceRecord u = new UserBalanceRecord();

		u.setUserid(getInteger(res, "userid"));
		u.setTotal(getInteger(res, "total"));
		u.setCreatetime(getInteger(res, "createtime"));
		u.setUpdatetime(getInteger(res, "updatetime"));
		u.setBalid(getInteger(res, "balid"));

		return u;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(UserBalanceRecord u)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("userid", u.getUserid());
		sqlParams.addValue("total", u.getTotal());
		sqlParams.addValue("balid", u.getBalid());
		sqlParams.addValue("createtime", u.getCreatetime());
		sqlParams.addValue("updatetime", u.getUpdatetime());

		return sqlParams;
	}

	@Override
	public UserBalanceRecord emptyInstance()
	{
		return new UserBalanceRecord();
	}
}
