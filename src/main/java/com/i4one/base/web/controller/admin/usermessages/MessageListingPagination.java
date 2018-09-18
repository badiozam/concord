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
package com.i4one.base.web.controller.admin.usermessages;

import com.i4one.base.dao.qualifier.NotNullValue;
import com.i4one.base.dao.qualifier.NullValue;
import com.i4one.base.model.usermessage.UserMessage;
import com.i4one.base.web.controller.TerminableSortableListingPagination;

/**
 * @author Hamid Badiozamani
 */
public class MessageListingPagination extends TerminableSortableListingPagination<UserMessage>
{
	private boolean showPrivateMessages;

	public MessageListingPagination()
	{
		super();

		showPrivateMessages = false;
		initQualifiers();
	}

	public boolean getShowPrivateMessages()
	{
		return isShowPrivateMessages();
	}

	public boolean isShowPrivateMessages()
	{
		return showPrivateMessages;
	}

	public void setShowPrivateMessages(boolean showPrivateMessages)
	{
		this.showPrivateMessages = showPrivateMessages;
		initQualifiers();
	}

	private void initQualifiers()
	{
		if ( isShowPrivateMessages() )
		{
			getPagination().getColumnQualifier().setQualifier("userid", new NotNullValue());
		}
		else
		{
			getPagination().getColumnQualifier().setQualifier("userid", new NullValue());
		}
	}
}
