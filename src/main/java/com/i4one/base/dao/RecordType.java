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

import com.i4one.base.core.JSONSerializable;
import java.io.Serializable;

/**
 * @author Hamid Badiozamani
 */
public interface RecordType extends Serializable, JSONSerializable
{
	public RecordType clone() throws CloneNotSupportedException;

	/**
	 * Tests whether the record exists in the database or not.
	 * 
	 * @return True if the record exists, false otherwise
	 */
	boolean exists();

	/**
	 * Tests whether the record exists in the database or not.
	 * 
	 * @return True if the record exists, false otherwise
	 */
	boolean getExists();

	/**
	 * Constructs the full table name that stores this record.
	 * 
	 * @return The full table name in the form of schema.table
	 */
	String getFullTableName();

	/**
	 * Gets the database schema that the table holding this record belongs to.
	 * 
	 * @return The record type's schema
	 */
	String getSchemaName();

	/**
	 * Sets the schema name of the table that holds this record
	 * 
	 * @param schemaName The new schema name
	 */
	void setSchemaName(String schemaName);

	/**
	 * Get the database table holding this record.
	 * 
	 * @return The table name
	 */
	String getTableName();

	/**
	 * Set the name of the table holding this record
	 * 
	 * @param tableName The new table name
	 */
	void setTableName(String tableName);

	/**
	 * Tests whether the record has been loaded from the database or not.
	 * 
	 * @return True if the record has been loaded, false otherwise
	 */
	boolean isLoaded();

	/**
	 * Sets the loaded flag indicating whether this record has been loaded
	 * from the database or not.
	 * 
	 * @param isLoaded The new flag.
	 */
	void setLoaded(boolean isLoaded);

	/**
	 * Make a unique identifying key for this type of record with the given serial number.
	 * 
	 * @param ser The serial number to use when creating a unique key
	 * 
	 * @return A string uniquely identifying a record in the database with the given serial number.
	 */
	String makeKey(int ser);

	/**
	 * Make a unique identifying key for this record. This method has the same effect as
	 * calling makeKey(getSer());
	 * 
	 * @return A string uniquely identifying this record in the database
	 */
	String makeKey();

	/**
	 * Get the unique serial number of this record
	 * 
	 * @return This record's serial number
	 */
	int getSer();

	/**
	 * Set the serial number of this record. If the serial number is
	 * different than before, the rest of the record is loaded from
	 * the database.
	 * 
	 * @param ser The new serial number
	 */
	void setSer(int ser);
}
