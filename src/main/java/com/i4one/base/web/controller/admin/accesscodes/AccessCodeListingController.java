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
package com.i4one.base.web.controller.admin.accesscodes;

import com.i4one.base.model.accesscode.AccessCode;
import com.i4one.base.model.accesscode.AccessCodeManager;
import com.i4one.base.model.accesscode.AccessCodeRecord;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.TerminableListingPagination;
import com.i4one.base.web.controller.admin.BaseTerminableListingController;
import java.io.IOException;
import java.io.OutputStream;
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
public class AccessCodeListingController extends BaseTerminableListingController<AccessCodeRecord, AccessCode>
{
	private AccessCodeManager accessCodeManager;

	@RequestMapping(value = { "**/base/admin/accesscodes/listing", "**/base/admin/accesscodes/index" })
	public Model viewListing(@ModelAttribute("listing") TerminableListingPagination<AccessCode> listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		return viewListingImpl(listing, result, request, response);
	}

	@RequestMapping(value = "**/base/admin/accesscodes/export.csv", produces="text/csv")
	public void exportListing(@ModelAttribute("listing") TerminableListingPagination<AccessCode> listing, BindingResult result, HttpServletRequest request, OutputStream out) throws IOException
	{
		exportListingImpl(listing, result, request, out);
	}

	@Override
	protected TerminablePagination getItemPagination(Model model, TerminableListingPagination<AccessCode> listing)
	{
		// Filter depending on the display (or just display the live items)
		//
		return new TerminablePagination(model.getTimeInSeconds(), new ClientPagination(model.getSingleClient(), listing.getPagination()));

	}

	public AccessCodeManager getAccessCodeManager()
	{
		return accessCodeManager;
	}

	@Autowired
	public void setAccessCodeManager(AccessCodeManager accessCodeManager)
	{
		this.accessCodeManager = accessCodeManager;
	}

	@Override
	protected TerminableManager<AccessCodeRecord, AccessCode> getManager()
	{
		return getAccessCodeManager();
	}
}
