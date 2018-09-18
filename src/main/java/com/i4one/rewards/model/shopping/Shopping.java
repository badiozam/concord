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
package com.i4one.rewards.model.shopping;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.expendable.TerminableExpendableClientType;
import com.i4one.rewards.model.BaseTerminableRewardsClientType;
import com.i4one.rewards.model.TerminableRewardsClientType;
import com.i4one.rewards.model.shopping.category.ShoppingCategory;

/**
 * A shopping reward opportunity.
 * 
 * @author Hamid Badiozamani
 */
public class Shopping extends BaseTerminableRewardsClientType<ShoppingRecord, Shopping, ShoppingCategory> implements TerminableRewardsClientType<ShoppingRecord, ShoppingCategory>,TerminableExpendableClientType<ShoppingRecord, Shopping>
{
	static final long serialVersionUID = 42L;

	public Shopping()
	{
		super(new ShoppingRecord());
	}

	protected Shopping(ShoppingRecord delegate)
	{
		super(delegate);
	}

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getOutro().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".outro.empty", "Outro cannot be empty", new Object[]{"item", this}));
		}

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();
	}

	public IString getIntro()
	{
		return getDelegate().getIntro();
	}

	public void setIntro(IString intro)
	{
		getDelegate().setIntro(intro);
	}

	public IString getOutro()
	{
		return getDelegate().getOutro();
	}

	public void setOutro(IString intro)
	{
		getDelegate().setOutro(intro);
	}

	public int getOrderWeight()
	{
		return getDelegate().getOrderweight();
	}

	public void setOrderWeight(int orderweight)
	{
		getDelegate().setOrderweight(orderweight);
	}

	public int getUserLimit()
	{
		return getDelegate().getUserlimit();
	}

	public void setUserLimit(int userLimit)
	{
		getDelegate().setUserlimit(userLimit);
	}

	public boolean isSoldOutDisplay()
	{
		return getDelegate().getSoldoutdisplay();
	}

	public boolean getSoldOutDisplay()
	{
		return getDelegate().getSoldoutdisplay();
	}

	public void setSoldOutDisplay(boolean soldOutDisplay)
	{
		getDelegate().setSoldoutdisplay(soldOutDisplay);
	}

	public boolean getSoldOut()
	{
		return isSoldOut();
	}

	public boolean isSoldOut()
	{
		return (getInitialReserve() == 0 ? getPrize().getCurrentInventory() == 0 : getCurrentReserve() == 0);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle() + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
	}

	@Override
	protected ShoppingCategory initCategory(int categoryid)
	{
		ShoppingCategory retVal = new ShoppingCategory();
		retVal.setSer(categoryid);
		retVal.loadedVersion();

		return retVal;
	}
}
