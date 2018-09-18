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
package com.i4one.promotion.model.code;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface CodeResponseManager extends ActivityManager<CodeResponseRecord, CodeResponse, Code>
{
	/**
	 * Attempts to process a given code for a particular user. If the code was
	 * previously played by the user, the corresponding CodeResponse record is
	 * set as both pre and post elements and no action is taken. Otherwise,
	 * a new CodeResponse object is created.
	 * 
	 * @param codeResponse The code response to create
	 * 
	 * @return The result of the processing
	 */
	@Override
	public ReturnType<CodeResponse> create(CodeResponse codeResponse);

	/**
	 * Attempts to record a response for any number of codes that match the
	 * given code string identifier.
	 * 
	 * @param codeStr The code string identifier
	 * @param user The user responding to the codes
	 * @param pagination The pagination to use when retrieving the codes,
	 * 	a terminable pagination with the current time chains this object.
	 * 
	 * @return A set of responses that were recorded for the given codes
	 */
	public List<ReturnType<CodeResponse>> processCodes(String codeStr, User user, TerminablePagination pagination);
}
