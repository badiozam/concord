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

import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class AdminRecordMapper extends BaseRowMapper<AdminRecord>
{
	@Override
	protected AdminRecord mapRowInternal(ResultSet res) throws SQLException
	{
		AdminRecord a = new AdminRecord();

		a.setUsername(res.getString("username"));
		a.setPassword(res.getString("password"));
		a.setEmail(res.getString("email"));
		a.setName(res.getString("name"));
		a.setForceUpdate(getBoolean(res, "forceupdate"));

		return a;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(AdminRecord a)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("name", a.getName());
		sqlParams.addValue("username", a.getUsername());
		//sqlParams.addValue("password", a.getPassword());
		sqlParams.addValue("email", a.getEmail());
		sqlParams.addValue("forceupdate", a.isForceUpdate());

		return sqlParams;
	}

	@Override
	public AdminRecord emptyInstance()
	{
		return new AdminRecord();
	}
}
