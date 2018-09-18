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
package com.i4one.base.model.user;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.qualifier.LikeString;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;

/**
 * @author Hamid Badiozamani
 */
public class UserSearchCriteria extends SimplePaginationFilter
{
	private String firstName;
	private String lastName;

	private String email;

	private String street;
	private String city;
	private String state;
	private String zipcode;

	private String gender;

	private int birthDay;
	private int birthMonth;
	private int birthYear;

	public UserSearchCriteria()
	{
		super();

		setLimit(1000);
	}

	@Override
	protected void initQualifiers()
	{
		super.initQualifiers();

		getColumnQualifier().setQualifier("firstname", Utils.isEmpty(getFirstName()) ? null : new LikeString(getFirstName().toLowerCase()));
		getColumnQualifier().setQualifier("lastname", Utils.isEmpty(getLastName()) ? null : new LikeString(getLastName().toLowerCase()));

		getColumnQualifier().setQualifier("email", Utils.isEmpty(getEmail()) ? null : new LikeString(getEmail().toLowerCase()));

		getColumnQualifier().setQualifier("street", Utils.isEmpty(getStreet()) ? null : new LikeString(getStreet().toLowerCase()));
		getColumnQualifier().setQualifier("city", Utils.isEmpty(getCity()) ? null : new LikeString(getCity().toLowerCase()));
		getColumnQualifier().setQualifier("state", Utils.isEmpty(getState()) ? null : new LikeString(getState().toLowerCase()));
		getColumnQualifier().setQualifier("zipcode", Utils.isEmpty(getZipcode()) ? null : new LikeString(getZipcode().toLowerCase()));

		if ( !Utils.isEmpty(gender) && !gender.equalsIgnoreCase("x") )
		{
			getColumnQualifier().setQualifier("gender", getGender().toLowerCase());
		}
		else
		{
			getColumnQualifier().setQualifier("gender", null);
		}
	}
	
	@Override
	protected String toStringInternal()
	{
		return "fn:" + getFirstName() + "ln:" + getLastName() + ",em:" + getEmail() + ",street:" + getStreet() + ",city:" + getCity() + ",state:" + getState() + ",zc:" + getZipcode() + ",g:" + getGender() + "," + super.toStringInternal();
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getZipcode()
	{
		return zipcode;
	}

	public void setZipcode(String zipcode)
	{
		this.zipcode = zipcode;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public int getBirthDay()
	{
		return birthDay;
	}

	public void setBirthDay(int birthDay)
	{
		this.birthDay = birthDay;
	}

	public int getBirthMonth()
	{
		return birthMonth;
	}

	public void setBirthMonth(int birthMonth)
	{
		this.birthMonth = birthMonth;
	}

	public int getBirthYear()
	{
		return birthYear;
	}

	public void setBirthYear(int birthYear)
	{
		this.birthYear = birthYear;
	}
}
