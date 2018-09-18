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
package com.i4one.promotion.model.jobs;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.model.user.facebook.AccessToken;
import com.i4one.base.model.user.facebook.AccessTokenManager;
import com.i4one.base.web.RequestState;
import com.i4one.promotion.model.socialphrase.SocialPhraseManager;
import com.i4one.promotion.model.socialphrase.SocialPhraseResponse;
import com.i4one.promotion.model.socialphrase.SocialPhraseResponseManager;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Post;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author Hamid Badiozamani
 */
@DisallowConcurrentExecution 
public class SocialPhraseJob extends QuartzJobBean
{
	private final Log logger;

	private SocialPhraseManager socialPhraseManager;
	private SocialPhraseResponseManager socialPhraseResponseManager;

	private AccessTokenManager accessTokenManager;
	private SingleClientManager clientManager;
	private UserManager userManager;

	private RequestState requestState;

	public SocialPhraseJob()
	{
		super();

		logger = LogFactory.getLog(getClass());
		getLogger().debug("Instantiating " + this);
	}

	@Override
	protected void executeInternal(JobExecutionContext jec) throws JobExecutionException
	{
		// XXX: Need to set up a "background" admin with limited permissions
		// who will be running this process
		//
		Admin admin = new Admin();
		admin.setSer(2);
		admin.loadedVersion();

		getRequestState().setAdmin(admin);

		int currentTimeSeconds = Utils.currentTimeSeconds();

		getLogger().debug("Executing " + this + " at " + currentTimeSeconds);
		getClientManager().getAllClients(SingleClient.getRoot(), SimplePaginationFilter.NONE).stream().forEach((currClient) ->
		{
			processSocialPhrases(jec, currentTimeSeconds, currClient);
		});
		getLogger().debug("Done with " + this);
	}

	protected void processSocialPhrases(JobExecutionContext jec, int currentTimeSeconds, SingleClient client)
	{
		Set<AccessToken> accessTokens = getAccessTokenManager().getAllByClient(client, currentTimeSeconds, SimplePaginationFilter.NONE);

		Date pastTwoWeeks = new Date((currentTimeSeconds * 1000L) - 1000L * 60L * 60L * 24L * 14L);
		String fbSecret = client.getOptionValue("fb.secret");

		for ( AccessToken token : accessTokens )
		{
			getLogger().debug("Considering token " + token + " for user " + token.getUser().getUsername());

			FacebookClient fbClient = new DefaultFacebookClient(token.getToken(), fbSecret, Version.VERSION_2_6);
			Connection<Post> userPosts = fbClient.fetchConnection("me/posts", Post.class, Parameter.with("since", pastTwoWeeks), Parameter.with("limit", 200));

			List<Post> posts = new ArrayList<>();
			for (List<Post> postBatch : userPosts )
			{
				posts.addAll(postBatch);
			}

			for ( Post post : posts )
			{
				TerminablePagination terminablePagination = new TerminablePagination((int)(post.getCreatedTime().getTime() / 1000L), SimplePaginationFilter.NONE);

				List<ReturnType<SocialPhraseResponse>> processed = getSocialPhraseResponseManager().processSocialPhrases(post.getMessage(), token.getUser(), terminablePagination);

				for ( ReturnType<SocialPhraseResponse> response : processed )
				{
					getLogger().debug("Processed post " + post.getMessage() + " for user " + token.getUser().getUsername() + " matching phrase " + response.getPost().getSocialPhrase());
				}
			}
		}
	}

	public SingleClientManager getClientManager()
	{
		return clientManager;
	}

	@Autowired
	public void setClientManager(SingleClientManager clientManager)
	{
		this.clientManager = clientManager;
	}

	public SocialPhraseManager getSocialPhraseManager()
	{
		return socialPhraseManager;
	}

	@Autowired
	public void setSocialPhraseManager(SocialPhraseManager socialPhraseManager)
	{
		this.socialPhraseManager = socialPhraseManager;
	}

	public SocialPhraseResponseManager getSocialPhraseResponseManager()
	{
		return socialPhraseResponseManager;
	}

	@Autowired
	public void setSocialPhraseResponseManager(SocialPhraseResponseManager socialPhraseResponseManager)
	{
		this.socialPhraseResponseManager = socialPhraseResponseManager;
	}

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
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

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	protected final Log getLogger()
	{
		return logger;
	}
}
