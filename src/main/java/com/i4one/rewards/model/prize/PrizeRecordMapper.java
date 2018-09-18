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

import com.i4one.base.dao.BaseSiteGroupRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class PrizeRecordMapper extends BaseSiteGroupRecordRowMapper<PrizeRecord> implements RowMapper<PrizeRecord>
{
	@Override
	protected PrizeRecord mapRowInternal(ResultSet res) throws SQLException
	{
		PrizeRecord prizeRecord = new PrizeRecord();

		prizeRecord.setTitle(new IString(res.getString("title")));
		prizeRecord.setDescr(new IString(res.getString("descr")));

		prizeRecord.setThumbnailurl(res.getString("thumbnailurl"));
		prizeRecord.setDetailpicurl(res.getString("detailpicurl"));

		prizeRecord.setInitinventory(getInteger(res, "initinventory"));
		prizeRecord.setCurrinventory(getInteger(res, "currinventory"));

		return prizeRecord;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(PrizeRecord o)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("title", o.getTitle());
		sqlParams.addValue("descr", o.getDescr());

		sqlParams.addValue("thumbnailurl", o.getThumbnailurl());
		sqlParams.addValue("detailpicurl", o.getDetailpicurl());

		sqlParams.addValue("initinventory", o.getInitinventory());
		sqlParams.addValue("currinventory", o.getCurrinventory());

		return sqlParams;
	}

	@Override
	public PrizeRecord emptyInstance()
	{
		return new PrizeRecord();
	}
}
