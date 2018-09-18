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
package com.i4one.base.web.controller.admin;

import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.model.manager.terminable.TerminableClientType;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.web.controller.ListingPagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.TerminableListingPagination;
import java.util.Collections;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Hamid Badiozamani
 */
public abstract class BaseTerminableListingController<U extends TerminableClientRecordType, T extends TerminableClientType<U>> extends BaseListingController<U, T>
{
	public static final String EMPTYINSTANCE = "emptyInstance";

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		// We need this to be able to generalize the URL going to the
		// admin history by feature
		//
		model.put(EMPTYINSTANCE, getManager().emptyInstance());

		return model;
	}

	@Override
	protected Set<T> getListing(ListingPagination<T> listing, Model model)
	{
		if ( listing instanceof TerminableListingPagination )
		{
			TerminableListingPagination<T> terminableListing = (TerminableListingPagination<T>)listing;
			TerminablePagination pagination = getItemPagination(model, terminableListing);

			Set<T> items;
			if ( terminableListing.isLiveListing() )
			{
				items = getManager().getLive(pagination);
			}
			else
			{
				if ( terminableListing.isPastListing())
				{
					pagination.setPast();
				}
				else if ( terminableListing.isFutureListing())
				{
					pagination.setFuture();
				}
				items = getManager().getByRange(pagination);
			}

			return items;
		}
		else
		{
			getLogger().error("Expecting a TerminableListing pagination but got {} instead", listing.getClass());
			return Collections.EMPTY_SET;
		}
	}

	protected abstract TerminablePagination getItemPagination(Model model, TerminableListingPagination<T> listing);

	@Override
	protected abstract TerminableManager<U, T> getManager();
}
