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
package com.i4one.base.web.controller.admin.emailblasts.builders;

import com.i4one.base.model.emailblast.builder.EmailBlastBuilderManager;
import com.i4one.base.web.controller.ListingPagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
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
public class EmailBlastBuilderListingController extends BaseAdminViewController
{
	private EmailBlastBuilderManager emailBlastBuilderManager;

	@RequestMapping(value = { "**/base/admin/emailblasts/builders/listing", "**/base/admin/emailblasts/builders/index" })
	public Model setEmailBlastListing(@ModelAttribute("listing") ListingPagination listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, listing);

		listing.setItems(getEmailBlastBuilderManager().getAllBuilders(model.getSingleClient(), listing.getPagination()));

		return initResponse(model, response, listing);
	}

	public EmailBlastBuilderManager getEmailBlastBuilderManager()
	{
		return emailBlastBuilderManager;
	}

	@Autowired
	public void setEmailBlastBuilderManager(EmailBlastBuilderManager emailBlastBuilderManager)
	{
		this.emailBlastBuilderManager = emailBlastBuilderManager;
	}
}
