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
package com.i4one.base.model.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Calendar;
import java.util.function.Supplier;

/**
 * @author Hamid Badiozamani
 */
@JsonTypeName("ag")
public class AgeGenderReport extends BaseCalendarDemo implements Demo
{
	private String gender;
	private int fromAge;
	private int toAge;

	public AgeGenderReport()
	{
		super();

		initInternal("o", 0, 1);
	}

	public AgeGenderReport(String gender, int fromAge, int toAge, Supplier<Calendar> calSupplier)
	{
		super(calSupplier);

		initInternal(gender, fromAge, toAge);
	}

	private void initInternal(String gender, int fromAge, int toAge)
	{
		this.gender = gender;
		this.fromAge = fromAge;
		this.toAge = toAge;

		if ( fromAge >= toAge )
		{
			throw new IllegalArgumentException("fromAge " + fromAge + " cannot be greater than or equal to toAge" + toAge);
		}

		initTitle();
	}

	private void initTitle()
	{
		newDisplayName(fromAge + "." + toAge);
		setTitle(getModelName() + "." +  gender + "." + fromAge + "." + toAge);
	}

	@Override
	public boolean isEligible(ReportUsageType item)
	{
		int age = item.getUser().getAgeInYears(getCalendar());

		return age >= fromAge && age <= toAge && item.getUser().getGender().equals(gender);
	}

	@JsonIgnore
	@Override
	public String getJSONTitle()
	{
		return "";
	}

	@JsonProperty("_f")
	public int getFromAge()
	{
		return fromAge;
	}

	@JsonProperty("_f")
	public void setFromAge(int fromAge)
	{
		this.fromAge = fromAge;
		initTitle();
	}

	@JsonProperty("_t")
	public int getToAge()
	{
		return toAge;
	}

	@JsonProperty("_t")
	public void setToAge(int toAge)
	{
		this.toAge = toAge;
		initTitle();
	}

	@JsonProperty("_g")
	public String getGender()
	{
		return gender;
	}

	@JsonProperty("_g")
	public void setGender(String gender)
	{
		this.gender = gender;
		initTitle();
	}

	@Override
	public AgeGenderReport cloneReport()
	{
		return new AgeGenderReport(getGender(), getFromAge(), getToAge(), getCalendarSupplier());
	}

	@Override
	protected boolean isExclusiveToInternal(ReportType right)
	{
		if ( right instanceof AgeGenderReport )
		{
			AgeGenderReport rightAgeDemo = (AgeGenderReport)right;
			return getFromAge() < rightAgeDemo.getToAge() || getToAge() > rightAgeDemo.getToAge();
			//return true;
		}
		else
		{
			return false;
		}
	}
}
