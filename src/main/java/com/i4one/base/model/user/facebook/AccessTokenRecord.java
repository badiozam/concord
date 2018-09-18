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
package com.i4one.base.model.user.facebook;

import com.i4one.base.dao.BaseClientRecordType;
import com.i4one.base.dao.ClientRecordType;

/**
 * @author Hamid Badiozamani
 */
public class AccessTokenRecord extends BaseClientRecordType implements ClientRecordType
{
	static final long serialVersionUID = 42L;

	private Integer userid;

	private String fbid;
	private String accesstoken;
	private String state;
	private String redir;
	private String code;

	private Integer expiration;
	private Integer timestamp;

	public AccessTokenRecord()
	{
		userid = 0;
		expiration = 0;
		timestamp = 0;

		fbid = "";
		state = "";
		redir = "";
		code= "";
		accesstoken = "";
	}

	@Override
	public String getTableName()
	{
		return "facebooktokens";
	}

	public Integer getUserid()
	{
		return userid;
	}

	public void setUserid(Integer userid)
	{
		this.userid = userid;
	}

	public String getFbid()
	{
		return fbid;
	}

	public void setFbid(String fbid)
	{
		this.fbid = fbid;
	}

	public String getAccesstoken()
	{
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken)
	{
		this.accesstoken = accesstoken;
	}

	public Integer getExpiration()
	{
		return expiration;
	}

	public void setExpiration(Integer expiration)
	{
		this.expiration = expiration;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getRedir()
	{
		return redir;
	}

	public void setRedir(String redir)
	{
		this.redir = redir;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public Integer getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Integer timestamp)
	{
		this.timestamp = timestamp;
	}

}
