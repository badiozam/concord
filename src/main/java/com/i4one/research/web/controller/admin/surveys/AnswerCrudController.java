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
import com.i4one.research.model.survey.answer.Answer;
import com.i4one.research.model.survey.answer.AnswerManager;
import com.i4one.research.model.survey.answer.AnswerRecord;
import com.i4one.research.model.survey.response.Response;
import com.i4one.research.model.survey.response.ResponseManager;
import java.io.IOException;
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

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AnswerCrudController extends BaseCrudController<AnswerRecord, Answer>
{
	private AnswerManager answerManager;
	private ResponseManager responseManager;

	@Override
	protected String getMessageRoot()
	{
		return "msg.research.admin.surveys.answer";
	}

	@Override
	protected Manager<AnswerRecord, Answer> getManager()
	{
		return getAnswerManager();
	}

	@RequestMapping(value = { "**/research/admin/surveys/removeanswer" }, method = RequestMethod.GET)
	public void remove(@RequestParam(value="answerid") Integer answerId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		Model model = initRequest(request, null);

		Answer answer = new Answer();
		if ( answerId != null )
		{
			answer.setSer(answerId);
			answer.loadedVersion();

			getLogger().debug("Removing answer " + answer);
		}

		if ( answer.exists() )
		{
			// The database should handle the cascading removal thru the use of foreign keys
			//
			getAnswerManager().remove(answer);

			// If we make it this far, we were successfull
			//
			success(model, "msg.research.admin.survey.removeanswer.success");
		}
		else
		{
			fail(model, "msg.research.admin.survey.removeanswer.failure", null, Errors.NO_ERRORS);
		}

		String redirURL = "updatequestion.html?questionid=" + answer.getQuestion().getSer() + "&surveyid=" + answer.getQuestion().getSurvey(false).getSer();
		response.sendRedirect(redirURL);

		redirOnSuccess(model, null, null, null, redirURL, request, response);
	}

	@RequestMapping(value = { "**/research/admin/surveys/cloneanswer" }, method = RequestMethod.GET)
	public void clone(@RequestParam(value="answerid") Integer answerId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		Model model = initRequest(request, null);

		Answer answer = new Answer();
		if ( answerId != null )
		{
			answer.setSer(answerId);
			answer.loadedVersion();
		}

		String redirURL = "updatequestion.html?questionid=" + answer.getQuestion(false).getSer();
		if ( answer.exists() )
		{
			ReturnType<Answer> clonedAnswer = getAnswerManager().clone(answer);

			// If we make it this far, we were successfull
			//
			success(model, "msg.research.admin.survey.cloneanswer.success");
			redirURL = "updateanswer.html?questionid=" + answer.getQuestion(false).getSer() + "&answerid=" + clonedAnswer.getPost().getSer();
		}
		else
		{
			fail(model, "msg.research.admin.survey.cloneanswer.failure", null, Errors.NO_ERRORS);
		}

		response.sendRedirect(redirURL);
		//redirOnSuccess(model, null, null, null, redirURL, request, response);
	}

	@RequestMapping(value = { "**/research/admin/surveys/updateanswer"}, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("answer") Answer answer,
					@RequestParam(value = "questionid", required = true) Integer questionId,
					@RequestParam(value = "answerid", required = false) Integer answerId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model retVal = createUpdateImpl(answer, answerId, request, response);

		answer.getQuestion().setSer(questionId);

		return retVal;
	}

	@RequestMapping(value = "**/research/admin/surveys/updateanswer", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("answer") Answer answer, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, answer);

		try
		{
			if ( answer.exists() )
			{
				getLogger().debug("Updating " + answer);
				getAnswerManager().update(answer);

				success(model, "msg.research.admin.survey.updateanswer.success");
			}
			else
			{
				getAnswerManager().create(answer);
				success(model, "msg.research.admin.survey.createanswer.success");
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.research.admin.survey.updateanswer.failure", result, errors);
		}

		return initResponse(model, response, answer);
	}

	@RequestMapping(value = { "**/research/admin/surveys/reportanswer" }, method = RequestMethod.GET)
	public Model report(@RequestParam(value="answerid") Integer answerId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		Model model = initRequest(request, null);

		Answer answer = new Answer();
		if ( answerId != null )
		{
			answer.setSer(answerId);
			answer.loadedVersion();
		}

		if ( answer.exists() )
		{
			Set<Response> responses = getResponseManager().getAllResponsesByAnswer(answer, SimplePaginationFilter.NONE);
			model.put("responses", responses);
			model.put("answer", answer);

			// If we make it this far, we were successfull
			//
			success(model, "msg.research.admin.survey.cloneanswer.success");
		}
		else
		{
			fail(model, "msg.research.admin.survey.cloneanswer.failure", null, Errors.NO_ERRORS);
		}

		return initResponse(model, response, null);
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
}
