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
public interface ClientOptionRecordDao extends Dao<ClientOptionRecord>
{
	/**
	 * Get a single option by key
	 *
	 * @param clientid The client the option belongs to
	 * @param key The key to look up
	 *
	 * @return The option or null if not found
	 */
	public ClientOptionRecord getOption(int clientid, String key);

	/**
	 * Retrieves all options that start with the given key value
	 *
	 * @param clientid The client to which the keys belong
	 * @param keyStartsWith The value that the keys must start with
	 * @param pagination The qualifier/sorting info
	 *
	 * @return A list of options whose keys start with the given string
	 */
	public List<ClientOptionRecord> getOptions(int clientid, String keyStartsWith, PaginationFilter pagination);
	/**
	 * Gets all valid keys
	 * 
	 * @param pagination The qualifier/sorting info
	 *
	 * @return All valid keys
	 */
	public List<String> getAllKeys(PaginationFilter pagination);
}
