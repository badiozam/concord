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

import com.i4one.base.dao.BaseClientRecordType;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.dao.UsageRecordType;

/**
 * This record contains all of the values stored in the users table. The UsageRecordType is
 * implemented to indicate registration of a new user.
 *
 * @author Hamid Badiozamani
 */
public class UserRecord extends BaseClientRecordType implements ClientRecordType,UsageRecordType
{
	static final long serialVersionUID = 42L;

	private String username;
	private String password;
	private String email;

	private String firstname;
	private String lastname;
	private String street;
	private String city;
	private String state;
	private String zipcode;
	private String cellphone;
	private String homephone;

	private Integer status;
	private Character gender;
	private Boolean isMarried;

	private Boolean forceUpdate;
	private Boolean canEmail;
	private Boolean canSMS;
	private Boolean canCall;

	private Integer birthmm;
	private Integer birthdd;
	private Integer birthyyyy;

	private Integer lastlogintime;
	private Integer createtime;

	private Integer lastbirthyear;

	public UserRecord()
	{
		status = 0;

		forceUpdate = false;
		canEmail = false;
		canCall = false;
		canSMS = false;

		lastbirthyear = 0;

		// Have to keep gender null in order to allow updates to work
		// by not copying over gender
		//
		// gender = ' ';

		/*
		username = "";
		password = "";
		email = "";
		firstname = "";
		lastname = "";
		street = "";
		city = "";
		state = "";
		zipcode = "";
		cellphone = "";
		homephone = "";
			*/
	}

	@Override
	public String getTableName()
	{
		return "users";
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getFirstname()
	{
		return firstname;
	}

	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}

	public Integer getStatus()
	{
		return status;
	}

	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public Character getGender()
	{
		return gender;
	}

	public void setGender(Character gender)
	{
		this.gender = gender;
	}

	public Boolean getIsMarried()
	{
		return isMarried;
	}

	public void setIsMarried(Boolean isMarried)
	{
		this.isMarried = isMarried;
	}

	public String getLastname()
	{
		return lastname;
	}

	public void setLastname(String lastname)
	{
		this.lastname = lastname;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
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

	public String getHomephone()
	{
		return homephone;
	}

	public void setHomephone(String homephone)
	{
		this.homephone = homephone;
	}

	public String getCellphone()
	{
		return cellphone;
	}

	public void setCellphone(String cellphone)
	{
		this.cellphone = cellphone;
	}

	public Boolean getForceUpdate()
	{
		return forceUpdate;
	}

	public void setForceUpdate(Boolean forceUpdate)
	{
		this.forceUpdate = forceUpdate;
	}

	public Boolean getCanCall()
	{
		return canCall;
	}

	public void setCanCall(Boolean canCall)
	{
		this.canCall = canCall;
	}

	public Boolean getCanEmail()
	{
		return canEmail;
	}

	public void setCanEmail(Boolean canEmail)
	{
		this.canEmail = canEmail;
	}

	public Boolean getCanSMS()
	{
		return canSMS;
	}

	public void setCanSMS(Boolean canSMS)
	{
		this.canSMS = canSMS;
	}

	public Integer getCreatetime()
	{
		return createtime;
	}

	public void setCreatetime(Integer createTimeSeconds)
	{
		this.createtime = createTimeSeconds;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public Integer getLastlogintime()
	{
		return lastlogintime;
	}

	public void setLastlogintime(Integer lastLoginTimeSeconds)
	{
		this.lastlogintime = lastLoginTimeSeconds;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public Integer getBirthmm()
	{
		return birthmm;
	}

	public void setBirthmm(Integer birthmm)
	{
		this.birthmm = birthmm;
	}

	public Integer getBirthdd()
	{
		return birthdd;
	}

	public void setBirthdd(Integer birthdd)
	{
		this.birthdd = birthdd;
	}

	public Integer getBirthyyyy()
	{
		return birthyyyy;
	}

	public void setBirthyyyy(Integer birthyyyy)
	{
		this.birthyyyy = birthyyyy;
	}

	public Integer getLastbirthyear()
	{
		return lastbirthyear;
	}

	public void setLastbirthyear(Integer lastbirthyear)
	{
		this.lastbirthyear = lastbirthyear;
	}

	@Override
	public Integer getTimestamp()
	{
		return getCreatetime();
	}

	@Override
	public void setTimestamp(Integer timestamp)
	{
		// Silently ignore, we don't throw an exception because the copyProperties
		// method will still call this when copying over items
	}

	@Override
	public Integer getUserid()
	{
		return getSer();
	}

	@Override
	public void setUserid(Integer userid)
	{
		// Silently ignore, we don't throw an exception because the copyProperties
		// method will still call this when copying over items
	}
}