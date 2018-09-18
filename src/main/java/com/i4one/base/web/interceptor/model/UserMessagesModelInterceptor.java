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
package com.i4one.base.web.interceptor.model;

import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.pagination.UserPagination;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import com.i4one.base.model.usermessage.UserMessage;
import com.i4one.base.model.usermessage.UserMessageManager;
import com.i4one.base.web.controller.Model;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class UserMessagesModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	private static final String MESSAGES = "userMessages";

	private UserMessageManager userMessageManager;

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		HttpServletRequest request = model.getRequest();
		User user = model.getUser();

		Map<String, Object> map = new HashMap<>();

		if ( request.getServletPath().endsWith("base/user/index.html") )
		{
			// Message pagination filter: sorted by descending orderweight, start time, end time
			// and finally serial number
			//
			TerminablePagination generalPagination = new TerminablePagination(model.getTimeInSeconds(),
						new UserPagination(new User(), new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE)));

			generalPagination.setOrderBy("orderweight DESC, starttm DESC, endtm, ser DESC");

			Set<UserMessage> allMessages = new LinkedHashSet<>();

			// First we get display user's private messages
			//
			if ( user.exists() ) 
			{
				TerminablePagination userPagination = new TerminablePagination(model.getTimeInSeconds(),
						new SiteGroupPagination(model.getSiteGroups(),
							new UserPagination(getUser(model), SimplePaginationFilter.NONE)));
				userPagination.setOrderBy("orderweight DESC, starttm DESC, endtm, ser DESC");

				// ---------- User Messages ----------
				//
				Set<UserMessage> messages = getUserMessageManager().getLive(userPagination);

				allMessages.addAll(messages);
			}

			// Then we add the public messages
			//
			Set<UserMessage> publicMessages = getUserMessageManager().getLive(generalPagination);
			allMessages.addAll(publicMessages);


			map.put(MESSAGES, allMessages);
		}

		return map;
	}

	public UserMessageManager getUserMessageManager()
	{
		return userMessageManager;
	}

	@Autowired
	public void setUserMessageManager(UserMessageManager userMessageManager)
	{
		this.userMessageManager = userMessageManager;
	}

}
