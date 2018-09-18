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

import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.HistoricalManager;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("base.PreferenceManager")
public class HistoricalPreferenceManager extends BaseHistoricalManager<PreferenceRecord, Preference> implements HistoricalManager<PreferenceRecord, Preference>, PreferenceManager
{
	private PrivilegedManager<PreferenceRecord, Preference> privilegedPreferenceManager;

	@Override
	public Set<Preference> getAllPreferences(SingleClient client, PaginationFilter pagination)
	{
		return getPreferenceManager().getAllPreferences(client, pagination);
	}

	@Override
	public PreferenceSettings getSettings(SingleClient client)
	{
		return getPreferenceManager().getSettings(client);
	}

	@Override
	public ReturnType<PreferenceSettings> updateSettings(PreferenceSettings settings)
	{
		return getPreferenceManager().updateSettings(settings);
	}

	public PreferenceManager getPreferenceManager()
	{
		return (PreferenceManager) getPrivilegedPreferenceManager();
	}

	public PrivilegedManager<PreferenceRecord, Preference> getPrivilegedPreferenceManager()
	{
		return privilegedPreferenceManager;
	}

	@Autowired
	public <P extends PreferenceManager & PrivilegedManager<PreferenceRecord, Preference>>
	 void setPrivilegedPreferenceManager(P privilegedPreferenceManager)
	{
		this.privilegedPreferenceManager = privilegedPreferenceManager;
	}

	@Override
	public PrivilegedManager<PreferenceRecord, Preference> getImplementationManager()
	{
		return getPrivilegedPreferenceManager();
	}

	@Override
	public PaginableRecordTypeDao<PreferenceRecord> getDao()
	{
		return getPreferenceManager().getDao();
	}
}
