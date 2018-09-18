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
package com.i4one.base.model.manager.pagination;

import com.i4one.base.core.Utils;
import com.i4one.base.model.client.SiteGroup;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hamid Badiozamani
 */
public class SiteGroupPagination extends SimplePaginationFilter
{
	private final Set<SiteGroup> siteGroups;

	public SiteGroupPagination(SiteGroup client, PaginationFilter chain)
	{
		super(chain);

		siteGroups = new HashSet<>();
		siteGroups.add(client);
	}

	public SiteGroupPagination(Collection<SiteGroup> clients, PaginationFilter chain)
	{
		super(chain);

		siteGroups = new HashSet<>();
		siteGroups.addAll(clients);
	}

	@Override
	protected void initQualifiers()
	{
		super.initQualifiers();

		getColumnQualifier().setQualifier("sitegroupid", siteGroups.size() > 0 ? getSitegroupids() : null);
	}

	@Override
	public String toStringInternal()
	{
		if (!siteGroups.isEmpty())
		{
			if ( getLogger().isTraceEnabled() )
			{
				return "sitegroups: [" + Utils.toCSV(siteGroups, (sg) -> { return String.valueOf(sg.getSer()); }) + "], " + super.toStringInternal();
			}
			else
			{
				return "sg:[" + Utils.toCSV(siteGroups, (sg) -> { return String.valueOf(sg.getSer()); }) + "]," + super.toStringInternal();
			}
		}
		else
		{
			// No difference between us and our parent class if there's no sitegroup specified
			//
			return super.toStringInternal();
		}
	}

	protected Set<Integer> getSitegroupids()
	{
		return siteGroups.stream().map(SiteGroup::getSer).collect(Collectors.toSet());
	}

	public SiteGroup getSiteGroup()
	{
		if ( siteGroups.isEmpty() )
		{
			return new SiteGroup();
		}
		else
		{
			return siteGroups.iterator().next();
		}
	}

	public Set<SiteGroup> getSiteGroups()
	{
		return Collections.unmodifiableSet(siteGroups);
	}

}
