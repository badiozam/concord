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
package com.i4one.rewards.model;

import com.i4one.base.model.category.Category;
import com.i4one.base.model.manager.categorizable.CategorizableTerminableClientType;
import com.i4one.base.model.manager.terminable.TerminableSiteGroupType;
import java.text.ParseException;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 */
public interface TerminableRewardsClientType<T extends TerminableRewardsClientRecordType, U extends Category<?>> extends CategorizableTerminableClientType<T,U>,TerminableSiteGroupType<T>,WinnablePrizeType<T>
{
	public Date getPurchaseStartTime();

	public int getPurchaseStartTimeSeconds();
	public void setPurchaseStartTimeSeconds(int starttm);

	public String getPurchaseStartTimeString();
	public void setPurchaseStartTimeString(String startTimeStr) throws ParseException;

	public Date getPurchaseEndTime();

	public int getPurchaseEndTimeSeconds();
	public void setPurchaseEndTimeSeconds(int endtm);

	public String getPurchaseEndTimeString();
	public void setPurchaseEndTimeString(String endTimeStr) throws ParseException;

	@Deprecated
	public boolean isAvailable();

	public boolean isAvailableDuring(int startTimeSeconds, int endTimeSeconds);
	public boolean isAvailableAt(int timestamp);
}
