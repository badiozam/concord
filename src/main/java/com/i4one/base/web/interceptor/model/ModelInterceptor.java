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

import com.i4one.base.web.controller.Model;
import java.util.Map;

/**
 * This interface sets out methods for intercepting and adding to the model
 * that eventually gets sent to a given controller. ModelInterceptors are defined
 * and passed to the {@link com.i4one.base.web.ModelManager} which traverses the list
 * and calls on initializing the a Model object at request and response time.
 *
 * @author Hamid Badiozamani
 */
public interface ModelInterceptor
{
	/**
	 * Initialize the model map for the given request. This method is to be invoked
	 * just before the response is rendered
	 * 
	 * @param model The current model
	 * 
	 * @return The map to be merged with the current model
	 */
	public Map<String,Object> initResponseModel(Model model);

	/**
	 * Initialize the model map for the given request. This method is to be invoked
	 * prior to the request being processed.
	 * 
	 * @param model The current model
	 * 
	 * @return The map to be merged with the current model
	 */
	public Map<String,Object> initRequestModel(Model model);
}
