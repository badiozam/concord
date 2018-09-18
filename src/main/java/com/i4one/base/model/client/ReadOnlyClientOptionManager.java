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

import com.i4one.base.model.manager.BaseReadOnlyManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class ReadOnlyClientOptionManager extends BaseReadOnlyManager<ClientOptionRecord, ClientOption> implements ClientOptionManager
{
	private ClientOptionManager clientOptionManager;

	@Override
	public Manager getImplementationManager()
	{
		return clientOptionManager;
	}

	@Override
	public ClientOption getOption(SingleClient client, String key)
	{
		return getClientOptionManager().getOption(client, key);
	}

	@Override
	public String getOptionValue(SingleClient client, String key)
	{
		return getClientOptionManager().getOptionValue(client, key);
	}

	@Override
	public List<ClientOption> getOptions(SingleClient client, String startsWithKey, PaginationFilter pagination)
	{
		return getClientOptionManager().getOptions(client, startsWithKey, pagination);
	}

	@Override
	public List<String> getAllKeys(PaginationFilter pagination)
	{
		return getClientOptionManager().getAllKeys(pagination);
	}

	public ClientOptionManager getClientOptionManager()
	{
		return clientOptionManager;
	}

	@Autowired
	@Qualifier("base.CachedClientOptionManager")
	public void setClientOptionManager(ClientOptionManager clientOptionManager)
	{
		this.clientOptionManager = clientOptionManager;
	}

}
