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
package com.i4one.predict.web.controller.user;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import com.i4one.predict.model.player.Player;
import com.i4one.predict.model.player.PlayerManager;
import com.i4one.predict.model.term.Term;
import com.i4one.predict.model.term.TermManager;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class PlayerViewController extends BaseUserViewController
{
	private PlayerManager playerManager;
	private TermManager termManager;

	public Model initRequest(HttpServletRequest request, ChartFilter pagination)
	{
		Model retVal = super.initRequest(request, pagination);

		// Force the pagination go by 25 per page regardless of what the incoming request wants
		//
		pagination.setPerPage(25);

		// Force there to be no more than 100 pages so we only display the top 100 * perPage
		//
		if ( pagination.getTotalPages() > 100 )
		{
			pagination.setTotalPages(100);
		}

		return retVal;
	}

	@RequestMapping(value = "**/predict/user/top10", method = RequestMethod.GET)
	public Model topPlayers(@ModelAttribute("pagination") ChartFilter pagination, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, pagination);

		pagination.setCurrentPage(0);

		getLogger().debug("GET method returning default chart with pagination: " + pagination);
		model.put("players", getPlayersChart(model, pagination));

		return initResponse(model, response, pagination);
	}

	@RequestMapping(value = "**/predict/user/top10", method = RequestMethod.POST)
	public Model topPlayersPagination(@ModelAttribute("pagination") ChartFilter pagination, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, pagination);

		getLogger().debug("POST method returning chart with pagination: " + pagination);
		model.put("players", getPlayersChart(model, pagination));

		return initResponse(model, response, pagination);
	}

	protected Set<Player> getPlayersChart(Model model, ChartFilter pagination)
	{
		SingleClient client = model.getSingleClient();

		// We can modify this later to specify a given term from the request
		// parameters instead of just the live one
		//
		Term currTerm = new Term();
		if ( pagination.getTermid() != 0 )
		{
			currTerm = getTermManager().getById(pagination.getTermid());
		}
		
		if ( !currTerm.exists() )
		{
			currTerm = getTermManager().getLatestTerm(client);
		}
		Set<Player> players = getPlayerManager().getAllPlayers(currTerm, pagination);

		return players;
	}

	protected Term getTerm(Model model)
	{
		return getTermManager().getLiveTerm(model.getSingleClient());
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

}
