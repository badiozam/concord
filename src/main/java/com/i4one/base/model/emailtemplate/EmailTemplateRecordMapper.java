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
package com.i4one.base.model.emailtemplate;

import com.i4one.base.dao.BaseClientRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class EmailTemplateRecordMapper extends BaseClientRecordRowMapper<EmailTemplateRecord>
{
	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(EmailTemplateRecord er)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("key", er.getKey());

		sqlParams.addValue("fromaddr", er.getFromaddr());
		sqlParams.addValue("bcc", er.getBcc());
		sqlParams.addValue("replyto", er.getReplyto());
		sqlParams.addValue("subject", er.getSubject());
		sqlParams.addValue("htmlbody", er.getHtmlbody());
		sqlParams.addValue("textbody", er.getTextbody());

		return sqlParams;
	}

	@Override
	protected EmailTemplateRecord mapRowInternal(ResultSet res) throws SQLException
	{
		EmailTemplateRecord er = new EmailTemplateRecord();

		er.setKey(res.getString("key"));

		er.setFromaddr(new IString(res.getString("fromaddr")));
		er.setBcc(new IString(res.getString("bcc")));

		er.setReplyto(new IString(res.getString("replyto")));

		er.setSubject(new IString(res.getString("subject")));
		er.setHtmlbody(new IString(res.getString("htmlbody")));
		er.setTextbody(new IString(res.getString("textbody")));

		return er;
	}

	@Override
	public EmailTemplateRecord emptyInstance()
	{
		return new EmailTemplateRecord();
	}
}
