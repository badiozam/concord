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

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.UserType;
import com.i4one.base.model.user.User;
import com.i4one.predict.model.term.Term;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 */
public class Player extends BaseRecordTypeDelegator<PlayerRecord> implements UserType
{
	static final long serialVersionUID = 42L;

	private transient User user;
	private transient Term term;

	public Player()
	{
		super(new PlayerRecord());
	}

	protected Player(PlayerRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		if ( user == null )
		{
			user = new User();
		}
		user.resetDelegateBySer(getDelegate().getUserid());

		if ( term == null )
		{
			term = new Term();
		}
		term.resetDelegateBySer(getDelegate().getTermid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setUser(getUser());
		setTerm(getTerm());
	}

	@Override
	public User getUser()
	{
		return getUser(true);
	}

	public User getUser(boolean doLoad)
	{
		if ( doLoad )
		{
			user.loadedVersion();
		}

		return user;
	}

	@Override
	public void setUser(User user)
	{
		this.user = user;
		getDelegate().setUserid(user.getSer());
	}

	public Term getTerm()
	{
		return getTerm(true);
	}

	public Term getTerm(boolean doLoad)
	{
		if ( doLoad )
		{
			term.loadedVersion();
		}

		return term;
	}

	public void setTerm(Term term)
	{
		this.term = term;
		getDelegate().setTermid(term.getSer());
	}

	public int getCorrect()
	{
		return getDelegate().getCorrect();
	}

	public void setCorrect(int correctPredictions)
	{
		getDelegate().setCorrect(correctPredictions);
	}

	public int getTotal()
	{
		return getDelegate().getTotal();
	}

	public void setTotal(int total)
	{
		getDelegate().setTotal(total);
	}

	public int getPending()
	{
		return getDelegate().getPending();
	}

	public void setPending(int pending)
	{
		getDelegate().setPending(pending);
	}

	public int getIncorrect()
	{
		return getDelegate().getIncorrect();
	}

	public void setIncorrect(int incorrectPredictions)
	{
		getDelegate().setIncorrect(incorrectPredictions);
	}

	public int getScore()
	{
		return getDelegate().getScore();
	}

	public void setScore(int score)
	{
		getDelegate().setScore(score);
	}

	public float getCorrectIncorrectRatio()
	{
		float total = getCorrect() + getIncorrect();
		if ( total == 0 )
		{
			return 0;
		}
		else
		{
			return (float)(getCorrect()) / total;
		}
	}

	public float getAccuracy()
	{
		return getDelegate().getAccuracy();
	}

	public void setAccuracy(float accuracy)
	{
		getDelegate().setAccuracy(accuracy);
	}

	public int getWinStreak()
	{
		return getDelegate().getWinstreak();
	}

	public void setWinStreak(int winstreak)
	{
		getDelegate().setWinstreak(winstreak);
	}

	public int getScoreRank()
	{
		return getDelegate().getScorerank();
	}

	public void setScoreRank(int rank)
	{
		getDelegate().setScorerank(rank);
	}
	
	public int getWinStreakRank()
	{
		return getDelegate().getWinstreakrank();
	}

	public void setWinStreakRank(int rank)
	{
		getDelegate().setWinstreakrank(rank);
	}
	
	public int getAccuracyRank()
	{
		return getDelegate().getAccuracyrank();
	}

	public void setAccuracyRank(int rank)
	{
		getDelegate().setAccuracyrank(rank);
	}

	public Date getUpdateTime()
	{
		return Utils.toDate(getUpdateTimeSeconds());
	}
	
	public int getUpdateTimeSeconds()
	{
		return getDelegate().getUpdatetime();
	}

	public void setUpdateTimeSeconds(int updateTime)
	{
		getDelegate().setUpdatetime(updateTime);
	}

	public Date getLastPlayedTime()
	{
		return Utils.toDate(getLastPlayedTimeSeconds());
	}

	public int getLastPlayedTimeSeconds()
	{
		return getDelegate().getLastplayedtime();
	}

	public void setLastPlayedTimeSeconds(int updateTime)
	{
		getDelegate().setLastplayedtime(updateTime);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getTermid() + "-" + getDelegate().getUserid();
	}

}
