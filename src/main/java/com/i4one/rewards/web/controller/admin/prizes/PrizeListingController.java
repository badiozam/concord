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
package com.i4one.rewards.web.controller.admin.prizes;

import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.prize.PrizeManager;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class PrizeListingController extends BaseAdminViewController
{
	private PrizeManager prizeManager;

	protected Set<Prize> doSearchPrizes(PrizeListing listing, Model model)
	{
		// Filter depending on the display (or just display the live items)
		//
		Set<SiteGroup> siteGroups = model.getSiteGroups();
		PaginationFilter pagination = new SiteGroupPagination(siteGroups, listing.getPagination());

		return getPrizeManager().search(listing.getSearch(), pagination);
	}
	
	@RequestMapping(value = "**/rewards/admin/prizes/listing", method=RequestMethod.POST, produces="application/json")
	public @ResponseBody Set<Prize> searchPrizes(@RequestBody PrizeListing listing, HttpServletRequest request)
	{
		Model model = initRequest(request, null);

		return doSearchPrizes(listing, model);
	}

	@RequestMapping(value = { "**/rewards/admin/prizes/listing", "**/rewards/admin/prizes/index" })
	public Model setPrizeListing(@ModelAttribute("listing") PrizeListing listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, listing);

		Set<Prize> prizes = doSearchPrizes(listing, model);

		//model.put("prizes", prizes);
		listing.setItems(prizes);

		return initResponse(model, response, listing);
	}

	public PrizeManager getPrizeManager()
	{
		return prizeManager;
	}

	@Autowired
	public void setPrizeManager(PrizeManager prizeManager)
	{
		this.prizeManager = prizeManager;
	}
}
