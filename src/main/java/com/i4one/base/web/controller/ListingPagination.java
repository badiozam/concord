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
package com.i4one.base.web.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.core.Base;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.JSONSerializable;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.springframework.beans.BeanUtils;

/**
 * A listing pagination request/response object. This object is used to query
 * a manager for a list of RecordTypeDelegators. The returned items can be
 * stored via the items property. The pagination is meant to be configured
 * prior to calling a manager class to retrieve the listing.
 * 
 * @author Hamid Badiozamani
 * @param <T> The type of items being requested
 */
public class ListingPagination<T extends RecordTypeDelegator<?>> extends BaseLoggable implements JSONSerializable
{
	private PaginationFilter pagination;
	private Collection<T> items;

	private String action;
	private int actionItemId;

	public ListingPagination()
	{
		super();

		pagination = new SimplePaginationFilter();

		action = "none";
		actionItemId = 0;
	}

	@JsonIgnore
	public PaginationFilter getPagination()
	{
		return pagination;
	}

	@JsonIgnore
	public void setPagination(PaginationFilter pagination)
	{
		this.pagination = pagination;
	}

	@JsonIgnore
	public Collection<T> getItems()
	{
		return items;
	}

	@JsonIgnore
	public void setItems(Collection<T> items)
	{
		this.items = items;
	}

	@JsonIgnore
	public boolean isRemove()
	{
		return getAction().equalsIgnoreCase("remove");
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public int getActionItemId()
	{
		return actionItemId;
	}

	public void setActionItemId(int actionItemId)
	{
		this.actionItemId = actionItemId;
	}

	// These methods allow JSON access to the underlying pagination
	//
	public int getPerPage()
	{
		return getPagination().getPerPage();
	}

	public void setPerPage(int perPage)
	{
		getPagination().setPerPage(perPage);
	}

	public int getCurrentPage()
	{
		return getPagination().getCurrentPage();
	}

	public void setCurrentPage(int currentPage)
	{
		getPagination().setCurrentPage(currentPage);
	}

	public String getOrderBy()
	{
		return getPagination().getOrderBy();
	}

	public void setOrderBy(String orderBy)
	{
		getPagination().setOrderBy(orderBy);
	}

	@JsonIgnore
	public String getJSONString()
	{
		return toJSONString();
	}

	@JsonIgnore
	public void setJSONString(String json) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		fromJSONString(json);
	}

	@Override
	public void fromJSONString(String json) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		ListingPagination fromJSON = Base.getInstance().getJacksonObjectMapper().readValue(json, this.getClass());
		BeanUtils.copyProperties(fromJSON, this, "JSONString");
	}

	@Override
	public String toJSONString()
	{
		try
		{
			return Base.getInstance().getJacksonObjectMapper().writeValueAsString(this);
		}
		catch (IOException ex)
		{
			return ex.getLocalizedMessage();
		}
	}
}
