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
package com.i4one.research.model.survey.answer;

import com.i4one.base.dao.BasePaginableJdbcDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcAnswerRecordDao extends BasePaginableJdbcDao<AnswerRecord> implements AnswerRecordDao
{
	@Override
	protected AnswerRecordMapper initMapper()
	{
		return new AnswerRecordMapper();
	}

	@Override
	public List<AnswerRecord> getAnswers(int questionid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("questionid", questionid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE questionid = :questionid", sqlParams, pagination);
	}

}
