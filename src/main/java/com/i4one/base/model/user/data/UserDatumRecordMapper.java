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
package com.i4one.base.model.user.data;

import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class UserDatumRecordMapper extends BaseRowMapper<UserDatumRecord>
{

	@Override
	protected UserDatumRecord mapRowInternal(ResultSet res) throws SQLException
	{
		UserDatumRecord ud = new UserDatumRecord();

		ud.setUserid(getInteger(res, "userid"));
		ud.setKey(res.getString("key"));
		ud.setValue(res.getString("value"));
		ud.setTimestamp(getInteger(res, "timestamp"));
		ud.setDuration(getInteger(res, "duration"));

		return ud;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(UserDatumRecord ud)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("key", ud.getKey());
		sqlParams.addValue("value", ud.getValue());
		sqlParams.addValue("timestamp", ud.getTimestamp());
		sqlParams.addValue("duration", ud.getDuration());

		addForeignKey(sqlParams, "userid", ud.getUserid());

		return sqlParams;
	}

	@Override
	public UserDatumRecord emptyInstance()
	{
		return new UserDatumRecord();
	}
}
