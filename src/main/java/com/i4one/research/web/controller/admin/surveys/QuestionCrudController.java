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

import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseCrudController;
import com.i4one.research.model.survey.answer.AnswerManager;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.question.QuestionManager;
import com.i4one.research.model.survey.question.QuestionRecord;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class QuestionCrudController extends BaseCrudController<QuestionRecord, Question>
{
	private QuestionManager questionManager;
	private AnswerManager answerManager;

	@Override
	public Model initRequest(HttpServletRequest request, Question modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof Question )
		{
			Question question = (Question)modelAttribute;

			question.setAnswers(getAnswerManager().getAnswers(question, SimplePaginationFilter.NONE));
		}

		// The type of questions and displays
		//
		// 0 - Single Answer: Radio Buttons
		// 1 - Single Answer: Drop-down
		// 2 - Multiple Answer: Checkboxes
		// 3 - Open Answer: Single Line
		// 4 - Open Answer: Multiple Lines
		//
		Map<Integer, String> questions = new LinkedHashMap<>();
		questions.put(Question.TYPE_SINGLEANSWER_RADIO, model.buildMessage("msg.research.admin.survey.updatequestion.singleanswerradio"));
		questions.put(Question.TYPE_SINGLEANSWER_SELECT, model.buildMessage("msg.research.admin.survey.updatequestion.singleanswerselect"));
		questions.put(Question.TYPE_MULTIANSWER_CHECKBOX, model.buildMessage("msg.research.admin.survey.updatequestion.multianswercheckbox"));
		questions.put(Question.TYPE_OPENANSWER_SINGLE, model.buildMessage("msg.research.admin.survey.updatequestion.openanswersingle"));
		questions.put(Question.TYPE_OPENANSWER_MULTI, model.buildMessage("msg.research.admin.survey.updatequestion.openanswermulti"));

		model.put("validQuestionType", questions);

		// The types of open answers allowed
		//
		Map<String, String> validAnswerTypes = new LinkedHashMap<>();
		validAnswerTypes.put("^[\\s\\S].*$", model.buildMessage("msg.research.admin.survey.updatequestion.validanswers.anyresponse"));
		validAnswerTypes.put("^[\\s\\S].+$", model.buildMessage("msg.research.admin.survey.updatequestion.validanswers.notempty"));
		validAnswerTypes.put("^\\d+(\\.\\d{1,})?$", model.buildMessage("msg.research.admin.survey.updatequestion.validanswers.numbers"));

		model.put("validAnswerTypes", validAnswerTypes);

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.research.admin.surveys.question";
	}

	@Override
	protected Manager<QuestionRecord, Question> getManager()
	{
		return getQuestionManager();
	}

	@RequestMapping(value = { "**/research/admin/surveys/removequestion" }, method = RequestMethod.GET)
	public void remove(@RequestParam(value="questionid") Integer questionId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		Model model = initRequest(request, null);

		Question question = new Question();
		if ( questionId != null )
		{
			question.setSer(questionId);
			question.loadedVersion();

			getLogger().debug("Removing question " + question);
		}

		if ( question.exists() )
		{
			// The database should handle the cascading removal thru the use of foreign keys
			//
			getQuestionManager().remove(question);

			// If we make it this far, we were successfull
			//
			success(model, "msg.research.admin.survey.removequestion.success");
		}
		else
		{
			fail(model, "msg.research.admin.survey.removequestion.failure", null, Errors.NO_ERRORS);
		}

		String redirURL = model.getBaseURL() + "/research/admin/surveys/update.html?id=" + question.getSurvey(false).getSer();
		response.sendRedirect(redirURL);

		redirOnSuccess(model, null, null, null, redirURL, request, response);
	}

	@RequestMapping(value = { "**/research/admin/surveys/clonequestion" }, method = RequestMethod.GET)
	public void clone(@RequestParam(value="questionid") Integer questionId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		Model model = initRequest(request, null);

		Question question = new Question();
		if ( questionId != null )
		{
			question.setSer(questionId);
			question.loadedVersion();
		}

		String redirURL = model.getBaseURL() + "/research/admin/surveys/update.html?id=" + question.getSurvey(false).getSer();
		if ( question.exists() )
		{
			ReturnType<Question> clonedQuestion = getQuestionManager().clone(question);

			// If we make it this far, we were successfull
			//
			success(model, "msg.research.admin.survey.clonequestion.success");
			redirURL = model.getBaseURL() + "/research/admin/surveys/updatequestion.html?surveyid=" + question.getSurvey(false).getSer() + "&questionid=" + clonedQuestion.getPost().getSer();
		}
		else
		{
			fail(model, "msg.research.admin.survey.clonequestion.failure", null, Errors.NO_ERRORS);
		}

		response.sendRedirect(redirURL);
		//redirOnSuccess(model, null, null, null, redirURL, request, response);
	}

	@RequestMapping(value = { "**/research/admin/surveys/updatequestion"}, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("question") Question question,
					@RequestParam(value = "surveyid", required = true) Integer surveyId,
					@RequestParam(value = "questionid", required = false) Integer questionId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = this.createUpdateImpl(question, questionId, request, response);

		question.getSurvey().setSer(surveyId);

		return initResponse(model, response, question);
	}

	@RequestMapping(value = "**/research/admin/surveys/updatequestion", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("question") Question question, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, question);

		try
		{
			if ( question.exists() )
			{
				getLogger().debug("Updating " + question);
				getQuestionManager().update(question);

				success(model, "msg.research.admin.survey.updatequestion.success");
			}
			else
			{
				getQuestionManager().create(question);

				success(model, "msg.research.admin.survey.createquestion.success");
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.research.admin.survey.updatequestion.failure", result, errors);
		}

		return initResponse(model, response, question);
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
