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
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.admin.CSVExportUsageType;
import java.text.DateFormat;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class MemberCSVExportUsageType extends CSVExportUsageType
{
	private SingleClient client;

	public MemberCSVExportUsageType(SingleClient client)
	{
		this.client = client;
	}

	@Override
	public String toCSV(boolean header)
	{
		StringBuilder csv = new StringBuilder();

		if ( header )
		{
			// XXX: Needs i18n, consider returning a list of strings
			// instead so that the caller can convert to messages
			//
			//ser	stationid	username	password	email	firstname	lastname	street	city	state	zipcode	birthyyyy	birthmm	birthdd	gender	ismarried	canemail	emailformat	cansms	homephone	workphone	cellphone	createtime	lastlogintime	birthtm	rdm_properties	rdm_permissions	rdm_m2opoints

			csv.append(Utils.csvEscape("ser")).append(",");
			csv.append(Utils.csvEscape("stationid")).append(",");
			csv.append(Utils.csvEscape("username")).append(",");
			csv.append(Utils.csvEscape("password")).append(",");
			csv.append(Utils.csvEscape("email")).append(",");
			csv.append(Utils.csvEscape("firstname")).append(",");
			csv.append(Utils.csvEscape("lastname")).append(",");
			csv.append(Utils.csvEscape("street")).append(",");
			csv.append(Utils.csvEscape("city")).append(",");
			csv.append(Utils.csvEscape("state")).append(",");
			csv.append(Utils.csvEscape("zipcode")).append(",");
			csv.append(Utils.csvEscape("birthyyyy")).append(",");
			csv.append(Utils.csvEscape("birthmm")).append(",");
			csv.append(Utils.csvEscape("birthdd")).append(",");
			csv.append(Utils.csvEscape("gender")).append(",");
			csv.append(Utils.csvEscape("ismarried")).append(",");
			csv.append(Utils.csvEscape("canemail")).append(",");
			csv.append(Utils.csvEscape("emailformat")).append(",");
			csv.append(Utils.csvEscape("cansms")).append(",");
			csv.append(Utils.csvEscape("homephone")).append(",");
			csv.append(Utils.csvEscape("workphone")).append(",");
			csv.append(Utils.csvEscape("cellphone")).append(",");
			csv.append(Utils.csvEscape("createtime")).append(",");
			csv.append(Utils.csvEscape("lastlogintime")).append(",");
			csv.append(Utils.csvEscape("birthtm")).append(",");
		}
		else
		{
			User user = getDelegate().getUser();
			DateFormat dateFormat = client.getDateFormat();

			csv.append("\"").append(convertInteger(user.getUser().getSer())).append("\"").append(",");
			csv.append("\"").append(convertInteger(user.getClient().getSer())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getUsername())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getPassword())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getEmail())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getFirstName())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getLastName())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getStreet())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getCity())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getState())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getZipcode())).append("\"").append(",");
			csv.append("\"").append(convertInteger(user.getBirthYYYY())).append("\"").append(",");
			csv.append("\"").append(convertInteger(user.getBirthMM())).append("\"").append(",");
			csv.append("\"").append(convertInteger(user.getBirthDD())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getGender())).append("\"").append(",");
			csv.append("\"").append(convertBoolean(user.getIsMarried())).append("\"").append(",");
			csv.append("\"").append(convertBoolean(user.getCanEmail())).append("\"").append(",");
			csv.append("\"").append(1).append("\"").append(",");	// All e-mails to be sent via HTML
			csv.append("\"").append(convertBoolean(user.getCanSMS())).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getHomePhone())).append("\"").append(",");
			csv.append("\"").append(csvEscape("")).append("\"").append(",");
			csv.append("\"").append(csvEscape(user.getCellPhone())).append("\"").append(",");
			csv.append("\"").append(csvEscape(dateFormat.format(user.getCreateTime()))).append("\"").append(",");
			csv.append("\"").append("").append("\"").append(",");
			csv.append("\"").append(csvEscape(dateFormat.format(user.getBirthCalendar().getTime()))).append("\"").append(",");
		}

		return csv.toString();
	}

	private String csvEscape(String str)
	{
		return Utils.csvEscape(Utils.forceEmptyStr(str));
	}

	private int convertBoolean(Boolean val)
	{
		if (val == null ||!val )
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}

	private int convertInteger(Integer val)
	{
		return (val == null) ? 0 :val;
	}

	public SingleClient getClient()
	{
		return client;
	}

	@Autowired
	public void setClient(SingleClient client)
	{
		this.client = client;
	}
}
