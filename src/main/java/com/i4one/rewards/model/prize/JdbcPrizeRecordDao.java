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

import com.i4one.base.core.Utils;
import com.i4one.base.dao.BaseSiteGroupJdbcDao;
import com.i4one.base.dao.BaseSiteGroupRecordRowMapper;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcPrizeRecordDao extends BaseSiteGroupJdbcDao<PrizeRecord> implements PrizeRecordDao
{
	@Override
	protected BaseSiteGroupRecordRowMapper<PrizeRecord> initMapper()
	{
		return new PrizeRecordMapper();
	}


	@Override
	public void incrementInitinventory(int prizeid, int amount)
	{
		cacheEvict(prizeid);

		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("prizeid", prizeid);
		sqlParams.addValue("amount", amount);

		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET initinventory = initinventory + :amount WHERE ser = :prizeid", sqlParams);
	}

	@Override
	public void incrementCurrinventory(int prizeid, int amount)
	{
		cacheEvict(prizeid);

		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("prizeid", prizeid);
		sqlParams.addValue("amount", amount);

		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET currinventory = currinventory + :amount WHERE ser = :prizeid", sqlParams);
	}

	@Override
	public List<PrizeRecord> search(String title, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE LOWER(title) LIKE '%" + Utils.sqlEscape(title.toLowerCase())+ "%'", sqlParams, pagination);
	}
}
