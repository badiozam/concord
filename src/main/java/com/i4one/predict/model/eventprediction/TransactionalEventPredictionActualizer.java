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
package com.i4one.predict.model.eventprediction;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.transaction.TransactionManager;
import com.i4one.base.web.RequestState;
import com.i4one.base.web.interceptor.ClientInterceptor;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * XXX: Needs to happen in its own Quartz Job
 * 
 * @author Hamid Badiozamani
 */
@Service("predict.EventPredictionActualizer")
public class TransactionalEventPredictionActualizer extends BaseLoggable implements EventPredictionActualizer
{
	private RequestState requestState;
	private EventPredictionActualizer eventPredictionActualizer;

	private MessageManager messageManager;
	private TransactionManager transactionManager;

	@Transactional(readOnly = false)
	@Override
	public ReturnType<EventPrediction> actualize(EventPrediction ep, EventOutcome actualOutcome)
	{
		getLogger().debug("Actualize outcome called");
		ReturnType<EventPrediction> retVal = getEventPredictionActualizer().actualize(ep, actualOutcome);

		/*
		 * 
		getLogger().debug("Updating transaction record with correct description");
		Transaction t = (Transaction) retVal.get("transaction");
		t.setDescr(getMessageManager().getMessage(ep.getUser().getClient(), "msg.eventPredictionManager.actualizeOutcome.xaction.descr").buildMessage("xaction", t, "eventPrediction", ep));
		getTransactionRecordDao().save(t.getDelegate());

		return retVal;
		 */

		// This is our transaction that reflects the creation of a new prediction record
		//
		//
		// Which client should the transaction go under?
		//
		//SingleClient client = ep.getUser().getClient();
		//SingleClient client = ep.getEvent().getClient(true);
		SingleClient client = ClientInterceptor.getSingleClient(getRequestState().getRequest());

		Transaction t = new Transaction();
		t.setClient(client);

		// If all goes well, this will be the actual outcome and it will be needed in building the message
		// which is why we set it here
		//
		ep.getEvent().setActualOutcome(actualOutcome);

		setTransactionDescr(client, t, "msg.eventPredictionManager.actualizeOutcome.xaction.descr", "eventPrediction", ep);
		t.setSourceIP(getRequestState().getRequest().getRemoteAddr());
		t.setStatus(Transaction.SUCCESSFUL);
		t.setUser(ep.getUser());

		// See if we can locate a user balance update transaction and set us
		// as the the parent transaction
		//
		t.setTimeStampSeconds(getRequestState().getRequest().getTimeInSeconds());
		getTransactionManager().create(t);

		// Set us as the parent for all of the child chain transactions
		//
		getLogger().debug("Found " + retVal.getAllChains().size() + " chains");
		for ( ReturnType<?> currRetVal : retVal.getAllChains() )
		{
			Object txObject = currRetVal.get("transaction");
			if ( txObject instanceof Transaction )
			{
				Transaction tx = (Transaction)txObject;

				tx.setDescr(new IString(""));
				getTransactionManager().setParentTransaction(tx, t);
			}
			else
			{
				getLogger().debug("No 'transaction' item found for " + Utils.toCSV(currRetVal.entrySet()));
			}
		}

		// Set our master transaction record since we've created a new transaction
		//
		retVal.put("transaction", t);

		return retVal;
	}

	// XXX: Copied from BaseTransactionalManager
	private void setTransactionDescr(SingleClient client, Transaction transaction, String key, Object... args)
	{
		IString descr = new IString();

		List<String> langs = client.getLanguageList();

		for ( String lang : langs )
		{
			Map<String, Object> argMap = Utils.makeMap(args);
			argMap.put("xaction", transaction);

			descr.set(lang, getMessageManager().buildMessage(client, key, lang, argMap));
		}

		transaction.setDescr(descr);
	}

	public EventPredictionActualizer getEventPredictionActualizer()
	{
		return eventPredictionActualizer;
	}

	@Autowired
	public void setEventPredictionActualizer(EventPredictionActualizer userMessageEventPredictionActualizer)
	{
		this.eventPredictionActualizer = userMessageEventPredictionActualizer;
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

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}
}
