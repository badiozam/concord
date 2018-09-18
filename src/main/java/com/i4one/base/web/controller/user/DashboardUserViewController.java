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
package com.i4one.base.web.controller.user;

import com.i4one.base.core.Utils;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.facebook.AccessToken;
import com.i4one.base.model.user.facebook.AccessTokenManager;
import com.i4one.base.web.controller.Model;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.Post;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class DashboardUserViewController extends BaseUserViewController
{
	private AccessTokenManager accessTokenManager;

	@RequestMapping("**/base/user/index")
	public Model viewDashboard(HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, response);

		User currUser = model.getUser();
		if ( currUser.exists() )
		{
			AccessToken prevToken = getAccessTokenManager().getByUser(model.getSingleClient(), currUser);
			if ( prevToken.exists() )
			{
				Date oneWeekAgo = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 365);
	
				try
				{
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
		
					for ( Post post : posts )
					{
						if ( Utils.forceEmptyStr(post.getMessage()).toLowerCase().contains("canada predicts") ||
							Utils.forceEmptyStr(post.getMessage()).toLowerCase().contains("#insomnia") ||
							Utils.forceEmptyStr(post.getMessage()).toLowerCase().contains("#grandson")  ||
							Utils.forceEmptyStr(post.getMessage()).toLowerCase().contains("#earpiercing")
							)
						{
							// Give a nice thank you
							//
							model.put("fbthanks", post);
							break;
						}
					}
				}
				catch (FacebookOAuthException ex)
				{
					getLogger().debug("Couldn't authenticate FB token: {}", ex);
				}
			}
			else
			{
				getLogger().debug("No facebook token was found for the user");
			}
		}

		return initResponse(model, response, null);
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
