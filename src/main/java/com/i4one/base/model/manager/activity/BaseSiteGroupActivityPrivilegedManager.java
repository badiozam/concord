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
package com.i4one.base.model.manager.activity;

import com.i4one.base.dao.activity.ActivityRecordType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.SiteGroupActivityType;
import com.i4one.base.model.SiteGroupType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.client.SiteGroupManager;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseSiteGroupActivityPrivilegedManager<U extends ActivityRecordType, T extends SiteGroupActivityType<U, V>, V extends SiteGroupType<?>> extends BaseActivityPrivilegedManager<U, T, V> implements PrivilegedManager<U, T>, ActivityManager<U, T, V>
{
	private SiteGroupManager siteGroupManager;

	@Override
	protected void checkWrite(T item, User user, String methodName)
	{
		super.checkWrite(item, user, methodName);

		// We check to make sure the item can be accessed by the incoming request's client,
		// this prevents users from accessing things such as clickthrus directly by serial
		// number from a client that doesn't have access.
		//
		SiteGroup siteGroup = getSiteGroupManager().getSiteGroup(item.getActionItem().getSiteGroup().getSer());
		SingleClient client = getRequestState().getSingleClient();

		if ( !siteGroup.contains(client))
		{
			getLogger().debug("Site Group: " + item.getActionItem().getSiteGroup() + " does not include the client " + client);
			throw new Errors(getInterfaceName() + "." + methodName, new ErrorMessage("msg." + getInterfaceName() + "." + methodName + ".denied", "Insufficient privileges for $item feature $featureName thru client $client.", new Object[] { "item", item, "featureName", getFeatureName(), "client", client }, null));
		}
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
