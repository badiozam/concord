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
package com.i4one.base.model.sms;

import com.i4one.base.core.Base;
import static com.i4one.base.core.Base.getInstance;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class NexmoResponse
{
	static final long serialVersionUID = 42L;

	private List<NexmoMessage> messages;

	public List<NexmoMessage> getMessages()
	{
		return messages;
	}

	public void setMessages(List<NexmoMessage> messages)
	{
		this.messages = messages;
	}

	public boolean isSuccessful()
	{
		boolean retVal = true;
		for ( NexmoMessage currMessage : messages )
		{
			retVal &= currMessage.isSuccessful();
		}

		return retVal;
	}

	@Override
	public String toString()
	{
		return getInstance().getGson().toJson(this);
	}

}
