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

import com.i4one.base.core.Utils;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for all JDBC DAOs
 * 
 * @author Hamid Badiozamani
 * 
 * @param <U> The record type to retrieve using JDBC
 */
public abstract class BaseJdbcDao<U extends RecordType> extends NamedParameterJdbcDaoSupport implements Dao<U>
{
	private DataSource i4oneDataSource;

	private CacheManager cacheManager;

	private SimpleJdbcInsert jdbcInsert;

	// We use a single instance of the mapper. Since these classes are stateless,
	// their instances can be shared successfully across multiple requests
	//
	private RecordTypeRowMapper<U> mapper;
	private SerRowMapper serRowMapper;

	// Instantiate an empty instance
	//
	private U empty;

	// The cache we use for direct serial number lookups
	//
	private Cache cache;

	/**
	 * Initialize a stateless row mapper for use with this Dao
	 *
	 * @return A new stateless row mapper
	 */
	protected abstract RecordTypeRowMapper<U> initMapper();

	/**
	 * Initializer method called by the bean initializer
	 */
	@PostConstruct
	public void init()
	{
		// Get around the final method of DaoSupport
		//
		setDataSource(i4oneDataSource);

		cache = getCacheManager().getCache("daoCache");
		mapper = initMapper();
		empty = mapper.emptyInstance();

		CachedRowMapper cachedMapper = new CachedRowMapper();
		cachedMapper.setMapper(mapper);
		cachedMapper.setCache(cache);

		// Use caching
		//
		mapper = cachedMapper;

		jdbcInsert = new SimpleJdbcInsert(getDataSource()).withTableName(empty.getFullTableName());
		jdbcInsert.usingGeneratedKeyColumns(new String[] { "ser" });
	}

	/**
	 * Queries for a set of objects using the given query
	 *
	 * @param query The query to execute
	 * @param sqlParams The parameters to use in the query
	 *
	 * @return A (potentially) empty list of items retrieved from the database
	 */
	public List<U> query(String query, MapSqlParameterSource sqlParams)
	{
		return queryInternal(query, 0, 0, getOrderBy(), sqlParams, getMapper());
	}

	/**
	 * Queries for a set of objects using the given query and parameters
	 *
	 * @param query The query to execute
	 * @param offset The number of rows to offset
	 * @param limit The maximum number of rows to return
	 * @param orderBy The key(s) to sort the result by
	 * @param sqlParams The parameters to use in the query
	 * @param mapper The mapper object that converts the ResultSet values to objects
	 *
	 * @return A (potentially) empty list of items retrieved from the database
	 */
	protected List<U> queryInternal(String query, int offset, int limit, String orderBy, MapSqlParameterSource sqlParams, RecordTypeRowMapper<U> mapper)
	{
		return directQuery(Utils.makeSQLwithOffset(query, offset, limit, orderBy, true), sqlParams, mapper);
	}

	/**
	 * Queries for a set of objects without applying sort order
	 * 
	 * @param <U> The type of the objects to retrieve
	 * @param query The query to execute
	 * @param sqlParams The parameters to use in the query
	 * @param mapper The mapper object that converts the ResultSet values to objects
	 *
	 * @return A (potentially) empty list of items retrieved from the database
	 */
	protected <U extends Object> List<U> directQuery(String query, MapSqlParameterSource sqlParams, RowMapper<U> mapper)
	{
		getLogger().trace("Direct Query: " + query + " w/ map " + Utils.toCSV(sqlParams.getValues().values()));
		return getNamedParameterJdbcTemplate().query(query, sqlParams, mapper);
	}

	/**
	 * Queries for a single object using the given query and parameters.
	 *
	 * @param query The query to execute
	 * @param sqlParams The parameters to use in the query
	 *
	 * @return The first entry of items retrieved from the database or null if the query
	 * 	returned no objects
	 */
	public U querySingle(String query, MapSqlParameterSource sqlParams)
	{
		return querySingleGeneric(query, sqlParams, getMapper());
	}

	/**
	 * Queries for a generic single object using the given query and parameters.
	 *
	 * @param <U> The type of the object to retrieve
	 * @param query The query to execute
	 * @param sqlParams The parameters to use in the query
	 * @param mapper The mapper object that converts the ResultSet values to objects
	 *
	 * @return The first entry of items retrieved from the database or null if the query
	 * 	returned no objects
	 */
	public <U extends Object> U querySingleGeneric(String query, MapSqlParameterSource sqlParams, RowMapper<U> mapper)
	{
		List<U> retVal = directQuery(Utils.makeSQLwithOffset(query, 0, 1, null, true), sqlParams, mapper);

		// See if there were any results returned
		//
		if ( !retVal.isEmpty())
		{
			// Return the first element even if there were multiple results returned
			//
			return retVal.get(0);
		}
		else
		{
			getLogger().trace("Returning null for " + query + " w/ params = " + Utils.toCSV(sqlParams.getValues().entrySet()));
			return null;
		}
	}

	protected void cacheEvict(int ser)
	{
		cache.evict(makeKey(ser));
	}

	@Override
	public U getBySer(int ser)
	{
		return getBySer(ser, false);
	}

	//@CacheEvict(value = "daoCache", key = "target.makeKey(#ser)", condition = "#forUpdate",  beforeInvocation = true)
	@Override
	public U getBySer(int ser, boolean forUpdate)
	{
		U retVal;

		if ( !forUpdate )
		{
			retVal = cache.get(makeKey(ser), (Class<U>) empty.getClass());
			if ( retVal == null )
			{
				retVal = getBySerInternal(ser, forUpdate);
			}
		}
		else
		{
			cacheEvict(ser);
			retVal = getBySerInternal(ser, forUpdate);
		}


		try
		{
			// If we're going to use caching we have to return a clone
			// of the object since outside sources are to own the returned
			// value. Therefore, we lose any guarantee that the cached
			// item correctly portrays the original.
			//
			if ( retVal != null )
			{
				retVal = (U) retVal.clone();
			}

			return retVal;
		}
		catch (CloneNotSupportedException ex)
		{
			getLogger().debug("Couldn't clone: ", ex);
			return null;
		}
	}
	
	private U getBySerInternal(int ser, boolean forUpdate)
	{
		String forUpdateStr = forUpdate ? " FOR UPDATE" : "";

		if ( ser > 0 )
		{
			getLogger().trace("Getting object " + getClass().getSimpleName() + " by serial number " + ser);

			// Create a map of name-value pairs for the query
			//
			MapSqlParameterSource sqlParams = new MapSqlParameterSource();
			sqlParams.addValue("ser", ser);

			// Look for the record, note that any caching is done by the mapper
			//
			List<U> retValArray = getNamedParameterJdbcTemplate().query("SELECT * FROM " + getEmpty().getFullTableName() + " WHERE ser = :ser" + forUpdateStr, sqlParams, getMapper());

			// See if there were any records found
			//
			if ( !retValArray.isEmpty())
			{
				// Return the first element even if there were multiple results returned
				// (which of course, shouldn't happen)
				//
				U retVal = retValArray.iterator().next();
				return retVal;
			}
		}

		// No such record exists
		//
		return null;
	}

	//@CacheEvict(value = "daoCache", key = "target.makeKey(#ser)")
	@Transactional(readOnly = false)
	@Override
	public void deleteBySer(int ser)
	{
		getLogger().trace("Deleting object " + getClass().getSimpleName() + " by serial number " + ser);

		// Create a map of name-value pairs for the query
		//
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("ser", ser);

		// Delete the record
		//
		getNamedParameterJdbcTemplate().update("DELETE FROM " + getEmpty().getFullTableName() + " WHERE ser = :ser", sqlParams);
		cacheEvict(ser);
	}

	@Transactional(readOnly = false)
	@Override
	public void updateBySer(U o)
	{
		// This eviction happens in the more generalized updateBySer(T o, String... columnList) method
		// cacheEvict(o.getSer());

		MapSqlParameterSource sqlParams = getMapper().getSqlParameterSource(o);
		updateBySer(o, getColumnList(sqlParams));
	}

	protected String[] getColumnList(MapSqlParameterSource sqlParams)
	{
		Map<String, Object> columnMap = sqlParams.getValues();
		String[] columnList = new String[columnMap.size()];

		int i = 0;
		for ( String key : columnMap.keySet())
		{
			columnList[i] = key;
			i++;
		}

		return columnList;
	}

	@Transactional(readOnly = false)
	@Override
	public void updateBySer(U o, String... columnList)
	{
		cacheEvict(o.getSer());

		MapSqlParameterSource sqlParams = getMapper().getSqlParameterSource(o);

		StringBuilder columnListSQL = new StringBuilder();
		int i = 0;

		for ( String currColumn : columnList )
		{
			if ( i > 0 )
			{
				columnListSQL.append(",");
			}

			columnListSQL.append(currColumn).append("=:").append(currColumn);
			i++;
		}

		getLogger().trace("UPDATE " + getEmpty().getFullTableName() + " SET " + columnListSQL.toString() + " WHERE ser = :ser w/ sqlParams = " + sqlParams.getValues());
		getNamedParameterJdbcTemplate().update("UPDATE " + getEmpty().getFullTableName() + " SET " + columnListSQL.toString() + " WHERE ser = :ser", sqlParams);

		// If we make it this far, the contents of the database should be
		// reflecting the contents of this object. As a result, an extra
		// database hit is unncessary.
		//
		o.setLoaded(true);
	}

	@Transactional(readOnly = false)
	@Override
	public void insert(U o)
	{
		cacheEvict(o.getSer());

		MapSqlParameterSource sqlParams = getMapper().getSqlParameterSource(o);

		StringBuilder columnListSQLNames = new StringBuilder();
		int i = 0;

		String[] columnList = getColumnList(sqlParams);
		for ( String currColumn : columnList )
		{
			if ( i > 0 )
			{
				columnListSQLNames.append(",");
			}
			columnListSQLNames.append(currColumn);

			i++;
		}

		StringBuilder columnListSQLValues = new StringBuilder();
		i = 0;
		for ( String currColumn : columnList )
		{
			if ( i > 0 )
			{
				columnListSQLValues.append(",");
			}

			columnListSQLValues.append(":").append(currColumn);
			i++;
		}


		getLogger().trace("INSERT INTO " + getEmpty().getFullTableName() + " (" + columnListSQLNames.toString() + ") VALUES (" + columnListSQLValues.toString() + ") w/ sqlParams = " + sqlParams);
		List<Integer> createdSers = getNamedParameterJdbcTemplate().query("INSERT INTO " + getEmpty().getFullTableName() + " (" + columnListSQLNames.toString() + ") VALUES (" + columnListSQLValues.toString() + ") RETURNING ser", sqlParams, serRowMapper);

		Integer createdSer = createdSers.iterator().next();
		o.setSer(createdSer);

		// If we make it this far, the contents of the database should be
		// reflecting the contents of this object. As a result, an extra
		// database hit is unncessary.
		//
		o.setLoaded(true);
	}

	@Override
	public String getOrderBy()
	{
		return "ser";
	}

	@Override
	public void processReport(U sample, TopLevelReport report)
	{
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public void processReport(U sample, ClassificationReport<U, ?> report, PaginationFilter pagination)
	{
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	public List<U> getByOffset(String orderby, int offset, int limit)
	{
		getLogger().trace("Getting object " + getClass().getName() + " by offset " + offset + ", limit " + limit + "ordered by " + orderby);

		return getNamedParameterJdbcTemplate().query("SELECT * FROM " + getEmpty().getFullTableName() + " ORDER BY " + Utils.sqlEscape(orderby) + " LIMIT " + limit + " OFFSET " + offset + ";", new MapSqlParameterSource(), initMapper());
	}

	/**
	 * Returns a uniquely identifying string for the target type
	 * 
	 * @param ser The serial number of the object
	 * 
	 * @return  A string that uniquely identifies an object of the target type
	 */
	public String makeKey(Object ser)
	{
		if ( ser instanceof Number)
		{
			Number n = (Number)ser;
			return empty.makeKey(n.intValue());
		}
		else
		{
			return "null";
		}
	}

	protected RecordTypeRowMapper<U> getMapper()
	{
		return mapper;
	}

	protected SimpleJdbcInsert getJdbcInsert()
	{
		return jdbcInsert;
	}

	protected Log getLogger()
	{
		return logger;
	}

	public U getEmpty()
	{
		return empty;
	}

	public DataSource getI4oneDataSource()
	{
		return i4oneDataSource;
	}

	@Autowired
	public void setI4oneDataSource(DataSource i4oneDataSource)
	{
		this.i4oneDataSource = i4oneDataSource;
	}

	public CacheManager getCacheManager()
	{
		return cacheManager;
	}

	@Autowired
	public void setCacheManager(CacheManager cacheManager)
	{
		this.cacheManager = cacheManager;
	}

	public SerRowMapper getIntColumnMapper()
	{
		return serRowMapper;
	}

	@Autowired
	public void setIntColumnMapper(SerRowMapper serRowMapper)
	{
		this.serRowMapper = serRowMapper;
	}

}