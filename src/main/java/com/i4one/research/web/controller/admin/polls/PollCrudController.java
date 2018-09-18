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
package com.i4one.research.web.controller.admin.polls;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import com.i4one.base.model.category.CategorySelectStringifier;
import com.i4one.research.model.poll.Poll;
import com.i4one.research.model.poll.PollAnswerManager;
import com.i4one.research.model.poll.PollManager;
import com.i4one.research.model.poll.PollRecord;
import com.i4one.research.model.poll.category.PollCategory;
import com.i4one.research.model.poll.category.PollCategoryManager;
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
public class PollCrudController extends BaseTerminableSiteGroupTypeCrudController<PollRecord, Poll>
{
	private PollManager pollManager;
	private PollAnswerManager pollAnswerManager;
	private PollCategoryManager pollCategoryManager;

	@Override
	public Model initRequest(HttpServletRequest request, Poll modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelPoll )
		{
			WebModelPoll poll = (WebModelPoll)modelAttribute;
			SingleClient client = model.getSingleClient();

			// Same thing for categories
			//
			Set<PollCategory> pollCategories = getPollCategoryManager().getAllCategories(new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));
			model.put("categories", toSelectMapping(pollCategories, new CategorySelectStringifier(client), model.getLanguage()));
		}

		// The type of questions and displays
		//
		// 0 - Single Answer: Radio Buttons
		// 1 - Single Answer: Drop-down
		//
		Map<Integer, String> questions = new LinkedHashMap<>();
		questions.put(Poll.TYPE_SINGLEANSWER_RADIO, model.buildMessage("msg.research.admin.polls.update.singleanswerradio"));
		questions.put(Poll.TYPE_SINGLEANSWER_SELECT, model.buildMessage("msg.research.admin.polls.update.singleanswerselect"));

		model.put("validQuestionType", questions);

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.research.admin.polls";
	}

	@Override
	protected Manager<PollRecord, Poll> getManager()
	{
		return getPollManager();
	}

	@RequestMapping(value = { "**/research/admin/polls/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("poll") WebModelPoll poll,
					@RequestParam(value = "id", required = false) Integer pollId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = createUpdateImpl(poll, pollId, request, response);

		// We only set the answers when displaying the update form which is why this isn't in initRequest.
		// The reason for this is that calling setPollAnswers(..) would erase any form elements that
		// were updated during POST submission
		//
		poll.setPollAnswers(getPollAnswerManager().getAnswers(poll));

		if ( !poll.exists() )
		{
			poll.setPollingStartTimeSeconds(poll.getStartTimeSeconds());
			poll.setPollingEndTimeSeconds(poll.getEndTimeSeconds());

			poll.setIntro(model.buildIMessage("research.pollManager.defaultIntro"));
			poll.setOutro(model.buildIMessage("research.pollManager.defaultOutro"));
		}

		return model;
	}

	@RequestMapping(value = "**/research/admin/polls/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("poll") WebModelPoll poll, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(poll, result, request, response);
	}

	@RequestMapping(value = { "**/research/admin/polls/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer pollId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(pollId, request, response);
	}

	public PollManager getPollManager()
	{
		return pollManager;
	}

	@Autowired
	public void setPollManager(PollManager pollManager)
	{
		this.pollManager = pollManager;
	}

	public PollCategoryManager getPollCategoryManager()
	{
		return pollCategoryManager;
	}

	@Autowired
	public void setPollCategoryManager(PollCategoryManager pollCategoryManager)
	{
		this.pollCategoryManager = pollCategoryManager;
	}

	public PollAnswerManager getPollAnswerManager()
	{
		return pollAnswerManager;
	}

	@Autowired
	public void setPollAnswerManager(PollAnswerManager pollAnswerManager)
	{
		this.pollAnswerManager = pollAnswerManager;
	}
}
