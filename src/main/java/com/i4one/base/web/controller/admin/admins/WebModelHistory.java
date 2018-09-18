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
package com.i4one.base.web.controller.admin.admins;

import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.HistoryPagination;
import com.i4one.base.model.manager.GenericFeature;
import com.i4one.base.model.manager.pagination.PaginationFilter;

/**
 * @author Hamid Badiozamani
 */
public class WebModelHistory
{
	private GenericFeature item;
	private PaginationFilter pagination;
	private String backURL;
	private Admin admin;

	public WebModelHistory()
	{
		pagination = new HistoryPagination();
		item = new GenericFeature();
		backURL = "index.html";

		admin = new Admin();
	}

	public PaginationFilter getPagination()
	{
		return pagination;
	}

	public void setPagination(PaginationFilter pagination)
	{
		this.pagination = pagination;
	}

	public GenericFeature getItem()
	{
		return item;
	}

	public void setItem(GenericFeature item)
	{
		this.item = item;
	}

	public String getBackURL()
	{
		return backURL;
	}

	public void setBackURL(String backURL)
	{
		this.backURL = backURL;
	}

	public Admin getAdmin()
	{
		return admin;
	}

	public void setAdmin(Admin admin)
	{
		this.admin = admin;
	}
}
