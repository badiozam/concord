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
package com.i4one.promotion.web.controller.admin.codes;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.controller.admin.ReportSettings;
import com.i4one.promotion.model.code.Code;
import com.i4one.promotion.model.code.CodeManager;
import com.i4one.promotion.model.code.CodeResponse;
import com.i4one.promotion.model.code.CodeResponseManager;
import com.i4one.promotion.model.code.reports.CodeResultsReport;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class CodeReportController extends BaseAdminReportViewController
{
	private CodeManager codeManager;
	private CodeResponseManager codeResponseManager;

	@RequestMapping(value = "**/promotion/admin/codes/report")
	public Model usage(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Code code = getCodeManager().getById(reportSettings.getId());
		Model model = initRequest(request, reportSettings, code);

		SingleClient client = model.getSingleClient();
		CodeResultsReport triviaResultsReport = new CodeResultsReport(code, client.getCalendar());

		CodeResponse template = new CodeResponse();
		template.setCode(code);

		return classificationReport(model,
			reportSettings,
			triviaResultsReport,
			() -> { return getCodeResponseManager().getReport(template, triviaResultsReport, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/promotion/admin/codes/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		usage(reportSettings, request, response);
	}

	public CodeResponseManager getCodeResponseManager()
	{
		return codeResponseManager;
	}

	@Autowired
	public void setCodeResponseManager(CodeResponseManager codeResponseManager)
	{
		this.codeResponseManager = codeResponseManager;
	}

	public CodeManager getCodeManager()
	{
		return codeManager;
	}

	@Autowired
	public void setCodeManager(CodeManager codeManager)
	{
		this.codeManager = codeManager;
	}
}
