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
package com.i4one.promotion.web.controller.user.trivias;

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
import com.i4one.promotion.model.trivia.Trivia;
import com.i4one.promotion.model.trivia.TriviaAnswerManager;
import com.i4one.promotion.model.trivia.TriviaManager;
import com.i4one.promotion.model.trivia.TriviaResponse;
import com.i4one.promotion.model.trivia.TriviaResponseManager;
import com.i4one.promotion.model.trivia.category.TriviaCategory;
import com.i4one.promotion.model.trivia.category.TriviaCategoryManager;
import com.i4one.promotion.web.interceptor.PromotionCategoriesModelInterceptor;
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
public class TriviaResponseFormController extends BaseCategorizableListingController<Trivia, TriviaCategory>
{
	private TriviaManager triviaManager;
	private TriviaAnswerManager triviaAnswerManager;
	private TriviaResponseManager triviaResponseManager;
	private TriviaCategoryManager triviaCategoryManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelTriviaResponse )
		{
			WebModelTriviaResponse triviaResponse = (WebModelTriviaResponse)modelAttribute;

			triviaResponse.setTriviaAnswerManager(getTriviaAnswerManager());
		}

		return model;
	}

	@Override
	public Model initResponse(Model model, HttpServletResponse response, Object modelAttribute)
	{
		Model retVal = super.initResponse(model, response, modelAttribute);

		if ( modelAttribute instanceof WebModelTriviaResponse )
		{
			WebModelTriviaResponse triviaResponse = (WebModelTriviaResponse)modelAttribute;

			setTitle(model, triviaResponse.getTrivia().getTitle());
		}

		return retVal;
	}

	@Override
	protected Set<TriviaCategory> loadCategories(Model model)
	{
		return (Set<TriviaCategory>) model.get(PromotionCategoriesModelInterceptor.TRIVIA_CATEGORIES);
	}

	@RequestMapping("**/promotion/user/trivias/index")
	public ModelAndView listAllTrivias(@RequestParam(value = "categoryid", defaultValue = "0") int categoryid, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, null);

		TriviaCategory category = super.viewListingImpl(categoryid, model, request, response);

		// Only display the live trivias
		//
		Set<Trivia> liveTrivias = getTriviaManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
			new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE)));

		User user = model.getUser();

		// Go through all of the available trivias and load the status of any the 
		// user may have participated in
		//
		Map<Trivia, TriviaResponse> trivias = new LinkedHashMap<>();
		liveTrivias.forEach((trivia) ->
		{
			trivias.put(trivia, getTriviaResponseManager().getActivity(trivia, user));
		});

		getLogger().debug("We have " + trivias.size() + " trivias in category " + categoryid);

		model.put("trivias", trivias);
		addMessageToModel(model, Model.TITLE, "msg.promotion.user.trivias.index.title");

		// We have more than one or we have no trivias, in either case we can have the view
		// determine the outcome
		//
		ModelAndView retVal = new ModelAndView();
		retVal.addAllObjects(initResponse(model, response, null));

		return retVal;
	}

	protected void initTriviaResponse(WebModelTriviaResponse triviaResponse) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException 
	{
		TriviaResponse oldResponse = getTriviaResponseManager().getActivity(triviaResponse.getTrivia(), triviaResponse.getUser());
		if ( oldResponse.exists() )
		{
			triviaResponse.copyFrom(oldResponse);
		}

		getLogger().debug("Trivia response: " + triviaResponse);
	}

	@RequestMapping(value = "**/promotion/user/trivias/trivia", method = RequestMethod.GET)
	public Model triviaForm(@ModelAttribute("triviaResponse") WebModelTriviaResponse triviaResponse,
					@RequestParam(value = "triviaid", required = false) Integer triviaId,
					@RequestParam(value = "categoryid", required = false) Integer categoryId,
					BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, triviaResponse);

		if ( triviaId != null )
		{
			triviaResponse.setTrivia(getTriviaManager().getById(triviaId));

			initTriviaResponse(triviaResponse);
		}
		else if ( categoryId != null)
		{
			TriviaCategory category = getTriviaCategoryManager().getById(categoryId);

			PaginationFilter pagination = new SimplePaginationFilter();
			pagination.setOrderBy("orderweight, endtm, ser DESC");

			Set<Trivia> trivias = getTriviaManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
				new SiteGroupPagination(model.getSiteGroups(), pagination)));

			if ( !trivias.isEmpty() )
			{
				// Make the user default to the first trivia
				//
				triviaResponse.setTrivia(trivias.iterator().next());
				initTriviaResponse(triviaResponse);

				// The user had previously played this trivia game
				//
				if ( triviaResponse.getTriviaAnswer().exists() )
				{
					ReturnType<TriviaResponse> processedResponse = new ReturnType<>();
					processedResponse.setPre(triviaResponse);
					processedResponse.setPost(triviaResponse);

					success(model, "msg.promotion.user.trivias.index.prevplayed", processedResponse, SubmitStatus.ModelStatus.PREVPLAYED);
				}
			}
			else
			{
				getLogger().debug("Couldn't find any live trivias in category " + category);
			}
		}

		return initResponse(model, response, triviaResponse);
	}


	@RequestMapping(value = "**/promotion/user/trivias/trivia", method = RequestMethod.POST)
	public Model processTrivia(@ModelAttribute("triviaResponse") @Valid WebModelTriviaResponse triviaResponse, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Model model = initRequest(request, triviaResponse);

		try
		{
			ReturnType<TriviaResponse> processedResponse = getTriviaResponseManager().create(triviaResponse);

			if ( processedResponse.getPre().exists() )
			{
				success(model, "msg.promotion.user.trivias.index.prevplayed", processedResponse, SubmitStatus.ModelStatus.PREVPLAYED);
			}
			else if ( !processedResponse.getPost().exists() )
			{
				fail(model, "msg.promotion.user.trivias.index.expired", result, new Errors());
			}
			else
			{
				TriviaResponse userResponse = processedResponse.getPost();

				if ( userResponse.isCorrect() )
				{
					success(model, "msg.promotion.user.trivias.index.successful", processedResponse, SubmitStatus.ModelStatus.SUCCESSFUL);
				}
				else
				{
					fail(model, "msg.promotion.user.trivias.index.wrong", result, new Errors());
				}
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.promotion.user.trivias.index.failed", result, errors);
		}

		return initResponse(model, response, triviaResponse);
	}

	public TriviaResponseManager getTriviaResponseManager()
	{
		return triviaResponseManager;
	}

	@Autowired
	public void setTriviaResponseManager(TriviaResponseManager triviaResponseManager)
	{
		this.triviaResponseManager = triviaResponseManager;
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

	public TriviaAnswerManager getTriviaAnswerManager()
	{
		return triviaAnswerManager;
	}

	@Autowired
	public void setTriviaAnswerManager(TriviaAnswerManager triviaAnswerManager)
	{
		this.triviaAnswerManager = triviaAnswerManager;
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

	@Override
	public CategoryManager<?, TriviaCategory> getCategoryManager()
	{
		return getTriviaCategoryManager();
	}
}
