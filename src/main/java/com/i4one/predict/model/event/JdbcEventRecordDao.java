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
package com.i4one.predict.model.event;

import com.i4one.base.dao.categorizable.BaseCategorizableJdbcDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.predict.model.eventprediction.EventPredictionRecord;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcEventRecordDao extends BaseCategorizableJdbcDao<EventRecord> implements EventRecordDao
{
	@Override
	protected EventRecordMapper initMapper()
	{
		return new EventRecordMapper();
	}

	@Override
	public List<EventRecord> getAllActualized(int starttm, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("starttm", starttm);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE starttm >= starttm AND actualoutcomeid IS NOT NULL", sqlParams, pagination);
	}

	@Override
	public Boolean hasUsage(int eventid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("eventid", eventid);

		EventPredictionRecord eventPredictionRecord = new EventPredictionRecord();
		Integer firstSer = querySingleGeneric("SELECT ser FROM " + eventPredictionRecord.getFullTableName() + " WHERE itemid = :eventid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));

		return firstSer != null;
	}
}