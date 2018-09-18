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
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class PrizeFulfillmentRecordMapper extends BaseRowMapper<PrizeFulfillmentRecord> implements RowMapper<PrizeFulfillmentRecord>
{
	@Override
	public PrizeFulfillmentRecord emptyInstance()
	{
		return new PrizeFulfillmentRecord();
	}

	@Override
	protected PrizeFulfillmentRecord mapRowInternal(ResultSet res) throws SQLException
	{
		PrizeFulfillmentRecord retVal = new PrizeFulfillmentRecord();

		retVal.setTimestamp(getInteger(res, "timestamp"));
		retVal.setItemid(getInteger(res, "itemid"));
		retVal.setQuantity(getInteger(res, "quantity"));

		retVal.setAdminid(getInteger(res, "adminid"));

		retVal.setStatus(getInteger(res, "status"));
		retVal.setNotes(res.getString("notes"));

		return retVal;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(PrizeFulfillmentRecord item)
	{
		RecordTypeSqlParameterSource retVal = new RecordTypeSqlParameterSource();

		retVal.addValue("timestamp", item.getTimestamp());
		retVal.addValue("quantity", item.getQuantity());

		addForeignKey(retVal, "itemid", item.getItemid());
		addForeignKey(retVal, "adminid", item.getAdminid());

		retVal.addValue("status", item.getStatus());
		retVal.addValue("notes", item.getNotes());

		return retVal;
	}
}
