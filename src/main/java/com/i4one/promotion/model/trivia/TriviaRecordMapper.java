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
package com.i4one.promotion.model.trivia;

import com.i4one.base.dao.categorizable.BaseCategorizableSiteGroupRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class TriviaRecordMapper extends BaseCategorizableSiteGroupRecordRowMapper<TriviaRecord> implements RowMapper<TriviaRecord>
{

	@Override
	public TriviaRecord emptyInstance()
	{
		return new TriviaRecord();
	}

	@Override
	protected TriviaRecord mapRowInternal(ResultSet res) throws SQLException
	{
		TriviaRecord r = new TriviaRecord();

		r.setTitle(new IString(res.getString("title")));
		r.setDescr(new IString(res.getString("descr")));

		r.setIntro(new IString(res.getString("intro")));
		r.setOutro(new IString(res.getString("outro")));
		r.setOrderweight(getInteger(res, "orderweight"));
		r.setQuestiontype(getInteger(res, "questiontype"));
		r.setRandomize(getBoolean(res, "randomize"));
		r.setAnswercount(getInteger(res, "answercount"));

		r.setCorrectanswerid(getInteger(res, "correctanswerid"));

		return r;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(TriviaRecord o)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("title", o.getTitle());
		sqlParams.addValue("descr", o.getDescr());

		sqlParams.addValue("intro", o.getIntro());
		sqlParams.addValue("outro", o.getOutro());
		sqlParams.addValue("orderweight", o.getOrderweight());
		sqlParams.addValue("questiontype", o.getQuestiontype());
		sqlParams.addValue("randomize", o.getRandomize());
		sqlParams.addValue("answercount", o.getAnswercount());

		addForeignKey(sqlParams, "correctanswerid", o.getCorrectanswerid());

		return sqlParams;
	}
}
