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
package com.i4one.base.model.usermessage;

import com.i4one.base.dao.terminable.BaseTerminableSiteGroupJdbcDao;
import com.i4one.base.dao.terminable.TerminableSiteGroupRecordRowMapper;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcUserMessageRecordDao extends BaseTerminableSiteGroupJdbcDao<UserMessageRecord> implements UserMessageRecordDao
{
	public JdbcUserMessageRecordDao()
	{
		super();
	}

	@Override
	protected TerminableSiteGroupRecordRowMapper<UserMessageRecord> initMapper()
	{
		return new UserMessageRecordMapper();
	}

	@Override
	public boolean exists(UserMessageRecord userMessageRecord)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("sitegroupid", userMessageRecord.getSitegroupid());
		sqlParams.addValue("userid", userMessageRecord.getUserid());
		sqlParams.addValue("starttm", userMessageRecord.getStarttm());
		sqlParams.addValue("endtm", userMessageRecord.getEndtm());

		sqlParams.addValue("title", userMessageRecord.getTitle().toString());

		return !queryGeneric("SELECT ser FROM " + getEmpty().getFullTableName() + " WHERE sitegroupid = :sitegroupid AND userid = :userid AND title = :title AND starttm = :starttm AND endtm = :endtm", sqlParams, SimplePaginationFilter.SINGLE, getIntColumnMapper()).isEmpty();
	}
}
