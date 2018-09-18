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
package com.i4one.base.web.controller.admin.friendref;

import com.i4one.base.model.Errors;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.friendref.FriendRefManager;
import com.i4one.base.model.friendref.FriendRefSettings;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.base.web.interceptor.ClientInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class FriendRefSettingsController extends BaseAdminViewController
{
	private FriendRefManager friendRefManager;

	@ModelAttribute("settings")
	public FriendRefSettings initSettings(HttpServletRequest request)
	{
		return getFriendRefManager().getSettings(ClientInterceptor.getSingleClient(request));
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object object)
	{
		Model retVal = super.initRequest(request, object);

		if ( object instanceof FriendRefSettings )
		{
			FriendRefSettings settings = (FriendRefSettings)object;

			settings.setClient(retVal.getSingleClient());
		}

		return retVal;
	}

	@RequestMapping(value = "**/base/admin/friendrefs/index", method = RequestMethod.GET)
	public Model viewFriendReferralSettings(@ModelAttribute("settings") FriendRefSettings settings,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		return initResponse(model, response, settings);
	}

	@RequestMapping(value = "**/base/admin/friendrefs/index", method = RequestMethod.POST)
	public Model updateFriendReferralSettings(@ModelAttribute("settings") FriendRefSettings settings, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		try
		{
			// This has to happen since the settings object is not a ClientType
			//
			SingleClient client = model.getSingleClient();
			settings.setClient(client);

			getFriendRefManager().updateSettings(settings);

			success(model, "msg.base.admin.friendrefs.index.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.friendrefs.index.failure", result, errors);
		}

		return initResponse(model, response, settings);
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
