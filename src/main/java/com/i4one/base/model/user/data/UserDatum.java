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
package com.i4one.base.model.user.data;

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.UserType;
import com.i4one.base.model.user.User;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 */
public class UserDatum extends BaseRecordTypeDelegator<UserDatumRecord> implements UserType
{
	static final long serialVersionUID = 42L;

	private transient User user;

	public UserDatum()
	{
		super(new UserDatumRecord());
	}

	protected UserDatum(UserDatumRecord delegate)
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
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setUser(getUser());
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

	public String getKey()
	{
		return getDelegate().getKey();
	}

	public void setKey(String name)
	{
		getDelegate().setKey(name);
	}

	public String getValue()
	{
		return getDelegate().getValue();
	}

	public void setValue(String value)
	{
		getDelegate().setValue(value);
	}

	public Date getTimeStamp()
	{
		return Utils.toDate(getTimeStampSeconds());
	}

	public int getTimeStampSeconds()
	{
		return getDelegate().getTimestamp();
	}

	public void setTimeStampSeconds(int timestamp)
	{
		getDelegate().setTimestamp(timestamp);
	}

	public int getDurationSeconds()
	{
		return getDelegate().getDuration();
	}

	public void setDurationSeconds(int durationSeconds)
	{
		getDelegate().setDuration(durationSeconds);
	}

	public int getExpirationSeconds()
	{
		return getTimeStampSeconds() + getDurationSeconds();
	}

	public Date getExpiration()
	{
		return Utils.toDate(getExpirationSeconds());
	}

	public boolean hasExpired(int currentTimeStamp)
	{
		return currentTimeStamp > getExpirationSeconds();
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getUserid() + "-" + getDelegate().getKey();
	}
}
