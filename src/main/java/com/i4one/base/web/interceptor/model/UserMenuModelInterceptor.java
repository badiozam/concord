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

import com.i4one.base.model.friendref.FriendRefManager;
import com.i4one.base.model.friendref.FriendRefSettings;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.menu.UserMenu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This interceptor initializes the menu to be displayed on the front-end.
 *
 * @author Hamid Badiozamani
 */
@Service
public class UserMenuModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	private FriendRefManager friendRefManager;

	public static final String MENUS = "menus";

	// The user menu to display, this can be ignored and/or superceded by subclasses or
	// by the VM file itself
	//
	private UserMenu menu;
	private UserMenu membersMenu;
	private UserMenu nonmembersMenu;

	@Override
	public void init()
	{
		super.init();

		getLogger().debug("Initializing MenuModelInterceptor");

		menu = new UserMenu();
		menu.setLeftAligned(true);

		menu.put("msg.base.user.menu.index", "/base/user/index.html");
		menu.put("msg.base.user.menu.howtoplay", "/base/user/static/faqs.html");

		membersMenu = new UserMenu();
		membersMenu.setLeftAligned(false);

		UserMenu membersSubMenu = new UserMenu();
		membersSubMenu.setLeftAligned(true);
		membersSubMenu.put("msg.base.user.menu.account.profile", "/base/user/account/profile.html");
		membersSubMenu.put("msg.base.user.menu.account.resetpassword", "/base/user/account/resetpassword.html");
		membersSubMenu.put("msg.base.user.menu.account.activity", "/base/user/account/activity.html");
		membersSubMenu.put("msg.base.user.menu.account.logout", "/base/user/account/logout.html");

		membersMenu.put("msg.base.user.menu.account.loggedin", membersSubMenu);

		// You can only refer a friend if you're logged in
		//
		//membersMenu.put("msg.base.user.friendref.index.title", "/base/user/friendref/index.html");

		nonmembersMenu = new UserMenu();
		nonmembersMenu.setLeftAligned(false);
		nonmembersMenu.put("msg.base.user.menu.account.login", "/base/user/account/login.html");
		nonmembersMenu.put("msg.base.user.menu.account.register", "/base/user/account/register.html");
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		Map<String, Object> retVal = new HashMap<>();

		HttpServletRequest request = model.getRequest();
		if ( isUserRequest(request))
		{
			initMenu(retVal, model, request);
		}

		return retVal;
	}

	private void initMenu(Map<String, Object> map, Model model, HttpServletRequest request)
	{
		// Add our basic menu to the model object for rendering
		//
		List<Map<String,Object>> menus = new ArrayList<>();

		// This is displayed on the left side of the nav bar
		//
		UserMenu leftMenu = new UserMenu(menu);
		menus.add(leftMenu);

		// This is displayed on the right side
		//
		if ( model.getUser().exists() )
		{
			// XXX: Needs caching on a per-client basis
			menus.add(new UserMenu(getMembersMenu()));
		}
		else
		{
			// XXX: Needs caching on a per-client basis
			menus.add(new UserMenu(getNonmembersMenu()));
		}

		// Only add friend referral if it's enabled
		//
		FriendRefSettings settings = getFriendRefManager().getSettings(model.getSingleClient());
		if ( settings.isEnabled() )
		{
			getLogger().debug("Friend referral is enabled");
			leftMenu.put("msg.base.user.friendref.index.title", "/base/user/friendref/index.html");
		}
		else
		{
			getLogger().debug("Friend referral is disabled");
		}

		map.put(MENUS, menus);
	}

	public Map<String, Object> getMenu()
	{
		return Collections.unmodifiableMap(menu);
	}

	protected void setMenu(Map<String, Object> menu)
	{
		this.menu.clear();
		this.menu.putAll(menu);
	}

	private UserMenu getMembersMenu()
	{
		return membersMenu;
	}

	private UserMenu getNonmembersMenu()
	{
		return nonmembersMenu;
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
