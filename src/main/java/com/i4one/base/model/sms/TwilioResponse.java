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

/**
 * @author Hamid Badiozamani
 */
public class TwilioResponse
{
	private String account_sid;
	private String api_version;
	private String body;
	private String num_segments;
	private String num_media;
	private String status;
	private String from;
	private String to;
	private String direction;
	private String sid;

	@Override
	public String toString()
	{
		return getInstance().getGson().toJson(this);
	}

	public String getAccount_sid()
	{
		return account_sid;
	}

	public void setAccount_sid(String account_sid)
	{
		this.account_sid = account_sid;
	}

	public String getApi_version()
	{
		return api_version;
	}

	public void setApi_version(String api_version)
	{
		this.api_version = api_version;
	}

	public String getBody()
	{
		return body;
	}

	public void setBody(String body)
	{
		this.body = body;
	}

	public String getNum_segments()
	{
		return num_segments;
	}

	public void setNum_segments(String num_segments)
	{
		this.num_segments = num_segments;
	}

	public String getNum_media()
	{
		return num_media;
	}

	public void setNum_media(String num_media)
	{
		this.num_media = num_media;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getTo()
	{
		return to;
	}

	public void setTo(String to)
	{
		this.to = to;
	}

	public String getDirection()
	{
		return direction;
	}

	public void setDirection(String direction)
	{
		this.direction = direction;
	}

	public String getSid()
	{
		return sid;
	}

	public void setSid(String sid)
	{
		this.sid = sid;
	}

}
