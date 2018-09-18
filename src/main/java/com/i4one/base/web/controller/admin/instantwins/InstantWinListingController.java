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
package com.i4one.base.web.controller.admin.instantwins;

import com.i4one.base.model.manager.attachable.NonExclusiveTerminablePagination;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.instantwin.InstantWinRecord;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.TerminableListingPagination;
import com.i4one.base.web.controller.admin.BaseTerminableListingController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class InstantWinListingController extends BaseTerminableListingController<InstantWinRecord, InstantWin>
{
	@RequestMapping(value = {"**/admin/instantwins/listing", "**/base/admin/instantwins/index" } )
	public Model viewListing(@ModelAttribute(value = "listing") TerminableListingPagination<InstantWin> listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		return viewListingImpl(listing, result, request, response);
	}

	@Override
	protected TerminablePagination getItemPagination(Model model, TerminableListingPagination<InstantWin> listing)
	{
		SingleClient client = model.getSingleClient();
		return new NonExclusiveTerminablePagination(model.getTimeInSeconds(), new ClientPagination(client, listing.getPagination()));
	}

	@Override
	protected TerminableManager<InstantWinRecord, InstantWin> getManager()
	{
		return getInstantWinManager();
	}
}
