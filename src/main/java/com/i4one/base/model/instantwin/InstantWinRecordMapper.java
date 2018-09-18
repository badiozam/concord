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
package com.i4one.base.model.instantwin;

import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.dao.terminable.BaseTerminableClientRecordRowMapper;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class InstantWinRecordMapper extends BaseTerminableClientRecordRowMapper<InstantWinRecord>
{

	@Override
	protected InstantWinRecord mapRowInternal(ResultSet res) throws SQLException
	{
		InstantWinRecord retVal = new InstantWinRecord();

		retVal.setUserid(getInteger(res, "userid"));

		retVal.setTitle(new IString(res.getString("title")));
		retVal.setPercentwin(getFloat(res, "percentwin"));
		retVal.setWinnercount(getInteger(res, "winnercount"));
		retVal.setWinnerlimit(getInteger(res, "winnerlimit"));
		retVal.setExclusive(getBoolean(res, "exclusive"));

		retVal.setWinnermsg(new IString(res.getString("winnermsg")));
		retVal.setLosermsg(new IString(res.getString("losermsg")));

		return retVal;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(InstantWinRecord item)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();


		sqlParams.addValue("title", item.getTitle());
		sqlParams.addValue("percentwin", item.getPercentwin());
		sqlParams.addValue("winnercount", item.getWinnercount());
		sqlParams.addValue("winnerlimit", item.getWinnerlimit());
		sqlParams.addValue("exclusive", item.getExclusive());

		sqlParams.addValue("winnermsg", item.getWinnermsg());
		sqlParams.addValue("losermsg", item.getLosermsg());

		// We're going to allow null foreign keys here
		// addForeignKey(sqlParams, "userid", item.getUserid());
		sqlParams.addValue("userid", item.getUserid());

		return sqlParams;
	}

	@Override
	public InstantWinRecord emptyInstance()
	{
		return new InstantWinRecord();
	}
	
}
