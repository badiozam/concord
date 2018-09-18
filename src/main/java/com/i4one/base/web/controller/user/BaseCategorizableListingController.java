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
package com.i4one.base.web.controller.user;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.category.Category;
import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseCategorizableListingController<T extends RecordTypeDelegator<?>, C extends Category<?>> extends BaseUserViewController
{
	public abstract CategoryManager<?, C> getCategoryManager();

	public C viewListingImpl(int categoryid, Model model, HttpServletRequest request, HttpServletResponse response)
	{
		C category = getCategoryManager().getById(categoryid);

		Set<C> categories = loadCategories(model);
		if ( categories.size() == 1 )
		{
			// If we only have one category, we immediately go to that category
			//
			category = categories.iterator().next();
			model.put("onlyCategory", true);
		}
		else
		{
			model.put("onlyCategory", false);
		}

		model.put("categories", categories);
		model.put("category", category);

		return category;
	}

	protected Set<C> loadCategories(Model model)
	{
		return getCategoryManager().getAllCategories(SimplePaginationFilter.NONE);
	}
}
