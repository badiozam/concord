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

import com.i4one.base.dao.RecordTypeRowMapper;
import com.i4one.base.dao.activity.BaseActivityRecordRowMapper;
import java.sql.ResultSet;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * A base mapper class for all prize winning types.
 * 
 * @author Hamid Badiozamani
 */
public abstract class BasePrizeWinningRecordRowMapper<T extends PrizeWinningRecordType> extends BaseActivityRecordRowMapper<T> implements RecordTypeRowMapper<T>
{
	@Override
	public T mapRow(ResultSet res, int rowNum) throws java.sql.SQLException
	{
		T retVal = super.mapRow(res, rowNum);

		retVal.setPrizewinningid(getInteger(res, "prizewinningid"));

		return retVal;
	}

	@Override
	public MapSqlParameterSource getSqlParameterSource(T o)
	{
		MapSqlParameterSource retVal = super.getSqlParameterSource(o);

		addForeignKey(retVal, "prizewinningid", o.getPrizewinningid());

		return retVal;
	}
}