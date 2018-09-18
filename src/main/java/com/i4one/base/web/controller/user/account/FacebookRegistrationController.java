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
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.model.user.facebook.AccessToken;
import com.i4one.base.model.user.facebook.AccessTokenManager;
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import com.i4one.base.web.persistence.Persistence;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class FacebookRegistrationController extends BaseUserViewController
{
	private UserManager userManager;
	private AccessTokenManager accessTokenManager;

	private Authenticator<User> userAuthenticator;

	@RequestMapping(value = "**/user/account/facebookreg", method = RequestMethod.GET)
	public void registrationConnect(@RequestParam(value = "code", required = false) String code,
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
			String fbSecret = actualToken.getClient().getOptionValue("fb.secret");

			// At this point we have an actual token with the proper code, and we can look up the user
			//
			FacebookClient fbClient = new DefaultFacebookClient(accessToken, fbSecret, Version.VERSION_2_6);
			com.restfb.types.User fbUser = fbClient.fetchObject("me", com.restfb.types.User.class, Parameter.with("fields", "id,first_name,last_name,middle_name,email,gender,birthday,hometown"));

			// See if we can find a user that was loaded off of the given access token in the database
			//
			AccessToken dbToken = getAccessTokenManager().getById(client, fbUser.getId());

			// Load the user from the database so that their information can be matched
			//
			User accessUser = dbToken.getUser();
			accessUser.loadedVersion();

			getLogger().debug("Got access token " + dbToken + " w/ user = " + accessUser);
			if ( accessUser.exists() )
			{
				getLogger().debug("User exists, attempting to log in");

				BindingResult result = new DirectFieldBindingResult(accessUser, "authlogin");
				getUserAuthenticator().login(model, accessUser, response);

				getLogger().debug("loginImpl result was " + result.getFieldErrors());

				// We have the access token and we've set it as a cookie or session value,
				// now we can redirect back to the registration page
				//
				response.sendRedirect(model.getRequest().getBaseURL() + "/base/user/account/alreadyregistered.html");
			}
			else
			{
				User byEmail = new User();
				byEmail.setEmail(fbUser.getEmail());

				// If there's a user by the same e-mail it's safe to assume that it's the same person
				//
				if ( getUserManager().existsUser(byEmail) )
				{
					getLogger().debug("Found a user with the e-mail {}", fbUser.getEmail());

					persistedToken.setUser(byEmail);
					persistedToken.setClient(client);
					persistedToken.setFacebookId(fbUser.getId());
					getRequestState().set(AccessToken.PERSISTENCE, persistedToken);

					User fbAuthUser = getUserManager().authenticate(byEmail, client);
					if ( fbAuthUser.exists() )
					{
						getUserAuthenticator().login(model, fbAuthUser, response);

						// We have the access token and we've set it as a cookie or session value, we 
						// can now redirect back to the home page
						//
						response.sendRedirect(model.getRequest().getBaseURL() + "/base/user/account/alreadyregistered.html");
					}
					else
					{
						// Something went wrong with the login, best we can do is register a new user
						//
						Persistence.putObject(request, response, AccessToken.PERSISTENCE, actualToken);
						response.sendRedirect(model.getRequest().getBaseURL() + "/base/user/account/register.html");
					}
				}
				else
				{
					// No user with the given Facebook ID exists in our database, continue with the registration
					//
					// We have the access token and we've set it as a cookie or session value,
					// now we can redirect back to the registration page
					//
					Persistence.putObject(request, response, AccessToken.PERSISTENCE, actualToken);
					response.sendRedirect(model.getRequest().getBaseURL() + "/base/user/account/register.html");
				}
			}
		}
		else
		{
			AccessToken accessToken = new AccessToken();
			accessToken.setRedir(model.getRequest().getFullRequestURI());
			accessToken.setClient(client);

			// Set a cookie (or store as a session value). This is to preserve the UUID associated with the token
			//
			Persistence.putObject(request, response, AccessToken.PERSISTENCE, accessToken);
			response.sendRedirect(accessToken.getDialogURL());
		}
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

	public Authenticator<User> getUserAuthenticator()
	{
		return userAuthenticator;
	}

	@Autowired
	public void setUserAuthenticator(Authenticator<User> userAuthenticator)
	{
		this.userAuthenticator = userAuthenticator;
	}
}

