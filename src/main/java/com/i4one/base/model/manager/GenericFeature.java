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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.RecordTypeDelegator;

/**
 * @author Hamid Badiozamani
 */
public class GenericFeature extends BaseRecordTypeDelegator<RecordType> implements RecordTypeDelegator<RecordType>
{
	static final long serialVersionUID = 42L;

	public GenericFeature()
	{
		super(new GenericFeatureRecord(0, "base.none"));
	}

	public GenericFeature(int featureId, String featureName)
	{
		super(new GenericFeatureRecord(featureId, featureName));
	}

	public GenericFeature(RecordType delegate)
	{
		super(new GenericFeatureRecord(delegate.getSer(), delegate.getFullTableName()));
	}

	protected GenericFeature(GenericFeatureRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
	}

	@JsonIgnore
	public String getFullTableName()
	{
		return getDelegate().getFullTableName();
	}
	
	public String getTableName()
	{
		return getDelegate().getTableName();
	}

	public void setTableName(String tableName)
	{
		getDelegate().setTableName(tableName);
	}

	public String getSchemaName()
	{
		return getDelegate().getSchemaName();
	}

	public void setSchemaName(String schemaName)
	{
		getDelegate().setSchemaName(schemaName);
	}

	@JsonIgnore
	public void setFeatureName(String featureName)
	{
		GenericFeatureRecord gfr = new GenericFeatureRecord(getSer(), featureName);

		if ( !getFeatureName().equals(featureName) )
		{
			// We're switching features, which means we can't hold on to
			// the old delegate object and all bets are off as to what
			// type that object would have been
			//
			setOwnedDelegate(gfr);
		}
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getFullTableName() + ":" + getDelegate().getSer();
	}
}
