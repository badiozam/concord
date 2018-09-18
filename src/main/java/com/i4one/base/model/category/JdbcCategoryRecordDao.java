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
package com.i4one.base.model.category;

import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * @author Hamid Badiozamani
 * @param <U>
 */
public abstract class JdbcCategoryRecordDao<U extends CategoryRecord> extends BaseCategoryJdbcDao<U> implements CategoryRecordDao<U>
{
	@Override
	public U getByTitle(String title, PaginationFilter pagination)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("title", title);

		return querySingle("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE title = :title", sqlParams, pagination);
	}

	@Override
	public List<U> getAllCategories(PaginationFilter pagination)
	{
		return query("SELECT * FROM " + getEmpty().getFullTableName(), new MapSqlParameterSource(), pagination);
	}

	/**
	 * Tests whether there are any objects in the given table having a categoryid
	 * value of the given item.
	 * 
	 * @param item The category to test
	 * @param tableName The table containing member objects
	 * 
	 * @return True if there are items belonging to the category, false otherwise
	 */
	protected boolean isEmptyImpl(U item, String tableName)
	{
		return isEmptyImpl(item, tableName, "categoryid");
	}

	/**
	 * Tests whether there are any objects in the given table having a categoryid
	 * value of the given item.
	 * 
	 * @param item The category to test
	 * @param tableName The table containing member objects
	 * @param categoryColumn The name of the category column in the member objects table
	 * 
	 * @return True if there are items belonging to the category, false otherwise
	 */
	protected boolean isEmptyImpl(U item, String tableName, String categoryColumn)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("categoryid", item.getSer());

		return querySingleGeneric("SELECT ser FROM " + tableName + " WHERE " + categoryColumn + " = :categoryid", sqlParams, new SingleColumnRowMapper<Integer>()) == null;

	}
	
}
