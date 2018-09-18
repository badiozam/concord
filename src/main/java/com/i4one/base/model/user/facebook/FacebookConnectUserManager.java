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
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.CachedUserManager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.user.account.NewUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class FacebookConnectUserManager extends CachedUserManager implements UserManager
{
	private AccessTokenManager accessTokenManager;

	@Transactional(readOnly = false)
	@Override
	public User authenticate(User user, SingleClient client)
	{
		// If we already have a FB access token, we're being asked to connect it
		// with a given user for authentication
		//
		AccessToken token = (AccessToken) getRequestState().get(AccessToken.PERSISTENCE);
		if ( token != null )
		{
			User dbUser = getUserForLogin(token.getUser(false));
			if ( dbUser.exists() )
			{
				User retVal = super.authenticate(dbUser, client);

				token.setUser(dbUser);
				getAccessTokenManager().connect(token);

				return retVal;
			}
			else
			{
				return super.authenticate(user, client);
			}
		}
		else
		{
			return super.authenticate(user, client);
		}
	}

	@Override
	public ReturnType<User> create(User user)
	{
		ReturnType<User> retVal = super.create(user);

		if ( user instanceof NewUser )
		{
			NewUser newUser = (NewUser)user;

			// Save the user's facebook info if the registration was successful
			//
			if ( !Utils.isEmpty(newUser.getFacebookAccessToken()) && !Utils.isEmpty(newUser.getFacebookId()))
			{
				AccessToken token = new AccessToken();
				token.setFacebookId(newUser.getFacebookId());
				token.setToken(newUser.getFacebookAccessToken());
				token.setClient(newUser.getClient(false));

				AccessToken prevToken = getAccessTokenManager().getById(newUser.getClient(), token.getFacebookId());
				if ( prevToken.exists() )
				{
					throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".create.alreadyFbConnected", "There is already an account associated with the logged in Facebook account. Please log in using Facebook to access this account.", new Object[] { "user", user }));
				}
				else
				{
					token.setUser(retVal.getPost());
					getAccessTokenManager().connect(token);
				}
			}
		}

		return retVal;
	}

	public AccessTokenManager getAccessTokenManager()
	{
		return accessTokenManager;
	}

	@Autowired
	public void setAccessTokenManager(AccessTokenManager accessTokenManager)
	{
		this.accessTokenManager = accessTokenManager;
	}
}