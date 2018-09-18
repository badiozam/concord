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
package com.i4one.base.model.manager.activity;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.dao.activity.ActivityRecordType;
import com.i4one.base.dao.activity.ActivityRecordTypeDao;
import com.i4one.base.model.ActivityType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.NullTopLevelReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseActivityManager<U extends ActivityRecordType, T extends ActivityType<U,V>, V extends RecordTypeDelegator<?>> extends BasePaginableManager<U, T> implements ActivityManager<U,T,V>
{
	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		item.setTimeStampSeconds(getActivityTimeStampSeconds(item));
		return super.create(item);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		item.setTimeStampSeconds(getActivityTimeStampSeconds(item));
		return super.update(item);
	}

	/**
	 * Returns the activity time in seconds since epoch. Some subclasses may
	 * wish to modulate the granularity to ensure that times fall on the end
	 * of minutes or hours. The default behavior is to return the current time.
	 * 
	 * @param item The item for which the activity time stamp is being generated
	 * 
	 * @return The time stamp in seconds
	 */
	protected int getActivityTimeStampSeconds(T item)
	{
		return Utils.currentTimeSeconds();
	}

	@Override
	public T getActivity(V parent, User user)
	{
		T retVal = emptyInstance();

		U delegate = getDao().get(parent.getSer(), user.getSer());
		if ( delegate != null )
		{
			retVal.setOwnedDelegate(delegate);
		}

		return retVal;
	}

	@Override
	public boolean hasActivity(V parent)
	{
		return getDao().hasActivity(parent.getSer());
	}

	@Override
	public List<ReturnType<T>> revert(V parent, PaginationFilter pagination)
	{
		Set<T> activity = getAllActivity(parent, pagination);

		List<ReturnType<T>> retVal = new ArrayList<>();
		T empty = emptyInstance();
		for ( T item : activity )
		{
			ReturnType<T> removed = new ReturnType<>();

			removed.setPre(remove(item));
			removed.setPost(empty);

			retVal.add(removed);
		}

		return retVal;
	}

	@Override
	public Set<T> getAllActivity(V parent, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByItem(parent.getSer(), pagination));
	}

	@Override
	public ActivityReport getReport(T item, TopLevelReport topLevelReport, PaginationFilter pagination)
	{
		if ( topLevelReport instanceof ActivityReport )
		{
			ActivityReport report = (ActivityReport) topLevelReport;
			TopLevelReport dbReport = getReportManager().loadReport(report);
	
			if ( dbReport instanceof NullTopLevelReport )
			{
				if ( report.getSubReports().isEmpty())
				{
					// If the report has no demographics, it's useless. We allow
					// the caller to set custom sub-reports, demographics, etc.
					// but otherwise, we'll load the default demographics for the
					// client before beginning processing
					//
					getReportManager().populateReport(report);
					getLogger().debug("Populated report with: " + Utils.toCSV(report.getSubReports()));
				}
				else
				{
					getLogger().debug("Incoming report has the following demographics: " + Utils.toCSV(report.getSubReports()));
				}
	
				getDao().processReport(item.getDelegate(), report);
				getReportManager().saveReport(report);
	
				return report;
			}
			else
			{
				getLogger().debug("Loaded manager report successfully!");
	
				// We set the item here since these are not serialized by the report manager
				// and the title chains, bread crumbs, etc. depend on them to be set
				//
				ActivityReport activityReport =  (ActivityReport)dbReport;
				activityReport.setItem(item.getActionItem());
	
				getLogger().debug("Returning manager report with item " + item);
	
				return activityReport;
			}
		}
		else
		{
			throw new IllegalArgumentException("Expecting an ActivityReport");
		}
	}

	@Override
	public <R extends Object> ClassificationReport<U, R> getReport(T item, ClassificationReport<U, R> report, PaginationFilter pagination)
	{
		getDao().processReport(item.getDelegate(), report, pagination);
		return report;
	}

	@Override
	public ActivityRecordTypeDao<U> getDao()
	{
		PaginableRecordTypeDao<U> dao = super.getDao();
		if ( dao instanceof ActivityRecordTypeDao)
		{
			ActivityRecordTypeDao<U> paginableDao = (ActivityRecordTypeDao<U>) dao;

			return paginableDao;
		}
		else
		{
			return null;
		}
	}
}
