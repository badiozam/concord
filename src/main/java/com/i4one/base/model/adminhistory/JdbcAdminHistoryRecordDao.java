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
package com.i4one.base.model.adminhistory;

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
public class JdbcAdminHistoryRecordDao extends BasePaginableJdbcDao<AdminHistoryRecord> implements AdminHistoryRecordDao
{
	@Override
	protected BaseRowMapper<AdminHistoryRecord> initMapper()
	{
		return new AdminHistoryRecordMapper();
	}

	@Override
	public List<AdminHistoryRecord> getAdminHistoryByParent(int parentid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("parentid", parentid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE parentid = :parentid", sqlParams, pagination);
	}

	@Override
	public List<AdminHistoryRecord> getAdminHistoryByAdmin(int adminid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("adminid", adminid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE adminid = :adminid", sqlParams, pagination);
	}

	@Override
	public List<AdminHistoryRecord> getAdminHistory(String feature, int featureid, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("feature", feature);
		sqlParams.addValue("featureid", featureid);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE feature = :feature AND featureid = :featureid", sqlParams, pagination);
	}

	@Override
	public List<AdminHistoryRecord> getAdminHistoryByFeature(String feature, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("feature", feature);

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE feature = :feature", sqlParams, pagination);
	}
}
