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
package com.i4one.rewards.model.prize;

import com.i4one.base.model.BaseSiteGroupType;
import com.i4one.base.model.SiteGroupType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class Prize extends BaseSiteGroupType<PrizeRecord> implements SiteGroupType<PrizeRecord>
{
	public Prize()
	{
		super(new PrizeRecord());
	}

	protected Prize(PrizeRecord delegate)
	{
		super(delegate);
	}
	
	@Override
	protected void init()
	{
		super.init();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getTitle().isBlank() )
		{
			retVal.addError(new ErrorMessage("title", "msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".title.empty", "Prize title cannot be empty", new Object[]{"item", this}));
		}

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		if ( !exists() )
		{
			// If we're creating then we need to make sure the current
			// inventory matches the initial
			//
			setCurrentInventory(getInitialInventory());
		}
	}

	public IString getTitle()
	{
		return getDelegate().getTitle();
	}

	public void setTitle(IString title)
	{
		getDelegate().setTitle(title);
	}

	public IString getDescr()
	{
		return getDelegate().getDescr();
	}

	public void setDescr(IString descr)
	{
		getDelegate().setDescr(descr);
	}

	public String getThumbnailURL()
	{
		return getDelegate().getThumbnailurl();
	}

	public void setThumbnailURL(String title)
	{
		getDelegate().setThumbnailurl(title);
	}

	public String getDetailPicURL()
	{
		return getDelegate().getDetailpicurl();
	}

	public void setDetailPicURL(String detailpicurl)
	{
		getDelegate().setDetailpicurl(detailpicurl);
	}

	public int getInitialInventory()
	{
		return getDelegate().getInitinventory();
	}

	public void setInitialInventory(int initInventory)
	{
		getDelegate().setInitinventory(initInventory);
	}

	public int getCurrentInventory()
	{
		return getDelegate().getCurrinventory();
	}

	public void setCurrentInventory(int currInventory)
	{
		getDelegate().setCurrinventory(currInventory);
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getClient(false).getSer() + "-" + getTitle().hashCode() + "-" + getInitialInventory();
	}
}
