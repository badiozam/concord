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
package com.i4one.base.model.manager.triggerable;

import com.i4one.base.model.manager.attachable.BaseAttachedManager;
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balancetrigger.UserBalanceTrigger;
import com.i4one.base.model.balancetrigger.UserBalanceTriggerManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseTriggeredManager<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseAttachedManager<U, T> implements TriggeredManager<U,T>
{
	private UserBalanceTriggerManager userBalanceTriggerManager;

	@Override
	protected void processAttached(ReturnType<T> processedItem, String methodName)
	{
		T item = processedItem.getPost();
		User triggerUser = getUser(item);
		if (triggerUser.exists())
		{
			List<ReturnType<UserBalanceTrigger>> processedTriggers = getUserBalanceTriggerManager().processTriggers(triggerUser,
				getRequestState().getSingleClient(),
				this,
				methodName,
				getAttachee(item),
				new TerminablePagination(getRequestState().getRequest().getTimeInSeconds(), SimplePaginationFilter.NONE));

			processedItem.put("processedTriggers", processedTriggers);
			getLogger().debug("Processed triggers = " + processedTriggers);
		}
	}

	public UserBalanceTriggerManager getUserBalanceTriggerManager()
	{
		return userBalanceTriggerManager;
	}

	@Autowired
	public void setUserBalanceTriggerManager(UserBalanceTriggerManager userBalanceTriggerManager)
	{
		this.userBalanceTriggerManager = userBalanceTriggerManager;
	}
}