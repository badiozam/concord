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

import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.dao.terminable.BaseTerminableClientRecordRowMapper;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class AccessCodeRecordMapper extends BaseTerminableClientRecordRowMapper<AccessCodeRecord>
{

	@Override
	protected AccessCodeRecord mapRowInternal(ResultSet res) throws SQLException
	{
		AccessCodeRecord retVal = new AccessCodeRecord();

		retVal.setCode(res.getString("code"));
		retVal.setPages(res.getString("pages"));

		retVal.setTitle(new IString(res.getString("title")));
		retVal.setDescr(new IString(res.getString("descr")));
		retVal.setAccessdenied(new IString(res.getString("accessdenied")));

		return retVal;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(AccessCodeRecord item)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("code", item.getCode());
		sqlParams.addValue("pages", item.getPages());

		sqlParams.addValue("title", item.getTitle());
		sqlParams.addValue("descr", item.getDescr());
		sqlParams.addValue("accessdenied", item.getAccessdenied());

		return sqlParams;
	}

	@Override
	public AccessCodeRecord emptyInstance()
	{
		return new AccessCodeRecord();
	}
}