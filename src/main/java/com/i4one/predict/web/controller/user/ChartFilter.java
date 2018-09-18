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
package com.i4one.predict.web.controller.user;

import com.i4one.base.dao.qualifier.SimpleComparisonQualifier;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;

/**
 * @author Hamid Badiozamani
 */
public class ChartFilter extends SimplePaginationFilter
{
	static final long serialVersionUID = 42L;

	private int termid;

	public ChartFilter()
	{
		super();

		construct();
	}

	public ChartFilter(PaginationFilter chain)
	{
		super(chain);

		construct();
	}

	private void construct()
	{
		// Modified for test per Reg: 8/2/2017
		//
		//setOrderBy("correct");
		//setQualifier("totalGreaterThanZero", new SimpleComparisonQualifier("total", ">", 0));

		termid = 0;
		setOrderBy("total");
	}

	@Override
	public final void setOrderBy(String orderBy)
	{
		// Force the sort columns to be descending
		// 
		if (orderBy.startsWith("score") )
		{
			super.setOrderBy("scorerank, correct DESC, winstreakrank, ser");
		}
		else if ( orderBy.startsWith("correct") )
		{
			super.setOrderBy("correct DESC, winstreakrank, scorerank, ser");
		}
		else if ( orderBy.startsWith("winstreak") )
		{
			super.setOrderBy("winstreakrank, correct DESC, scorerank, ser");
		}
		else if ( orderBy.startsWith("total"))
		{
			super.setOrderBy("total DESC, correct DESC, scorerank, ser");
		}
		else // if ( orderBy.startsWith("accuracy") )
		{
			super.setOrderBy("accuracyrank, correct DESC, scorerank, ser");
		}
	}

	public int getTermid()
	{
		return termid;
	}

	public void setTermid(int termid)
	{
		this.termid = termid;
	}

}
