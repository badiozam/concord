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
package com.i4one.base.web.interceptor.model;

import com.i4one.base.core.Utils;
import com.i4one.base.model.client.ClientOptionManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class FileServerModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	public static final String FILES_URL = "filesurl";

	// XXX: Needs to be handled by a FileServerManager
	private static final String FILEMANAGER_HTTPURL = "fileManager.httpBaseURL"; 
	private static final String FILEMANAGER_HTTPSURL = "fileManager.httpsBaseURL"; 

	private ClientOptionManager readOnlyClientOptionManager;

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		SingleClient client = model.getSingleClient();

		if ( model.getRequest().isSecure() )
		{
			return Utils.makeMap(FILES_URL, getReadOnlyClientOptionManager().getOptionValue(client, FILEMANAGER_HTTPSURL));
		}
		else
		{
			return Utils.makeMap(FILES_URL, getReadOnlyClientOptionManager().getOptionValue(client, FILEMANAGER_HTTPURL));
		}
	}

	public ClientOptionManager getReadOnlyClientOptionManager()
	{
		return readOnlyClientOptionManager;
	}

	@Autowired
	public void setReadOnlyClientOptionManager(ClientOptionManager readOnlyClientOptionManager)
	{
		this.readOnlyClientOptionManager = readOnlyClientOptionManager;
	}

}
