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
package com.i4one.promotion.web.controller.admin.trivias;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import com.i4one.base.model.category.CategorySelectStringifier;
import com.i4one.promotion.model.trivia.Trivia;
import com.i4one.promotion.model.trivia.TriviaAnswerManager;
import com.i4one.promotion.model.trivia.TriviaManager;
import com.i4one.promotion.model.trivia.TriviaRecord;
import com.i4one.promotion.model.trivia.category.TriviaCategory;
import com.i4one.promotion.model.trivia.category.TriviaCategoryManager;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
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
public class TriviaCrudController extends BaseTerminableSiteGroupTypeCrudController<TriviaRecord, Trivia>
{
	private TriviaManager triviaManager;
	private TriviaAnswerManager triviaAnswerManager;
	private TriviaCategoryManager triviaCategoryManager;

	@Override
	public Model initRequest(HttpServletRequest request, Trivia modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelTrivia )
		{
			WebModelTrivia trivia = (WebModelTrivia)modelAttribute;
			SingleClient client = model.getSingleClient();

			// Same thing for categories
			//
			Set<TriviaCategory> triviaCategories = getTriviaCategoryManager().getAllCategories(new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));
			model.put("categories", toSelectMapping(triviaCategories, new CategorySelectStringifier(client), model.getLanguage()));
		}

		// The type of questions and displays
		//
		// 0 - Single Answer: Radio Buttons
		// 1 - Single Answer: Drop-down
		//
		Map<Integer, String> questions = new LinkedHashMap<>();
		questions.put(Trivia.TYPE_SINGLEANSER_RADIO, model.buildMessage("msg.promotion.admin.trivias.update.singleanswerradio"));
		questions.put(Trivia.TYPE_SINGLEANSWER_SELECT, model.buildMessage("msg.promotion.admin.trivias.update.singleanswerselect"));

		model.put("validQuestionType", questions);

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.promotion.admin.trivias";
	}

	@Override
	protected Manager<TriviaRecord, Trivia> getManager()
	{
		return getTriviaManager();
	}

	@RequestMapping(value = { "**/promotion/admin/trivias/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("trivia") WebModelTrivia trivia,
					@RequestParam(value = "id", required = false) Integer triviaId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = createUpdateImpl(trivia, triviaId, request, response);

		// We only set the answers when displaying the update form which is why this isn't in initRequest.
		// The reason for this is that calling setTriviaAnswers(..) would erase any form elements that
		// were updated during POST submission
		//
		trivia.setTriviaAnswers(getTriviaAnswerManager().getAnswers(trivia, SimplePaginationFilter.NONE));

		if ( !trivia.exists() )
		{
			trivia.setIntro(model.buildIMessage("promotion.triviaManager.defaultIntro"));
			trivia.setOutro(model.buildIMessage("promotion.triviaManager.defaultOutro"));
		}

		return model;
	}

	@RequestMapping(value = "**/promotion/admin/trivias/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("trivia") WebModelTrivia trivia, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(trivia, result, request, response);
	}

	@RequestMapping(value = { "**/promotion/admin/trivias/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer triviaId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(triviaId, request, response);
	}

	public TriviaManager getTriviaManager()
	{
		return triviaManager;
	}

	@Autowired
	public void setTriviaManager(TriviaManager triviaManager)
	{
		this.triviaManager = triviaManager;
	}

	public TriviaCategoryManager getTriviaCategoryManager()
	{
		return triviaCategoryManager;
	}

	@Autowired
	public void setTriviaCategoryManager(TriviaCategoryManager triviaCategoryManager)
	{
		this.triviaCategoryManager = triviaCategoryManager;
	}

	public TriviaAnswerManager getTriviaAnswerManager()
	{
		return triviaAnswerManager;
	}

	@Autowired
	public void setTriviaAnswerManager(TriviaAnswerManager triviaAnswerManager)
	{
		this.triviaAnswerManager = triviaAnswerManager;
	}
}
