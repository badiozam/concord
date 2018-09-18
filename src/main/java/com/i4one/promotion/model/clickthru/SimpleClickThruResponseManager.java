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
import com.i4one.base.model.manager.activity.BaseActivityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleClickThruResponseManager extends BaseActivityManager<ClickThruResponseRecord, ClickThruResponse, ClickThru> implements ClickThruResponseManager
{
	/** Minimum cooldown between clicks */
	private static final int MIN_COOLDOWN = 3;

	private ClickThruManager clickThruManager;

	@Override
	public ClickThruResponse emptyInstance()
	{
		return new ClickThruResponse();
	}

	@Override
	protected ReturnType<ClickThruResponse> createInternal(ClickThruResponse clickThruResponse)
	{
		// We ensure that the accurate time stamp is reflected
		//
		clickThruResponse.setQuantity(1);

		// Handled by the superclass
		//clickThruResponse.setTimeStampSeconds(Utils.currentTimeSeconds());

		ClickThruResponse prevResponse = getActivity(clickThruResponse.getClickThru(), clickThruResponse.getUser());
		ClickThruResponseRecord lockedRecord = null;

		if ( prevResponse.exists() )
		{
			// We want to make sure we have a lock on that record and go off of that locked item
			// for any checks. If the item didn't exist, there's no reason to lock.
			//
			lockedRecord = lock(prevResponse);
			prevResponse = new ClickThruResponse(lockedRecord);
		}

		// In order to avoid double/triple clicks, we test for to ensure a minimum cooldown period has passed
		// before considering the attempt to be separate from previous ones.
		//
		if ( prevResponse.exists() && 
			prevResponse.getTimeStampSeconds() + MIN_COOLDOWN >= clickThruResponse.getTimeStampSeconds() )
		{
			// The clickThru was previously played, we can set the pre and post
			// to be the same object to indicate that no change took place
			//
			ReturnType<ClickThruResponse> prevPlayed = new ReturnType<>();

			prevPlayed.setPre(prevResponse);
			prevPlayed.setPost(prevResponse);

			return prevPlayed;
		}
		else
		{
			// We do this to ensure that all qualifiers (such as client ownership) are satisfied.
			// This prevents situations where a user can attempt a click thru directly by serial
			// number on a different client
			//
			if ( !clickThruResponse.getClickThru().isValidAt(clickThruResponse.getTimeStampSeconds()) )
			{
				getLogger().debug("Click thru is no longer valid");

				// The clickThru has expired by the time we reached this point
				//
				ReturnType<ClickThruResponse> expired = new ReturnType<>();

				// We set both to the clickThruResponse which should not exist
				// at this point, the reason we do this is to preserve the user
				// and item id that was attempted for other managers
				//
				expired.setPre(clickThruResponse);
				expired.setPost(clickThruResponse);

				return expired;
			}
			else if ( prevResponse.exists() )
			{
				getLogger().debug("Click thru is valid incrementing the previous response " + prevResponse);

				// Updated the previous response
				//
				clickThruResponse.setSer(prevResponse.getSer());
				clickThruResponse.setQuantity(prevResponse.getQuantity() + 1);

				// Note that at this point, since we haven't called clickThruResponse.loadedVersion()
				// all of the previous data (i.e. timestamp) is still preserved

				return super.updateInternal(lockedRecord, clickThruResponse);
			}
			else
			{
				getLogger().debug("Click thru is valid creating response " + clickThruResponse);

				return super.createInternal(clickThruResponse);
			}
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClickThruResponse> update(ClickThruResponse clickThruResponse)
	{
		throw new UnsupportedOperationException("Can't update a clickThru response.");
	}

	@Override
	public ClickThruResponseRecordDao getDao()
	{
		return (ClickThruResponseRecordDao) super.getDao();
	}

	public ClickThruManager getClickThruManager()
	{
		return clickThruManager;
	}

	@Autowired
	public void setClickThruManager(ClickThruManager clickThruManager)
	{
		this.clickThruManager = clickThruManager;
	}
}
