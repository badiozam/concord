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
package com.i4one.base.web.controller.admin.admins;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.admin.AdminManager;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilegeRecord;
import com.i4one.base.model.adminprivilege.Privilege;
import com.i4one.base.model.adminprivilege.PrivilegeManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseCrudController;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AdminPrivilegeCrudController extends BaseCrudController<ClientAdminPrivilegeRecord, ClientAdminPrivilege>
{
	private AdminManager adminManager;
	private PrivilegeManager privilegeManager;
	private AdminPrivilegeManager adminPrivilegeManager;

	@Override
	public Model initRequest(HttpServletRequest request, ClientAdminPrivilege modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		Set<Privilege> privileges = getPrivilegeManager().getAllPrivileges(SimplePaginationFilter.NONE);
		Map<String, String> privilegeFeatures = privileges.stream()
			.map(Privilege::getFeature)				// We want to return only the feature value
			.collect(Collectors.toSet())				// Dedupe the collection
			.stream()
			.sorted()						// Sort the names
			.collect(Collectors.toMap(String::toString,		// Convert it to a map
				String::toString,
				(item1, item2) -> { return item1; },
				() -> { return new LinkedHashMap<>(); })	// LinkedHashMap preserves sort order
		);


		// For now, we're using the same string as the feature to present to the user
		//
		model.put("privilegeFeatures", privilegeFeatures);
		model.put("privilegeReadWrites",
			Utils.makeMap(Boolean.TRUE, model.buildMessage("msg.base.admin.admins.updateprivilege.readwrite"),
				Boolean.FALSE, model.buildMessage("msg.base.admin.admins.updateprivilege.readonly"))
			);

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.admins";
	}

	@Override
	protected Manager<ClientAdminPrivilegeRecord, ClientAdminPrivilege> getManager()
	{
		return getAdminPrivilegeManager();
	}

	@RequestMapping(value="**/base/admin/admins/updateprivilege", method = RequestMethod.GET)
	public Model createUpdatePrivilege(@ModelAttribute("clientAdminPrivilege") ClientAdminPrivilege clientAdminPrivilege,
		@RequestParam(value = "clientadminprivid", required = false) Integer clientAdminPrivId,
		@RequestParam(value = "adminid", required = false) Integer adminId,
		BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		if ( clientAdminPrivId != null )
		{
			clientAdminPrivilege.setSer(clientAdminPrivId);
			clientAdminPrivilege.loadedVersion();
		}
		else if ( adminId != null )
		{
			clientAdminPrivilege.getAdmin().setSer(adminId);
			clientAdminPrivilege.getAdmin().loadedVersion();
		}

		Model model = initRequest(request, clientAdminPrivilege);

		return initResponse(model, response, clientAdminPrivilege);
	}

	@RequestMapping(value="**/base/admin/admins/updateprivilege", method = RequestMethod.POST)
	public Model doCreateUpdatePrivilege(@ModelAttribute("clientAdminPrivilege") ClientAdminPrivilege clientAdminPrivilege, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, clientAdminPrivilege);

		try
		{
			// We pass in a "false" flag here to not have the database overwrite
			// what the user had entered
			//
			Privilege priv = clientAdminPrivilege.getPrivilege(false);
			priv = getPrivilegeManager().lookupPrivilege(priv.getFeature(), priv.getReadWrite());

			// This ensures that the serial number is valid, but also allows us to request
			// a feature/rw flag as separate controls on the form
			//
			clientAdminPrivilege.setPrivilege(priv);

			if ( clientAdminPrivilege.exists() )
			{
				getAdminPrivilegeManager().update(clientAdminPrivilege);
				success(model, "msg.base.admin.admins.updateprivilege.success");
			}
			else
			{
				getAdminPrivilegeManager().grant(clientAdminPrivilege);

				success(model, "msg.base.admin.admins.createprivilege.success");
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.admins.updateprivilege.failure", result, errors);
		}

		return initResponse(model, response, clientAdminPrivilege);
	}

	@RequestMapping(value = "**/base/admin/admins/removeprivilege" )
	public void removeAdminPrivilege(@RequestParam("clientadminprivid") Integer clientAdminPrivId, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);

		ClientAdminPrivilege priv = new ClientAdminPrivilege();
		if ( clientAdminPrivId != null )
		{
			priv.setSer(clientAdminPrivId);
			priv.loadedVersion();

			getLogger().debug("Removing admin privilege " + priv);
		}

		if ( priv.exists() )
		{
			getAdminPrivilegeManager().revoke(priv);
			success(model, "msg.base.admin.admins.removeprivilege.success");
		}
		else
		{
			fail(model, "msg.base.admin.admins.removeprivilege.failure", null, Errors.NO_ERRORS);
		}

		String redirURL = "update.html?id=" + priv.getAdmin(false).getSer();
		response.sendRedirect(redirURL);

		redirOnSuccess(model, null, null, null, redirURL, request, response);
	}

	public AdminManager getAdminManager()
	{
		return adminManager;
	}

	@Autowired
	public void setAdminManager(AdminManager adminManager)
	{
		this.adminManager = adminManager;
	}

	public AdminPrivilegeManager getAdminPrivilegeManager()
	{
		return adminPrivilegeManager;
	}

	@Autowired
	public void setAdminPrivilegeManager(AdminPrivilegeManager adminPrivilegeManager)
	{
		this.adminPrivilegeManager = adminPrivilegeManager;
	}

	public PrivilegeManager getPrivilegeManager()
	{
		return privilegeManager;
	}

	@Autowired
	public void setPrivilegeManager(PrivilegeManager privilegeManager)
	{
		this.privilegeManager = privilegeManager;
	}
}
