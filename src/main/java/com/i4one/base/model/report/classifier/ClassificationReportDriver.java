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
package com.i4one.base.model.report.classifier;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.dao.RecordTypeRowMapper;
import com.i4one.base.dao.RecordType;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * @author Hamid Badiozamani
 */
public class ClassificationReportDriver<U extends RecordType> extends BaseLoggable implements RowCallbackHandler
{
	private int rowNum;

	private final ClassificationReport<U, ?> report;
	private final RecordTypeRowMapper<U> mapper;

	public ClassificationReportDriver(ClassificationReport<U, ?> report, RecordTypeRowMapper<U> mapper) 
	{
		this.report = report;
		this.mapper = mapper;

		rowNum = 0;
	}

	@Override
	public void processRow(ResultSet rs) throws SQLException
	{
		U record = mapper.mapRow(rs, rowNum++);

		getLogger().trace("Processing record {}", record);
		report.processRecord(record);
	}
}
