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
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.transaction.TransactionManager;
import com.i4one.base.model.user.User;
import com.i4one.base.web.RequestState;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseTransactionalManager<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseDelegatingManager<U,T> implements TransactionalManager<U, T>
{
	private TransactionManager transactionManager;
	private MessageManager messageManager;

	// The request scope state
	//
	private RequestState requestState;

	/**
	 * Instantiates and initializes a new transaction object for the given user.
	 * A status of successful, the incoming request's IP address and the request
	 * time is used to initialize the transaction object
	 * 
	 * @param user The user for whom the transaction will belong
	 * 
	 * @return A newly initialized transaction for the given user
	 */
	protected Transaction newTransaction(User user)
	{
		Transaction retVal = new Transaction();
		retVal.setTimeStampSeconds(getRequestState().getRequest().getTimeInSeconds());
		retVal.setStatus(Transaction.SUCCESSFUL);
		retVal.setSourceIP(getRequestState().getRequest().getRemoteAddr());

		retVal.setUser(user);

		// We need to use the request state's client
		//
		retVal.setClient(getRequestState().getSingleClient());

		return retVal;
	}

	/**
	 * Set the transaction description for the given object. The transaction's description
	 * is an IString which will contain all of the request client's supported languages
	 * built messages. The arguments used to build each message will have a "xaction" key
	 * with the value being that of the transaction parameter that is passed in.
	 * 
	 * @param transaction The transaction whose description we are to set
	 * @param key The message key for building the description string
	 * @param args The arguments to pass to the message builder
	 */
	protected void setTransactionDescr(Transaction transaction, String key, Object... args)
	{
		IString descr = new IString();

		SingleClient client = getRequestState().getSingleClient();
		List<String> langs = client.getLanguageList();

		for ( String lang : langs )
		{
			Map<String, Object> argMap = Utils.makeMap(args);
			argMap.put("xaction", transaction);
			argMap.put("request", getRequestState().getRequest());
			argMap.put("model", getRequestState().getModel());

			descr.set(lang, getMessageManager().buildMessage(client, key, lang, argMap));
		}

		transaction.setDescr(descr);
	}

	protected Transaction createTransaction(ReturnType<?> callChainReturns, Transaction transaction)
	{
		ReturnType<Transaction> createdXaction = getTransactionManager().create(transaction);

		if ( callChainReturns != null )
		{
			setTransactionChainOwnership(callChainReturns, transaction);
			callChainReturns.put("transaction", createdXaction.getPost());
		}

		return createdXaction.getPost();
	}

	/**
	 * Goes through the given ReturnType's call chain and makes the given transaction
	 * the parent of all of their transactions. Note that the parent transaction must
	 * already exist.
	 * 
	 * @param callChainReturns The call chain to go through
	 * @param parentTransaction The parent transaction
	 */
	protected void setTransactionChainOwnership(ReturnType<?> callChainReturns, Transaction parentTransaction)
	{
		if ( parentTransaction.exists() )
		{
			// First things first, we check our own return value
			//
			getLogger().trace("Setting parent transaction to {} for {}", parentTransaction, callChainReturns.keySet());
			setParentTransaction(callChainReturns, parentTransaction);
			getLogger().trace("Done setting parent transaction to {} for {}", parentTransaction, callChainReturns);
		}
		else
		{
			throw new IllegalArgumentException("Non-existent parent transaction: " + parentTransaction);
		}
	}

	/**
	 * This method allows subclasses to modify a future child transaction.
	 * The default implementation does not modify the child transaction at
	 * all.
	 * 
	 * @param child The future child transaction
	 * @param parentTransaction The parent transaction which will own this child
	 * 
	 * @return The child transaction 
	 */
	protected Transaction decorateChildTransaction(Transaction child, Transaction parentTransaction)
	{
		return child;
	}

	private void setParentTransaction(Object txObject, Transaction parentTransaction)
	{
		getLogger().trace("Considering object " + txObject);

		if ( txObject instanceof Transaction )
		{
			getLogger().trace("Object is a Transaction setting its parent to {}", parentTransaction);

			Transaction tx = (Transaction)txObject;
			getTransactionManager().setParentTransaction(decorateChildTransaction(tx, parentTransaction), parentTransaction);
		}
		else if ( txObject instanceof ReturnType<?> )
		{
			ReturnType<?> currRetVal = (ReturnType<?>)txObject;

			getLogger().trace("Object is a ReturnType w/ keys {}, recursing through values", currRetVal.keySet());

			// Maybe the return value is a transaction object, too
			//
			setParentTransaction(currRetVal.getPost(), parentTransaction);

			// Check all internal objects of the map as well
			//
			currRetVal.values().stream().forEach((currObj) ->
			{
				setParentTransaction(currObj, parentTransaction);
			});

			getLogger().trace("Object is a ReturnType, recursing through chains");

			// Check all of our chains as well
			//
			currRetVal.getAllChains().stream().forEach((currRetValChains) ->
			{
				setParentTransaction(currRetValChains, parentTransaction);
			});

			getLogger().trace("Object is a ReturnType, recursion done");
		}
		else if ( txObject instanceof Collection<?> )
		{
			getLogger().trace("Object is a collection, traversing");

			// Traverse the collection looking for any internal ReturnType's,
			// Collections or transactions
			//
			Collection<?> currRetValList = (Collection<?>)txObject;

			currRetValList.stream().forEach((currObj) ->
			{
				setParentTransaction(currObj, parentTransaction);
			});

			getLogger().trace("Object is a collection, traversal done");
		}
	}

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	public TransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Autowired
	public void setTransactionManager(TransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}
}
