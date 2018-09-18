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
package com.i4one.base.model.client;

import com.i4one.base.dao.BaseRecordType;

/**
 * @author Hamid Badiozamani
 */
public class SingleClientRecord extends BaseRecordType
{
	static final long serialVersionUID = 42L;

	private Integer parentid;

	private String email;
	private String name;
	private String descr;
	private String tz;
	private String languages;
	private String country;
	private String domain;
	private String operator;
	private String address;
	private String url;
	private String logourl;

	public SingleClientRecord()
	{
		email = "";
		name = "";
		descr = "";

		tz = "";
		languages = "";
		country = "";

		domain = "";

		operator = "";
		address = "";
		url = "";
		logourl = "";
	}

	@Override
	public String getTableName()
	{
		return "clients";
	}

	public String getDescr()
	{
		return descr;
	}

	public void setDescr(String descr)
	{
		this.descr = descr;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getTz()
	{
		return tz;
	}

	public void setTz(String tz)
	{
		this.tz = tz;
	}

	public String getLanguages()
	{
		return languages;
	}

	public void setLanguages(String languages)
	{
		this.languages = languages;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public String getOperator()
	{
		return operator;
	}

	public void setOperator(String operator)
	{
		this.operator = operator;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getLogourl()
	{
		return logourl;
	}

	public void setLogourl(String logourl)
	{
		this.logourl = logourl;
	}

	public Integer getParentid()
	{
		return parentid;
	}

	public void setParentid(Integer parentid)
	{
		this.parentid = parentid;
	}
}
