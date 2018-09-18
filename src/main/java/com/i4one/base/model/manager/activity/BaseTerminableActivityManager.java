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
package com.i4one.base.model.manager.activity;

import com.i4one.base.dao.activity.ActivityRecordType;
import com.i4one.base.model.ActivityType;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.terminable.TerminableRecordTypeDelegator;

/**
 * @author Hamid Badiozamani
 * @param <U> The activity record type
 * @param <T> The activity type
 * @param <V> The owner type
 */
public abstract class BaseTerminableActivityManager<U extends ActivityRecordType, T extends ActivityType<U,V>, V extends TerminableRecordTypeDelegator<?>> extends BaseActivityManager<U, T, V> implements ActivityManager<U,T,V>
{
	@Override
	protected ReturnType<T> createInternal(T response)
	{
		T prevResponse = getActivity(response.getActionItem(), response.getUser());

		if ( prevResponse.exists() )
		{
			// The event was previously played, we can set the pre and post
			// to be the same object to indicate that no change took place
			//
			ReturnType<T> prevPlayed = new ReturnType<>();

			prevPlayed.setPre(prevResponse);
			prevPlayed.setPost(prevResponse);

			return prevPlayed;
		}
		else
		{
			if ( !response.getActionItem().isValidAt(response.getTimeStampSeconds()) )
			{
				// The event has expired by the time we reached this point
				//
				ReturnType<T> expired = new ReturnType<>();

				expired.setPre(response);
				expired.setPost(response);

				return expired;
			}
			else
			{
				return super.createInternal(response);
			}
		}
	}
}
