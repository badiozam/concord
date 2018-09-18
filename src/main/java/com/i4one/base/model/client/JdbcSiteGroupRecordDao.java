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

import com.i4one.base.core.Pair;
import com.i4one.base.dao.BaseJdbcDao;
import com.i4one.base.dao.RecordTypeRowMapper;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcSiteGroupRecordDao extends BaseJdbcDao<SiteGroupRecord> implements SiteGroupRecordDao
{
	@Override
	protected RecordTypeRowMapper<SiteGroupRecord> initMapper()
	{
		return new SiteGroupRecordMapper();
	}

	@Override
	public List<SiteGroupRecord> getAll(PaginationFilter pagination)
	{
		Pair<String, MapSqlParameterSource> exec = pagination.configureSQL("SELECT * FROM " + getEmpty().getFullTableName(), new MapSqlParameterSource());
		return directQuery(exec.getKey(), exec.getValue(), getMapper());
	}

	@Override
	public List<SiteGroupRecord> getSiteGroups(int clientid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("clientid", clientid);

		return directQuery("SELECT sg.* FROM " + getEmpty().getFullTableName() + " sg, base.sitegroupmap sgm  WHERE sgm.sitegroupid = sg.ser AND sgm.clientid = :clientid", sqlParams, getMapper());
	}

	@Override
	public List<SingleClientRecord> getSingleClients(int sitegroupid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("sitegroupid", sitegroupid);

		SingleClientRecord empty = new SingleClientRecord(); 

		return directQuery("SELECT c.* FROM " + empty.getFullTableName() + " c, base.sitegroupmap cgm WHERE cgm.sitegroupid = :sitegroupid AND cgm.clientid=c.ser", sqlParams, new ClientRecordMapper());
	}

	@Override
	public void associate(int sitegroupid, int clientid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("sitegroupid", sitegroupid);
		sqlParams.addValue("clientid", clientid);

		getNamedParameterJdbcTemplate().update("INSERT INTO base.sitegroupmap (sitegroupid, clientid) VALUES (:sitegroupid, :clientid)", sqlParams);
	}

	@Override
	public void dissociate(int sitegroupid, int clientid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("sitegroupid", sitegroupid);
		sqlParams.addValue("clientid", clientid);

		getNamedParameterJdbcTemplate().update("DELETE FROM base.sitegroupmap WHERE sitegroupid=:sitegroupid AND clientid = :clientid", sqlParams);
	}

	@Override
	public boolean isAssociated(int sitegroupid, int clientid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("sitegroupid", sitegroupid);
		sqlParams.addValue("clientid", clientid);

		Integer firstSer = querySingleGeneric("SELECT ser FROM base.sitegroupmap WHERE sitegroupid = :sitegroupid AND clientid = :clientid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));

		return firstSer != null;
	}
}
