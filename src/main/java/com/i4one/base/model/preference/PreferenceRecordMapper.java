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
package com.i4one.base.model.preference;

import com.i4one.base.dao.BaseSiteGroupRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class PreferenceRecordMapper extends BaseSiteGroupRecordRowMapper<PreferenceRecord> implements RowMapper<PreferenceRecord>
{

	@Override
	public PreferenceRecord emptyInstance()
	{
		return new PreferenceRecord();
	}

	@Override
	protected PreferenceRecord mapRowInternal(ResultSet res) throws SQLException
	{
		PreferenceRecord sr = new PreferenceRecord();

		sr.setTitle(new IString(res.getString("title")));

		sr.setOrderweight(getInteger(res, "orderweight"));
		sr.setQuestiontype(getInteger(res, "questiontype"));

		sr.setMinresponses(getInteger(res, "minresponses"));
		sr.setMaxresponses(getInteger(res, "maxresponses"));

		sr.setValidanswer(res.getString("validanswer"));
		sr.setDefaultvalue(new IString(res.getString("defaultvalue")));

		sr.setAnswercount(getInteger(res, "answercount"));

		return sr;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(PreferenceRecord o)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("title", o.getTitle());

		sqlParams.addValue("orderweight", o.getOrderweight());
		sqlParams.addValue("questiontype", o.getQuestiontype());

		sqlParams.addValue("minresponses", o.getMinresponses());
		sqlParams.addValue("maxresponses", o.getMaxresponses());

		sqlParams.addValue("validanswer", o.getValidanswer());
		sqlParams.addValue("defaultvalue", o.getDefaultvalue());

		sqlParams.addValue("answercount", o.getAnswercount());

		return sqlParams;
	}
}
