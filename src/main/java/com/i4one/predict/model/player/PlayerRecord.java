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

import com.i4one.base.dao.BaseRecordType;
import com.i4one.base.dao.UserRecordType;

/**
 * @author Hamid Badiozamani
 */
public class PlayerRecord extends BaseRecordType implements UserRecordType
{
	static final long serialVersionUID = 42L;

	private Integer userid;
	private Integer termid;

	private Integer score;
	private Integer winstreak;
	private Float accuracy;

	private Integer total;
	private Integer pending;
	private Integer correct;
	private Integer incorrect;

	private Integer scorerank;
	private Integer winstreakrank;
	private Integer accuracyrank;

	private Integer updatetime;
	private Integer lastplayedtime;

	public PlayerRecord()
	{
		userid = 0;
		termid = 0;

		scorerank = 0;
		accuracyrank = 0;
		winstreakrank = 0;

		total = 0;
		pending = 0;
		correct = 0;
		incorrect = 0;

		score = 0;
		winstreak = 0;
		accuracy = 0.0f;
	}

	@Override
	public String getSchemaName()
	{
		return "predict";
	}

	@Override
	public String getTableName()
	{
		return "players";
	}

	@Override
	public Integer getUserid()
	{
		return userid;
	}

	@Override
	public void setUserid(Integer userid)
	{
		this.userid = userid;
	}

	public Integer getTermid()
	{
		return termid;
	}

	public void setTermid(Integer termid)
	{
		this.termid = termid;
	}

	public Integer getScore()
	{
		return score;
	}

	public void setScore(Integer score)
	{
		this.score = score;
	}

	public Integer getWinstreak()
	{
		return winstreak;
	}

	public void setWinstreak(Integer winstreak)
	{
		this.winstreak = winstreak;
	}

	public Float getAccuracy()
	{
		return accuracy;
	}

	public void setAccuracy(Float accuracy)
	{
		this.accuracy = accuracy;
	}

	public Integer getCorrect()
	{
		return correct;
	}

	public void setCorrect(Integer correct)
	{
		this.correct = correct;
	}

	public Integer getIncorrect()
	{
		return incorrect;
	}

	public void setIncorrect(Integer incorrect)
	{
		this.incorrect = incorrect;
	}

	public Integer getUpdatetime()
	{
		return updatetime;
	}

	public void setUpdatetime(Integer updatetime)
	{
		this.updatetime = updatetime;
	}

	public Integer getLastplayedtime()
	{
		return lastplayedtime;
	}

	public void setLastplayedtime(Integer lastplayedtime)
	{
		this.lastplayedtime = lastplayedtime;
	}

	public Integer getScorerank()
	{
		return scorerank;
	}

	public void setScorerank(Integer scorerank)
	{
		this.scorerank = scorerank;
	}

	public Integer getWinstreakrank()
	{
		return winstreakrank;
	}

	public void setWinstreakrank(Integer winstreakrank)
	{
		this.winstreakrank = winstreakrank;
	}

	public Integer getAccuracyrank()
	{
		return accuracyrank;
	}

	public void setAccuracyrank(Integer accuracyrank)
	{
		this.accuracyrank = accuracyrank;
	}

	public Integer getPending()
	{
		return pending;
	}

	public void setPending(Integer pending)
	{
		this.pending = pending;
	}

	public Integer getTotal()
	{
		return total;
	}

	public void setTotal(Integer total)
	{
		this.total = total;
	}

}
