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
package com.i4one.base.model.emailblast;

import com.i4one.base.model.targeting.TargetListPagination;
import com.i4one.base.dao.BaseClientJdbcDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcEmailBlastRecordDao extends BaseClientJdbcDao<EmailBlastRecord> implements EmailBlastRecordDao
{
	@Override
	protected EmailBlastRecordMapper initMapper()
	{
		return new EmailBlastRecordMapper();
	}

	@Override
	public List<EmailBlastRecord> getFuture(int asOf, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("currtime", asOf);
		sqlParams.addValue("status", EmailBlastStatus.COMPLETED);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE maturetm > :currtime AND status != :status", sqlParams, pagination);
	}

	@Override
	public List<EmailBlastRecord> getLive(int asOf, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("currtime", asOf);
		sqlParams.addValue("status", EmailBlastStatus.COMPLETED);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE maturetm <= :currtime AND status != :status", sqlParams, pagination);
	}

	@Override
	public List<EmailBlastRecord> getCompleted(int asOf, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("currtime", asOf);
		sqlParams.addValue("status", EmailBlastStatus.COMPLETED);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE maturetm <= :currtime AND status = :status", sqlParams, pagination);
	}

	@Override
	public boolean hasCompiledTargetList(int id)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("emailblastid", id);

		List<Integer> items = queryGeneric("SELECT userid FROM base.emailblastusers WHERE emailblastid = :emailblastid",
			sqlParams,
			TargetListPagination.SINGLE(),
			SingleColumnRowMapper.newInstance(Integer.class));

		return !items.isEmpty();
	}

	@Override
	public void compileTargetList(int id, String targetSQL)
	{
		if ( targetSQL.contains(";"))
		{
			throw new IllegalArgumentException("Invalid query");
		}
		else
		{
			getNamedParameterJdbcTemplate().getJdbcOperations().update("INSERT INTO base.emailblastusers (userid, emailblastid, processed) SELECT *, " + id + ", false FROM (" + targetSQL + ") AS target");
		}
	}

	@Override
	public List<Integer> getTargetList(int id, TargetListPagination pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("emailblastid", id);

		return queryGeneric("SELECT userid FROM base.emailblastusers WHERE emailblastid = :emailblastid", sqlParams, pagination, SingleColumnRowMapper.newInstance(Integer.class));
	}
}
