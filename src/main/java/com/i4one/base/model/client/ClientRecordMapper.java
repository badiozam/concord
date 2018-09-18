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

import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class ClientRecordMapper extends BaseRowMapper<SingleClientRecord>
{
	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(SingleClientRecord c)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();
		sqlParams.addValue("tz", c.getTz());
		sqlParams.addValue("country", c.getCountry());
		sqlParams.addValue("languages", c.getLanguages());
		sqlParams.addValue("domain", c.getDomain());

		sqlParams.addValue("operator", c.getOperator());
		sqlParams.addValue("address", c.getAddress());
		sqlParams.addValue("url", c.getUrl());
		sqlParams.addValue("logourl", c.getLogourl());

		sqlParams.addValue("email", c.getEmail());
		sqlParams.addValue("descr", c.getDescr());
		sqlParams.addValue("name", c.getName());
		sqlParams.addValue("parentid", c.getParentid());

		return sqlParams;
	}

	@Override
	protected SingleClientRecord mapRowInternal(ResultSet res) throws SQLException
	{
		SingleClientRecord client = new SingleClientRecord();

		client.setTz(res.getString("tz"));
		client.setCountry(res.getString("country"));
		client.setLanguages(res.getString("languages"));
		client.setDomain(res.getString("domain"));

		client.setOperator(res.getString("operator"));
		client.setAddress(res.getString("address"));
		client.setUrl(res.getString("url"));
		client.setLogourl(res.getString("logourl"));

		client.setName(res.getString("name"));
		client.setEmail(res.getString("email"));
		client.setDescr(res.getString("descr"));
		client.setParentid(getInteger(res, "parentid"));

		return client;
	}

	@Override
	public SingleClientRecord emptyInstance()
	{
		return new SingleClientRecord();
	}
}
