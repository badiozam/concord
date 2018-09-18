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
package com.i4one.base.model.preference;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface PreferenceManager extends PaginableManager<PreferenceRecord, Preference>
{
	/**
	 * Return all preferences that match the pagination filter and are visible
	 * to the given client.
	 * 
	 * @param client The client which is requesting the preferences.
	 * @param pagination The pagination filter to match against
	 * 
	 * @return A (potentially empty) set of all matching preferences.
	 */
	public Set<Preference> getAllPreferences(SingleClient client, PaginationFilter pagination);

	/**
	 * Get the current settings for the given manager.
	 * 
	 * @param client The client for which to retrieve the settings.
	 * 
	 * @return The current preference settings
	 */
	public PreferenceSettings getSettings(SingleClient client);

	/**
	 * Update preference settings.
	 * 
	 * @param settings The new preference settings to update
	 * 
	 * @return The result of the updated settings
	 */
	public ReturnType<PreferenceSettings> updateSettings(PreferenceSettings settings);
}
