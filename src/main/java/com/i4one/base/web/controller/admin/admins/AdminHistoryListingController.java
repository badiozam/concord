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

import com.i4one.base.model.adminhistory.AdminHistory;
import com.i4one.base.model.adminhistory.AdminHistoryManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AdminHistoryListingController extends BaseAdminViewController
{
	private AdminHistoryManager adminHistoryManager;

	@RequestMapping(value = "**/base/admin/admins/history", method = RequestMethod.GET)
	public Model getAdminHistoryListing(@ModelAttribute("display") WebModelHistory display, HttpServletRequest request, HttpServletResponse response)
	{
		display.getPagination().setOrderBy("timestamp DESC");

		return postAdminHistoryListing(display, request, response);
	}

	@RequestMapping(value = "**/base/admin/admins/history", method = RequestMethod.POST)
	public Model postAdminHistoryListing(@ModelAttribute("display") WebModelHistory display, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, display);

		getLogger().debug("Display item is " + display.getItem());

		// Always include the current term when displaying future and/or past
		//
		Set<AdminHistory> histories;
		if ( !display.getItem().exists() )
		{
			if ( "base.none".equals(display.getItem().getFeatureName()) && !display.getAdmin().exists() )
			{
				histories = getAdminHistoryManager().getAdminHistoryByAdmin(getAdmin(model), display.getPagination());
			}
			else if ( display.getAdmin().exists() )
			{
				histories = getAdminHistoryManager().getAdminHistoryByAdmin(display.getAdmin(), display.getPagination());
			}
			else
			{
				histories = getAdminHistoryManager().getAdminHistoryForFeature(display.getItem(), display.getPagination());
			}
		}
		else
		{
			histories = getAdminHistoryManager().getAdminHistoryForItem(display.getItem(), display.getPagination());
		}

		histories.forEach( (currHistory) ->
		{
			currHistory.setChildren(getAdminHistoryManager().getAdminHistoryByParent(currHistory, SimplePaginationFilter.NONE));
		});

		model.put("history", histories);

		return initResponse(model, response, display);
	}

	public AdminHistoryManager getAdminHistoryManager()
	{
		return adminHistoryManager;
	}

	@Autowired
	public void setAdminHistoryManager(AdminHistoryManager adminHistoryManager)
	{
		this.adminHistoryManager = adminHistoryManager;
	}

}
