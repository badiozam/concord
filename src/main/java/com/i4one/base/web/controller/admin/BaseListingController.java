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

import com.i4one.base.dao.RecordType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.web.controller.ListingPagination;
import com.i4one.base.web.controller.Model;
import java.io.OutputStream;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseListingController<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseManagedTypeController<U, T>
{
	protected Model viewListingImpl(ListingPagination<T> listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, listing);

		handleActions(listing, result, model);

		Set<T> items = getListing(listing, model);
		listing.setItems(items);

		return initResponse(model, response, listing);
	}

	protected void exportListingImpl(ListingPagination<T> listing, BindingResult result, HttpServletRequest request, OutputStream out)
	{
		Model model = initRequest(request, listing);
		Set<T> items = getListing(listing, model);

		getManager().exportCSV(items, out);
	}

	protected abstract Set<T> getListing(ListingPagination<T> listing, Model model);

	/**
	 * Handles any actions other than displaying the listing. The default implementation
	 * provides the remove action.
	 * 
	 * @param listing The listing form backing object
	 * @param result The binding result
	 * @param model  The initialized model
	 */
	protected void handleActions(ListingPagination<T> listing, BindingResult result, Model model)
	{
		if ( listing.isRemove() )
		{
			int id = listing.getActionItemId();
			T item = getManager().getById(id);

			try
			{
				if ( item.exists() )
				{
					// The database should handle the cascading removal thru the use of foreign keys
					//
					getManager().remove(item);
		
					// If we make it this far, we were successfull
					//
					success(model, "msg.base.admin.general.remove.success");
				}
				else
				{
					getLogger().debug("No such item exists with serial number {}", id);
					fail(model, "msg.base.admin.general.remove.failure", result, new Errors(new ErrorMessage("msg.base.general.listing.remove.dne", "Item with id $item.ser does not exist", new Object[]{"item", item})));
				}
			}
			catch (Errors errors)
			{
				getLogger().debug("Caught errors {}", errors);
				fail(model, "msg.base.admin.general.remove.failure", result, errors);
			}
		}

	}
}
