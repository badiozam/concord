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

import com.i4one.base.core.Pair;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.menu.AdminMenu;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * This interceptor initializes the menu to be displayed on the back-end.
 *
 * @author Hamid Badiozamani
 */
@Service
public class AdminMenuModelInterceptor extends BaseAdminMenuModelInterceptor
{
	@Override
	public void init()
	{
		super.init();

		getLogger().debug("Initializing MenuModelInterceptor");

		AdminMenu baseMenu = new AdminMenu();
		getMenu().put("msg.base.admin.frontend.title", new Pair<>("/base/user/index.html", "target='_blank'"), "");
		getMenu().put("msg.base.admin.index.pageTitle", baseMenu, ""); 

		baseMenu.put("msg.base.admin.client.menuTitle", new LinkedHashMap<>(), "base.clients");
		baseMenu.put("msg.base.admin.admins.menuTitle", new LinkedHashMap<>(), "base.admins");
		baseMenu.put("msg.base.admin.members.menuTitle", new LinkedHashMap<>(), "base.users");
		baseMenu.put("msg.base.admin.preferences.menuTitle", new LinkedHashMap<>(), "base.preferences");
		baseMenu.put("msg.base.admin.friendrefs.menuTitle", new LinkedHashMap<>(), "base.friendrefs");
		baseMenu.put("msg.base.admin.usermessages.menuTitle", new LinkedHashMap<>(), "base.usermessages");
		baseMenu.put("msg.base.admin.emailblasts.menuTitle", new LinkedHashMap<>(), "base.emailblasts");
		baseMenu.put("msg.base.admin.accesscodes.index.pageTitle", "/base/admin/accesscodes/index.html", "base.accesscodes");
		baseMenu.put("msg.base.admin.balancetriggers.index.pageTitle", "/base/admin/balancetriggers/index.html", "base.balancetriggers");
		baseMenu.put("msg.base.admin.instantwins.index.pageTitle", "/base/admin/instantwins/index.html", "base.instantwins");
		baseMenu.put("msg.base.admin.pages.index.pageTitle", "/base/admin/pages/index.html", "base.pages");
		baseMenu.put("msg.base.admin.messages.index.pageTitle", "/base/admin/messages/index.html", "base.messages");

		// Moved to the right side of the menu along side the "currently logged in" section
		//
		//menu.put("msg.base.admin.messages.logout.title", "/base/admin/auth/logout.html");

		Map<String, Object> settingsMenu = (Map<String, Object>) baseMenu.get("msg.base.admin.client.menuTitle");
		settingsMenu.put("msg.base.admin.client.index.pageTitle", "/base/admin/client/index.html");
		settingsMenu.put("msg.base.admin.client.style.pageTitle", "/base/admin/client/style.html");
		settingsMenu.put("msg.base.admin.client.content.pageTitle", "/base/admin/client/content.html");
		settingsMenu.put("msg.base.admin.client.frontpage.pageTitle", "/base/admin/client/frontpage.html");
		settingsMenu.put("msg.base.admin.client.membershome.pageTitle", "/base/admin/client/membershome.html");

		// Initialize the members menu
		//
		Map<String, Object> membersMenu = (Map<String, Object>) baseMenu.get("msg.base.admin.members.menuTitle");
		membersMenu.put("msg.base.admin.members.index.lookup.pageTitle", "/base/admin/members/index.html");
		membersMenu.put("msg.base.admin.members.search.pageTitle", "/base/admin/members/search.html");
		membersMenu.put("msg.base.admin.members.creditdebit.pageTitle", "/base/admin/members/creditdebit.html");
		membersMenu.put("msg.emails.base.registration.title", "/base/admin/members/regemail.html");
		membersMenu.put("msg.emails.base.forgotPassword.title", "/base/admin/members/forgotemail.html");
		membersMenu.put("msg.emails.base.processBirthday.title", "/base/admin/members/birthdayemail.html");
		membersMenu.put("msg.base.admin.members.reports.userlogin.pageTitle", "/base/admin/members/reports/userlogin.html");
		membersMenu.put("msg.base.admin.members.reports.membership.pageTitle", "/base/admin/members/reports/membership.html");
		membersMenu.put("msg.base.admin.members.reports.registration.pageTitle", "/base/admin/members/reports/registration.html");
		membersMenu.put("msg.base.admin.members.reports.postalranking.pageTitle", "/base/admin/members/reports/postalranking.html");

		// The user preferences menu
		//
		Map<String, Object> preferencesMenu = (Map<String, Object>) baseMenu.get("msg.base.admin.preferences.menuTitle");
		preferencesMenu.put("msg.base.admin.preferences.listing.pageTitle", "/base/admin/preferences/index.html");
		preferencesMenu.put("msg.base.admin.preferences.settings.index.pageTitle", "/base/admin/preferences/settings/index.html");

		//menu.put("msg.base.admin.members.index.title", unmodifiableMap(membersMenu));

		Map<String, Object> adminsMenu = (Map<String, Object>) baseMenu.get("msg.base.admin.admins.menuTitle");
		adminsMenu.put("msg.base.admin.admins.index.pageTitle", "/base/admin/admins/index.html");
		adminsMenu.put("msg.base.admin.admins.history.pageTitle", "/base/admin/admins/history.html");
		//menu.put("msg.base.admin.admins.index.title", unmodifiableMap(adminsMenu));

		Map<String, Object> friendrefsMenu = (Map<String, Object>) baseMenu.get("msg.base.admin.friendrefs.menuTitle");
		friendrefsMenu.put("msg.base.admin.friendrefs.index.pageTitle", "/base/admin/friendrefs/index.html");
		friendrefsMenu.put("msg.emails.base.friendref.title", "/base/admin/emails/index.html?id=emails.base.friendref");
		friendrefsMenu.put("msg.base.admin.friendrefs.reports.referrals.pageTitle", "/base/admin/friendrefs/reports/referrals.html");
		friendrefsMenu.put("msg.base.admin.friendrefs.reports.signups.pageTitle", "/base/admin/friendrefs/reports/signups.html");

		Map<String, Object> userMessagesMenu = (Map<String, Object>) baseMenu.get("msg.base.admin.usermessages.menuTitle");
		userMessagesMenu.put("msg.base.admin.usermessages.index.pageTitle.publicMessages", "/base/admin/usermessages/index.html?showPrivateMessages=false");
		userMessagesMenu.put("msg.base.admin.usermessages.index.pageTitle.privateMessages", "/base/admin/usermessages/index.html?showPrivateMessages=true");
		userMessagesMenu.put("msg.base.admin.usermessages.import.pageTitle", "/base/admin/usermessages/import.html");
		//userMessagesMenu.put("msg.base.admin.usermessages.settings.pageTitle", "/base/admin/usermessages/settings.html");

		Map<String, Object> emailBlastMenu = (Map<String, Object>) baseMenu.get("msg.base.admin.emailblasts.menuTitle");
		emailBlastMenu.put("msg.base.admin.emailblasts.index.pageTitle", "/base/admin/emailblasts/index.html");
		emailBlastMenu.put("msg.base.admin.emailblasts.builders.index.pageTitle", "/base/admin/emailblasts/builders/index.html");
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		HttpServletRequest request = model.getRequest();
		Map<String, Object> retVal = new HashMap<>();

		if (isAdminRequest(request))
		{
			retVal.put(MENU, buildMenu(getAdmin(request), model));
			return retVal;
		}

		return retVal;
	}

}
