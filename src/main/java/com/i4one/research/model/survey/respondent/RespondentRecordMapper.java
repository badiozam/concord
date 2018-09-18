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
package com.i4one.research.model.survey.respondent;

import com.i4one.base.dao.activity.BaseActivityRecordRowMapper;
import com.i4one.base.dao.BaseUsageRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class RespondentRecordMapper extends BaseActivityRecordRowMapper<RespondentRecord> implements RowMapper<RespondentRecord>
{
	@Override
	public RespondentRecord emptyInstance()
	{
		return new RespondentRecord();
	}

	@Override
	protected RespondentRecord mapRowInternal(ResultSet res) throws SQLException
	{
		RespondentRecord sr = new RespondentRecord();

		sr.setCurrentpage(getInteger(res, "currentpage"));
		sr.setStartpage(getInteger(res, "startpage"));
		sr.setUpdatetime(getInteger(res, "updatetime"));
		sr.setHasfinished(getBoolean(res, "hasfinished"));

		return sr;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(RespondentRecord sr)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("currentpage", sr.getCurrentpage());
		sqlParams.addValue("startpage", sr.getStartpage());
		sqlParams.addValue("updatetime", sr.getUpdatetime());
		sqlParams.addValue("hasfinished", sr.getHasfinished());

		return sqlParams;
	}
}
