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
package com.i4one.base.web.controller.user.friendref;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.friendref.FriendRef;
import com.i4one.base.model.friendref.FriendRefManager;
import com.i4one.base.model.friendref.FriendRefSettings;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class FriendRefController extends BaseUserViewController
{
	private FriendRefManager friendRefManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}
 
	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		addMessageToModel(model, Model.TITLE, "msg.base.user.friendref.index.title");

		FriendRefSettings settings = getFriendRefManager().getSettings(model.getSingleClient());
		model.put("settings", settings);

		if ( modelAttribute instanceof FriendRef )
		{
			FriendRef friendRef = (FriendRef)modelAttribute;
			friendRef.setUser(model.getUser());

			if ( Utils.isEmpty(friendRef.getMessage()) )
			{
				friendRef.setMessage(settings.getDefaultMessage().get(model.getLanguage()));
			}
		}

		return model;
	}

	@Override
	public Model initResponse(Model model, HttpServletResponse response, Object modelAttribute)
	{
		Model retVal = super.initResponse(model, response, modelAttribute);

		if ( modelAttribute instanceof FriendRef )
		{
			FriendRef friendRef = (FriendRef)modelAttribute;

			if ( friendRef instanceof WebModelFriendRef)
			{
				WebModelFriendRef webModelFriendRef = (WebModelFriendRef)friendRef;
				webModelFriendRef.setPastFriendRefs(getFriendRefManager().getFriendsByUser(model.getUser(),
					new ClientPagination(model.getSingleClient(), SimplePaginationFilter.DESC)));
			}
		}

		return retVal;
	}

	@RequestMapping(value = "**/base/user/friendref/index", method = RequestMethod.GET)
	public Model getFriendRef(@ModelAttribute("friendRef") WebModelFriendRef friendRef, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, friendRef);

		return initResponse(model, response, friendRef);
	}

	@RequestMapping(value = "**/base/user/friendref/index", method = RequestMethod.POST)
	public Model submitFriendRef(@ModelAttribute("friendRef") @Valid WebModelFriendRef friendRef, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, friendRef);
		try
		{
			getFriendRefManager().create(friendRef);

			// Reset the form's contents if we were successful
			//
			friendRef.setEmail("");
			friendRef.setFirstName("");
			friendRef.setLastName("");

			success(model, "msg.friendRefManager.create.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.friendRefManager.create.failure", result, errors);
		}
		
		return initResponse(model, response, friendRef);
	}

	public FriendRefManager getFriendRefManager()
	{
		return friendRefManager;
	}

	@Autowired
	public void setFriendRefManager(FriendRefManager friendRefManager)
	{
		this.friendRefManager = friendRefManager;
	}

}
