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
package com.i4one.research.model.survey.respondent;

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseActivityType;
import com.i4one.base.model.SiteGroupActivityType;
import com.i4one.base.model.UsageType;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.SurveyRecord;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 */
public class Respondent extends BaseActivityType<RespondentRecord, SurveyRecord, Survey> implements UsageType<RespondentRecord>,SiteGroupActivityType<RespondentRecord, Survey>
{
	static final long serialVersionUID = 42L;

	public Respondent()
	{
		super(new RespondentRecord());
	}

	protected Respondent(RespondentRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();
	}

	public PaginationFilter getPagination()
	{
		PaginationFilter retVal = new SimplePaginationFilter();

		retVal.setCurrentPage(getCurrentPage() % Respondent.this.getSurvey().getTotalPages());
		retVal.setOrderBy("orderweight, ser");
		retVal.setPerPage(Respondent.this.getSurvey().getPerPage());
		
		return retVal;
	}

	public int getUpdateTimeSeconds()
	{
		return getDelegate().getUpdatetime();
	}

	public void setUpdateTimeSeconds(int timestamp)
	{
		getDelegate().setUpdatetime(timestamp);
	}

	public Date getUpdateTime()
	{
		return Utils.toDate(getUpdateTimeSeconds());
	}
	
	public int getLastPlayedTimeSeconds()
	{
		return getTimeStampSeconds();
	}

	public void setLastPlayedTimeSeconds(int lastplayedtime)
	{
		setTimeStampSeconds(lastplayedtime);
	}

	public Date getLastPlayedTime()
	{
		return getTimeStamp();
	}

	public int getStartPage()
	{
		return getDelegate().getStartpage();
	}

	public void setStartPage(int startpage)
	{
		getDelegate().setStartpage(startpage);
	}

	/**
	 * The current page of the survey the user is on. The current page is
	 * always greater than or equal to the start page.
	 * 
	 * @return The current page of the survey
	 */
	public int getCurrentPage()
	{
		return getDelegate().getCurrentpage();
	}

	public void setCurrentpage(int currentPage)
	{
		getDelegate().setCurrentpage(currentPage);
	}

	/**
	 * Get the total number of completed pages. 
	 * 
	 * @return The total number of completed pages
	 */
	public int getCompletedPages()
	{
		return getCurrentPage() - getStartPage();
	}

	public boolean getHasFinished()
	{
		return getDelegate().getHasfinished();
	}

	public void setHasFinished(boolean hasFinished)
	{
		getDelegate().setHasfinished(hasFinished);
	}

	public void setHasFinished()
	{
		getDelegate().setHasfinished( getCompletedPages() >= Respondent.this.getSurvey().getTotalPages() );
	}

	public Survey getSurvey()
	{
		return getActionItem();
	}

	public Survey getSurvey(boolean doLoad)
	{
		return getActionItem(doLoad);
	}

	public void setSurvey(Survey survey)
	{
		setActionItem(survey);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getUserid() + "-" + getDelegate().getItemid();
	}

	@Override
	protected Survey newActionItem()
	{
		return new Survey();
	}
}