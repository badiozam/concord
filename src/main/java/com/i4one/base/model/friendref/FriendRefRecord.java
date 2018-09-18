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
package com.i4one.base.model.friendref;

import com.i4one.base.dao.BaseClientRecordType;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.dao.UsageRecordType;

/**
 * @author Hamid Badiozamani
 */
public class FriendRefRecord extends BaseClientRecordType implements ClientRecordType,UsageRecordType
{
	private Integer userid;
	private Integer friendid;
	private String email;
	private String firstname;
	private String lastname;
	private Integer timestamp;
	private String message;

	public FriendRefRecord()
	{
		super();

		userid = 0;

		email = "";
		firstname = "";
		lastname = "";
		timestamp = 0;
		message = "";
	}

	@Override
	public String getTableName()
	{
		return "friendrefs";
	}

	@Override
	public Integer getUserid()
	{
		return userid;
	}

	@Override
	public void setUserid(Integer userid)
	{
		this.userid = userid;
	}

	public Integer getFriendid()
	{
		return friendid;
	}

	public void setFriendid(Integer friendid)
	{
		this.friendid = friendid;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstname()
	{
		return firstname;
	}

	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}

	public String getLastname()
	{
		return lastname;
	}

	public void setLastname(String lastname)
	{
		this.lastname = lastname;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	@Override
	public Integer getTimestamp()
	{
		return timestamp;
	}

	@Override
	public void setTimestamp(Integer timestamp)
	{
		this.timestamp = timestamp;
	}

}
