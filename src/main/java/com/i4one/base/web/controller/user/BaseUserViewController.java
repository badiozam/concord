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

import com.i4one.base.model.ActivityType;
import com.i4one.base.model.ClientType;
import com.i4one.base.model.UserType;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.BaseViewController;
import com.i4one.base.web.controller.Model;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseUserViewController extends BaseViewController implements UserViewController
{
	public static final String USER_REDIRTO_KEY = "userRedirto";

	@Override
	protected String getRedirKey()
	{
		return USER_REDIRTO_KEY;
	}

	/**
	 * Initializes the request by performing one of several tasks:
	 *
	 * <ul>
	 * 	<li>Initializes the model by retrieving the currently logged in user</li>
	 * 	<li>Searches the controller for all properties that may
	 * implement the Stateful interface and setting the user and source IP address
	 * attributes for each (if supported)</li>
	 * 	<li>Sets the client for the incoming model attribute if the type matches SingleClientType</li>
	 * </ul>
	 *
	 * @param request The incoming HTTP request
	 * @param modelAttribute The model attribute used in the form (if any)
	 *
	 * @return The initialized model
	 */
	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);
		User user = model.getUser();

		if ( modelAttribute instanceof ClientType )
		{
			ClientType clientModelAttribute = (ClientType)modelAttribute;
			clientModelAttribute.setClient(model.getSingleClient());

			getLogger().debug("modelAttribute now has client " + ((ClientType)modelAttribute).getClient());
		}

		// We also ensure that the currently logged in user will always be used
		//
		if ( user != null && user.exists() && modelAttribute instanceof UserType )
		{
			UserType userModelAttribute = (UserType)modelAttribute;
			userModelAttribute.setUser(user);
		}

		if ( modelAttribute instanceof ActivityType )
		{
			ActivityType activity = (ActivityType)modelAttribute;
			activity.setTimeStampSeconds(model.getTimeInSeconds());
		}

		return model;
	}

}
