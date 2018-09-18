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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.core.Base;
import com.i4one.base.core.BaseLoggable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * This class represents an object in the database that has a serial number
 *
 * @author Hamid Badiozamani
 */
public abstract class BaseRecordType extends BaseLoggable implements Comparable<RecordType>, Cloneable, RecordType
{
	// Every type has to have a unique serial number
	//
	private int ser;

	// Whether the record has been loaded from the database or not
	//
	private transient boolean isLoaded;

	@SuppressWarnings("rawtypes")
	private transient Set<Map.Entry> beanProps;

	public BaseRecordType()
	{
		ser = 0;
	}

	@SuppressWarnings({ "unchecked","rawtypes" })
	private void init()
	{
		try
		{
			Map beanPropsMap = PropertyUtils.describe(this);
			beanPropsMap.remove("logger");

			beanProps = beanPropsMap.entrySet();
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			getLogger().debug("", e);
		}
	}


	@JsonIgnore
	@Override
	public String getSchemaName()
	{
		return "base";
	}

	@Override
	public void setSchemaName(String schemaName)
	{
		// Silently ignore since PropertyUtils might try to copy the schema name over
		//throw new UnsupportedOperationException("Can't set schema name for a concrete type");
	}

	@JsonIgnore
	@Override
	public String getFullTableName()
	{
		return getSchemaName() + "." + getTableName();
	}

	@Override
	public void setTableName(String tableName)
	{
		// Silently ignore since PropertyUtils might try to copy the table name over
		//throw new UnsupportedOperationException("Can't set table name for a concrete type");
	}

	@Override
	public String makeKey(int ser)
	{
		return getFullTableName() + ":" + ser;
	}

	@Override
	public String makeKey()
	{
		return makeKey(getSer());
	}

	@Override
	public boolean exists()
	{
		return getSer() > 0;
	}

	@Override
	public boolean getExists()
	{
		return exists();
	}

	@JsonIgnore
	@Override
	public boolean isLoaded()
	{
		return isLoaded;
	}

	@Override
	public void setLoaded(boolean isLoaded)
	{
		this.isLoaded = isLoaded;
	}

	@Override
	public final int getSer()
	{
		return ser;
	}

	@Override
	public void setSer(int val)
	{
		this.ser = val;
	}

	/**
	 * Compares two DbTypes to determine whether one is less than the other
	 * by comparing their serial numbers. The parameter given may not
	 * necessarily have to be the same type as this object
	 *
	 * @param right The object to compare to, may not necessarily be the same type
	 *	as this object
	 *
	 * @return 0 if equal, 1 if this object is greater than the right, -1 otherwise
	 */
	@Override
	public int compareTo(RecordType right)
	{
		return getSer() == right.getSer() ? 0 : ( getSer() < right.getSer() ? -1 : 1 );
	}

	/**
	 * Determines whether this object is equal to another. The type of the object
	 * given must be exactly the same as ours otherwise the method will always
	 * return false
	 *
	 * @param right The object to compare to
	 *
	 * @return true if the objects are equal, false otherwise
	 */
	@Override
	public boolean equals(Object right)
	{
		if ( right == null )
		{
			return false;
		}
		else
		{
			if ( getClass().equals(right.getClass()))
			{
				getLogger().trace("Testing equality of " + this + " vs " + right);
				return Objects.equals(getSer(), ((RecordType)right).getSer());
			}
			else
			{
				getLogger().trace(this.getClass() + " is not of the same class as " + right.getClass());
				return false;
			}
		}
	}

	/**
	 * Computes the hash code for this object based on the type and serial number. This
	 * means that all objects of the same class and same serial number are considered
	 * to be equal.
	 *
	 * @return This object's hash code
	 */
	@Override
	public int hashCode()
	{
		return getClass().hashCode() + getSer();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String toString()
	{
		StringBuilder beanPropsStr = new StringBuilder();
		try
		{
			// We might get an unintialized beanprops after deserialization
			//
			if ( beanProps == null )
			{
				init();
			}

			for ( Map.Entry currProp : beanProps )
			{
				if ( !currProp.getKey().equals("class") )
				{
					beanPropsStr.append(" ");
					beanPropsStr.append(currProp.getKey());
					beanPropsStr.append("=");
					beanPropsStr.append(PropertyUtils.getProperty(this, (String)currProp.getKey()));
				}
			}
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			beanPropsStr.append("Could not read properties: ");
			beanPropsStr.append(e);
		}

		return "[" + getClass().getSimpleName() + ":" + beanPropsStr.toString() + "]";
	}

	@Override
	public String toJSONString()
	{
		//return getInstance().getGson().toJson(this);
		try
		{
			return Base.getInstance().getJacksonObjectMapper().writeValueAsString(this);
		}
		catch (IOException ex)
		{
			return ex.getLocalizedMessage();
		}
	}

	@Override
	public void fromJSONString(String json) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		//PropertyUtils.copyProperties(this, getInstance().getGson().fromJson(json, this.getClass()));
		PropertyUtils.copyProperties(this, Base.getInstance().getJacksonObjectMapper().readValue(json, this.getClass()));
	}

	/**
	 * Clones the current object
	 *
	 * @return A new object with all of the properties of the current ones
	 * @throws CloneNotSupportedException
	 */
	@Override
	public RecordType clone() throws CloneNotSupportedException
	{
		try
		{
			// Not guaranteed to return a new object
			//
			//BaseRecordType retVal = (BaseRecordType)super.clone();
			RecordType retVal = getClass().newInstance();
			PropertyUtils.copyProperties(retVal, this);

			return retVal;
		}
		catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
		//catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
		{
			getLogger().debug("Could not clone " + this, ex);
			return null;
		}
	}
}