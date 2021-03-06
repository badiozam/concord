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
package com.i4one.base.model.userlogin;

import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.dao.activity.BaseQuantifiedActivityRecordRowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class UserLoginRecordMapper extends BaseQuantifiedActivityRecordRowMapper<UserLoginRecord> implements RowMapper<UserLoginRecord>
{
	@Override
	public UserLoginRecord emptyInstance()
	{
		return new UserLoginRecord();
	}

	@Override
	protected UserLoginRecord mapRowInternal(ResultSet res) throws SQLException
	{
		UserLoginRecord retVal = new UserLoginRecord();

		retVal.setSrcip(res.getString("srcip"));
		retVal.setUseragent(res.getString("useragent"));

		return retVal;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(UserLoginRecord item)
	{
		RecordTypeSqlParameterSource retVal = new RecordTypeSqlParameterSource();

		retVal.addValue("srcip", item.getSrcip());
		retVal.addValue("useragent", item.getUseragent());

		return retVal;
	}

}
