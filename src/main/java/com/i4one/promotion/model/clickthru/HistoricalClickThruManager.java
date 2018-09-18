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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.HistoricalManager;
import com.i4one.base.model.manager.terminable.BaseTerminableHistoricalManager;
import com.i4one.base.model.manager.terminable.TerminablePrivilegedManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("promotion.ClickThruManager")
public class HistoricalClickThruManager extends BaseTerminableHistoricalManager<ClickThruRecord, ClickThru> implements HistoricalManager<ClickThruRecord, ClickThru>, ClickThruManager
{
	private TerminablePrivilegedManager<ClickThruRecord, ClickThru> privilegedClickThruManager;

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

	public ClickThruManager getClickThruManager()
	{
		return (ClickThruManager) getPrivilegedClickThruManager();
	}

	public TerminablePrivilegedManager<ClickThruRecord, ClickThru> getPrivilegedClickThruManager()
	{
		return privilegedClickThruManager;
	}

	@Autowired
	public <P extends ClickThruManager & TerminablePrivilegedManager<ClickThruRecord, ClickThru>>
	 void setPrivilegedClickThruManager(P privilegedClickThruManager)
	{
		this.privilegedClickThruManager = privilegedClickThruManager;
	}

	@Override
	public TerminablePrivilegedManager<ClickThruRecord, ClickThru> getImplementationManager()
	{
		return getPrivilegedClickThruManager();
	}
}
