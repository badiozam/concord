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
package com.i4one.base.web.interceptor;


import com.i4one.base.core.Base;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import com.i4one.base.core.Utils;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.ModelManager;
import com.i4one.base.web.URLTokenizer;
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.user.UserViewController;
import com.i4one.base.web.controller.user.account.UserAuthController;
import com.i4one.base.web.persistence.Persistence;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Authorization interceptor to ensure only logged in users are able to access
 * controllers require it (via the isAuthRequired() method). This interceptor
 * depends on the ClientInterceptor in order to ensure i18n messages are
 * properly displayed in the event of unauthorized access.
 * 
 * @author Hamid Badiozamani
 */
public class UserAuthInterceptor extends HandlerInterceptorAdapter
{
	private final Log logger;
	private String unauthView;
	private ModelManager modelManager;

	private UserManager userManager;
	private Authenticator<User> userAuthenticator;

	public UserAuthInterceptor()
	{
		logger = LogFactory.getLog(getClass());
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		if ( getLogger().isTraceEnabled() )
		{
			getLogger().trace("preHandle for " + request.getContextPath() + ", requestURI = " + request.getRequestURI() + " with handler " + handler.getClass() + " and request " + request.getClass());
		}

		UserViewController controller = null;
		if ( handler instanceof UserViewController )
		{
			controller = (UserViewController) handler;
		}
		else if ( handler instanceof HandlerMethod )
		{
			if ( ((HandlerMethod)handler).getBean() instanceof UserViewController)
			{
				controller = (UserViewController)((HandlerMethod)handler).getBean();
			}
		}


		if ( controller != null )
		{
			getLogger().debug("Found: " + controller);

			SingleClient client = ClientInterceptor.getSingleClient(request);
			String clientDomain = client.getDomain();
			String serverName = request.getServerName();

			// We force users to use the client's given domain
			//
			if ( !serverName.contains(clientDomain) && !serverName.contains("dev"))
			{
				response.sendRedirect("http://" + clientDomain );
				return false;
			}

				User currUser = UserAdminModelInterceptor.getUser(request);
				if ( !request.getRequestURI().endsWith(getUnauthView()) && currUser.exists() && !getUserManager().existsUser(currUser) )
				{
					getLogger().debug("User is set in the cookies but doesn't exist in the database, w/ unauthview = " + getUnauthView() +" and request URI = " + request.getRequestURI());

					// We're logging the user out since they no longer exist in the database
					//
					// This needs to change to a database session design
					//
					getUserAuthenticator().logout(currUser, response);
					handleUnauth(currUser, request, response, false);

					return false;
				}

			if ( controller.isAuthRequired())
			{
				getLogger().trace("Auth required for " + request.getRequestURI() + " at context path " + request.getContextPath());

				//User currUser = UserAdminModelInterceptor.getUser(request);
				if ( !currUser.exists() )
				{
					if ( request.getRequestURI().endsWith(".json"))
					{
						String language = ClientInterceptor.getLanguage(request);

						Base.getInstance().getJacksonObjectMapper().writeValue(response.getWriter(),  new JsonUnauthMessage(client, language));
						return false;
					}
					else
					{
						handleUnauth(currUser, request, response, true);
						return false;
					}
				}
				else
				{
					// XXX: Might want to put in more conditions to be satisified since right now
					// anyone can just set a "User" object and be logged in as the user with that
					// particular serial number. Consider using an AuthTokenManager instead
					//
				}
			}
			else if ( !(controller instanceof UserAuthController) )
			{
				// If the user navigates to a page that does not require authorization, and is not the login
				// page itself, then we can assume they're no longer interested in going back to that page
				// once they log in. 
				//
				Persistence.putObject(request, response, UserAuthController.USER_REDIRTO_KEY, null);
			}
		}

		return super.preHandle(request, response, handler);
	}

	private void handleUnauth(User currUser, HttpServletRequest request, HttpServletResponse response, boolean setRedirOnLogin) throws IOException
	{
		URLTokenizer urlTokenizer = new URLTokenizer(request);
	
		String redirURL = urlTokenizer.getBaseURL() + "/" + getUnauthView();
		getLogger().trace("User " + currUser + " is not logged in, redirecting to '" + redirURL + "'");
	
		if ( setRedirOnLogin )
		{
			// This is where we send the user back to after they've successfully authenticated
			//
			StringBuffer requestURL = request.getRequestURL();

			String queryString = Utils.forceEmptyStr(request.getQueryString());
			String fullURL = queryString.isEmpty() ? requestURL.toString() : requestURL.append("?").append(queryString).toString();

			getLogger().debug("Setting redir URL to " + fullURL);
			Persistence.putObject(request, response, UserAuthController.USER_REDIRTO_KEY, fullURL);
		}
		else
		{
			Persistence.putObject(request, response, UserAuthController.USER_REDIRTO_KEY, null);
		}

		response.sendRedirect(redirURL);
	}

	public String getUnauthView()
	{
		return unauthView;
	}

	public void setUnauthView(String unauthView)
	{
		this.unauthView = unauthView;
	}

	public Log getLogger()
	{
		return logger;
	}

	public ModelManager getModelManager()
	{
		return modelManager;
	}

	@Autowired
	public void setModelManager(ModelManager modelManager)
	{
		this.modelManager = modelManager;
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager readOnlyUserManager)
	{
		this.userManager = readOnlyUserManager;
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
