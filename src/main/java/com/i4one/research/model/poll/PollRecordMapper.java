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
package com.i4one.research.model.poll;

import com.i4one.base.dao.categorizable.BaseCategorizableSiteGroupRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class PollRecordMapper extends BaseCategorizableSiteGroupRecordRowMapper<PollRecord> implements RowMapper<PollRecord>
{

	@Override
	public PollRecord emptyInstance()
	{
		return new PollRecord();
	}

	@Override
	protected PollRecord mapRowInternal(ResultSet res) throws SQLException
	{
		PollRecord sr = new PollRecord();

		sr.setPollingstarttm(getInteger(res, "pollingstarttm"));
		sr.setPollingendtm(getInteger(res, "pollingendtm"));

		sr.setIntro(new IString(res.getString("intro")));
		sr.setOutro(new IString(res.getString("outro")));
		sr.setOrderweight(getInteger(res, "orderweight"));
		sr.setQuestiontype(getInteger(res, "questiontype"));
		sr.setRandomize(getBoolean(res, "randomize"));
		sr.setTitle(new IString(res.getString("title")));
		sr.setAnswercount(getInteger(res, "answercount"));

		return sr;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(PollRecord o)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("pollingstarttm", o.getPollingstarttm());
		sqlParams.addValue("pollingendtm", o.getPollingendtm());

		sqlParams.addValue("title", o.getTitle());
		sqlParams.addValue("intro", o.getIntro());
		sqlParams.addValue("outro", o.getOutro());
		sqlParams.addValue("orderweight", o.getOrderweight());
		sqlParams.addValue("questiontype", o.getQuestiontype());
		sqlParams.addValue("randomize", o.getRandomize());
		sqlParams.addValue("answercount", o.getAnswercount());

		return sqlParams;
	}
}
