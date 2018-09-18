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
package com.i4one.base.model.message;

import com.i4one.base.dao.BasePaginableJdbcDao;
import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcMessageRecordDao extends BasePaginableJdbcDao<MessageRecord> implements MessageRecordDao
{
	@Override
	protected BaseRowMapper<MessageRecord> initMapper()
	{
		return new MessageRecordMapper();
	}

	@Override
	public MessageRecord getMessage(int clientid, String language, String key)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("clientid", clientid);
		sqlParams.addValue("language", language);
		sqlParams.addValue("key", key);

		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE clientid = :clientid AND language = :language AND key = :key", sqlParams);
	}

	@Override
	public List<MessageRecord> getMessagesByKey(int clientid, String key)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("clientid", clientid);
		sqlParams.addValue("key", key);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE clientid = :clientid AND key = :key", sqlParams);
	}

	@Override
	public List<MessageRecord> searchMessagesByKey(int clientid, String key, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("clientid", clientid);
		sqlParams.addValue("key", key);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE clientid = :clientid AND key LIKE :key", sqlParams, pagination);
	}

	@Override
	public List<MessageRecord> getMessagesByLanguage(int clientid, String language)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("clientid", clientid);
		sqlParams.addValue("language", language);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE clientid = :clientid AND key = :key", sqlParams);
	}

	@Override
	public String getOrderBy()
	{
		return "key";
	}
}
