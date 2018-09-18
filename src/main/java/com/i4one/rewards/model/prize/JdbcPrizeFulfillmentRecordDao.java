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
package com.i4one.rewards.model.prize;

import com.i4one.base.dao.RecordTypeRowMapper;
import com.i4one.base.dao.activity.BaseQuantifiedActivityJdbcDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcPrizeFulfillmentRecordDao extends BaseQuantifiedActivityJdbcDao<PrizeFulfillmentRecord> implements PrizeFulfillmentRecordDao
{
	@Override
	public List<PrizeFulfillmentRecord> getByUser(int adminid, PaginationFilter pagination)
	{
		throw new UnsupportedOperationException("Prize fulfillments are only accessible by administrators.");
	}

	@Override
	public List<PrizeFulfillmentRecord> getAll(int itemid, int adminid, int timestamp, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);
		sqlParams.addValue("adminid", adminid);
		sqlParams.addValue("timestamp", timestamp);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid AND adminid = :adminid AND timestamp = :timestamp", sqlParams, pagination);
	}

	@Override
	public List<PrizeFulfillmentRecord> getAll(int itemid, int adminid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);
		sqlParams.addValue("adminid", adminid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid AND adminid = :adminid", sqlParams, pagination);
	}

	@Override
	public int getCountByItem(int itemid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);

		return querySingleGeneric("SELECT COUNT(*) FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));
	}

	@Override
	public PrizeFulfillmentRecord get(int itemid, int adminid)
	{
		throw new UnsupportedOperationException("There can be multiple fulfillments per prize winning, use getAll(..) instead");
	}

	@Override
	public void removeAll(int itemid, int adminid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);
		sqlParams.addValue("adminid", adminid);

		getNamedParameterJdbcTemplate().update("DELETE FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid AND adminid = :adminid", sqlParams);
	}

	@Override
	protected RecordTypeRowMapper<PrizeFulfillmentRecord> initMapper()
	{
		return new PrizeFulfillmentRecordMapper();
	}
}
