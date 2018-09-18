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
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface ClientOptionManager extends Manager<ClientOptionRecord, ClientOption>, Cloneable
{
	/**
	 * Gets an option value for a given client
	 *
	 * @param client The client to search the options for
	 * @param key The option key to look up
	 *
	 * @return The client's option, or its inherited value or a non-existent value if not found
	 */
	public ClientOption getOption(SingleClient client, String key);

	/**
	 * Gets an option value for a given client
	 *
	 * @param client The client to search the options for
	 * @param key The option key to look up
	 *
	 * @return The client's option value, or its inherited value.
	 * @throws IllegalArgumentException if the value is not found
	 */
	public String getOptionValue(SingleClient client, String key);

	/**
	 * Gets a group of options for a given client with the given key
	 *
	 * @param client The client to search the options for
	 * @param startsWithKey The string that option keys must start with
	 * @param pagination The pagination/ordering information
	 *
	 * @return The client's list of options that start with the given string
	 */
	public List<ClientOption> getOptions(SingleClient client, String startsWithKey, PaginationFilter pagination);

	/**
	 * Get all possible option keys in blocks
	 *
	 * @param pagination The pagination/ordering information
	 *
	 * @return All possible option keys
	 */
	public List<String> getAllKeys(PaginationFilter pagination);

}
