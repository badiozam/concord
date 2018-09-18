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
package com.i4one.base.dao.qualifier;

import java.util.Map;

/**
 * @author Hamid Badiozamani
 */
public interface ColumnQualifier
{
	/**
	 * Build the SQL translation of the condition
	 * 
	 * @return The SQL string representing the condition
	 */
	public String buildSQL();

	/**
	 * Returns a list of qualifier names and their corresponding values.
	 * This is in essence the value map that will be used for named
	 * parameter mapping
	 * 
	 * @return The map of all qualifiers
	 */
	public Map<String, Object> getAllQualifiers();

	/**
	 * Get the current value of a given qualifier by name.
	 * 
	 * @param name The name of the qualifier to look up.
	 * 
	 * @return  The value of the qualifier or null if not found.
	 */
	public Object getQualifier(String name);

	/**
	 * Set a qualifier to the given value. Null values remove the qualifier
	 * from the list.<br/>
	 * <br/>
	 * The key parameter is interpreted as the name of the value and is typically
	 * set to be the column name that is being qualified. Since the key must be
	 * unique, in cases where multiple qualifications are necessary for the same
	 * column, the Qualifier class will contain the column name whereas this key
	 * will uniquely identify each individual qualification.<br/>
	 * <br/>
	 * The value parameter is either a Qualifier type to be consulted when building
	 * the qualification or a simple type that implies direct equality.
	 * 
	 * @param key The qualifier key.
	 * @param value The qualifier value.
	 */
	public void setQualifier(String key, Object value);
}
