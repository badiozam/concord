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
package com.i4one.base.web.controller.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.core.Base;
import com.i4one.base.core.Utils;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.GenericClassification;
import com.i4one.base.model.report.classifier.GenericSubclassification;
import com.i4one.base.model.report.classifier.Subclassification;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class ReportSettings extends BaseLoggable
{
	private Integer id;
	private RecordTypeDelegator<?> item;

	private String titleChain;
	private String lineage;
	private final Set<String> crumbs; 

	private boolean printableView;
	private boolean showTables;
	private boolean showRawData;
	private boolean csv;
	private CSVExportUsageType csvExportUsageType;

	private PaginationFilter pagination;

	public ReportSettings()
	{
		// By making id an object instead of a primitive, we allow it
		// to be null for reports that don't need it
		//
		id = 0;

		titleChain = "";
		lineage = "";
		crumbs = new LinkedHashSet<>();

		printableView = false;
		showTables = false;
		showRawData = false;
		csv = false;

		pagination = new SimplePaginationFilter();
		pagination.setPerPage(0);

		csvExportUsageType = new CSVExportUsageType();
	}

	public String getTitleChain()
	{
		return titleChain;
	}

	public void setTitleChain(String titleChain)
	{
		this.titleChain = titleChain;
	}

	public void setLineage(String lineage)
	{
		this.lineage = lineage.replaceFirst("^\\[", "").replaceFirst("\\]$", "").replaceFirst("^,", "");
		setBreadCrumbs(this.lineage);
	}

	public String getLineage()
	{
		return this.lineage;
	}

	@JsonIgnore
	public <T extends Object> Set<Subclassification<T>> getLineageSet()
	{
		Set<Subclassification<T>> retVal = new HashSet<>();

		String[] classifications = lineage.split(",");
		for ( String classification : classifications )
		{
			String[] classComponents = classification.split("#");
			if ( classComponents.length == 2 )
			{
				Classification<T,? extends Subclassification<T>> generic = new GenericClassification<>(classComponents[0]);
				Subclassification<T> genericSub = new GenericSubclassification<>(generic, classComponents[1]);

				retVal.add(genericSub);
			}
		}

		getLogger().debug("Lineage is parsed to be {}", retVal);
		return retVal;
	}

	@JsonIgnore
	public String getBreadCrumbsCSV()
	{
		return Utils.toCSV(crumbs);
	}

	@JsonIgnore
	public Set<String> getBreadCrumbs()
	{
		return crumbs;
	}

	@JsonIgnore
	public void setBreadCrumbs(String crumbs)
	{
		this.crumbs.clear();
		for ( String crumb : Utils.forceEmptyStr(crumbs).split(","))
		{
			if ( !Utils.isEmpty(crumb) )
			{
				this.crumbs.add(crumb);
			}
		}
	}

	public boolean isShowTables()
	{
		return showTables;
	}

	public void setShowTables(boolean showTables)
	{
		this.showTables = showTables;
	}

	public boolean getShowRawData()
	{
		return isShowRawData();
	}

	public boolean isShowRawData()
	{
		return showRawData;
	}

	public void setShowRawData(boolean showRawData)
	{
		this.showRawData = showRawData;
	}

	public boolean getPrintableView()
	{
		return isPrintableView();
	}

	public boolean isPrintableView()
	{
		return printableView;
	}

	public void setPrintableView(boolean printableView)
	{
		this.printableView = printableView;
	}

	public boolean isCsv()
	{
		return csv;
	}

	public void setCsv(boolean csv)
	{
		this.csv = csv;
	}

	@JsonIgnore
	public CSVExportUsageType getCSVExportUsageType()
	{
		return csvExportUsageType;
	}

	@JsonIgnore
	public void setCSVExportUsageType(CSVExportUsageType csvExportUsageType)
	{
		this.csvExportUsageType = csvExportUsageType;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	@JsonIgnore
	public RecordTypeDelegator<?> getItem()
	{
		return item;
	}

	@JsonIgnore
	public void setItem(RecordTypeDelegator<?> item)
	{
		this.item = item;
	}

	public String toJSONString()
	{
		//return getInstance().getGson().toJson(this);
		try
		{
			return Base.getInstance().getJacksonObjectMapper().writeValueAsString(this);
		}
		catch (IOException ex)
		{
			return ex.getLocalizedMessage();
		}
	}

	@JsonIgnore
	public void setJSON(String json) throws IOException
	{
		//PropertyUtils.copyProperties(this, getInstance().getGson().fromJson(json, this.getClass()));
		ReportSettings fromJSON = Base.getInstance().getJacksonObjectMapper().readValue(json, this.getClass());
		copyFromInternal(fromJSON);
	}

	protected void copyFromInternal(ReportSettings right)
	{
		// We only copy the fields that do not have JsonIgnore on them
		//
		this.setTitleChain(right.getTitleChain());
		this.setLineage(right.getLineage());
		this.setId(right.getId());
		this.setCsv(right.isCsv());

		this.setPrintableView(right.isPrintableView());
		this.setShowRawData(right.isShowRawData());
		this.setShowTables(right.isShowTables());
	}

	@JsonIgnore
	public PaginationFilter getPagination()
	{
		return pagination;
	}

	@JsonIgnore
	public void setPagination(PaginationFilter pagination)
	{
		this.pagination = pagination;
	}
}
