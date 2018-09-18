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
package com.i4one.base.model.manager.terminable;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.terminable.TerminableSiteGroupRecordType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.SiteGroupType;
import com.i4one.base.model.Errors;
import com.i4one.base.model.SimpleSiteGroupType;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.i18n.IString;
import java.util.List;

/**
 * Provides basic implementation for a TerminableSiteGroupType. Inherits behavior
 * from TerminableSingleType and adds client group implementation through composition.
 * 
 * @author Hamid Badiozamani
 */
public abstract class BaseTerminableSiteGroupType<U extends TerminableSiteGroupRecordType> extends BaseTerminableSingleClientType<U> implements TerminableSiteGroupType<U>
{
	private transient SimpleSiteGroupType<U, SiteGroupType<U>> siteGroupType;

	public BaseTerminableSiteGroupType(U delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( siteGroupType == null )
		{
			siteGroupType = new SimpleSiteGroupType(this);
		}
		siteGroupType.init();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getTitle().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".title.empty", "Title cannot be empty", new Object[]{"item", this}));
		}

		retVal.merge(siteGroupType.validate());

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		siteGroupType.setOverrides();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		siteGroupType.actualizeRelations();
	}

	@Override
	public IString getTitle()
	{
		return getDelegate().getTitle();
	}

	@Override
	public void setTitle(IString title)
	{
		getDelegate().setTitle(title);
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		if ( super.fromCSVInternal(csv) )
		{
			setTitle(new IString(csv.get(0))); csv.remove(0);

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected StringBuilder toCSVInternal(boolean header)
	{
		StringBuilder retVal = super.toCSVInternal(header);
		
		if ( header )
		{
			// XXX: Needs to be i18n
			retVal.append("Title").append(",");
		}
		else
		{
			retVal.append("\"").append(Utils.csvEscape(getTitle().toString())).append("\",");
		}
		
		return retVal;
	}

	@Override
	public SiteGroup getSiteGroup()
	{
		return siteGroupType.getSiteGroup();
	}
	
	public SiteGroup getSiteGroup(boolean doLoad)
	{
		return siteGroupType.getSiteGroup(doLoad);
	}

	@Override
	public void setSiteGroup(SiteGroup siteGroup)
	{
		siteGroupType.setSiteGroup(siteGroup);
	}
}