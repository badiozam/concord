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
package com.i4one.predict.model.eventprediction;

import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.dao.activity.BaseQuantifiedActivityJdbcDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.predict.model.event.EventRecord;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcEventPredictionRecordDao extends BaseQuantifiedActivityJdbcDao<EventPredictionRecord> implements EventPredictionRecordDao
{
	private SimplePaginationFilter descOrder;

	@Override
	public void init()
	{
		super.init();

		descOrder = new SimplePaginationFilter();
		descOrder.setOrderBy(getEmpty().getFullTableName() + ".ser DESC");
	}

	@Override
	protected BaseRowMapper<EventPredictionRecord> initMapper()
	{
		return new EventPredictionRecordMapper();
	}

	@Override
	public List<EventPredictionRecord> getAllEventPredictionsByUser(int userid, int termid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("userid", userid);
		sqlParams.addValue("termid", termid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE userid = :userid AND termid = :termid" , sqlParams, pagination);
	}

	@Override
	public List<EventPredictionRecord> getPendingEventPredictionsByUser(int userid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("userid", userid);

		EventRecord eventRecord = new EventRecord();

		return query("SELECT * FROM " + getEmpty().getFullTableName() + ", " + eventRecord.getFullTableName() + " WHERE " + getEmpty().getFullTableName() + ".userid = :userid AND " + getEmpty().getFullTableName() + ".itemid = " + eventRecord.getFullTableName() + ".ser AND " + eventRecord.getFullTableName() + ".actualoutcomeid IS NULL", sqlParams, pagination);
	}

	@Override
	public List<EventPredictionRecord> getPostedEventPredictionsByUser(int userid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("userid", userid);

		EventRecord eventRecord = new EventRecord();

		return query("SELECT * FROM " + getEmpty().getFullTableName() + ", " + eventRecord.getFullTableName() + " WHERE " + getEmpty().getFullTableName() + ".userid = :userid AND " + getEmpty().getFullTableName() + ".itemid = " + eventRecord.getFullTableName() + ".ser AND " + eventRecord.getFullTableName() + ".actualoutcomeid IS NOT NULL", sqlParams, descOrder);
	}
}