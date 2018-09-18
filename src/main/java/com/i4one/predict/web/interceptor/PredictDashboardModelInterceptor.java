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
package com.i4one.predict.web.interceptor;

import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.BaseModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventManager;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.eventoutcome.EventOutcomeManager;
import com.i4one.predict.model.eventprediction.EventPrediction;
import com.i4one.predict.model.eventprediction.EventPredictionManager;
import com.i4one.predict.model.player.Player;
import com.i4one.predict.model.player.PlayerManager;
import com.i4one.predict.model.term.Term;
import com.i4one.predict.model.term.TermManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PredictDashboardModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	private TermManager termManager;
	private PlayerManager playerManager;
	private EventManager eventManager;
	private EventOutcomeManager eventOutcomeManager;
	private EventPredictionManager eventPredictionManager;

	private static final String PASTPREDICTIONS = "pastPredictions";
	private static final String NEWEVENTS = "newEvents";
	private static final String ENDINGSOON = "endingSoon";
	private static final String ALLOWANCETRIGGERS = "allowanceTriggers";
	private static final String PASTEVENTS = "pastEvents";
	private static final String FEATUREDOUTCOMES = "featuredOutcomes";

	private static final String SCORERANKINGS = "scoreRankings";
	private static final String ACCURACYRANKINGS = "accuracyRankings";
	private static final String WINSTREAKRANKINGS = "winStreakRankings";


	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		HttpServletRequest request = model.getRequest();
		User user = getUser(model);

		Map<String, Object> map = new HashMap<>();

		if ( request.getServletPath().endsWith("base/user/index.html") )
		{
			SingleClient client = model.getSingleClient();
	
			if ( user.exists() ) 
			{
				getLogger().debug("Found the index page, adding pending list of predictions to the model");
	
				// How many seconds until the user is eligible for each allowance type credit
				//
				Term currTerm = getTermManager().getLiveTerm(client);
				map.put(ALLOWANCETRIGGERS, getPlayerManager().getAllowanceEligibility(user, currTerm, model.getTimeInSeconds()));
	
				// The user's pending predictions sorted by post time
				//
				PaginationFilter pastPredictionsPagination = new SimplePaginationFilter();
				pastPredictionsPagination.setCurrentPage(0);
				pastPredictionsPagination.setPerPage(5);
				pastPredictionsPagination.setOrderBy("ser DESC");
				Set<EventPrediction> eventPredictions = getEventPredictionManager().getAllPredictionsByUser(user, currTerm, pastPredictionsPagination);
				map.put(PASTPREDICTIONS, eventPredictions);
	
				// The first three live events sorted by descending start time
				//
				PaginationFilter newEventsPagination = new SimplePaginationFilter();
				newEventsPagination.setCurrentPage(0);
				newEventsPagination.setPerPage(3);
				newEventsPagination.setOrderBy("starttm DESC, ser");
				map.put(NEWEVENTS, getEventManager().getLive(new CategoryPagination(new EventCategory(), model.getTimeInSeconds(), new ClientPagination(client, newEventsPagination))));
	
				// The last three live events sorted by end time
				//
				PaginationFilter endingSoonPagination = new SimplePaginationFilter();
				endingSoonPagination.setCurrentPage(0);
				endingSoonPagination.setPerPage(3);
				endingSoonPagination.setOrderBy("endtm, ser DESC");
				map.put(ENDINGSOON, getEventManager().getLive(new CategoryPagination(new EventCategory(), model.getTimeInSeconds(), new ClientPagination(client, endingSoonPagination))));
	
				// The last five recently closed events
				//
				PaginationFilter pastEventsPagination = new SimplePaginationFilter();
				pastEventsPagination.setCurrentPage(0);
				pastEventsPagination.setPerPage(5);
				pastEventsPagination.setOrderBy("endtm DESC, ser DESC");
				map.put(PASTEVENTS, getEventManager().getAllActualized(client, currTerm.getStartTimeSeconds(), pastEventsPagination));
	
				// The players in the neighborhood of the logged-in user
				//
				Player player = getPlayerManager().getPlayer(currTerm, user);
	
				int perPage = 5;
				int playerPage = (player.getScoreRank() - 1) / perPage;
	
				PaginationFilter rankingsPagination = new SimplePaginationFilter();
				rankingsPagination.setPerPage(perPage);
				rankingsPagination.setCurrentPage(playerPage);
				rankingsPagination.setOrderBy("scorerank, ser");
	
				map.put(SCORERANKINGS, getPlayerManager().getAllPlayers(currTerm, rankingsPagination));
	
				playerPage = (player.getAccuracyRank() - 1) / perPage;
				rankingsPagination.setCurrentPage(playerPage);
				rankingsPagination.setOrderBy("accuracyrank, ser");
	
				map.put(ACCURACYRANKINGS, getPlayerManager().getAllPlayers(currTerm, rankingsPagination));
	
				playerPage = (player.getWinStreakRank() - 1) / perPage;
				rankingsPagination.setCurrentPage(playerPage);
				rankingsPagination.setOrderBy("winstreakrank, ser");
	
				map.put(WINSTREAKRANKINGS, getPlayerManager().getAllPlayers(currTerm, rankingsPagination));
			}
			else // Front page with no user logged in
			{
				// Here we have a list of all live events for a given client,
				// we're going to single out one to feature
				//
				Map<Integer, EventOutcome> featuredOutcomes = new HashMap<>();
	
				// We want to feature 10 event outcomes at a time
				//
				//Long index = (long) currentTimeSeconds();
				Long index = model.getTimeInMillis();
	
				int targetSize = 10;
				for (int i = 0; featuredOutcomes.size() < targetSize && i < targetSize * 3; i++ )
				{
					EventOutcome randomOutcome = getRandomOutcome(model, new EventCategory(), index + i);
					featuredOutcomes.put(randomOutcome.getSer(), randomOutcome);
				}
	
				map.put(FEATUREDOUTCOMES, featuredOutcomes.values().toArray());
			}
		}

		return map;
	}

	protected EventOutcome getRandomOutcome(Model model, EventCategory category, Long index)
	{
		// We check for valid events (i.e. those with outcomes) and we're going
		// to go through all of the events before giving up
		//
		Set<Event> allEvents = getEventManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
			new ClientPagination(model.getSingleClient(), SimplePaginationFilter.NONE)));

		// Get a random event, filtering out those that don't have any possible outcomes
		//
		List<Event> allEventsArray = allEvents.stream().
			filter( (event) -> { return !event.getPossibleOutcomes().isEmpty(); })
			.collect(Collectors.toList());

		Collections.shuffle(allEventsArray);

		for ( int i = 0; i < allEventsArray.size(); i++ )
		{
			Event event = allEventsArray.get((int)((index + i) % allEventsArray.size()));
			//Set<EventOutcome> outcomes = getEventOutcomeManager().getEventOutcomes(event, SimplePaginationFilter.NONE);
			//if ( !event.isPlayable() && event.getPossibleOutcomes().isEmpty() )
			if ( !event.isPlayable(model.getTimeInSeconds()) )
			{
				// Try the next event, this one doesn't have any outcomes in it
				//
			}
			else
			{
				/*
				// Select a random outcome to feature
				//
				int outcomeIndex = (int) round(random() * outcomes.size());
				outcomeIndex %= outcomes.size();

				EventOutcome featuredOutcome = outcomes.get(outcomeIndex);
				*/
				EventOutcome featuredOutcome = event.getRandomPossibleOutcome();
				if ( featuredOutcome.exists() )
				{
					featuredOutcome.setEvent(event);
					return featuredOutcome;
				}
				else
				{
					// Try this same event again and see if we get an
					// outcome that has a likelihood greater than 0
					//
					i--;
				}
			}
		}

		return new EventOutcome();

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

	public EventPredictionManager getEventPredictionManager()
	{
		return eventPredictionManager;
	}

	@Autowired
	public void setEventPredictionManager(EventPredictionManager eventPredictionManager)
	{
		this.eventPredictionManager = eventPredictionManager;
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

	public PlayerManager getPlayerManager()
	{
		return playerManager;
	}

	@Autowired
	public void setPlayerManager(PlayerManager playerManager)
	{
		this.playerManager = playerManager;
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
}
