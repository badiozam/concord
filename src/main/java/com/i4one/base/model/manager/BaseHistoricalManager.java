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

import static com.i4one.base.core.Base.getInstance;
import static com.i4one.base.core.Utils.currentTimeSeconds;
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.adminhistory.AdminHistory;
import com.i4one.base.model.adminhistory.AdminHistoryRecordDao;
import com.i4one.base.model.adminprivilege.Privilege;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.web.RequestState;
import java.util.Collection;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseHistoricalManager<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseDelegatingManager<U,T> implements HistoricalManager<U, T>
{
	private MessageManager messageManager;

	// The request scope state
	//
	private RequestState requestState;

	@Override
	protected void initInternal(Map<String, Object> options)
	{
		// This is the only option that matters for us
		//
		if ( options.containsKey(HistoricalManager.ATTR_RECORDHISTORYENABLED))
		{
			getRequestState().set(ATTR_RECORDHISTORYENABLED, options.get(ATTR_RECORDHISTORYENABLED));
		}
	}

	/**
	 * Creates a new AdminHistoryRecord to be created for a given method
	 *
	 * @param before The object before the action took place
	 * @param after The object after the action took place
	 * @param methodName The method performing the action
	 *
	 * @return An admin history record for the given action
	 */
	protected AdminHistory newAdminHistory(T before, T after, String methodName)
	{
		SingleClient client = getRequestState().getSingleClient();

		// We'll skip recording objects that are the same reference, we'll also not record
		// anything if an administrator is not specified. The privileged managers are to
		// maintain security, if they say it's ok not to have an admin, we won't stop them
		// but we can't record history either.
		//
		// This is useful for cases where a manager can update items that can also be updated
		// by users themselves
		//
		if ( getAdmin().exists() && before != after && isRecordHistoryEnabled() )
		{
			AdminHistory history = new AdminHistory();
			history.setAdmin(getAdmin());
			history.setBefore(before);
			history.setAfter(after);
			history.setAction(methodName);
			history.setClient(client);
			history.setFeature(getFeatureName());
			history.setFeatureID(before, after);
			history.setSourceIP(getRequestState().getRequest().getRemoteAddr());
			history.setTimeStampSeconds(currentTimeSeconds());

			// Note that history records are stored in the client's default language
			//
			history.setDescr(getMessageManager().buildMessage(client,
				"msg." + getFeatureName() + "." + methodName + ".xaction.descr",
				getRequestState().getModel().getLanguage(),
				"before", before,
				"after", after));

			if ( !history.getBefore().equals(history.getAfter()) )
			{
				getLogger().debug("Saving record: " + history);

				// Only save to the database if the before and after are
				// different, otherwise we can assume no change has been
				// made to the objects
				//
				getAdminHistoryRecordDao().insert(history.getDelegate());
			}
			else
			{
				getLogger().debug("Skipping history record because " + history.getBefore() + " is equal to " + history.getAfter());
			}

			return history;
		}
		else
		{
			getLogger().debug("Skipping history record because admin " + getAdmin() + "  doesn't exist or records " + before + " and " + after + " are the same, or history recording is disabled: " + !isRecordHistoryEnabled());
			return new AdminHistory();
		}
	}

	/**
	 * Goes through the given ReturnType's call chain and makes the given history record
	 * the parent of all of their history records. Note that the parent history records must
	 * already exist for this call to have any effect.
	 * 
	 * @param callChainReturns The call chain to go through
	 * @param parent The parent history record
	 */
	protected void setHistoryChainOwnership(ReturnType<?> callChainReturns, AdminHistory parent)
	{
		if ( parent.exists() )
		{
			setParentTransaction(callChainReturns, parent);
		}
	}

	private void setParentTransaction(Object historyObject, AdminHistory parent)
	{
		getLogger().debug("Considering object " + historyObject);

		if ( historyObject instanceof AdminHistory )
		{

			AdminHistory history = (AdminHistory)historyObject;
			getLogger().debug("Object is a history record checking whether it exists and if it has an existing parent");

			if ( history.exists() && !history.getParent().exists() )
			{
				getLogger().debug("History record has no existing parent, setting the parent to " + parent);

				history.setParent(parent);

				getAdminHistoryRecordDao().updateBySer(history.getDelegate());
			}
		}
		else if ( historyObject instanceof ReturnType<?> )
		{
			ReturnType<?> currRetVal = (ReturnType<?>)historyObject;

			getLogger().debug("Object is a ReturnType with value " + currRetVal.getPost());

			// Maybe the return value is a history object, too
			//
			setParentTransaction(currRetVal.getPost(), parent);

			// Check all internal objects of the map as well
			//
			currRetVal.values().stream().forEach((currObj) ->
			{
				setParentTransaction(currObj, parent);
			});

			// Check all of our chains as well
			//
			currRetVal.getAllChains().stream().forEach((currRetValChains) ->
			{
				setParentTransaction(currRetValChains, parent);
			});
		}
		else if ( historyObject instanceof Collection<?> )
		{
			getLogger().debug("Object is a list, traversing");

			// Traverse the list looking for any internal ReturnType's, Lists or transactions
			//
			Collection<?> currRetValList = (Collection<?>)historyObject;

			currRetValList.stream().forEach((currObj) ->
			{
				setParentTransaction(currObj, parent);
			});
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		ReturnType<T> retVal = getImplementationManager().create(item);

		AdminHistory adminHistory = newAdminHistory(getImplementationManager().emptyInstance(), item, "create");
		setHistoryChainOwnership(retVal, adminHistory);

		retVal.put(ATTR_ADMINHISTORY, adminHistory);

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		ReturnType<T> retVal = getImplementationManager().update(item);

		AdminHistory adminHistory = newAdminHistory(retVal.getPre(), retVal.getPost(), "update");
		setHistoryChainOwnership(retVal, adminHistory);

		retVal.put(ATTR_ADMINHISTORY, adminHistory);

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public T remove(T item)
	{
		T retVal = getImplementationManager().remove(item);

		AdminHistory adminHistory = newAdminHistory(retVal, getImplementationManager().emptyInstance(), "remove");
		//setHistoryChainOwnership(retVal, adminHistory);

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> clone(T item)
	{
		ReturnType<T> retVal = getImplementationManager().clone(item);

		AdminHistory adminHistory = newAdminHistory(item, retVal.getPost(), "clone");
		setParentTransaction(retVal, adminHistory);

		retVal.put(ATTR_ADMINHISTORY, adminHistory);

		return retVal;
	}

	@Override
	public Admin getAdmin()
	{
		return getImplementationManager().getAdmin();
	}

	@Override
	public SingleClient getClient(T item)
	{
		return getImplementationManager().getClient(item);
	}

	@Override
	public String getFeatureName()
	{
		return getImplementationManager().getFeatureName();
	}

	@Override
	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	@Override
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}

	@Override
	public Privilege getReadPrivilege()
	{
		return getImplementationManager().getReadPrivilege();
	}

	@Override
	public Privilege getWritePrivilege()
	{
		return getImplementationManager().getWritePrivilege();
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager readOnlyMessageManager)
	{
		this.messageManager = readOnlyMessageManager;
	}

	public AdminHistoryRecordDao getAdminHistoryRecordDao()
	{
		return (AdminHistoryRecordDao) getInstance().getDaoManager().getNewDao("base.JdbcAdminHistoryRecordDao");
	}

	private boolean isRecordHistoryEnabled()
	{
		Boolean retVal = (Boolean)getRequestState().get(ATTR_RECORDHISTORYENABLED);
		if ( retVal != null )
		{
			return retVal;
		}
		else
		{
			return true;
		}
	}
}
