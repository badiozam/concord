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
package com.i4one.base.model.balance;

import com.i4one.base.dao.BaseClientJdbcDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcBalanceRecordDao extends BaseClientJdbcDao<BalanceRecord> implements BalanceRecordDao
{
	@Override
	protected BalanceRecordMapper initMapper()
	{
		return new BalanceRecordMapper();
	}

	@Override
	public BalanceRecord getBalance(int tableid, String tablename, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();

		sqlParams.addValue("featureid", tableid);
		sqlParams.addValue("feature", tablename);

		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE feature = :feature AND featureid = :featureid", sqlParams, pagination);
	}

	@Override
	public List<BalanceRecord> getAllBalances(PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();

		return query("SELECT * FROM " + getEmpty().getFullTableName(), sqlParams, pagination);
	}
}
