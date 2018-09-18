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
package com.i4one.base.tests.model.manager;

import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.tests.core.BaseTest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class PaginationFilterTest extends BaseTest
{
	private PaginationFilter filter;

	@Before
	public void init()
	{
		filter = new SimplePaginationFilter();
	}

	@Test
	public void testJSON() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		filter.setPerPage(100);
		filter.setCurrentPage(5);

		String json = filter.toJSONString();
		assertNotNull(json);

		SimplePaginationFilter fromJSON = new SimplePaginationFilter();
		fromJSON.fromJSONString(json);

		assertEquals(filter.toString(), fromJSON.toString());
	}
}
