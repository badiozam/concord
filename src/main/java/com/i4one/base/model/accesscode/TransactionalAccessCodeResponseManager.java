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
package com.i4one.base.model.accesscode;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.BaseTransactionalActivityManager;
import com.i4one.base.model.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("base.AccessCodeResponseManager")
public class TransactionalAccessCodeResponseManager extends BaseTransactionalActivityManager<AccessCodeResponseRecord, AccessCodeResponse, AccessCode> implements AccessCodeResponseManager
{
	private AccessCodeResponseManager accessCodeResponseManager;

	@Override
	public AccessCodeResponseManager getImplementationManager()
	{
		return getAccessCodeResponseManager();
	}

	protected void processCodeTransaction(ReturnType<AccessCodeResponse> retCodeResponse)
	{
		AccessCodeResponse pre = retCodeResponse.getPre();
		AccessCodeResponse post = retCodeResponse.getPost();

		Transaction t = newTransaction(post.getUser());

		if ( !post.exists() || (post.exists() && pre.equals(post)) )
		{
			// Previously played
			//
			t.setStatus(Transaction.FAILED);
		}

		// The message is responsible for displaying the proper previously played status
		//
		setTransactionDescr(t, "msg.accessCodeResponseManager.create.xaction.descr", "codeResponse", post, "pre", pre);

		if ( !pre.exists() )
		{
			// Only record a transaction if a previous access code
			// response record didn't exist
			//
			t = createTransaction(retCodeResponse, t);

			// Set our master transaction record since we've created a new transaction
			//
			retCodeResponse.put("transaction", t);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<AccessCodeResponse> create(AccessCodeResponse codeResponse)
	{
		ReturnType<AccessCodeResponse> retVal = getAccessCodeResponseManager().create(codeResponse);

		processCodeTransaction(retVal);

		return retVal;
	}

	public AccessCodeResponseManager getAccessCodeResponseManager()
	{
		return accessCodeResponseManager;
	}

	@Autowired
	@Qualifier("base.InstantWinningAccessCodeResponseManager")
	public void setAccessCodeResponseManager(AccessCodeResponseManager accessCodeResponseManager)
	{
		this.accessCodeResponseManager = accessCodeResponseManager;
	}
}
