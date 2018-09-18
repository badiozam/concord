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
package com.i4one.predict.model.jobs;

import com.i4one.base.core.Utils;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.jobs.BaseQuartzJob;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.model.user.UserManager;
import com.i4one.predict.model.eventprediction.EventPrediction;
import com.i4one.predict.model.eventprediction.EventPredictionManager;
import com.i4one.predict.model.player.Player;
import com.i4one.predict.model.player.PlayerManager;
import com.i4one.predict.model.term.Term;
import com.i4one.predict.model.term.TermManager;
import java.util.HashMap;
import java.util.Set;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Hamid Badiozamani
 */
@DisallowConcurrentExecution 
public class PlayerStatsJob extends BaseQuartzJob
{
	private int iterationUserLimit;

	private PlayerManager playerManager;
	private BalanceManager balanceManager;
	private UserManager userManager;
	private TermManager termManager;
	private SingleClientManager clientManager;
	private EventPredictionManager eventPredictionManager;
	private UserBalanceManager userBalanceManager;

	public PlayerStatsJob()
	{
		super();

		// By default, process 10 users at a time
		//
		iterationUserLimit = 10;
	}

	@Override
	protected void doExecuteInternal(JobExecutionContext jec) throws JobExecutionException
	{
		int currentTimeSeconds = Utils.currentTimeSeconds();

		getLogger().debug("Executing " + this + " at " + currentTimeSeconds);
		getClientManager().getAllClients(SingleClient.getRoot(), SimplePaginationFilter.NONE).stream().forEach( (currClient) ->
		{
			updatePlayers(jec, currentTimeSeconds, currClient);
			updateRanks(jec, currentTimeSeconds, currClient);
		});
		getLogger().debug("Done with " + this);
	}

	protected void updateRanks(JobExecutionContext jec, int currentTimeSeconds, SingleClient client)
	{
		updateAllRanks(client, currentTimeSeconds, new ScoreRankUpdater(this));
		updateAllRanks(client, currentTimeSeconds, new WinStreakRankUpdater(this));
		updateAllRanks(client, currentTimeSeconds, new AccuracyRankUpdater(this));
	}

	private void updateAllRanks(SingleClient client, int currentTimeSeconds, RankUpdater rankUpdater)
	{
		rankUpdater.initPagination();

		getTermManager().getLive(new TerminablePagination(currentTimeSeconds, new ClientPagination(client, SimplePaginationFilter.NONE))).stream().forEach((currTerm) ->
		{
			for ( int numPlayers = 0; rankUpdater.getPagination().getCurrentPage() <= 0 || numPlayers >= rankUpdater.getPagination().getPerPage(); )
			{
				numPlayers = 0;
				for ( Player currPlayer : getPlayerManager().getAllPlayers(currTerm, rankUpdater.getPagination()) )
				{
					// We're to check next round as well
					//
					numPlayers++;

					// Perform the update
					//
					rankUpdater.execute(currPlayer, numPlayers);
				}

				getLogger().trace("Rank updater processed  " + numPlayers + " w/ previous page at " + rankUpdater.getPagination().getCurrentPage());

				// Move up by the number of players we processed
				//
				rankUpdater.getPagination().setNextPage(true);
			}
		});
	}

	protected void updatePlayers(JobExecutionContext jec, int currentTimeSeconds, SingleClient client)
	{
		Term currTerm = getTermManager().getLatestTerm(client);
		getLogger().debug("Computing chart for " + client + " w/ current term {}", currTerm);

		if ( currTerm.exists() )
		{
			PaginationFilter userPagination = new SimplePaginationFilter();
			userPagination.setCurrentPage(0);
			userPagination.setPerPage(getIterationUserLimit());
			userPagination.setOrderBy("ser");
	
			getLogger().trace("Setting pagination to " + userPagination);
	
			for ( int numUsers = 0; userPagination.getCurrentPage() <= 0 || numUsers >= userPagination.getPerPage(); )
			{
				numUsers = 0;
				for ( User currUser : getUserManager().getAllMembers(client, userPagination) )
				{
					// We're to check next round as well
					//
					numUsers++;
	
					Player player = getPlayerManager().getPlayer(currTerm, currUser);
					if ( !player.exists() )
					{
						player = new Player();
						player.setUser(currUser);
						player.setTerm(currTerm);
					}
	
					// Modified to point balance per Reg: 6/19/2017
					//
					// Get the most recent total
					//
					//Balance predictionBalance = getBalanceManager().getBalance(currTerm);
					//UserBalance userBalance = getUserBalanceManager().getUserBalance(currUser, predictionBalance);
					Balance predictionBalance = getBalanceManager().getDefaultBalance(client);
					UserBalance userBalance = getUserBalanceManager().getUserBalance(currUser, predictionBalance);
					player.setScore(userBalance.getTotal());
	
					// Reset the stats so they can be recomputed
					//
					player.setCorrect(0);
					player.setIncorrect(0);
					player.setTotal(0);
					player.setPending(0);
					player.setAccuracy(0.0f);
	
					double correctRating = 0;
					double incorrectRating = 0;
	
					// Calculate the number of correct/incorrect predictions and while we're at it we can get the last time
					// the user made a prediction
					//
					int lastPlayedTime = 0;
					int currStreak = 0;
					int winStreak = 0;
	
					HashMap<Integer, EventPrediction> firstPreds = new HashMap<>();
					PaginationFilter winStreakPagination = new SimplePaginationFilter();
					winStreakPagination.setOrderBy("postedtm DESC, ser DESC");
	
					Set<EventPrediction> allPreds = getEventPredictionManager().getAllPredictionsByUser(currUser, currTerm, SimplePaginationFilter.NONE);
					for ( EventPrediction currPred : allPreds )
					{
						if ( currPred.getCorrect() != null  )
						{
							// We could load the actual outcome from the database and use the likelihood value off of
							// that but that would incur a database hit and we already know the formula to get payout
							// here
							//
							float likelihood = 1 / currPred.getPayout();
	
							// This ensures only the first prediction for a given event is taken into account
							//
							if ( !firstPreds.containsKey(currPred.getEvent(false).getSer()) )
							{
								if ( currPred.getCorrect() )
								{
									correctRating += currPred.getQuantity() * (1 - likelihood);
									player.setCorrect(player.getCorrect() + 1);
	
									getLogger().debug("Correct initial prediction for " + currUser.getUsername() + " is now " + player.getCorrect() + " for prediction " + currPred);
								}
								else
								{
									incorrectRating += currPred.getQuantity() * (1 - likelihood);
									player.setIncorrect(player.getIncorrect() + 1);
	
									getLogger().debug("Incorrect initial prediction for " + currUser.getUsername() + " is now " + player.getIncorrect() + " for prediction " + currPred);
								}
	
								if ( currPred.getCorrect())
								{
									currStreak++;
									winStreak = Utils.greater(winStreak, currStreak);
	
									getLogger().debug("Increasing the current streak for user " + currUser.getUsername() + " to " + currStreak + " for correct prediction " + currPred);
								}
								else
								{
									getLogger().debug("Reseting the current streak for user " + currUser.getUsername() + " to 0 for incorrect prediction " + currPred);
									currStreak = 0;
								}
	
								firstPreds.put(currPred.getEvent(false).getSer(), currPred);
							}
							else
							{
								getLogger().debug("The first prediction in the game " + currPred.getEvent(false).getSer() + " for " + currUser.getUsername() + " has already been counted, skipping " + currPred);
							}
						}
						else
						{
							player.setPending(player.getPending() + 1);
						}
	
	
						player.setTotal(player.getTotal() + 1);
						lastPlayedTime = Utils.greater(lastPlayedTime, currPred.getTimeStampSeconds());
					}
	
					// To prevent division by zero
					//
					correctRating++;
					incorrectRating++;
	
					player.setAccuracy((float)(correctRating / incorrectRating));
					player.setUpdateTimeSeconds(Utils.currentTimeSeconds());
					player.setLastPlayedTimeSeconds(lastPlayedTime);
					player.setWinStreak(Utils.greater(player.getWinStreak(), winStreak));
	
					if ( !player.exists() )
					{
						getPlayerManager().create(player);
					}
					else
					{
						getPlayerManager().update(player);
					}
				}
	
				getLogger().trace("Processed " + numUsers + " w/ previous page at " + userPagination.getCurrentPage());
	
				// Move up by the number of users we processed
				//
				userPagination.setNextPage(true);
			}
		}
	}

	public int getIterationUserLimit()
	{
		return iterationUserLimit;
	}

	public void setIterationUserLimit(int iterationUserLimit)
	{
		this.iterationUserLimit = iterationUserLimit;
	}

	public PlayerManager getPlayerManager()
	{
		return playerManager;
	}

	@Autowired
	@Qualifier("predict.CachedPlayerManager")
	public void setPlayerManager(PlayerManager playerManager)
	{
		this.playerManager = playerManager;
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	@Qualifier("base.ReadOnlyUserManager")
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	public SingleClientManager getClientManager()
	{
		return clientManager;
	}

	@Autowired
	@Qualifier("base.ReadOnlySingleClientManager")
	public void setClientManager(SingleClientManager clientManager)
	{
		this.clientManager = clientManager;
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

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	@Qualifier("base.CachedBalanceManager")
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	@Qualifier("base.CachedUserBalanceManager")
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}

	public EventPredictionManager getEventPredictionManager()
	{
		return eventPredictionManager;
	}

	@Autowired
	@Qualifier("predict.SimpleEventPredictionManager")
	public void setEventPredictionManager(EventPredictionManager eventPredictionManager)
	{
		this.eventPredictionManager = eventPredictionManager;
	}
}
