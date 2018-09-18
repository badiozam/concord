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
package com.i4one.research.model.survey;

import com.i4one.base.dao.categorizable.BaseCategorizableSiteGroupRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class SurveyRecordMapper extends BaseCategorizableSiteGroupRecordRowMapper<SurveyRecord> implements RowMapper<SurveyRecord>
{

	@Override
	public SurveyRecord emptyInstance()
	{
		return new SurveyRecord();
	}

	@Override
	protected SurveyRecord mapRowInternal(ResultSet res) throws SQLException
	{
		SurveyRecord sr = new SurveyRecord();

		sr.setIntro(new IString(res.getString("intro")));
		sr.setOutro(new IString(res.getString("outro")));
		sr.setRandomize(getBoolean(res, "randomize"));
		sr.setTitle(new IString(res.getString("title")));
		sr.setPerpage(getInteger(res, "perpage"));
		sr.setQuestioncount(getInteger(res, "questioncount"));

		return sr;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(SurveyRecord sr)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("title", sr.getTitle());
		sqlParams.addValue("intro", sr.getIntro());
		sqlParams.addValue("outro", sr.getOutro());
		sqlParams.addValue("perpage", sr.getPerpage());
		sqlParams.addValue("randomize", sr.getRandomize());
		sqlParams.addValue("questioncount", sr.getQuestioncount());

		return sqlParams;
	}
}
