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

import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import com.i4one.base.model.category.CategorySelectStringifier;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.answer.AnswerManager;
import com.i4one.research.model.survey.category.SurveyCategory;
import com.i4one.research.model.survey.category.SurveyCategoryRecord;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.question.QuestionManager;
import com.i4one.research.model.survey.SurveyManager;
import com.i4one.research.model.survey.SurveyRecord;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class SurveyCrudController extends BaseTerminableSiteGroupTypeCrudController<SurveyRecord, Survey>
{
	private CategoryManager<SurveyCategoryRecord, SurveyCategory> surveyCategoryManager;

	private SurveyManager surveyManager;
	private QuestionManager questionManager;
	private AnswerManager answerManager;

	@Override
	public Model initRequest(HttpServletRequest request, Survey modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelSurvey )
		{
			WebModelSurvey survey = (WebModelSurvey)modelAttribute;
			SingleClient client = model.getSingleClient();

			// Get all of the questions from the manager and add them, this might very well be an empty list
			// if the survey is brand new. We need to do this here since the incoming model object may only
			// contain the serial numbers because it may have been loaded by serial number directly and bypassing
			// the SurveyManager's initialization method
			//
			Set<Question> questions = getQuestionManager().getQuestions(survey, SimplePaginationFilter.NONE);
			questions.stream().forEach((currQuestion) ->
			{
				currQuestion.setAnswers(getAnswerManager().getAnswers(currQuestion, SimplePaginationFilter.NONE));
			});
			survey.setQuestions(questions);

			// Same thing for categories
			//
			Set<SurveyCategory> surveyCategories = getSurveyCategoryManager().getAllCategories(new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));
			model.put("categories", toSelectMapping(surveyCategories, new CategorySelectStringifier(client), model.getLanguage()));
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.research.admin.surveys";
	}

	@Override
	protected Manager<SurveyRecord, Survey> getManager()
	{
		return getSurveyManager();
	}

	@RequestMapping(value = { "**/research/admin/surveys/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer surveyId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return this.cloneImpl(surveyId, request, response);
	}

	@RequestMapping(value = { "**/research/admin/surveys/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("survey") WebModelSurvey survey,
					@RequestParam(value = "id", required = false) Integer surveyId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = createUpdateImpl(survey, surveyId, request, response);

		if ( !survey.exists() )
		{
			survey.setRandomize(false);
			survey.setIntro(model.buildIMessage("msg.research.admin.surveys.defaultintro"));
			survey.setOutro(model.buildIMessage("msg.research.admin.surveys.defaultoutro"));
		}

		return initResponse(model, response, survey);
	}

	@RequestMapping(value = "**/research/admin/surveys/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("survey") WebModelSurvey survey, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(survey, result, request, response);
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

	public CategoryManager<SurveyCategoryRecord, SurveyCategory> getSurveyCategoryManager()
	{
		return surveyCategoryManager;
	}

	@Autowired
	public void setSurveyCategoryManager(CategoryManager<SurveyCategoryRecord, SurveyCategory> surveyCategoryManager)
	{
		this.surveyCategoryManager = surveyCategoryManager;
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

	public AnswerManager getAnswerManager()
	{
		return answerManager;
	}

	@Autowired
	public void setAnswerManager(AnswerManager answerManager)
	{
		this.answerManager = answerManager;
	}
}
