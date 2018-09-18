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
package com.i4one.base.web.controller.admin.usermessages;

import com.i4one.base.core.Utils;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.model.usermessage.UserMessage;
import com.i4one.base.model.usermessage.UserMessageManager;
import com.i4one.base.model.usermessage.UserMessageRecord;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class UserMessageCrudController extends BaseTerminableSiteGroupTypeCrudController<UserMessageRecord, UserMessage>
{
	private UserManager userManager;
	private UserMessageManager userMessageManager;

	@Override
	public Model initRequest(HttpServletRequest request, UserMessage modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelUserMessage )
		{
			WebModelUserMessage userMessage = (WebModelUserMessage)modelAttribute;
			SingleClient client = model.getSingleClient();

			// We also need to set the client groups since userMessages have multi-client visibility
			//
			Set<SiteGroup> siteGroups = model.getSiteGroups();
			model.put("siteGroups", toSelectMapping(siteGroups, SiteGroup::getTitle));

			// Default to the first client group if one is not set already, the
			// parent's method only handles single client types and since we're
			// a multi-client type we need to handle our own
			//
			if ( !userMessage.getSiteGroup().exists() )
			{
				userMessage.setSiteGroup(siteGroups.iterator().next());
			}
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.usermessages";
	}

	@Override
	protected Manager<UserMessageRecord, UserMessage> getManager()
	{
		return getUserMessageManager();
	}

	@RequestMapping(value = { "**/base/admin/usermessages/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("usermessage") WebModelUserMessage userMessage,
					@RequestParam(value = "id", required = false) Integer userMessageId,
					HttpServletRequest request, HttpServletResponse response)
	{
		return this.createUpdateImpl(userMessage, userMessageId, request, response);
	}

	@RequestMapping(value = "**/base/admin/usermessages/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("usermessage") WebModelUserMessage userMessage, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		if ( !userMessage.getUser().exists() )
		{
			if ( Utils.isEmpty(userMessage.getUser().getUsername()) )
			{
				// Clear any previous user associated with the message
				//
				userMessage.setUser(new User());
			}
			else
			{
				User user = getUserManager().lookupUser(userMessage.getUser());
				if ( user.exists() )
				{
					// Only set the user if we find the user. We don't set a
					// non-existent user to allow the validate() method to throw
					// an exception for users that were set by the administrator
					// but were not found
					//
					userMessage.setUser(user);
				}
			}
		}

		return doUpdateImpl(userMessage, result, request, response);
	}

	@RequestMapping(value = { "**/base/admin/usermessages/remove" }, method = RequestMethod.GET)
	public ModelAndView remove(@RequestParam(value="id") Integer userMessageId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return removeImpl(userMessageId, request, response);
	}

	@RequestMapping(value = { "**/base/admin/usermessages/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer userMessageId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return this.cloneImpl(userMessageId, request, response);
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

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

}
