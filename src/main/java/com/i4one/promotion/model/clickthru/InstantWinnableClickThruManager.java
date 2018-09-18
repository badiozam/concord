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
package com.i4one.promotion.model.clickthru;


import com.i4one.base.dao.terminable.TerminableClientRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseInstantWinnableTerminableManager;
import com.i4one.base.model.manager.instantwinnable.InstantWinnableManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class InstantWinnableClickThruManager extends BaseInstantWinnableTerminableManager<ClickThruRecord, ClickThru> implements ClickThruManager,InstantWinnableManager<ClickThruRecord, ClickThru>
{
	private ClickThruManager clickthruManager;

	@Override
	public ClickThruSettings getSettings(SingleClient client)
	{
		return getClickThruManager().getSettings(client);
	}

	@Override
	public ReturnType<ClickThruSettings> updateSettings(ClickThruSettings settings)
	{
		return getClickThruManager().updateSettings(settings);
	}

	@Override
	public ClickThruManager getImplementationManager()
	{
		return getClickThruManager();
	}

	public ClickThruManager getClickThruManager()
	{
		return clickthruManager;
	}

	@Autowired
	@Qualifier("promotion.TriggerableClickThruManager")
	public void setClickThruManager(ClickThruManager clickthruManager)
	{
		this.clickthruManager = clickthruManager;
	}

	@Override
	public TerminableClientRecordTypeDao<ClickThruRecord> getDao()
	{
		return getClickThruManager().getDao();
	}
}
