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

import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.dao.categorizable.BaseCategorizableSiteGroupRecordRowMapper;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class EventRecordMapper extends BaseCategorizableSiteGroupRecordRowMapper<EventRecord> implements RowMapper<EventRecord>
{
	@Override
	protected EventRecord mapRowInternal(ResultSet res) throws SQLException
	{
		EventRecord er = new EventRecord();

		er.setTitle(new IString(res.getString("title")));
		er.setDescr(new IString(res.getString("descr")));
		er.setMinbid(getInteger(res, "minbid"));
		er.setMaxbid(getInteger(res, "maxbid"));
		er.setBrief(new IString(res.getString("brief")));
		er.setPromo(new IString(res.getString("promo")));
		er.setReference(new IString(res.getString("reference")));
		er.setPostsby(getInteger(res, "postsby"));
		er.setPostedtm(getInteger(res, "postedtm"));
		er.setUsagecount(getInteger(res, "usagecount"));

		er.setActualoutcomeid(getInteger(res, "actualoutcomeid"));

		// Removed per Reg:8/4/2017
		//
		//er.setClosesby(getInteger(res, "closesby"));

		return er;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(EventRecord er)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("title", er.getTitle());
		sqlParams.addValue("brief", er.getBrief());
		sqlParams.addValue("promo", er.getPromo());
		sqlParams.addValue("descr", er.getDescr());
		sqlParams.addValue("reference", er.getReference());
		sqlParams.addValue("minbid", er.getMinbid());
		sqlParams.addValue("maxbid", er.getMaxbid());
		sqlParams.addValue("postsby", er.getPostsby());
		sqlParams.addValue("postedtm", er.getPostedtm());
		sqlParams.addValue("usagecount", er.getUsagecount());

		addForeignKey(sqlParams, "actualoutcomeid", er.getActualoutcomeid());

		
		// Removed per Reg: 8/4/2017
		//
		sqlParams.addValue("closesby", er.getClosesby());

		return sqlParams;
	}

	@Override
	public EventRecord emptyInstance()
	{
		return new EventRecord();
	}

}
