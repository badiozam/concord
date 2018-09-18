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
package com.i4one.test;

import com.i4one.base.web.HttpServletRequestWrapper;
import com.i4one.base.web.RequestState;
import com.i4one.base.web.SimpleRequestState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("requestState")
@Scope("singleton")
public class MockRequestState extends SimpleRequestState implements RequestState
{
	private transient MockHttpServletResponse response;

	@Override
	public void init()
	{
		if ( response == null )
		{
			response = new MockHttpServletResponse();
		}

		super.init();
	}

	public MockHttpServletRequest getMockRequest()
	{
		HttpServletRequest request = super.getRequest();
		if ( request instanceof MockHttpServletRequest )
		{
			return (MockHttpServletRequest)request;
		}
		else if ( request instanceof HttpServletRequestWrapper )
		{
			return (MockHttpServletRequest) ((HttpServletRequestWrapper)request).getRequest();
		}
		else
		{
			return null;
		}
	}

	public void setMockRequest(MockHttpServletRequest request)
	{
		super.setRequest(request);
		init();
	}

	public MockHttpServletResponse getResponse()
	{
		return response;
	}

	public void setResponse(HttpServletResponse response)
	{
		this.response = (MockHttpServletResponse) response;
	}

}
