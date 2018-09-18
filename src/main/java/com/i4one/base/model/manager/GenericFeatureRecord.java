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
package com.i4one.base.model.manager;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.BaseRecordType;

/**
 * Placeholder that represents a database record
 * 
 * @author Hamid Badiozamani
 */
public class GenericFeatureRecord extends BaseRecordType
{
	static final long serialVersionUID = 42L;

	private String schemaName;
	private String tableName;

	public GenericFeatureRecord(int featureId, String featureName)
	{
		setSer(featureId);
		setFeatureName(featureName);
	}

	public final void setFeatureName(String featureName)
	{
		if ( featureName.contains("."))
		{
			// The feature name has to be two components
			//
			String[] featureNameComponents = featureName.split("\\.");
			schemaName = Utils.forceEmptyStr(featureNameComponents[0]);
			tableName = Utils.forceEmptyStr(featureNameComponents[1]);
		}
		else
		{
			schemaName = "";
			tableName = Utils.forceEmptyStr(featureName);
		}
	}

	@Override
	public String getTableName()
	{
		return tableName;
	}

	@Override
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	@Override
	public String getSchemaName()
	{
		return schemaName;
	}

	@Override
	public void setSchemaName(String schemaName)
	{
		this.schemaName = schemaName;
	}
}
