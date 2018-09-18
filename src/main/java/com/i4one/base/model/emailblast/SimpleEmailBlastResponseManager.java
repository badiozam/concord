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
package com.i4one.base.model.emailblast;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("base.EmailBlastResponseManager")
public class SimpleEmailBlastResponseManager extends BaseActivityManager<EmailBlastResponseRecord, EmailBlastResponse, EmailBlast> implements EmailBlastResponseManager
{
	private EmailBlastManager emailBlastManager;

	@Override
	public EmailBlastResponse emptyInstance()
	{
		return new EmailBlastResponse();
	}

	@Override
	protected ReturnType<EmailBlastResponse> createInternal(EmailBlastResponse emailBlastResponse)
	{
		// We ensure that the accurate time stamp is reflected
		//
		emailBlastResponse.setQuantity(1);

		// Handled by the superclass
		//emailBlastResponse.setTimeStampSeconds(Utils.currentTimeSeconds());

		EmailBlastResponse prevResponse = getActivity(emailBlastResponse.getEmailBlast(), emailBlastResponse.getUser());
		EmailBlastResponseRecord lockedRecord = null;

		if ( prevResponse.exists() )
		{
			// We want to make sure we have a lock on that record and go off of that locked item
			// for any checks. If the item didn't exist, there's no reason to lock.
			//
			lockedRecord = lock(prevResponse);
			prevResponse = new EmailBlastResponse(lockedRecord);
		}

		if ( prevResponse.exists() )
		{
			getLogger().debug("Incrementing the previous response " + prevResponse);

			// Updated the previous response
			//
			emailBlastResponse.setSer(prevResponse.getSer());
			emailBlastResponse.setQuantity(prevResponse.getQuantity() + 1);

			// Note that at this point, since we haven't called emailBlastResponse.loadedVersion()
			// all of the previous data (i.e. timestamp) is still preserved

			return super.updateInternal(lockedRecord, emailBlastResponse);
		}
		else
		{
			getLogger().debug("Creating response " + emailBlastResponse);

			return super.createInternal(emailBlastResponse);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<EmailBlastResponse> update(EmailBlastResponse emailResponse)
	{
		throw new UnsupportedOperationException("Can't update a email response.");
	}

	@Override
	public EmailBlastResponseRecordDao getDao()
	{
		return (EmailBlastResponseRecordDao) super.getDao();
	}

	public EmailBlastManager getEmailBlastManager()
	{
		return emailBlastManager;
	}

	@Autowired
	public void setEmailBlastManager(EmailBlastManager emailBlastManager)
	{
		this.emailBlastManager = emailBlastManager;
	}
}
