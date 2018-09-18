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
package com.i4one.base.model.user.facebook;

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.UserType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.User;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author Hamid Badiozamani
 */
public class AccessToken extends BaseSingleClientType<AccessTokenRecord> implements UserType,SingleClientType<AccessTokenRecord>
{
	static final long serialVersionUID = 42L;

	public static final String PERSISTENCE = "AccessToken";

	// Maybe we should be putting this in the database instead of hardcoded here, then again if the
	// URL scheme changes it's likely that this class will have to as well.
	//
	private static final String DIALOGURL = "http://www.facebook.com/dialog/oauth?client_id={0}&redirect_uri={1}&scope=email,user_birthday,user_posts&state={2}";
	private transient User user;

	public AccessToken()
	{
		super(new AccessTokenRecord());
	}
	
	protected AccessToken(AccessTokenRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( user == null )
		{
			user = new User();
		}
		user.resetDelegateBySer(getDelegate().getUserid());

		if ( Utils.isEmpty(getState()) )
		{
			getDelegate().setState(UUID.randomUUID().toString());
		}
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	public String getDialogURL() throws UnsupportedEncodingException
	{
		return MessageFormat.format(DIALOGURL, Utils.encodeURL(getClient().getOptionValue("fb.appid")), Utils.encodeURL(getRedir()), Utils.encodeURL(getState()));
	}

	public String getCode()
	{
		return getDelegate().getCode();
	}

	public void setCode(String code)
	{
		getDelegate().setCode(code);
	}

	public String getState()
	{
		return getDelegate().getState();
	}

	public void setState(String state)
	{
		getDelegate().setState(state);
	}

	public String getRedir()
	{
		return getDelegate().getRedir();
	}

	public void setRedir(String redir)
	{
		getDelegate().setRedir(redir);
	}

	public String getToken()
	{
		return getDelegate().getAccesstoken();
	}
	
	public void setToken(String token) 
	{
		getDelegate().setAccesstoken(token);
	}

	public Date getExpiration()
	{
		return Utils.toDate(getExpirationSeconds());
	}

	public int getExpirationSeconds()
	{
		return getDelegate().getExpiration();
	}
	
	public void setExpirationSeconds(int expiration)
	{
		getDelegate().setExpiration(expiration);
	}

	public String getFacebookId()
	{
		return getDelegate().getFbid();
	}

	public void setFacebookId(String fbId)
	{
		getDelegate().setFbid(fbId);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getAccesstoken();
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

}
