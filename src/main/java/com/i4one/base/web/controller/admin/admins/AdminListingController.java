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

import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminManager;
import com.i4one.base.model.admin.AdminRecord;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.ListingPagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseListingController;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AdminListingController extends BaseListingController<AdminRecord, Admin>
{
	private AdminManager adminManager;

	@RequestMapping(value = {"**/base/admin/admins/listing", "**/base/admin/admins/index" } )
	public Model viewListing(@ModelAttribute(value = "listing") ListingPagination<Admin> listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		return viewListingImpl(listing, result, request, response);
	}

	@Override
	protected Set<Admin> getListing(ListingPagination<Admin> listing, Model model)
	{
		return getAdminManager().getAdmins(getAdmin(model), listing.getPagination());
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

	@Override
	protected Manager<AdminRecord, Admin> getManager()
	{
		return getAdminManager();
	}
}
