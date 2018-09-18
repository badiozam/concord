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

import java.util.Collection;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Hamid Badiozamani
 */
public class ReportForwarder<T extends ReportType> implements ReportType<T>
{
	private ReportType<T> forward;

	public ReportForwarder(ReportType<T> forward)
	{
		this.forward = forward;
	}

	@Override
	public String getSchemaName()
	{
		return getForward().getSchemaName();
	}

	@Override
	public int getGroupingCount()
	{
		return getForward().getGroupingCount();
	}

	@Override
	public int getTotal()
	{
		return getForward().getTotal();
	}

	@Override
	public ReportType getParent()
	{
		return getForward().getParent();
	}

	@Override
	public void setParent(ReportType parent)
	{
		getForward().setParent(parent);
	}

	@Override
	public ReportType cloneReport()
	{
		return getForward().cloneReport();
	}

	@Override
	public void initReport()
	{
		getForward().initReport();
	}

	@Override
	public String getTitle()
	{
		return getForward().getTitle();
	}

	@Override
	public String getDisplayName()
	{
		return getForward().getDisplayName();
	}

	@Override
	public String getSeriesName()
	{
		return getForward().getSeriesName();
	}

	@Override
	public String getSeriesName(int i)
	{
		return getForward().getSeriesName(i);
	}

	@Override
	public String getDisplayType()
	{
		return getForward().getDisplayType();
	}

	@Override
	public Set<T> getSubReports()
	{
		return getForward().getSubReports();
	}

	@Override
	public void addReport(T report)
	{
		getForward().addReport(report);
	}

	@Override
	public boolean newReport(T report)
	{
		return getForward().newReport(report);
	}

	@Override
	public boolean removeReport(T report)
	{
		return getForward().removeReport(report);
	}

	@Override
	public boolean hasReport(T report)
	{
		return getForward().hasReport(report);
	}

	@Override
	public boolean isExclusiveTo(T report)
	{
		return getForward().isExclusiveTo(report);
	}

	@Override
	public boolean isClassCompatible(Class clazz)
	{
		return getForward().isClassCompatible(clazz);
	}

	@Override
	public boolean isSameReport(ReportType report)
	{
		return getForward().isSameReport(report);
	}

	@Override
	public void setSubReports(Collection<T> reports)
	{
		getForward().setSubReports(reports);
	}

	@Override
	public void clear()
	{
		getForward().clear();
	}

	@Override
	public void prune()
	{
		getForward().prune();
	}

	@Override
	public boolean isEligible(ReportUsageType usage)
	{
		return getForward().isEligible(usage);
	}

	@Override
	public void process(ReportUsageType usage)
	{
		getForward().process(usage);
	}

	public ReportType<T> getForward()
	{
		return forward;
	}

	public void setForward(ReportType<T> forward)
	{
		this.forward = forward;
	}

	@Override
	public ReportType findReport(String titleChain)
	{
		return getForward().findReport(titleChain);
	}

	@Override
	public Stack<ReportType> getAncestry()
	{
		return getForward().getAncestry();
	}

	@Override
	public String getTitleChain()
	{
		return getForward().getTitleChain();
	}

	@Override
	public String getModelName()
	{
		return getForward().getModelName();
	}

	@Override
	public String toJSONString()
	{
		return getForward().toJSONString();
	}

	@Override
	public void setProcessCallback(BiConsumer<ReportType<T>, ReportUsageType> processCallback)
	{
		getForward().setProcessCallback(processCallback);
	}

	@Override
	public String getDisplayNameChain()
	{
		return getForward().getDisplayNameChain();
	}

	@Override
	public void setTotal(int total)
	{
		getForward().setTotal(total);
	}

	@Override
	public void forAllProgeny(Function<ReportType, Boolean> function)
	{
		getForward().forAllProgeny(function);
	}
}
