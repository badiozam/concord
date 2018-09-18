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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface SingleClientManager extends Manager<SingleClientRecord, SingleClient>
{
	/**
	 * Retrieve a client by name
	 *
	 * @param name The name of the client
	 *
	 * @return The client with the given name or an empty client if not found
	 */
	public SingleClient getClient(String name);

	/**
	 * Retrieve a client by domain name
	 *
	 * @param domain The domain name of the client
	 *
	 * @return The first client that matches the domain name or an empty client
	 * 	if not found.
	 */
	public SingleClient getClientByDomain(String domain);

	/**
	 * Retrieve a client by serial number
	 *
	 * @param ser The serial number of the client
	 *
	 * @return The client with the given serial number or null if not found
	 */
	public SingleClient getClient(int ser);

	/**
	 * Retrieve a client's parent.
	 * 
	 * @param client The client whose parent to look up
	 * 
	 * @return The client's parent or the root client if at the root node
	 */
	public SingleClient getParent(SingleClient client);

	/**
	 * Gets a list of all client's names that are child clients to the
	 * given parameter
	 *
	 * @param parent The parent client whose children are to be retrieved
	 * @param pagination
	 *
	 * @return A list of all child clients under the parent or the parent client's
	 *      name if the parent has no children
	 */
	public List<SingleClient> getAllClients(SingleClient parent, PaginationFilter pagination);

	/**
	 * Get the current settings for the given manager.
	 * 
	 * @param client The client for which to retrieve the settings.
	 * 
	 * @return The current trivia settings
	 */
	public ClientSettings getSettings(SingleClient client);

	/**
	 * Update trivia settings.
	 * 
	 * @param settings The new trivia settings to update
	 * 
	 * @return The result of the updated settings
	 */
	public ReturnType<ClientSettings> updateSettings(ClientSettings settings);
}
