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
package com.i4one.base.model.client;

import com.i4one.base.model.manager.Manager;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface SiteGroupManager extends Manager<SiteGroupRecord, SiteGroup>
{
	/**
	 * Retrieve a client group by serial number
	 *
	 * @param ser The serial number of the client group
	 *
	 * @return The client group with the given serial number or an empty client group if not found
	 */
	public SiteGroup getSiteGroup(int ser);

	/**
	 * Returns a list of all groups that a given client belongs to
	 * 
	 * @param client The client to search the client groups for
	 * 
	 * @return A list of all groups the client belongs to
	 */
	public Set<SiteGroup> getSiteGroups(SingleClient client);

	/**
	 * Get a list of individual clients that belong to a particular group
	 * 
	 * @param siteGroup The group to look up
	 * 
	 * @return A (potentially empty) list of clients that belong to the given group
	 */
	public Set<SingleClient> getSingleClients(SiteGroup siteGroup);

	/**
	 * Link the given client to the given site group.
	 * 
	 * @param sitegroup The sitegroup to associate the client with
	 * @param client The client to associate
	 * 
	 * @return True if the item was newly associated, false otherwise
	 */
	public boolean associate(SiteGroup sitegroup, SingleClient client);

	/**
	 * Unlink the incoming client from the given site group.
	 * All other associations remain intact.
	 * 
	 * @param sitegroup The site group to dissociate the client from
	 * @param client The client to dissociate
	 * 
	 * @return True if the item was previously associated and was properly dissociated, false otherwise
	 */
	public boolean dissociate(SiteGroup sitegroup, SingleClient client);

}
