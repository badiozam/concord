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
package com.i4one.base.tests.core;

import com.i4one.base.dao.SerRowMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Hamid Badiozamani
 */
@Ignore
public class JdbcBaseDaoTest extends BaseDaoTest
{
	private int iterations;
	private SerRowMapper serRowMapper;

	@Before
	public void setUp()
	{
		iterations = 100000;
		serRowMapper = new SerRowMapper();

		getJdbcTemplate().update("CREATE TABLE test (ser serial, item int, str varchar(256));");
	}

	@Test
	public void testInsertTimes()
	{
		long startTm = System.currentTimeMillis();
		getLogger().debug("Starting insert x" + iterations + " with key return @ " + startTm);

		for (int i = 0; i < iterations; i++ )
		{
			getJdbcTemplate().query("INSERT INTO test (item, str) VALUES (" + i + ",'Item #" + i + "') RETURNING ser;", serRowMapper);
		}

		long endTm = System.currentTimeMillis();
		getLogger().debug("Ended insert x" + iterations + " with key return @ " + endTm);

		double totalTime = endTm - startTm;
		double perItem = totalTime / (iterations * 1.0f);
		getLogger().debug("Total time for insert with keys: " + totalTime + " => " + perItem);
	}

	@Test
	public void testInsertTimesNoKey()
	{
		long startTm = System.currentTimeMillis();
		getLogger().debug("Starting insert x" + iterations + " with @ " + startTm);

		for (int i = 0; i < iterations; i++ )
		{
			getJdbcTemplate().update("INSERT INTO test (item, str) VALUES (" + i + ",'Item #" + i + "');");
		}
		long endTm = System.currentTimeMillis();
		getLogger().debug("Ended insert x" + iterations + " @ " + endTm);

		double totalTime = endTm - startTm;
		double perItem = totalTime / (iterations * 1.0f);
		getLogger().debug("Total time for insert: " + totalTime + " => " + perItem);
	}

	@After
	public void cleanUp()
	{
		getJdbcTemplate().update("DROP TABLE test;");
	}

	public JdbcTemplate getJdbcTemplate()
	{
		return jdbcTemplate;
	}
}
