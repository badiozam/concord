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

import com.i4one.base.dao.Dao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface SiteGroupRecordDao extends Dao<SiteGroupRecord>
{
	/**
	 * Get all of the groups this client belongs to
	 * 
	 * @param clientid The client id whose groups we're to retrieve
	 * 
	 * @return The (potentially) empty set of client groups the client belongs to
	 */
	public List<SiteGroupRecord> getSiteGroups(int clientid);

	/**
	 * Get all of the clients that belong to a particular group
	 * 
	 * @param sitegroupid The group's id
	 * 
	 * @return A (potentially empty) list of clients that belong to this group
	 */
	public List<SingleClientRecord> getSingleClients(int sitegroupid);

	/**
	 * Determines whether a given client is associated with a given site group.
	 * 
	 * @param sitegroupid The sitegroup serial number
	 * @param clientid The client serial number
	 * 
	 * @return True if the client is associated with the sitegroup, false otherwise
	 */
	public boolean isAssociated(int sitegroupid, int clientid);

	/**
	 * Associate a client with a given site group.
	 * 
	 * @param sitegroupid The sitegroup serial number
	 * @param clientid The client serial number
	 */
	public void associate(int sitegroupid, int clientid);

	/**
	 * Disassociate a client with a given site group.
	 * 
	 * @param sitegroupid The sitegroup serial number
	 * @param clientid The client serial number
	 */
	public void dissociate(int sitegroupid, int clientid);
	/**
	 * Get all available site groups
	 * 
	 * @param pagination The sort ordering for the site groups
	 * 
	 * @return A list of all available site groups
	 */
	public List<SiteGroupRecord> getAll(PaginationFilter pagination);
}
