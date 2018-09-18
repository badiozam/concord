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
package com.i4one.base.model.client;

import com.i4one.base.dao.BasePaginableJdbcDao;
import com.i4one.base.dao.BaseRowMapper;
import static com.i4one.base.model.client.SingleClient.getRoot;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcClientOptionRecordDao extends BasePaginableJdbcDao<ClientOptionRecord> implements ClientOptionRecordDao
{
	@Override
	protected BaseRowMapper<ClientOptionRecord> initMapper()
	{
		return new ClientOptionRecordMapper();
	}

	@Override
	public ClientOptionRecord getOption(int clientid, String key)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("clientid", clientid);
		sqlParams.addValue("key", key);

		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE clientid = :clientid AND key = :key", sqlParams);
	}

	@Override
	public List<ClientOptionRecord> getOptions(int clientid, String keyStartsWith, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("clientid", clientid);
		sqlParams.addValue("keyStartsWith", keyStartsWith.toLowerCase() + "%");

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE clientid=:clientid AND LOWER(key) LIKE :keyStartsWith", sqlParams, pagination);
	}

	@Override
	public List<String> getAllKeys(PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("clientid", getRoot().getSer());

		return queryGeneric("SELECT key FROM " + getEmpty().getFullTableName() + " WHERE clientid = :clientid" , sqlParams, pagination, SingleColumnRowMapper.newInstance(String.class ));
	}
}
