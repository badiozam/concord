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
package com.i4one.base.model.accesscode;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.ActivityManager;

/**
 * @author Hamid Badiozamani
 */
public interface AccessCodeResponseManager extends ActivityManager<AccessCodeResponseRecord, AccessCodeResponse, AccessCode>
{
	/**
	 * Attempts to process a given code for a particular user. If the code was
	 * previously played by the user, the corresponding AccessCodeResponse record is
	 * set as both pre and post elements and no action is taken. Otherwise,
	 * a new AccessCodeResponse object is created.
	 * 
	 * @param codeResponse The code response to create
	 * 
	 * @return The result of the processing
	 */
	@Override
	public ReturnType<AccessCodeResponse> create(AccessCodeResponse codeResponse);
}