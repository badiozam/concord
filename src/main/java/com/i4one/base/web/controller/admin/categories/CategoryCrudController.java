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
package com.i4one.base.web.controller.admin.categories;

import com.i4one.base.model.category.Category;
import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.model.category.BaseCategoryRecord;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.client.SiteGroupManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseCrudController;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class CategoryCrudController<U extends BaseCategoryRecord, T extends Category<U>> extends BaseCrudController<U, T>
{
	private SiteGroupManager siteGroupManager;

	@Override
	public Model initRequest(HttpServletRequest request, T modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		Set<SiteGroup> siteGroups = getSiteGroupManager().getSiteGroups(model.getSingleClient());
		model.put("siteGroups", toSelectMapping(siteGroups, SiteGroup::getTitle));

		return model;
	}

	public abstract CategoryManager<U,T> getCategoryManager();

	@Override
	protected Manager<U, T> getManager()
	{
		return getCategoryManager();
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.categories";
	}

	public SiteGroupManager getSiteGroupManager()
	{
		return siteGroupManager;
	}

	@Autowired
	public void setSiteGroupManager(SiteGroupManager siteGroupManager)
	{
		this.siteGroupManager = siteGroupManager;
	}
}
