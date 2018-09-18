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
package com.i4one.research.web.controller.admin.surveys;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.ActivityReportSettings;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.controller.admin.ReportSettings;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.SurveyManager;
import com.i4one.research.model.survey.question.QuestionManager;
import com.i4one.research.model.survey.reports.SurveyResultsReport;
import com.i4one.research.model.survey.reports.SurveyUsageReport;
import com.i4one.research.model.survey.respondent.Respondent;
import com.i4one.research.model.survey.respondent.RespondentManager;
import com.i4one.research.model.survey.response.ResponseManager;
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
public class SurveyReportController extends BaseAdminReportViewController
{
	private RespondentManager respondentManager;

	private SurveyManager surveyManager;
	private QuestionManager questionManager;
	private ResponseManager responseManager;

	public Model usage(@ModelAttribute("reportSettings") ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Survey survey = getSurveyManager().getById(reportSettings.getId());

		Model model = initRequest(request, survey);

		getLogger().debug("Got request for report for survey with id " + reportSettings.getId());

		SurveyUsageReport surveyReport = new SurveyUsageReport(survey, model.getSingleClient().getCalendar());
		Respondent sample = new Respondent();
		sample.setSurvey(survey);

		return report(model, reportSettings, surveyReport, (report) -> { return getRespondentManager().getReport(sample, report, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/research/admin/surveys/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		usage(reportSettings, request, response);
	}

	@RequestMapping(value = "**/research/admin/surveys/report")
	public Model surveyUsage(@ModelAttribute("reportSettings") ActivityReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Survey survey = getSurveyManager().getById(reportSettings.getId());
		Model model = initRequest(request, reportSettings, survey);

		SingleClient client = model.getSingleClient();
		SurveyResultsReport surveyResultsReport = new SurveyResultsReport(survey, client.getCalendar(), getResponseManager());

		Respondent template = new Respondent();
		template.setSurvey(survey);

		return classificationReport(model,
			reportSettings,
			surveyResultsReport,
			() -> { return getRespondentManager().getReport(template, surveyResultsReport, reportSettings.getPagination()); }, response);
	}

	public RespondentManager getRespondentManager()
	{
		return respondentManager;
	}

	@Autowired
	public void setRespondentManager(RespondentManager respondentManager)
	{
		this.respondentManager = respondentManager;
	}

	public SurveyManager getSurveyManager()
	{
		return surveyManager;
	}

	@Autowired
	public void setSurveyManager(SurveyManager surveyManager)
	{
		this.surveyManager = surveyManager;
	}

	public ResponseManager getResponseManager()
	{
		return responseManager;
	}

	@Autowired
	public void setResponseManager(ResponseManager responseManager)
	{
		this.responseManager = responseManager;
	}

	public QuestionManager getQuestionManager()
	{
		return questionManager;
	}

	@Autowired
	public void setQuestionManager(QuestionManager questionManager)
	{
		this.questionManager = questionManager;
	}

}
