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
package com.i4one.base.dao.activity;

import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseQuantifiedActivityJdbcDao<T extends QuantifiedActivityRecordType> extends BaseActivityJdbcDao<T> implements QuantifiedActivityRecordTypeDao<T>
{
	@Override
	public int getTotalActivityQuantity(int itemid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);

		return querySingleGeneric("SELECT COALESCE(SUM(quantity),0) FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));
	}

	@Override
	public int getTotalActivityQuantity(int itemid, int userid)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", itemid);
		sqlParams.addValue("userid", userid);

		return querySingleGeneric("SELECT COALESCE(SUM(quantity),0) FROM " + getEmpty().getFullTableName() + " WHERE itemid = :itemid AND userid = :userid", sqlParams, SingleColumnRowMapper.newInstance(Integer.class));
	}
}
