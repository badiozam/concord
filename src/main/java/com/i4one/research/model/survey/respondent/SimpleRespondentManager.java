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
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.question.QuestionManager;
import com.i4one.research.model.survey.response.Response;
import com.i4one.research.model.survey.response.ResponseManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Hamid Badiozamani
 */
public class SimpleRespondentManager extends BaseActivityManager<RespondentRecord, Respondent, Survey> implements RespondentManager
{
	private QuestionManager questionManager;
	private ResponseManager responseManager;

	public SimpleRespondentManager()
	{
		super();
	}

	@Override
	public boolean isEligible(Survey survey, User user)
	{
		RespondentRecord delegate = getDao().get(survey.getSer(), user.getSer());
		if ( delegate == null )
		{
			// If the user hasn't started, they're eligible
			//
			return true;
		}
		else
		{
			// If the user hasn't finished they can continue
			//
			return !delegate.getHasfinished();
		}
	}

	@Override
	public Respondent getActivity(Survey survey, User user)
	{
		if ( survey.exists() && user.exists() )
		{
			RespondentRecord delegate = getDao().get(survey.getSer(), user.getSer());

			// If the current survey is live but this user doesn't have a player record
			// we create one
			//
			if ( delegate == null )
			{		
				getLogger().debug("No respondent entry for " + survey + " and user " + user);
				delegate = new RespondentRecord();
				if ( survey.isLive(Utils.currentTimeSeconds()) )
				{
					delegate.setUserid(user.getSer());
					delegate.setItemid(survey.getSer());

					if ( survey.getRandomize() )
					{
						Random random = new Random();
						delegate.setCurrentpage(random.nextInt(survey.getTotalPages()));
					}
					else
					{
						delegate.setCurrentpage(0);
					}
					delegate.setStartpage(delegate.getCurrentpage());
					delegate.setTimestamp(0);

					// Create an empty record for this player for now
					//
					getDao().insert(delegate);
				}
				else
				{
					// Non-existent member for the given non-live survey, ignore
					//
				}
			}

			getLogger().debug("Returning: " + delegate);
			return new Respondent(delegate);
		}
		else
		{
			getLogger().info("Non-existent survey and/or user given, skipping initialization");
			// Non-existent user/survey = non-existent player
			//
			return new Respondent();
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<List<ReturnType<Response>>> respond(Respondent respondent, Map<Question, List<Response>> responses)
	{
		// Gather errors as we go through the survey
		//
		Errors errors = new Errors();

		// Lock the record to prevent double writes
		//
		Respondent dbRespondent = getActivity(respondent.getSurvey(false), respondent.getUser(false));
		RespondentRecord oldRecord = getDao().getBySer(dbRespondent.getSer(), true);

		Survey survey = dbRespondent.getSurvey();
		getLogger().debug("respond(..) called with " + responses);

		if ( dbRespondent.getHasFinished() )
		{
			errors.addError(getInterfaceName() + ".respond", new ErrorMessage("msg.survey.respondentManager.respond.completed", "You have already completed this survey", new Object[] { "respondent", dbRespondent}, null));
			throw errors;
		}
		/*
		else if ( respondent.getCurrentPage() > dbRespondent.getCurrentPage() )
		{
			errors.addError(new ErrorMessage("msg.survey.respondentManager.respond.skipped", "Looks like you've skipped a page. Please complete page $nextPage first before attempting $skippedPage", new Object[] { "respondent", dbRespondent, "nextPage", dbRespondent.getCurrentPage() + 1, "skippedPage", respondent.getCurrentPage() }, null));
		}
		else if ( respondent.getCurrentPage() < dbRespondent.getCurrentPage() )
		{
			errors.addError(new ErrorMessage("msg.survey.respondentManager.respond.completedpage", "You have already submitted your answers for this page, please proceed to the next page", new Object[] { "respondent", dbRespondent, "nextPage", dbRespondent.getCurrentPage() + 1, "completedPage", respondent.getCurrentPage() }, null));
		}
		*/
		else
		{
			int currTime = Utils.currentTimeSeconds();

			PaginationFilter pagination = new SimplePaginationFilter();
			pagination.setOrderBy("orderweight, ser");
			pagination.setCurrentPage(dbRespondent.getCurrentPage());
			pagination.setPerPage(survey.getPerPage());
	
			// First make sure all of the questions we need are in the incoming responses
			//
			Set<Question> questions = getQuestionManager().getQuestions(survey, pagination);
			int questionNo = dbRespondent.getSurvey().getPerPage() * ( dbRespondent.getCurrentPage() - dbRespondent.getStartPage());
			for (Question question : questions)
			{
				questionNo++;
				String fieldName = "responses[" + question.getSer() + "]";
				if ( !responses.containsKey(question) || // The question isn't even in the map
					responses.get(question).isEmpty() ) // Question is here but no responses are listed
				{
					getLogger().debug("No responses for " + fieldName + ": " + responses.get(question));

					errors.addError(fieldName, new ErrorMessage(fieldName, "msg.survey.respondentManager.respond.noresponse", "You have not responded to question #$questionNo", new Object[] { "questionNo", questionNo, "question", question, "respondent", dbRespondent }, null));
				}
				else
				{
					getLogger().debug("Found responses for " + fieldName + ": " + responses.get(question));

					// We check the multi-answer questions only if the min/max are set
					//
					if ( question.getQuestionType() == Question.TYPE_MULTIANSWER_CHECKBOX && question.getMinResponses() > 0 && question.getMaxResponses() > 0 )
					{
						List<Response> currResponses = responses.get(question);
						if ( currResponses.size() < question.getMinResponses() )
						{
							errors.addError(fieldName, new ErrorMessage(fieldName, "msg.survey.respondentManager.respond.minresponses", "Please select at least $question.minResponses responses to question #$questionNo", new Object[] { "questionNo", questionNo, "question", question, "respondent", dbRespondent, "responses", responses }, null));
						}
						else if ( currResponses.size() > question.getMaxResponses() )
						{
							errors.addError(fieldName, new ErrorMessage(fieldName, "msg.survey.respondentManager.respond.maxresponses", "Please select no more than $question.maxResponses responses to question #$questionNo", new Object[] { "questionNo", questionNo, "question", question, "respondent", dbRespondent, "responses", responses }, null));
						}
					}
					else if ( question.getQuestionType() == Question.TYPE_OPENANSWER_MULTI || question.getQuestionType() == Question.TYPE_OPENANSWER_SINGLE )
					{
						// Check to make sure the open answer is qualified
						//
						String openAnswer = Utils.forceEmptyStr(responses.get(question).get(0).getOpenAnswer());
						if ( !openAnswer.matches(question.getValidAnswer()) )
						{
							getLogger().debug("Open answer '" + openAnswer + "' does not match " + question.getValidAnswer());

							errors.addError(fieldName, new ErrorMessage(fieldName, "msg.survey.respondentManager.respond.invalidanswer", "Please enter a valid response to question #$questionNo", new Object[] { "questionNo", questionNo, "question", question, "respondent", dbRespondent, "responses", responses }, null));
						}
					}
					else if ( question.getQuestionType() == Question.TYPE_SINGLEANSWER_SELECT )
					{
						// Check to make sure the answer selected from the drop down is a valid one (and not the place-holder)
						//
						if ( !responses.get(question).get(0).getAnswer().exists() )
						{
							errors.addError(fieldName, new ErrorMessage(fieldName, "msg.survey.respondentManager.respond.noresponse", "You have not responded to question #$questionNo", new Object[] { "questionNo", questionNo, "question", question, "respondent", dbRespondent, "responses", responses }, null));
						}
					}
					else // if ( question.getQuestionType() == Question.TYPE_SINGLEANSER_RADIO )
					{
						// Nothing to do with radio button selections
					}
				}
			}

			// Only continue with the recording if all of the questions were properly answered
			//
			if ( errors.hasErrors() )
			{
				throw errors;
			}
			else
			{
				ArrayList<ReturnType<Response>> responseCreateVals = new ArrayList<>();

				responses.entrySet().stream().forEach((questionResponse) ->
				{
					questionResponse.getValue().stream().forEach((currResponse) ->
					{
						// Override any inconsistencies
						//
						currResponse.setTimeStampSeconds(currTime);
						currResponse.setQuestion(questionResponse.getKey());
						currResponse.setRespondent(dbRespondent);

						// Consider updating this to addChain(..) sequences. The only problem right now
						// is that addChain(..) overwrites the current value instead of keeping a list
						// of method calls
						//
						ReturnType<Response> createVal = getResponseManager().create(currResponse);
						responseCreateVals.add(createVal);
					});
				});
	
				// We were successful, and therefore advance to the next page (if any)
				//
				dbRespondent.setCurrentpage(dbRespondent.getCurrentPage() + 1);
				dbRespondent.setLastPlayedTimeSeconds(Utils.currentTimeSeconds());
				dbRespondent.setHasFinished();

				ReturnType<List<ReturnType<Response>>> retVal = new ReturnType<>();

				ReturnType<Respondent> updateVal = updateInternal(oldRecord, dbRespondent);
				retVal.addChain(this, "update", updateVal);

				retVal.setPost(responseCreateVals);
				return retVal;
			}
		}
	}

	@Override
	public Set<Respondent> getAllRespondents(Survey survey, PaginationFilter pagination)
	{
		return this.convertDelegates(getDao().getByItem(survey.getSer(), pagination));
	}

	@Override
	public Respondent emptyInstance()
	{
		return new Respondent();
	}

	@Override
	public RespondentRecordDao getDao()
	{
		return (RespondentRecordDao)super.getDao();
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

	public ResponseManager getResponseManager()
	{
		return responseManager;
	}

	@Autowired
	public void setResponseManager(ResponseManager responseManager)
	{
		this.responseManager = responseManager;
	}
}