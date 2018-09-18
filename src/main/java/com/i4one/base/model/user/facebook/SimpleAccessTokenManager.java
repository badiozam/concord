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

import com.fasterxml.jackson.databind.JsonNode;
import com.i4one.base.core.Base;
import com.i4one.base.core.Utils;
import static com.i4one.base.core.Utils.encodeURL;
import static com.i4one.base.core.Utils.isEmpty;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import static java.text.MessageFormat.format;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleAccessTokenManager extends BaseSimpleManager<AccessTokenRecord, AccessToken> implements AccessTokenManager
{
	private static final String ACCESSTOKENURL = "https://graph.facebook.com/oauth/access_token?client_id={0}&client_secret={1}&code={2}&redirect_uri={3}";

	@Override
	public AccessToken emptyInstance()
	{
		return new AccessToken();
	}

	@Override
	public AccessToken getByCode(AccessToken token, String state)
	{
		if ( isEmpty(state) || !state.equalsIgnoreCase(token.getState()))
		{
			// State mismatch, throw exception
		}

		if ( !isEmpty(token.getCode()) )
		{
			StringBuilder accessTokenResult = new StringBuilder();
			try
			{
				String fbAppid = token.getClient().getOptionValue("fb.appid");
				String fbSecret = token.getClient().getOptionValue("fb.secret");
	
				URL oauthURL = new URL(format(
                                       ACCESSTOKENURL,
                                       encodeURL(fbAppid),
                                       encodeURL(fbSecret),
                                       encodeURL(token.getCode()),
                                       encodeURL(token.getRedir())
                                       )
                               );

	
				// Open a connection to facebook and read the access token
				//
				HttpURLConnection urlConn = (HttpURLConnection) oauthURL.openConnection();
				urlConn.connect();
	
				int statusCode = urlConn.getResponseCode();

				try ( InputStream iStream = (statusCode == HttpURLConnection.HTTP_OK) ? urlConn.getInputStream() : urlConn.getErrorStream() )
				{
					JsonNode node = Base.getInstance().getJacksonObjectMapper().readTree(iStream);
					getLogger().debug("Read node {}", node.toString());

					JsonNode errorNode = node.get("error");

					if (errorNode == null )
					{
						JsonNode accessTokenNode = node.get("access_token");
						token.setToken(accessTokenNode.asText(""));

						JsonNode expiresNode = node.get("expires_in");
						token.setExpirationSeconds(Utils.currentTimeSeconds() + expiresNode.asInt(0));
					}
	
					if ( statusCode != HttpURLConnection.HTTP_OK )
					{
						getLogger().debug("Got status code " + statusCode + " with body " + accessTokenResult);
	
						throw new Errors("accessTokenManager.getByCode", new ErrorMessage("msg.base.fb.accessTokenManager.error", "Facebook API returned other than successful code $code: $result", new Object[] { "item", token, "code", statusCode, "result", accessTokenResult }, null));
					}
				}
			}
			catch (IOException ioe)
			{
				throw new Errors("accessTokenManager.getByCode", new ErrorMessage("msg.base.fb.accessTokenManager.ioerror", "There was an error while attempting to retrieve an access token: $result",
					new Object[] { "item", token, "result", accessTokenResult }, ioe));
			}
		}
		else
		{
			// The code hasn't been initialized!
			throw new Errors("accessTokenManager.getByCode", new ErrorMessage("msg.base.fb.accessTokenManager.nocode", "There was no code specified",
				new Object[] { "item", token }, null));
		}

		return token;
	}

	@Override
	public AccessToken getById(SingleClient client, String fbId)
	{
		//AccessTokenRecord record = getDao().getById(client.getSer(), fbId);
		AccessTokenRecord record = getDao().getById(fbId);
		if ( record == null )
		{
			return new AccessToken();
		}
		else
		{
			return new AccessToken(record);
		}
	}

	@Override
	public AccessToken getByUser(SingleClient client, User user)
	{
		AccessTokenRecord record = getDao().getByUser(client.getSer(), user.getSer());
		if ( record == null )
		{
			return new AccessToken();
		}
		else
		{
			return new AccessToken(record);
		}
	}

	@Override
	public Set<AccessToken> getAllByClient(SingleClient client, int expirationTimestamp, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAllByClient(client.getSer(), expirationTimestamp, pagination));
	}

	@Transactional(readOnly = false)
	@Override
	public void connect(AccessToken accessToken)
	{
		getLogger().debug("Connecting " + accessToken.getFacebookId() + " to " + accessToken.getUser(false).getSer());
		AccessToken prevToken = getByUser(accessToken.getClient(false), accessToken.getUser(false));

		if ( prevToken.exists() )
		{
			getLogger().debug("Found a previous FB token for user " + accessToken.getUser(false).getSer());

			if ( !prevToken.getUser(false).equals(accessToken.getUser(false)))
			{
				throw new Errors("accessTokenManager.connect", new ErrorMessage("msg.base.fb.accessTokenManager.duplicate", "This Facebook login is already bound to another existing account, please log out then click the 'Login with Facebook' button to try again.",
					new Object[] { "item", accessToken }, null));
			}
			else
			{
				prevToken.setToken(accessToken.getToken());
				prevToken.setExpirationSeconds(accessToken.getExpirationSeconds());

				getLogger().debug("Updating user's token");
				getDao().updateBySer(prevToken.getDelegate());
			}
		}
		else
		{
			try
			{
				getLogger().debug("No previous token found, creating new record");
				prevToken.copyFrom(accessToken);
				getDao().insert(prevToken.getDelegate());
			}
			catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
			{
				getLogger().error("Error copying access token.", ex);
			}
		}
	}

	@Override
	public AccessTokenRecordDao getDao()
	{
		return (AccessTokenRecordDao) super.getDao();
	}

}
