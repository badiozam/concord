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

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.instantwinnable.BaseInstantWinningManager;
import com.i4one.base.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class InstantWinningAccessCodeResponseManager extends BaseInstantWinningManager<AccessCodeResponseRecord, AccessCodeResponse, AccessCode> implements AccessCodeResponseManager
{
	private AccessCodeResponseManager accessCodeResponseManager;

	@Override
	public User getUser(AccessCodeResponse item)
	{
		return item.getUser();
	}

	@Override
	public RecordTypeDelegator<?> getAttachee(AccessCodeResponse accessCodeResponse)
	{
		return accessCodeResponse.getAccessCode();
	}

	@Override
	public AccessCodeResponseManager getImplementationManager()
	{
		return getAccessCodeResponseManager();
	}

	@Override
	protected void processAttached(ReturnType<AccessCodeResponse> retAccessCodeResponse, String methodName)
	{
		// We only process the instant wins if the accessCode was successfully processed for the first time
		//
		AccessCodeResponse pre = retAccessCodeResponse.getPre();
		AccessCodeResponse post = retAccessCodeResponse.getPost();

		if (
			(post.exists() && pre.equals(post)) 	||	// Previously played
			(!post.exists())				// Failed/Expired
		)
		{
			getLogger().debug("Instant wins not processed for " + retAccessCodeResponse.getPost().getAccessCode() + " and user " + retAccessCodeResponse.getPost().getUser(false) + " since the accessCode was previously played or expired");
		}
		else
		{
			super.processAttached(retAccessCodeResponse, methodName);
		}

	}

	public AccessCodeResponseManager getAccessCodeResponseManager()
	{
		return accessCodeResponseManager;
	}

	@Autowired
	@Qualifier("base.TriggeredAccessCodeResponseManager")
	public void setAccessCodeResponseManager(AccessCodeResponseManager accessCodeResponseManager)
	{
		this.accessCodeResponseManager = accessCodeResponseManager;
	}

}
