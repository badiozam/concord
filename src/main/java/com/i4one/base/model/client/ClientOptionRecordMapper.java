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
package com.i4one.base.model.client;

import com.i4one.base.dao.BaseClientRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class ClientOptionRecordMapper extends BaseClientRecordRowMapper<ClientOptionRecord>
{
	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(ClientOptionRecord option)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();
		sqlParams.addValue("key", option.getKey());
		sqlParams.addValue("value", option.getValue());

		return sqlParams;
	}

	@Override
	protected ClientOptionRecord mapRowInternal(ResultSet res) throws SQLException
	{
		ClientOptionRecord retVal = new ClientOptionRecord();

		retVal.setKey(res.getString("key"));
		retVal.setValue(res.getString("value"));

		return retVal;
	}

	@Override
	public ClientOptionRecord emptyInstance()
	{
		return new ClientOptionRecord();
	}
}
