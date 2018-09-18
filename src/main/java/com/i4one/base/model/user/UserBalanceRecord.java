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

import com.i4one.base.dao.BaseRecordType;
import com.i4one.base.dao.UsageRecordType;
import com.i4one.base.dao.UserRecordType;

/**
 * @author Hamid Badiozamani
 */
public class UserBalanceRecord extends BaseRecordType implements UserRecordType,UsageRecordType
{
	static final long serialVersionUID = 42L;

	private Integer userid;
	private Integer total;
	private Integer createtime;
	private Integer updatetime;

	private Integer balid;

	@Override
	public String getTableName()
	{
		return "userbalances";
	}

	public UserBalanceRecord()
	{
		userid = 0;
		balid = 0;
		total = 0;

		createtime = 0;
		updatetime = 0;
	}

	public Integer getBalid()
	{
		return balid;
	}

	public final void setBalid(Integer balid)
	{
		this.balid = balid;
	}

	public Integer getTotal()
	{
		return total;
	}

	public final void setTotal(Integer total)
	{
		this.total = total;
	}

	public Integer getUpdatetime()
	{
		return updatetime;
	}

	public final void setUpdatetime(Integer updateTimeSeconds)
	{
		this.updatetime = updateTimeSeconds;
	}

	public Integer getCreatetime()
	{
		return createtime;
	}

	public void setCreatetime(Integer createtime)
	{
		this.createtime = createtime;
	}

	@Override
	public Integer getUserid()
	{
		return userid;
	}

	@Override
	public final void setUserid(Integer userid)
	{
		this.userid = userid;
	}

	@Override
	public Integer getTimestamp()
	{
		return getUpdatetime();
	}

	@Override
	public void setTimestamp(Integer timestamp)
	{
		setUpdatetime(timestamp);
	}

}
