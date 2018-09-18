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
package com.i4one.base.model.manager.pagination;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.core.JSONSerializable;
import com.i4one.base.core.Pair;
import com.i4one.base.dao.qualifier.ColumnQualifier;
import java.io.Serializable;
import java.util.Collection;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * @author Hamid Badiozamani
 */
public interface PaginationFilter extends Serializable, JSONSerializable
{
	public Pair<String, MapSqlParameterSource> configureSQL(String sql, MapSqlParameterSource sqlParams);

	public int getCurrentPage();
	public void setCurrentPage(int currentPage);

	public int getPerPage();
	public void setPerPage(int perPage);

	@JsonIgnore
	public int getTotalPages();
	public void setTotalPages(int totalPages);

	public String getOrderBy();
	public void setOrderBy(String orderBy);

	public boolean getNextPage();
	public void setNextPage(boolean nextPage);

	public boolean getPrevPage();
	public void setPrevPage(boolean prevPage);

	@JsonIgnore
	public ColumnQualifier getColumnQualifier();

	/**
	 * Apply this pagination filter to the given collection of items.
	 * 
	 * @param <T> The specific collection type of item being processed
	 * @param collection The collection of items to process
	 * 
	 * @return A collection of items that match the parameters of this
	 * 	pagination filter.
	 */
	public <T extends Collection<?>> T apply(T collection);

	@JsonIgnore
	public int getOffset();
	public void setOffset(int offset);

	@JsonIgnore
	public int getLimit();
	public void setLimit(int limit);
}
