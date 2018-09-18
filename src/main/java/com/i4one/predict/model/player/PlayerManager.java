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
package com.i4one.predict.model.player;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.UserBalanceTrigger;
import com.i4one.base.model.user.User;
import com.i4one.predict.model.term.Term;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface PlayerManager extends PaginableManager<PlayerRecord, Player>
{
	/**
	 * Constructs information about a player given their user account.
	 * 
	 * @param term The term for which to get the info
	 * @param user The user account
	 * 
	 * @return Their player account
	 */
	public Player getPlayer(Term term, User user);

	/**
	 * Get a list of all players for the current term with the given pagination limiter
	 * 
	 * @param term The term that the players belong to
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of all players
	 */
	public Set<Player> getAllPlayers(Term term, PaginationFilter pagination);

	/**
	 * Increases the user's credit balance by the allowance amount for the
	 * given term if eligible.
	 * 
	 * @param user The user who's requesting the collection
	 * @param term  The term for which the user wishes to collect the allowance for
	 * 
	 * @return A list of the triggers that were processed for this allowance
	 */
	public List<ReturnType<UserBalanceTrigger>> collectAllowance(User user, Term term);

	/**
	 * Gets the eligibility status of allowance triggers for a given user
	 * 
	 * @param user The user who's requesting the status
	 * @param term The term for which the user is requesting the status
	 * @param timestamp The timestamp to use as the reference point
	 * 
	 * @return A map of triggers and their eligibility in seconds
	 */
	public Map<BalanceTrigger, Integer> getAllowanceEligibility(User user, Term term, int timestamp);
}
