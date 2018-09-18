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
package com.i4one.predict.model.player;

import com.i4one.base.dao.BaseRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hamid Badiozamani
 */
public class PlayerRecordMapper extends BaseRowMapper<PlayerRecord>
{

	@Override
	public PlayerRecord emptyInstance()
	{
		return new PlayerRecord();
	}

	@Override
	protected PlayerRecord mapRowInternal(ResultSet res) throws SQLException
	{
		PlayerRecord pr = new PlayerRecord();

		pr.setScorerank(getInteger(res, "scorerank"));
		pr.setWinstreakrank(getInteger(res, "winstreakrank"));
		pr.setAccuracyrank(getInteger(res, "accuracyrank"));

		pr.setScore(getInteger(res, "score"));
		pr.setWinstreak(getInteger(res, "winstreak"));
		pr.setAccuracy(getFloat(res, "accuracy"));

		pr.setTotal(getInteger(res, "total"));
		pr.setPending(getInteger(res, "pending"));
		pr.setCorrect(getInteger(res, "correct"));
		pr.setIncorrect(getInteger(res, "incorrect"));

		pr.setUpdatetime(getInteger(res, "updatetime"));
		pr.setLastplayedtime(getInteger(res, "lastplayedtime"));

		pr.setUserid(getInteger(res, "userid"));
		pr.setTermid(getInteger(res, "termid"));

		return pr;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(PlayerRecord pr)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("total", pr.getTotal());
		sqlParams.addValue("pending", pr.getPending());
		sqlParams.addValue("correct", pr.getCorrect());
		sqlParams.addValue("incorrect", pr.getIncorrect());
		sqlParams.addValue("updatetime", pr.getUpdatetime());
		sqlParams.addValue("lastplayedtime", pr.getLastplayedtime());

		sqlParams.addValue("score", pr.getScore());
		sqlParams.addValue("winstreak", pr.getWinstreak());
		sqlParams.addValue("accuracy", pr.getAccuracy());

		sqlParams.addValue("scorerank", pr.getScorerank());
		sqlParams.addValue("winstreakrank", pr.getWinstreakrank());
		sqlParams.addValue("accuracyrank", pr.getAccuracyrank());

		addForeignKey(sqlParams, "termid", pr.getTermid());
		addForeignKey(sqlParams, "userid", pr.getUserid());

		return sqlParams;
	}
}
