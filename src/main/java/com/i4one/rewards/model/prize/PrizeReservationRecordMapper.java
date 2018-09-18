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

import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class PrizeReservationRecordMapper extends BaseRowMapper<PrizeReservationRecord>
{
	@Override
	public PrizeReservationRecord emptyInstance()
	{
		return new PrizeReservationRecord();
	}

	@Override
	protected PrizeReservationRecord mapRowInternal(ResultSet res) throws SQLException
	{
		PrizeReservationRecord retVal = emptyInstance();

		retVal.setAdminid(getInteger(res, "adminid"));
		retVal.setPrizeid(getInteger(res, "prizeid"));

		retVal.setAmount(getInteger(res, "amount"));

		retVal.setFeatureid(getInteger(res, "featureid"));
		retVal.setFeature(res.getString("feature"));

		retVal.setTimestamp(getInteger(res, "timestamp"));

		return retVal;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(PrizeReservationRecord item)
	{
		RecordTypeSqlParameterSource retVal = new RecordTypeSqlParameterSource();

		addForeignKey(retVal, "adminid", item.getAdminid());
		addForeignKey(retVal, "prizeid", item.getPrizeid());

		retVal.addValue("amount", item.getAmount());

		retVal.addValue("featureid", item.getFeatureid());
		retVal.addValue("feature", item.getFeature());

		retVal.addValue("timestamp", item.getTimestamp());

		return retVal;
	}
}
