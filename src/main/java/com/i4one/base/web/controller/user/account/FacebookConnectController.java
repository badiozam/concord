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
package com.i4one.base.web.controller.user.account;

import com.i4one.base.core.Utils;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.model.user.facebook.AccessToken;
import com.i4one.base.model.user.facebook.AccessTokenManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import com.i4one.base.web.persistence.Persistence;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Post;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class FacebookConnectController extends BaseUserViewController
{
	private UserManager userManager;
	private AccessTokenManager accessTokenManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@RequestMapping(value = "**/user/account/facebookconnect", method = RequestMethod.GET)
	public void accountConnect(@RequestParam(value = "code", required = false) String code,
					@RequestParam(value = "state", required = false) String state,
					HttpServletRequest request,
					HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);
		SingleClient client = model.getSingleClient();

		if ( !Utils.isEmpty(code) && !Utils.isEmpty(state) )
		{
			AccessToken persistedToken = (AccessToken) Persistence.getObject(request, AccessToken.PERSISTENCE);
			persistedToken.setCode(code);
			getLogger().debug("Persisted token = " + persistedToken);

			AccessToken actualToken = getAccessTokenManager().getByCode(persistedToken, state);
			getLogger().debug("Actual token = " + actualToken);

			String accessToken = actualToken.getToken();
			String fbAppid = actualToken.getClient().getOptionValue("fb.appid");
			String fbSecret = actualToken.getClient().getOptionValue("fb.secret");

			FacebookClient fbClient = new DefaultFacebookClient(accessToken, fbSecret, Version.VERSION_2_6);
			com.restfb.types.User fbUser = fbClient.fetchObject("me", com.restfb.types.User.class, Parameter.with("fields", "id"));

			FacebookClient.AccessToken extendedToken = fbClient.obtainExtendedAccessToken(fbAppid, fbSecret);
			actualToken.setToken(extendedToken.getAccessToken());
			actualToken.setExpirationSeconds((int) (extendedToken.getExpires().getTime() / 1000l));

			// Store the token in the database thus connecting the facebook ID with this particular account
			//
			actualToken.setUser(model.getUser());
			actualToken.setFacebookId(fbUser.getId());
			getAccessTokenManager().connect(actualToken);

			// We no longer need the access token since the information was saved in the database
			//
			Persistence.putObject(request, response, AccessToken.PERSISTENCE, null);
			response.sendRedirect(model.getRequest().getBaseURL() + "/base/user/index.html");
		}
		else
		{
			AccessToken accessToken = new AccessToken();
			accessToken.setRedir(model.getRequest().getFullRequestURI());
			accessToken.setClient(client);

			// Set a cookie (or store as a session value). This is mostly to preserve the UUID associated with the token
			//
			Persistence.putObject(request, response, AccessToken.PERSISTENCE, accessToken);
			response.sendRedirect(accessToken.getDialogURL());

			getLogger().debug("Access token is empty, redirecting to dialog URL " + accessToken.getDialogURL());
		}
	}

	@RequestMapping(value = "**/user/account/facebookfeed", method = RequestMethod.GET)
	public Model facebookFeed(HttpServletRequest request,
					HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, response);

		AccessToken prevToken = getAccessTokenManager().getByUser(model.getSingleClient(), model.getUser());
		if ( prevToken.exists() )
		{
			Date oneWeekAgo = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 365L);

			String fbSecret = prevToken.getClient().getOptionValue("fb.secret");
			FacebookClient fbClient = new DefaultFacebookClient(prevToken.getToken(), fbSecret, Version.VERSION_2_6);
			Connection<Post> userPosts = fbClient.fetchConnection("me/posts", Post.class, Parameter.with("since", oneWeekAgo), Parameter.with("limit", 200));
			//Connection<Post> userPosts = fbClient.fetchConnection("me/posts", Post.class, Parameter.with("limit", 200));

			List<Post> posts = new ArrayList<>();
			for (List<Post> postBatch : userPosts )
			{
				getLogger().debug("This post batch contains " + posts.size() + " posts");
				posts.addAll(postBatch);
			}

			model.put("posts", posts);
		}
		else
		{
			getLogger().debug("No facebook token was found for the user");
		}

		return initResponse(model, response, null);
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
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

