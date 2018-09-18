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
package com.i4one.rewards.web.controller.user.shoppings;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.WebModel;
import com.i4one.rewards.model.shopping.ShoppingPurchase;

/**
 * @author Hamid Badiozamani
 */
public class WebModelShoppingPurchase extends ShoppingPurchase implements WebModel
{
	private Model model;
	
	@Override
	public void init()
	{
		super.init();

		if ( getQuantity() == 0 )
		{
			setQuantity(1);
		}
	}
	
	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		getLogger().debug("Validating shopping purchase for shopping " + getShopping());
		if ( !getShopping().exists() )
		{
			errors.addError(new ErrorMessage("msg.rewards.ShoppingResponse.shopping.invalidShopping", "This shopping item does not exist", new Object[]{"item", this}));
		}
		else if ( !getShopping().isLive(getModel().getTimeInSeconds()) )
		{
			errors.addError(new ErrorMessage("msg.rewards.ShoppingResponse.shopping.notLive", "This shopping item is not currently available", new Object[]{"item", this}));
		}

		// The user has to be set
		//
		if ( !getUser().exists() )
		{
			errors.addError(new ErrorMessage("msg.rewards.shoppingPurchaseManager.create.userdne", "You must be logged in", new Object[] { "user", getUser()}, null));
		}

		return errors;
	}

	public String getConfirmationNumber()
	{
		return getSer() + "-" + getTimeStampSeconds();
	}

	@Override
	public Model getModel()
	{
		return model;
	}

	@Override
	public void setModel(Model model)
	{
		this.model = model;
	}
}
