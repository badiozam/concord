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
package com.i4one.base.model.instantwin;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.triggerable.BaseTriggeredActivityManager;
import com.i4one.base.model.user.User;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("base.UserInstantWinManager")
public class TriggeredUserInstantWinManager extends BaseTriggeredActivityManager<UserInstantWinRecord, UserInstantWin, InstantWin> implements UserInstantWinManager
{
	private UserInstantWinManager userInstantWinManager;

	@Override
	public User getUser(UserInstantWin item)
	{
		return item.getUser(false);
	}

	@Override
	public RecordTypeDelegator<?> getAttachee(UserInstantWin userInstantWin)
	{
		return userInstantWin.getInstantWin();
	}

	@Override
	public UserInstantWinManager getImplementationManager()
	{
		return getUserInstantWinManager();
	}

	@Override
	public boolean hasWon(InstantWin instantWin, User user)
	{
		return getUserInstantWinManager().hasWon(instantWin, user);
	}

	@Override
	public Set<UserInstantWin> getByUser(User user, PaginationFilter pagination)
	{
		return getUserInstantWinManager().getByUser(user, pagination);
	}

	@Override
	public Set<UserInstantWin> getByInstantWin(InstantWin instantWin, PaginationFilter pagination)
	{
		return getUserInstantWinManager().getByInstantWin(instantWin, pagination);
	}

	@Override
	public Set<UserInstantWin> getAllUserInstantWins(InstantWin instantWin, User user, PaginationFilter pagination)
	{
		return getUserInstantWinManager().getAllUserInstantWins(instantWin, user, pagination);
	}

	@Override
	protected void processAttached(ReturnType<UserInstantWin> userInstantWin, String methodName)
	{
		if ( userInstantWin.getPost().exists()  && userInstantWin.getPost().getDidWin() )
		{
			super.processAttached(userInstantWin, methodName);
		}
		else
		{
			getLogger().debug("User {} did not win {}, no triggers were processed", userInstantWin.getPost().getUser(), userInstantWin.getPost().getInstantWin());
		}
	}

	@Override
	public List<ReturnType<UserInstantWin>> processInstantWins(User user, Manager<?, ?> manager, String method, RecordTypeDelegator<?> item, PaginationFilter pagination)
	{
		List<ReturnType<UserInstantWin>> retVal = getUserInstantWinManager().processInstantWins(user, manager, method, item, pagination);

		for ( ReturnType<UserInstantWin> processedIW : retVal)
		{
			processAttached(processedIW, "processInstantWins");
		}

		return retVal;
	}

	public UserInstantWinManager getUserInstantWinManager()
	{
		return userInstantWinManager;
	}

	@Autowired
	public void setUserInstantWinManager(UserInstantWinManager simpleUserInstantWinManager)
	{
		this.userInstantWinManager = simpleUserInstantWinManager;
	}

}
