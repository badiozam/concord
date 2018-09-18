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

import com.i4one.base.dao.SiteGroupRecordType;
import com.i4one.base.model.SiteGroupType;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.web.controller.Model;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseSiteGroupTypeCrudController<U extends SiteGroupRecordType, T extends SiteGroupType<U>> extends BaseClientTypeCrudController<U, T>
{
	@Override
	public Model initRequest(HttpServletRequest request, T modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute != null )
		{
			// We limit the client groups to only those that we can access from this client level
			//
			Set<SiteGroup> siteGroups = model.getSiteGroups()
				.stream()
				//.filter( (siteGroup) -> { return siteGroup.belongsTo(model.getSingleClient());} )
				.filter( (siteGroup) -> { return siteGroup.contains(model.getSingleClient()) || siteGroup.belongsTo(model.getSingleClient()); } )
				.sorted()
				.collect(Collectors.toSet());

			model.put("siteGroups", toSelectMapping(siteGroups, SiteGroup::getTitle));

			// Default to the first client group if one is not set already, the
			// parent's method only handles single client types and since we're
			// a multi-client type we need to handle our own
			//
			if ( !modelAttribute.getSiteGroup().exists() && !siteGroups.isEmpty() )
			{
				modelAttribute.setSiteGroup(siteGroups.iterator().next());
			}

		}

		return model;
	}
}