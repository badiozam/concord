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
package com.i4one.base.model.friendref;

import com.i4one.base.dao.BaseClientRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class FriendRefRecordMapper extends BaseClientRecordRowMapper<FriendRefRecord>
{

	@Override
	protected FriendRefRecord mapRowInternal(ResultSet res) throws SQLException
	{
		FriendRefRecord retVal = new FriendRefRecord();

		retVal.setUserid(getInteger(res, "userid"));
		retVal.setFriendid(getInteger(res, "friendid"));

		retVal.setEmail(res.getString("email"));
		retVal.setFirstname(res.getString("firstname"));
		retVal.setLastname(res.getString("lastname"));
		retVal.setMessage(res.getString("message"));

		retVal.setTimestamp(getInteger(res, "timestamp"));

		return retVal;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(FriendRefRecord item)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("email", item.getEmail());
		sqlParams.addValue("firstname", item.getFirstname());
		sqlParams.addValue("lastname", item.getLastname());
		sqlParams.addValue("timestamp", item.getTimestamp());
		sqlParams.addValue("message", item.getMessage());

		addForeignKey(sqlParams, "userid", item.getUserid());
		addForeignKey(sqlParams, "friendid", item.getFriendid());

		return sqlParams;
	}

	@Override
	public FriendRefRecord emptyInstance()
	{
		return new FriendRefRecord();
	}
}
