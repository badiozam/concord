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
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.expendable.TerminableExpendableClientType;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.ListingPagination;
import com.i4one.base.web.controller.Model;
import org.springframework.validation.BindingResult;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseTerminableExpendableListingController<U extends TerminableClientRecordType, T extends TerminableExpendableClientType<U, T>> extends BaseTerminableListingController<U, T>
{
	private static final String REVERT_ACTION = "revert";

	@Override
	protected void handleActions(ListingPagination<T> listing, BindingResult result, Model model)
	{
		// The super class will handle the remove action
		//
		super.handleActions(listing, result, model);

		if ( listing.getAction().equalsIgnoreCase(REVERT_ACTION))
		{
			int id = listing.getActionItemId();
			T item = getManager().getById(id);

			try
			{
				if ( item.exists() )
				{
					// The database should handle the cascading removal thru the use of foreign keys
					//
					getActivityManager().revert(item, SimplePaginationFilter.NONE);
		
					// If we make it this far, we were successfull
					//
					success(model, "msg.base.admin.general.revert.success");
				}
				else
				{
					getLogger().debug("No such item exists with serial number {}", id);
					fail(model, "msg.base.admin.general.revert.failure", result, new Errors(new ErrorMessage("msg.base.general.listing.revert.dne", "Item with id $item.ser does not exist", new Object[]{"item", item})));
				}
			}
			catch (Errors errors)
			{
				getLogger().debug("Caught errors {}", errors);
				fail(model, "msg.base.admin.general.revert.failure", result, errors);
			}
		}

	}

	protected abstract ActivityManager<?, ?, T> getActivityManager();
}
