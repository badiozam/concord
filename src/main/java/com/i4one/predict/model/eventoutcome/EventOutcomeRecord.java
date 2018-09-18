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
package com.i4one.predict.model.eventoutcome;

import com.i4one.base.model.i18n.IString;
import com.i4one.predict.model.PredictRecordType;

/**
 * @author Hamid Badiozamani
 */
public class EventOutcomeRecord extends PredictRecordType
{
	static final long serialVersionUID = 42L;

	private Integer eventid;

	private IString descr;

	private Float likelihood;
	private Float baseline;
	private Integer usagecount;
	private Boolean locklikelihood;
	private Integer updatetime;

	private Boolean actualized;

	public EventOutcomeRecord()
	{
		super();

		eventid = 0;
		likelihood = 0.0f;
		baseline = 0.0f;
		usagecount = 0;
		locklikelihood = false;
		updatetime = 0;
		actualized = null;

		descr = new IString();
	}

	@Override
	public String getTableName()
	{
		return "eventoutcomes";
	}

	public Integer getEventid()
	{
		return eventid;
	}

	public void setEventid(Integer eventid)
	{
		this.eventid = eventid;
	}

	public Float getLikelihood()
	{
		return likelihood;
	}

	public void setLikelihood(Float likelihood)
	{
		this.likelihood = likelihood;
	}

	public IString getDescr()
	{
		return descr;
	}

	public void setDescr(IString descr)
	{
		this.descr = descr;
	}

	public Integer getUpdatetime()
	{
		return updatetime;
	}

	public void setUpdatetime(Integer updatetime)
	{
		this.updatetime = updatetime;
	}

	public Float getBaseline()
	{
		return baseline;
	}

	public void setBaseline(Float baseline)
	{
		this.baseline = baseline;
	}

	public Integer getUsagecount()
	{
		return usagecount;
	}

	public void setUsagecount(Integer usagecount)
	{
		this.usagecount = usagecount;
	}

	public Boolean getLocklikelihood()
	{
		return locklikelihood;
	}

	public void setLocklikelihood(Boolean locklikelihood)
	{
		this.locklikelihood = locklikelihood;
	}

	public Boolean getActualized()
	{
		return actualized;
	}

	public void setActualized(Boolean actualized)
	{
		this.actualized = actualized;
	}

}
