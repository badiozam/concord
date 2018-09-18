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
package com.i4one.base.model.accesscode;

import com.i4one.base.dao.RecordType;
import com.i4one.base.dao.RecordTypeRowMapper;
import com.i4one.base.dao.terminable.BaseTerminableClientJdbcDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import java.util.List;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcAccessCodeRecordDao extends BaseTerminableClientJdbcDao<AccessCodeRecord> implements AccessCodeRecordDao
{
	@Override
	public void init()
	{
		super.init();
	}

	@Override
	protected AccessCodeRecordMapper initMapper()
	{
		return new AccessCodeRecordMapper();
	}

	@Override
	public List<AccessCodeRecord> getLiveCodes(String code, TerminablePagination pagination)
	{
		// We get the live codes and use the pagination-set value to retrieve
		// live codes based on a particular time
		//
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("code", code);
		sqlParams.addValue("currtime", pagination.getCurrentTime());

		return query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE LOWER(code) = LOWER(:code) AND starttm <= :currtime AND endtm > :currtime", sqlParams, pagination);
	}
}
