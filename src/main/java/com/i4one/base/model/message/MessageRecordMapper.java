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
package com.i4one.base.model.message;

import com.i4one.base.dao.BaseClientRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class MessageRecordMapper extends BaseClientRecordRowMapper<MessageRecord>
{
	@Override
	protected MessageRecord mapRowInternal(ResultSet res) throws SQLException
	{
		MessageRecord m = new MessageRecord();

		m.setLanguage(res.getString("language"));
		m.setKey(res.getString("key"));
		m.setValue(res.getString("value"));
		m.setUpdateTime(getInteger(res, "updatetime"));

		return m;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(MessageRecord m)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("language", m.getLanguage());
		sqlParams.addValue("key", m.getKey());
		sqlParams.addValue("value", m.getValue());
		sqlParams.addValue("updatetime", m.getUpdateTime());

		return sqlParams;
	}

	@Override
	public MessageRecord emptyInstance()
	{
		return new MessageRecord();
	}
}
