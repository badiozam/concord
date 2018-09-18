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
package com.i4one.base.model.emailblast.builder;

import com.i4one.base.dao.BaseClientRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class EmailBlastBuilderRecordMapper extends BaseClientRecordRowMapper<EmailBlastBuilderRecord>
{
	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(EmailBlastBuilderRecord er)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		addForeignKey(sqlParams, "emailtemplateid", er.getEmailtemplateid());

		sqlParams.addValue("title", er.getTitle());
		sqlParams.addValue("target", er.getTarget());
		sqlParams.addValue("targetsql", er.getTargetsql());

		// State to be saved via an external call
		//
		//sqlParams.addValue("savedstate", er.getSavedstate());

		return sqlParams;
	}

	@Override
	protected EmailBlastBuilderRecord mapRowInternal(ResultSet res) throws SQLException
	{
		EmailBlastBuilderRecord er = new EmailBlastBuilderRecord();

		er.setEmailtemplateid(getInteger(res, "emailtemplateid"));

		er.setTitle(new IString(res.getString("title")));
		er.setTarget(new IString(res.getString("target")));
		er.setTargetsql(res.getString("targetsql"));
		er.setSavedstate(res.getString("savedstate"));

		return er;
	}

	@Override
	public EmailBlastBuilderRecord emptyInstance()
	{
		return new EmailBlastBuilderRecord();
	}
}
