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
package com.i4one.base.dao;

import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @deprecated Use spring to get DAOs
 * @author Hamid Badiozamani
 */
@Service("base.DaoManager")
public class ApplicationContextDaoManager extends SimpleDaoManager implements ApplicationContextAware
{
	private ApplicationContext context;

	/**
	 * Get a DAO by name
	 *
	 * @param daoName The name to look up
	 *
	 * @return The new DAO or null if not found
	 */
	@Override
	public Dao<? extends RecordType> getNewDao(String daoName)
	{
		Object bean = getApplicationContext().getBean(daoName);
		if ( bean instanceof Dao)
		{
			Dao<? extends RecordType> retVal = (Dao<? extends RecordType>)bean;

			return retVal;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setDaoMap(Map<String, Dao<? extends RecordType>> daoList)
	{
		throw new IllegalArgumentException("DaoMap is not used with this implementation");
	}

	public ApplicationContext getApplicationContext()
	{
		return context;
	}

	//@Autowired
	@Override
	public void setApplicationContext(ApplicationContext context)
	{
		this.context = context;
	}

}