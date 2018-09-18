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
package com.i4one.promotion.web.controller.admin.trivias;

import com.i4one.base.model.Errors;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.TerminableListingPagination;
import com.i4one.base.web.controller.TerminableSortableListingPagination;
import com.i4one.base.web.controller.admin.BaseTerminableListingController;
import com.i4one.promotion.model.trivia.Trivia;
import com.i4one.promotion.model.trivia.TriviaManager;
import com.i4one.promotion.model.trivia.TriviaRecord;
import java.util.Set;
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
public class TriviaListingController extends BaseTerminableListingController<TriviaRecord, Trivia>
{
	private TriviaManager triviaManager;

	@RequestMapping(value = { "**/promotion/admin/trivias/listing", "**/promotion/admin/trivias/index" })
	public Model viewListing(@ModelAttribute("listing") TerminableListingPagination<Trivia> listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		return viewListingImpl(listing, result, request, response);
	}

	@RequestMapping(value = {"**/promotion/admin/trivias/sort"}, method = RequestMethod.GET )
	public Model sortListing(@ModelAttribute("listing") TriviaListingPagination listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		return viewListingImpl(listing, result, request, response);
	}

	@RequestMapping(value = {"**/promotion/admin/trivias/sort"}, method = RequestMethod.POST )
	public Model doSortListing(@ModelAttribute("listing") TriviaListingPagination listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, listing);

		if ( !result.hasErrors() )
		{
			int orderWeight = listing.getOrdered().size();
			for (Trivia trivia : listing.getOrdered() )
			{
				getTriviaManager().loadById(trivia, trivia.getSer());
				trivia.setOrderWeight(orderWeight--);

				getTriviaManager().update(trivia);
			}

			for (Trivia trivia : listing.getUnordered() )
			{
				getTriviaManager().loadById(trivia, trivia.getSer());
				trivia.setOrderWeight(0);

				getTriviaManager().update(trivia);
			}
			success(model, "msg.base.admin.general.sort.success");
		}
		else
		{
			fail(model, "msg.base.admin.general.sort.failure", result, new Errors());
		}

		// Reload the items after having modified them
		//
		listing.setItems(getListing(listing, model));

		return initResponse(model, response, listing);
	}

	public TriviaManager getTriviaManager()
	{
		return triviaManager;
	}

	@Autowired
	public void setTriviaManager(TriviaManager triviaManager)
	{
		this.triviaManager = triviaManager;
	}

	@Override
	protected TerminablePagination getItemPagination(Model model, TerminableListingPagination<Trivia> listing)
	{
		Set<SiteGroup> siteGroups = model.getSiteGroups();
		return new TerminablePagination(model.getTimeInSeconds(),
			new SiteGroupPagination(siteGroups, listing.getPagination()));

	}

	@Override
	protected TerminableManager<TriviaRecord, Trivia> getManager()
	{
		return getTriviaManager();
	}
}
