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
package com.i4one.research.web.controller.user.surveys;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseCategorizableListingController;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.answer.AnswerManager;
import com.i4one.research.model.survey.category.SurveyCategory;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.question.QuestionManager;
import com.i4one.research.model.survey.respondent.Respondent;
import com.i4one.research.model.survey.respondent.RespondentManager;
import com.i4one.research.model.survey.response.Response;
import com.i4one.research.model.survey.response.ResponseManager;
import com.i4one.research.model.survey.SurveyManager;
import com.i4one.research.model.survey.category.SurveyCategoryManager;
import com.i4one.research.web.interceptor.SurveyCategoriesModelInterceptor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class SurveyViewController extends BaseCategorizableListingController<Survey, SurveyCategory>
{
	private SurveyCategoryManager surveyCategoryManager;
	private SurveyManager surveyManager;
	private QuestionManager questionManager;
	private AnswerManager answerManager;
	private ResponseManager responseManager;
	private RespondentManager respondentManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model retVal = super.initRequest(request, modelAttribute);
		if ( modelAttribute instanceof WebModelRespondent )
		{
			WebModelRespondent respondent = (WebModelRespondent) modelAttribute;

			respondent.setAnswerManager(getAnswerManager());
			respondent.setResponseManager(getResponseManager());
		}

		return retVal;
	}

	@Override
	public Model initResponse(Model model, HttpServletResponse response, Object modelAttribute)
	{
		Model retVal = super.initResponse(model, response, modelAttribute);

		if ( modelAttribute instanceof WebModelRespondent )
		{
			WebModelRespondent respondent = (WebModelRespondent)modelAttribute;

			setTitle(model, respondent.getSurvey().getTitle());
		}

		return retVal;
	}

	@Override
	protected Set<SurveyCategory> loadCategories(Model model)
	{
		return (Set<SurveyCategory>) model.get(SurveyCategoriesModelInterceptor.SURVEY_CATEGORIES);
	}

	@RequestMapping("**/research/user/surveys/index")
	public Model listAllSurveys(@RequestParam(value = "categoryid", defaultValue = "0") int categoryid, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, null);

		SurveyCategory category = viewListingImpl(categoryid, model, request, response);

		// Only display the live surveys
		//
		Set<Survey> liveSurveys = getSurveyManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
			new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE)));

		User user = model.getUser();

		// Go through all of the available trivias and load the status of any the 
		// user may have participated in
		//
		Map<Survey, Respondent> surveys = new LinkedHashMap<>();
		liveSurveys.forEach( (survey) ->
		{
			surveys.put(survey, getRespondentManager().getActivity(survey, user));
		});

		getLogger().debug("We have " + surveys.size() + " surveys in category " + categoryid);
		/*
		 * Took this out because if we do have only 1 survey then the back button will not work.
		 *
		 * Consider passing in a parameter to the redirect that will disable the back button
		 *
		if ( surveys.size() == 1 )
		{
			// If we only have one survey, let's just redirect immediately to that survey instead
			// of having the user click on it
			//
			return redirWithDefault(model, null, null, null, "survey.html?surveyid=" + surveys.keySet().iterator().next().getSer(), request, response);
		}
		else
		*/
		{
			model.put("surveys", surveys);

			addMessageToModel(model, Model.TITLE, "msg.research.user.surveys.index.pageTitle");

			// We have more than one or we have no surveys, in either case we can have the view
			// determine the outcome
			//
			return initResponse(model, response, null);
		}
	}

	protected void initRespondent(WebModelRespondent respondent) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException 
	{
		// Load the current location of the user (or create a new record for them if none exists)
		//
		respondent.copyFrom(getRespondentManager().getActivity(respondent.getSurvey(), respondent.getUser()));
		getLogger().debug("Respondent: " + respondent);

		// Load the questions (and their respective answers) for this particular page
		//
		respondent.getSurvey().setQuestions(getQuestionManager().getQuestions(respondent.getSurvey(), respondent.getPagination()));
		getLogger().debug("Survey: " + respondent.getSurvey());
	}

	@RequestMapping(value = "**/research/user/surveys/survey", method = RequestMethod.GET)
	public Model getSurvey(@ModelAttribute("respondent") WebModelRespondent respondent,
							@RequestParam(value = "surveyid", required = false) Integer surveyId,
							@RequestParam(value = "categoryid", required = false) Integer categoryId,
							BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, respondent);

		if ( surveyId != null )
		{
			getLogger().debug("Setting survey to id #" + surveyId);
			respondent.getSurvey().consume(getSurveyManager().getById(surveyId));

			getLogger().debug("Respondent is now: " + respondent);
		}
		else if ( categoryId != null)
		{
			SurveyCategory category = getSurveyCategoryManager().getById(categoryId);

			PaginationFilter pagination = new SimplePaginationFilter();
			pagination.setOrderBy("orderweight, endtm, ser DESC");

			Set<Survey> surveys = getSurveyManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
				new SiteGroupPagination(model.getSiteGroups(), pagination)));

			if ( !surveys.isEmpty() )
			{
				// Make the user default to the first survey
				//
				respondent.setSurvey(surveys.iterator().next());
			}
			else
			{
				getLogger().debug("Couldn't find any live surveys in category " + category);
			}
		}

		// Initialize the respondent now that we can be sure we have a survey,
		// user and pagination information all stored in the item
		//
		initRespondent(respondent);

		addMessageToModel(model, Model.TITLE, "msg.research.user.surveys.survey.pageTitle");

		return initResponse(model, response, respondent);
	}

	@RequestMapping(value = "**/research/user/surveys/survey", method = RequestMethod.POST)
	public Model submitAnswers(@ModelAttribute("respondent") @Valid WebModelRespondent respondent,
					BindingResult result,
					@RequestParam(value = "redirURL", required = false) String redirURL,
					@RequestParam(value = "redirView", required = false) String redirView,
					HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, respondent);

		addMessageToModel(model, Model.TITLE, "msg.research.user.surveys.survey.pageTitle");

		if ( !result.hasErrors() )
		{
			try
			{
				getLogger().debug("Performing respondent recording of " + respondent);

				ReturnType<List<ReturnType<Response>>> retVal = getRespondentManager().respond(respondent, respondent.getResponses());
				success(model, "msg.research.user.survey.index.successful", retVal);
			}
			catch (Errors errors)
			{
				fail(model, "msg.research.user.survey.index.failed", result, errors);
			}
			finally
			{
				// The fields that don't have errors need to be converted back
				// to the form field names (i.e. responses[..] => selectionResponses[..])  
				//
				// Get the next set of questions for the next page (if any)
				//
				initRespondent(respondent);
			}
		}

		return initResponse(model, response, respondent);
	}

	/**
	 * This method converts the incoming errors from the RespondentManager to the web model object.
	 * This overrides the superclass to translate any errors that are have a name of "responses[..]" to
	 * "selectionResponses[..]" or "openAnswerResponses[..]".
	 * 
	 * @param result The binding result that contains the form field bindings
	 * @param error The error message from the manager to convert 
	 */
	@Override
	protected void convertError(BindingResult result, ErrorMessage error)
	{
		getLogger().debug("Converting error " + error.getFieldName() + "(" + error.getMessageKey() + ")" );

		// This custom convert error method is here due to the PropertyEditor methods
		// not being called and thus a manual conversion method being necessary between
		// serial numbers and questions in the WebModelRespondent
		//
		String fieldName = error.getFieldName();
		if ( !Utils.isEmpty(fieldName) && fieldName.startsWith("responses["))
		{
			getLogger().debug("Found responses field error " + fieldName + " w/ value " + result.getRawFieldValue(fieldName));

			int questionId = Utils.extractIntegers(fieldName)[0];

			Question currQuestion = getQuestionManager().getById(questionId);

			String newFieldName;
			switch ( currQuestion.getQuestionType() )
			{
				case Question.TYPE_SINGLEANSWER_RADIO:
				case Question.TYPE_SINGLEANSWER_SELECT:
				case Question.TYPE_MULTIANSWER_CHECKBOX:
				{
					newFieldName = "selectionResponses[" + questionId + "]";
					break;
				}
				case Question.TYPE_OPENANSWER_MULTI:
				case Question.TYPE_OPENANSWER_SINGLE:
				default:
				{
					newFieldName = "openAnswerResponses[" + questionId + "]";
					break;
				}
			}

			result.addError(new FieldError(result.getObjectName(), newFieldName, result.getRawFieldValue(newFieldName), false, new String[] { error.getMessageKey() }, error.getParams(), error.getDefaultMessage()));

			getLogger().debug("Adding field error for " + newFieldName + ": " + result.getFieldError(newFieldName) );
				
		}
		else
		{
			super.convertError(result, error);
		}
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

	public QuestionManager getQuestionManager()
	{
		return questionManager;
	}

	@Autowired
	public void setQuestionManager(QuestionManager questionManager)
	{
		this.questionManager = questionManager;
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

	public AnswerManager getAnswerManager()
	{
		return answerManager;
	}

	@Autowired
	public void setAnswerManager(AnswerManager answerManager)
	{
		this.answerManager = answerManager;
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

	public SurveyCategoryManager getSurveyCategoryManager()
	{
		return surveyCategoryManager;
	}

	@Autowired
	public void setSurveyCategoryManager(SurveyCategoryManager surveyCategoryManager)
	{
		this.surveyCategoryManager = surveyCategoryManager;
	}

	@Override
	public CategoryManager<?, SurveyCategory> getCategoryManager()
	{
		return getSurveyCategoryManager();
	}

}
