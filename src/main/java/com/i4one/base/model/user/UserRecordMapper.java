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

import com.i4one.base.dao.BaseClientRecordRowMapper;
import com.i4one.base.dao.RecordTypeSqlParameterSource;
import java.sql.ResultSet;

/**
 * @author Hamid Badiozamani
 */
public class UserRecordMapper extends BaseClientRecordRowMapper<UserRecord>
{
	@Override
	protected UserRecord mapRowInternal(ResultSet res) throws java.sql.SQLException
	{
		UserRecord u = new UserRecord();

		u.setUsername( res.getString("username") );
		u.setPassword( res.getString("password") );
		u.setEmail( res.getString("email") );

		u.setFirstname(res.getString("firstname"));
		u.setLastname(res.getString("lastname"));
		u.setStreet(res.getString("street"));
		u.setCity(res.getString("city"));
		u.setState(res.getString("state"));
		u.setZipcode(res.getString("zipcode"));

		u.setHomephone(res.getString("homephone"));
		u.setCellphone(res.getString("cellphone"));

		u.setStatus(getInteger(res, "status"));
		u.setGender(getCharacter(res, "gender"));
		u.setIsMarried(getBoolean(res, "ismarried"));

		u.setForceUpdate(getBoolean(res, "forceupdate"));
		u.setCanEmail(getBoolean(res, "canemail"));
		u.setCanSMS(getBoolean(res, "cansms"));
		u.setCanCall(getBoolean(res, "cancall"));

		u.setBirthdd(getInteger(res, "birthdd"));
		u.setBirthmm(getInteger(res, "birthmm"));
		u.setBirthyyyy(getInteger(res, "birthyyyy"));

		u.setCreatetime(getInteger(res, "createtime"));
		u.setLastlogintime(getInteger(res, "lastlogintime"));

		u.setLastbirthyear(getInteger(res, "lastbirthyear"));

		return u;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(UserRecord u)
	{
		// Create a map of name-value pairs for the insert/update
		//
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("username", u.getUsername());
		//sqlParams.addValue("password", u.getPassword());
		sqlParams.addValue("email", u.getEmail());

		sqlParams.addValue("firstname", u.getFirstname());
		sqlParams.addValue("lastname", u.getLastname());
		sqlParams.addValue("street", u.getStreet());
		sqlParams.addValue("city", u.getCity());
		sqlParams.addValue("state", u.getState());
		sqlParams.addValue("zipcode", u.getZipcode());
		sqlParams.addValue("homephone", u.getHomephone());
		sqlParams.addValue("cellphone", u.getCellphone());

		sqlParams.addValue("status", u.getStatus());
		sqlParams.addValue("gender", u.getGender() == null ? null : u.getGender().toString());
		sqlParams.addValue("ismarried", u.getIsMarried());
		sqlParams.addValue("forceupdate", u.getForceUpdate());
		sqlParams.addValue("canemail", u.getCanEmail());
		sqlParams.addValue("cancall", u.getCanCall());
		sqlParams.addValue("cansms", u.getCanSMS());

		sqlParams.addValue("birthdd", u.getBirthdd());
		sqlParams.addValue("birthmm", u.getBirthmm());
		sqlParams.addValue("birthyyyy", u.getBirthyyyy());

		sqlParams.addValue("lastlogintime", u.getLastlogintime());
		sqlParams.addValue("createtime", u.getCreatetime());

		sqlParams.addValue("lastbirthyear", u.getLastbirthyear());

		return sqlParams;
	}

	@Override
	public UserRecord emptyInstance()
	{
		return new UserRecord();
	}
}
