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
package com.i4one.base.model.userlogin;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.triggerable.BaseTriggeredActivityManager;
import com.i4one.base.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class TriggeredUserLoginManager extends BaseTriggeredActivityManager<UserLoginRecord, UserLogin, SingleClient> implements UserLoginManager
{
	private UserLoginManager userLoginManager;

	@Override
	public User getUser(UserLogin item)
	{
		return item.getUser(false);
	}

	@Override
	public RecordTypeDelegator<?> getAttachee(UserLogin userLogin)
	{
		return userLogin.getClient();
	}

	@Override
	public UserLoginManager getImplementationManager()
	{
		return getUserLoginManager();
	}

	@Override
	protected void processAttached(ReturnType<UserLogin> retUserLogin, String methodName)
	{
		UserLogin pre = retUserLogin.getPre();
		UserLogin post = retUserLogin.getPost();

		if (
			(post.exists() && pre.equals(post) && pre.getQuantity() == post.getQuantity() ) 	||	// Previously played
			(!post.exists())				// Failed/Expired
		)
		{
			getLogger().debug("Triggers not processed for " + retUserLogin.getPost() + " and user " + retUserLogin.getPost().getUser(false));
		}
		else
		{
			super.processAttached(retUserLogin, methodName);
		}

	}

	public UserLoginManager getUserLoginManager()
	{
		return userLoginManager;
	}

	@Autowired
	@Qualifier("base.SimpleUserLoginManager")
	public void setUserLoginManager(UserLoginManager userLoginManager)
	{
		this.userLoginManager = userLoginManager;
	}
}
