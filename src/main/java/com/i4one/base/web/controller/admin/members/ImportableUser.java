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
package com.i4one.base.web.controller.admin.members;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.user.User;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class ImportableUser extends User
{
	private Integer pointBalance;

	@Override
	public void init()
	{
		super.init();

		pointBalance = 0;
	}

	@Override
	public Errors validate()
	{
		return Errors.NO_ERRORS;
	}
	public Integer getPointBalance()
	{
		return pointBalance;
	}

	public void setPointBalance(Integer pointBalance)
	{
		this.pointBalance = pointBalance;
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		if  ( super.fromCSVInternal(csv) )
		{
			try {
				String ser = csv.get(0); csv.remove(0);
				String stationid = csv.get(0); csv.remove(0);
				String username = csv.get(0); csv.remove(0);
				String password = csv.get(0); csv.remove(0);
				String email = csv.get(0); csv.remove(0);
				String firstname = csv.get(0); csv.remove(0);
				String lastname = csv.get(0); csv.remove(0);
				String street = csv.get(0); csv.remove(0);
				String city = csv.get(0); csv.remove(0);
				String state = csv.get(0); csv.remove(0);
				String zipcode = csv.get(0); csv.remove(0);
				String birthyyyy = csv.get(0); csv.remove(0);
				String birthmm = csv.get(0); csv.remove(0);
				String birthdd = csv.get(0); csv.remove(0);
				String gender = csv.get(0); csv.remove(0);
				String ismarried = csv.get(0); csv.remove(0);
				String canemail = csv.get(0); csv.remove(0);
				String emailformat = csv.get(0); csv.remove(0);
				String cansms = csv.get(0); csv.remove(0);
				String homephone = csv.get(0); csv.remove(0);
				String workphone = csv.get(0); csv.remove(0);
				String cellphone = csv.get(0); csv.remove(0);
				String createtime = csv.get(0); csv.remove(0);
				String lastlogintime = csv.get(0); csv.remove(0);
				String birthtm = csv.get(0); csv.remove(0);
				String points = csv.get(0); csv.remove(0);
	
				setUsername(username);
				getDelegate().setPassword(password);
				setEmail(email);
				setFirstName(firstname);
				setLastName(lastname);
				setStreet(street);
				setCity(city);
				setState(state);
				setZipcode(zipcode);

				setBirthYYYY(Utils.defaultIfNaN(birthyyyy, 0));
				setBirthMM(Utils.defaultIfNaN(birthmm, 0));
				setBirthDD(Utils.defaultIfNaN(birthdd, 0));

				setGender(gender);
				setIsMarried(Utils.defaultIfNaB(ismarried, false));
	
				setCanEmail(Utils.defaultIfNaB(canemail, false));
				setCanSMS(Utils.defaultIfNaB(cansms, false));

				setHomePhone(homephone);
				setCellPhone(cellphone);

				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
				dateFormat.setCalendar(getClient().getCalendar());
	
				setCreateTimeSeconds( (int) (dateFormat.parse(createtime).getTime() / 1000l) );
				setLastLoginTimeSeconds( (int) (dateFormat.parse(lastlogintime).getTime() / 1000l) );

				if ( getLastLoginTimeSeconds() < getCreateTimeSeconds() )
				{
					setLastLoginTimeSeconds(getCreateTimeSeconds());
				}

				setPointBalance(Utils.defaultIfNaN(points, 0));
	
				return true;
			}
			catch (ParseException ex)
			{
				getLogger().debug("Could not parse date", ex);
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}