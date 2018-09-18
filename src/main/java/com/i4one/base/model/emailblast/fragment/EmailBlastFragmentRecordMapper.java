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
package com.i4one.base.model.emailblast.fragment;

import com.i4one.base.dao.BaseClientRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class EmailBlastFragmentRecordMapper extends BaseClientRecordRowMapper<EmailBlastFragmentRecord>
{
	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(EmailBlastFragmentRecord er)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		addForeignKey(sqlParams, "emailblastid", er.getEmailblastid());

		sqlParams.addValue("status", er.getStatus());
		sqlParams.addValue("fragoffset", er.getFragoffset());
		sqlParams.addValue("fraglimit", er.getFraglimit());
		sqlParams.addValue("fragcount", er.getFragcount());
		sqlParams.addValue("fragsent", er.getFragsent());

		sqlParams.addValue("lastupdatetm", er.getLastupdatetm());
		sqlParams.addValue("owner", er.getOwner());

		return sqlParams;
	}

	@Override
	protected EmailBlastFragmentRecord mapRowInternal(ResultSet res) throws SQLException
	{
		EmailBlastFragmentRecord er = new EmailBlastFragmentRecord();

		er.setEmailblastid(getInteger(res, "emailblastid"));

		er.setStatus(getInteger(res, "status"));
		er.setFragoffset(getInteger(res, "fragoffset"));
		er.setFraglimit(getInteger(res, "fraglimit"));
		er.setFragcount(getInteger(res, "fragcount"));
		er.setFragsent(getInteger(res, "fragsent"));

		er.setLastupdatetm(getInteger(res, "lastupdatetm"));
		er.setOwner(res.getString("owner"));

		return er;
	}

	@Override
	public EmailBlastFragmentRecord emptyInstance()
	{
		return new EmailBlastFragmentRecord();
	}
}
