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
package com.i4one.predict.web.controller.user.events;

import static com.i4one.base.core.Utils.forceEmptyStr;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.base.web.controller.user.BaseUserViewController;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventManager;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.eventoutcome.EventOutcomeManager;
import com.i4one.predict.model.eventprediction.EventPrediction;
import com.i4one.predict.model.eventprediction.EventPredictionManager;
import com.i4one.predict.model.player.PlayerManager;
import com.i4one.predict.model.term.Term;
import com.i4one.predict.model.term.TermManager;
import com.i4one.predict.web.controller.user.PredictionHistoryPagination;
import com.i4one.predict.web.controller.user.WebModelEventPrediction;
import com.i4one.predict.web.interceptor.PredictBalanceModelInterceptor;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import static javax.imageio.ImageIO.write;
import static org.jfree.chart.ChartFactory.createPieChart;
import static org.jfree.chart.ChartFactory.createPieChart3D;

/**
 *
 * @author Hamid Badiozamani
 */
@Controller
public class PredictionViewController extends BaseUserViewController
{
	private TermManager termManager;
	private PlayerManager playerManager;
	private EventManager eventManager;
	private EventOutcomeManager eventOutcomeManager;
	private EventPredictionManager eventPredictionManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@RequestMapping(value = "**/predict/user/account/index")
	public Model getPredictionHistory(@ModelAttribute("predictionHistory") PredictionHistoryPagination predictionHistory, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, predictionHistory);
		SingleClient client = model.getSingleClient();

		addMessageToModel(model, Model.TITLE, "msg.predict.user.account.index.title");

		Term term = predictionHistory.getTerm();
		if ( !term.exists() )
		{
			term = getTermManager().getLiveTerm(client);
		}

		predictionHistory.setPredictions(getEventPredictionManager().getAllPredictionsByUser(model.getUser(), term, predictionHistory));

		return initResponse(model, response, predictionHistory);
	}

	@RequestMapping(value = "**/predict/user/holdings.pngg", produces="image/png")
	public void holdingsPNG(@RequestParam(value = "width", required = false, defaultValue = "250") int width,
				@RequestParam(value = "height", required = false, defaultValue = "150") int height,
				@RequestParam(value = "depth", required = false, defaultValue = "0.1") float depth,
				HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Model model = super.initRequest(request, null);

		// Plot a graph of the user's holdings
		//
		DefaultPieDataset data = new DefaultPieDataset();
		JFreeChart chart;
		if ( depth > 0.0f )
		{
			chart = createPieChart3D(null, (PieDataset)data, true, false, false);
		}
		else
		{
			chart = createPieChart(null, (PieDataset)data, true, false, false);
		}

		Plot plot = chart.getPlot();
		plot.setBackgroundAlpha(0.0f);
		if ( plot instanceof PiePlot3D )
		{
			PiePlot3D plot3d = (PiePlot3D)plot;
			plot3d.setDarkerSides(true);
			plot3d.setDepthFactor(depth);
		}

		// We want the labels to only appear in the legend
		//
		if ( plot instanceof PiePlot )
		{
			PiePlot piePlot = (PiePlot)plot;
			piePlot.setLabelGenerator(null);
		}

		User user = (User) model.get(UserAdminModelInterceptor.USER);

		PaginationFilter pagination = new SimplePaginationFilter();
		pagination.setCurrentPage(0);
		pagination.setPerPage(0);
		pagination.setOrderBy("predict.events.ser");

		Set<EventPrediction> eventPredictions = getEventPredictionManager().getPendingPredictionsByUser(user, pagination);

		// We can assume we have this because the user is logged in and this gets set for each page requested
		//
		int uninvested = ((UserBalance)model.get(PredictBalanceModelInterceptor.USERBALANCE)).getTotal();
		data.setValue(getMessageManager().buildMessage(model.getSingleClient(), "msg.predict.user.holdings.uninvested", model.getLanguage(), "amount", uninvested), uninvested);

		// Gather up all of the categories and tally the amount invested in each
		//
		Map<EventCategory, Integer> categories = new HashMap<>();
		eventPredictions.stream().forEach((currPred) ->
		{
			EventCategory currCategory = currPred.getEvent().getCategory();
			Integer amount = categories.get(currCategory);
			if ( amount != null )
			{
				amount += currPred.getQuantity();

			}
			else
			{
				amount = currPred.getQuantity();
			}

			categories.put(currCategory, amount);
		});

		categories.entrySet().stream().forEach((currCategory) ->
		{
			data.setValue(forceEmptyStr(currCategory.getKey().getTitle().get(model.getLanguage())), currCategory.getValue());
		});

		BufferedImage chartImage = chart.createBufferedImage(width, height);
		write(chartImage, "png", response.getOutputStream());
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		// We need the total number of pending predictions that the user has
		//
		User user = model.getUser();

		PaginationFilter pagination = new SimplePaginationFilter();
		pagination.setPerPage(0);
		pagination.setOrderBy("predict.events.ser");

		Set<EventPrediction> eventPredictions = getEventPredictionManager().getPendingPredictionsByUser(user, pagination);
		model.put("pendingPredictions", eventPredictions);

		return model;
	}

	@Override
	public Model initResponse(Model model, HttpServletResponse response, Object modelAttribute)
	{
		Model retVal = super.initResponse(model, response, modelAttribute);

		if ( modelAttribute instanceof EventPrediction )
		{
			EventPrediction prediction = (EventPrediction)modelAttribute;

			// These are all of the possible outcomes and other events in the same category
			//
			setPossibleOutcomes(model, prediction);
			setBidHistory(model, prediction);

			prediction.setEvent(getEventManager().getById(prediction.getEvent().getSer()));
		}

		return retVal;
	}

	@RequestMapping(value = "**/predict/user/events/bid", method = RequestMethod.GET)
	public Model getEvent(@ModelAttribute("prediction") WebModelEventPrediction prediction,
							@RequestParam(value = "eventid", required = false) Integer eventId,
							@RequestParam(value = "categoryid", required = false) Integer categoryId,
							BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, prediction);

		if ( eventId != null )
		{
			//prediction.getEvent().resetDelegateBySer(eventId);
			Event dbEvent = getEventManager().getById(eventId);

			prediction.setEvent(dbEvent);
		}
		else if ( categoryId != null)
		{
			EventCategory category = new EventCategory();
			category.setSer(categoryId);

			// We can go to the first game returned by the event manager
			//
			getLogger().debug("Getting live events for " + category);

			PaginationFilter pagination = new SimplePaginationFilter();
			pagination.setOrderBy("endtm, ser DESC");

			Set<Event> events = getEventManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
				new ClientPagination(model.getSingleClient(), pagination)));

			if ( !events.isEmpty() )
			{
				// Make the user default to the first event
				prediction.setEvent(events.iterator().next());

				// Make the user default to the last event
				//
				//prediction.setEvent(events..size() - 1));
			}
			else
			{
				getLogger().debug("Couldn't find any live events in category " + category);
			}
		}

		// Default to the minimum amount
		//
		prediction.setQuantity(prediction.getEvent().getMinBid());

		addMessageToModel(model, Model.TITLE, "msg.predict.event.bid.title");

		return initResponse(model, response, prediction);
	}

	@RequestMapping(value = "**/predict/user/events/bid", method = RequestMethod.POST)
	public Model placeBet(@ModelAttribute("prediction") @Valid WebModelEventPrediction prediction,
					BindingResult result,
					HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		getLogger().debug("Incoming prediction object is " + prediction);
		Model model = initRequest(request, prediction);

		addMessageToModel(model, Model.TITLE, "msg.predict.event.bid.title");

		try
		{
			getLogger().debug("Performing prediction recording of " + prediction);
			ReturnType<EventPrediction> processedEntry = getEventPredictionManager().create(prediction);

			// The raffle may have expired between the time the entry was validated and the time it takes
			// to commit to the database
			//
			if ( !processedEntry.getPost().exists() )
			{
				fail(model, "msg.predict.user.bid.expired", result, new Errors());
			}
			else
			{
				EventPrediction bid = processedEntry.getPost();
				prediction.copyOver(bid);

				success(model, "msg.predict.user.bid.successful", processedEntry, SubmitStatus.ModelStatus.SUCCESSFUL);
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.predict.user.bid.failed", result, errors);
		}

		return initResponse(model, response, prediction);
	}

	protected void setPossibleOutcomes(Model model, EventPrediction prediction)
	{
		// Get all of the possible outcomes
		//
		Map<Integer, EventOutcome> possibleOutcomes = new LinkedHashMap<>();
		Set<EventOutcome> outcomes = getEventOutcomeManager().getEventOutcomes(prediction.getEvent(), SimplePaginationFilter.NONE);
		outcomes.stream().forEach((outcome) ->
		{
			possibleOutcomes.put(outcome.getSer(), outcome);
		});

		model.put("outcomes", possibleOutcomes);
	}

	protected void setBidHistory(Model model, EventPrediction ep)
	{
		// Get all of previous bids for this event
		//
		Set<EventPrediction> bidHistory = getEventPredictionManager().getAllPredictions(ep.getEvent(false), ep.getUser(false), SimplePaginationFilter.DESC);

		model.put("bidHistory", bidHistory);
	}

	public EventOutcomeManager getEventOutcomeManager()
	{
		return eventOutcomeManager;
	}

	@Autowired
	public void setEventOutcomeManager(EventOutcomeManager eventOutcomeManager)
	{
		this.eventOutcomeManager = eventOutcomeManager;
	}

	public EventPredictionManager getEventPredictionManager()
	{
		return eventPredictionManager;
	}

	@Autowired
	public void setEventPredictionManager(EventPredictionManager eventPredictionManager)
	{
		this.eventPredictionManager = eventPredictionManager;
	}

	public EventManager getEventManager()
	{
		return eventManager;
	}

	@Autowired
	public void setEventManager(EventManager eventManager)
	{
		this.eventManager = eventManager;
	}

	public PlayerManager getPlayerManager()
	{
		return playerManager;
	}

	@Autowired
	public void setPlayerManager(PlayerManager playerManager)
	{
		this.playerManager = playerManager;
	}

	public TermManager getTermManager()
	{
		return termManager;
	}

	@Autowired
	public void setTermManager(TermManager termManager)
	{
		this.termManager = termManager;
	}

}
