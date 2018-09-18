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
package com.i4one.promotion.model.clickthru;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.BaseTransactionalActivityManager;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("promotion.ClickThruResponseManager")
public class TransactionalClickThruResponseManager extends BaseTransactionalActivityManager<ClickThruResponseRecord, ClickThruResponse, ClickThru> implements ClickThruResponseManager
{
	private ClickThruResponseManager clickThruResponseManager;

	@Override
	public ClickThruResponseManager getImplementationManager()
	{
		return getClickThruResponseManager();
	}

	protected void processClickThruTransaction(ReturnType<ClickThruResponse> retClickThruResponse)
	{
		ClickThruResponse pre = retClickThruResponse.getPre();
		ClickThruResponse post = retClickThruResponse.getPost();

		Transaction t = newTransaction(post.getUser());

		// We don't record these, because they're likely just double-clicks
		//
		if ( post.exists() && pre.equals(post) && pre.getQuantity() == post.getQuantity() )
		{
			// Previously played
			//
			t.setStatus(Transaction.FAILED);

			// No recording previously played for click thrus since they may
			// be played as long as they're valid and the prev played designation
			// is only used to prevent accidental double-clicks with the
			// MIN_COOLDOWN constant in SimpleClickThruResponseManager
			//
		}
		else if ( post.exists() )
		{
			// The message is responsible for displaying the proper expired status
			//
			setTransactionDescr(t, "msg.clickThruResponseManager.create.xaction.descr", "clickThruResponse", post);

			// See if we can locate a user balance update transaction and set us
			// as the the parent transaction
			//
			t = createTransaction(retClickThruResponse, t);

			// Set our master transaction record since we've created a new transaction
			//
			retClickThruResponse.put("transaction", t);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClickThruResponse> create(ClickThruResponse clickThruResponse)
	{
		ReturnType<ClickThruResponse> retVal = getClickThruResponseManager().create(clickThruResponse);

		processClickThruTransaction(retVal);

		return retVal;
	}

	public ClickThruResponseManager getClickThruResponseManager()
	{
		return clickThruResponseManager;
	}

	@Autowired
	@Qualifier("promotion.PrivilegedClickThruResponseManager")
	public void setClickThruResponseManager(ClickThruResponseManager clickThruResponseManager)
	{
		this.clickThruResponseManager = clickThruResponseManager;
	}
}
