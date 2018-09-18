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

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Utils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * This class manages all of the DAO objects that are to be used
 * in the application
 *
 * @author Hamid Badiozamani
 * @deprecated Use spring to get DAOs
 */
@Service
public class SimpleDaoManager extends BaseLoggable implements DaoManager
{
	// Used for retrieving the dao class of a given record type
	//
	private static final String DAO_SUFFIX = "Dao";

	// A map of dao names to instances
	//
	private final Map<String, Dao<? extends RecordType>> daoMap;

	public SimpleDaoManager()
	{
		daoMap = new HashMap<>();
	}

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
		getLogger().trace("Looking up DAO " + daoName);

		Dao<? extends RecordType> internal = daoMap.get(daoName);
		if ( internal != null )
		{
			getLogger().trace("Found instance of " + daoName + " return cloned version");
			return internal;
		}
		else
		{
			getLogger().trace("Instance of " + daoName + " not found");
			return null;
		}
	}

	/**
	 * Gets a new DAO object by an object instead of name
	 *
	 * @param <T> The type of object being retrieved by the DAO
	 * @param t The (likely generic) object instance of the DAO we're looking for
	 *
	 * @return The new DAO or null if not found
	 */
	@Override
	public <T extends RecordType> Dao<T> getNewDao(T t)
	{
		return getNewDao(t.getClass());
	}

	/**
	 * Gets a new DAO object by a class instead of name
	 *
	 * @param <T> The type of object being retrieved by the DAO
	 * @param  clazz The class of the DAO we're looking for
	 *
	 * @return The new DAO or null if not found
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T extends RecordType> Dao<T> getNewDao(Class clazz)
	{
		String daoName = getDaoName(clazz);

		return (Dao<T>) getNewDao(daoName);
	}

	@SuppressWarnings("rawtypes")
	private String getDaoName(Class clazz)
	{
		//String className = clazz.getSimpleName();

		String canonicalName = clazz.getCanonicalName();
		String[] nameComponents = canonicalName.split("\\.");

		getLogger().trace("canonicalName = " + canonicalName + " => " + Utils.toCSV(nameComponents));

		String schema = "";
		if ( nameComponents.length > 2 )
		{
			// com.i4one.<schema>.model...<className>
			//
			schema = nameComponents[2];
		}

		String className = nameComponents[nameComponents.length - 1];

		//return "jdbc" + className + DAO_SUFFIX;
		return schema + ".Jdbc" + className + DAO_SUFFIX;
	}

	/**
	 * Set a list of DAO objects
	 *
	 * @param daoList The list of DAO objects to store
	 */
	public void setDaoMap(Map<String, Dao<? extends RecordType>> daoList)
	{
		//for ( Map.Entry<String, Dao<? extends BaseRecordType>> dao : daoList.entrySet() )
		daoList.entrySet().stream().forEach( (dao) ->
		{
			String daoName = dao.getKey();

			getLogger().debug("Adding " + daoName + " w/ ref = " + dao);
			daoMap.put(daoName, dao.getValue());
		});
	}

}
