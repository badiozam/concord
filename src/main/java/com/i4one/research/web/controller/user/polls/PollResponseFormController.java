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
package com.i4one.research.web.controller.user.polls;

import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.base.web.controller.user.BaseCategorizableListingController;
import com.i4one.research.model.poll.Poll;
import com.i4one.research.model.poll.PollAnswerManager;
import com.i4one.research.model.poll.PollManager;
import com.i4one.research.model.poll.PollResponse;
import com.i4one.research.model.poll.PollResponseManager;
import com.i4one.research.model.poll.PollResults;
import com.i4one.research.model.poll.PollResultsManager;
import com.i4one.research.model.poll.category.PollCategory;
import com.i4one.research.model.poll.category.PollCategoryManager;
import com.i4one.research.web.interceptor.PollCategoriesModelInterceptor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
public class PollResponseFormController extends BaseCategorizableListingController<Poll, PollCategory>
{
	private PollManager pollManager;
	private PollAnswerManager pollAnswerManager;
	private PollResponseManager pollResponseManager;
	private PollResultsManager pollResultsManager;
	private PollCategoryManager pollCategoryManager;

	private static final String POLL_RESULTS = "results";

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelPollResponse )
		{
			WebModelPollResponse pollResponse = (WebModelPollResponse)modelAttribute;

			pollResponse.setPollAnswerManager(getPollAnswerManager());
		}

		return model;
	}

	@Override
	public Model initResponse(Model model, HttpServletResponse response, Object modelAttribute)
	{
		Model retVal = super.initResponse(model, response, modelAttribute);

		if ( modelAttribute instanceof WebModelPollResponse )
		{
			WebModelPollResponse pollResponse = (WebModelPollResponse)modelAttribute;

			setTitle(model, pollResponse.getPoll().getTitle());

			// Either they just played it in processPoll(..), or they had played it
			// previously in pollForm(..), or the poll has expired but is up for
			// viewing and in either of these cases they're going to want
			// to see the results
			//
			if ( pollResponse.exists() || !pollResponse.getPoll().isAvailable() )
			{
				getLogger().debug("Poll response exists, computing results");

				PollResults results = getPollResultsManager().computeResults(pollResponse.getPoll(false));
				model.put(POLL_RESULTS, results);
			}
			else
			{
				getLogger().debug("Poll response does not exist");
			}
		}

		return retVal;
	}

	@Override
	protected Set<PollCategory> loadCategories(Model model)
	{
		return (Set<PollCategory>) model.get(PollCategoriesModelInterceptor.POLL_CATEGORIES);
	}
	

	@RequestMapping("**/research/user/polls/index")
	public ModelAndView listAllPolls(@RequestParam(value = "categoryid", defaultValue = "0") int categoryid, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, null);

		PollCategory category = viewListingImpl(categoryid, model, request, response);

		// Only display the live polls
		//
		Set<Poll> livePolls = getPollManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
			new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE)));

		User user = model.getUser();

		// Go through all of the available polls and load the status of any the 
		// user may have participated in
		//
		Map<Poll, PollResponse> polls = new LinkedHashMap<>();
		livePolls.forEach((poll) ->
		{
			polls.put(poll, getPollResponseManager().getActivity(poll, user));
		});

		getLogger().debug("We have " + polls.size() + " polls in category " + categoryid);

		model.put("polls", polls);
		addMessageToModel(model, Model.TITLE, "msg.research.user.polls.index.title");

		// We have more than one or we have no polls, in either case we can have the view
		// determine the outcome
		//
		ModelAndView retVal = new ModelAndView();
		retVal.addAllObjects(initResponse(model, response, null));

		return retVal;
	}

	protected void initPollResponse(WebModelPollResponse pollResponse) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException 
	{
		// Load the current location of the user (or create a new record for them if none exists)
		//
		PollResponse oldResponse = getPollResponseManager().getActivity(pollResponse.getPoll(), pollResponse.getUser());
		if ( oldResponse.exists() )
		{
			pollResponse.copyFrom(oldResponse);
		}

		getLogger().debug("Poll response: " + pollResponse);
	}

	@RequestMapping(value = "**/research/user/polls/poll", method = RequestMethod.GET)
	public Model pollForm(@ModelAttribute("pollResponse") WebModelPollResponse pollResponse,
					@RequestParam(value = "pollid", required = false) Integer pollId,
					@RequestParam(value = "categoryid", required = false) Integer categoryId,
					BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, pollResponse);

		if ( pollId != null )
		{
			pollResponse.getPoll().consume(getPollManager().getById(pollId));

			initPollResponse(pollResponse);
		}
		else if ( categoryId != null)
		{
			PollCategory category = getPollCategoryManager().getById(categoryId);

			PaginationFilter pagination = new SimplePaginationFilter();
			pagination.setOrderBy("orderweight, endtm, ser DESC");

			Set<Poll> polls = getPollManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
				new SiteGroupPagination(model.getSiteGroups(), pagination)));

			if ( !polls.isEmpty() )
			{
				// Make the user default to the first poll
				//
				pollResponse.setPoll(polls.iterator().next());
				initPollResponse(pollResponse);

				// The user had previously played this poll game
				//
				if ( pollResponse.getPollAnswer().exists() )
				{
					ReturnType<PollResponse> processedResponse = new ReturnType<>();
					processedResponse.setPre(pollResponse);
					processedResponse.setPost(pollResponse);

					success(model, "msg.research.user.polls.index.prevplayed", processedResponse, SubmitStatus.ModelStatus.PREVPLAYED);
				}
			}
			else
			{
				getLogger().debug("Couldn't find any live polls in category " + category);
			}
		}

		return initResponse(model, response, pollResponse);
	}


	@RequestMapping(value = "**/research/user/polls/poll", method = RequestMethod.POST)
	public Model processPoll(@ModelAttribute("pollResponse") @Valid WebModelPollResponse pollResponse, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Model model = initRequest(request, pollResponse);

		try
		{
			ReturnType<PollResponse> processedResponse = getPollResponseManager().create(pollResponse);

			if ( processedResponse.getPre().exists() )
			{
				success(model, "msg.research.user.polls.index.prevplayed", processedResponse, SubmitStatus.ModelStatus.PREVPLAYED);
			}
			else if ( !processedResponse.getPost().exists() )
			{
				fail(model, "msg.research.user.polls.index.expired", result, new Errors());
			}
			else
			{
				PollResponse userResponse = processedResponse.getPost();

				if ( userResponse.exists() )
				{
					success(model, "msg.research.user.polls.index.successful", processedResponse, SubmitStatus.ModelStatus.SUCCESSFUL);
				}
				else
				{
					fail(model, "msg.research.user.polls.index.error", result, new Errors());
				}
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.research.user.polls.index.failed", result, errors);
		}

		return initResponse(model, response, pollResponse);
	}

	public PollResponseManager getPollResponseManager()
	{
		return pollResponseManager;
	}

	@Autowired
	public void setPollResponseManager(PollResponseManager pollResponseManager)
	{
		this.pollResponseManager = pollResponseManager;
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

	public PollAnswerManager getPollAnswerManager()
	{
		return pollAnswerManager;
	}

	@Autowired
	public void setPollAnswerManager(PollAnswerManager pollAnswerManager)
	{
		this.pollAnswerManager = pollAnswerManager;
	}

	public PollResultsManager getPollResultsManager()
	{
		return pollResultsManager;
	}

	@Autowired
	public void setPollResultsManager(PollResultsManager pollResultsManager)
	{
		this.pollResultsManager = pollResultsManager;
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

	@Override
	public CategoryManager<?, PollCategory> getCategoryManager()
	{
		return getPollCategoryManager();
	}

}
