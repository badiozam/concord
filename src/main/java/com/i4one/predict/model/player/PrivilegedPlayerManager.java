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

import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.UserBalanceTrigger;
import com.i4one.base.model.user.BaseUserPrivilegedManager;
import com.i4one.base.model.user.User;
import com.i4one.predict.model.term.Term;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedPlayerManager extends BaseUserPrivilegedManager<PlayerRecord, Player> implements PlayerManager
{
	private PlayerManager playerManager;

	@Override
	public User getUser(Player player)
	{
		return player.getUser();
	}

	@Override
	public SingleClient getClient(Player player)
	{
		return player.getUser().getClient();
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

	@Override
	public Manager<PlayerRecord, Player> getImplementationManager()
	{
		return getPlayerManager();
	}

	@Override
	public Player getPlayer(Term term, User user)
	{
		return getPlayerManager().getPlayer(term, user);
	}

	@Override
	public Set<Player> getAllPlayers(Term term, PaginationFilter pagination)
	{
		return getPlayerManager().getAllPlayers(term, pagination);
	}

	@Override
	public List<ReturnType<UserBalanceTrigger>> collectAllowance(User user, Term term)
	{
		return getPlayerManager().collectAllowance(user, term);
	}

	@Override
	public Map<BalanceTrigger, Integer> getAllowanceEligibility(User user, Term term, int timestamp)
	{
		return getPlayerManager().getAllowanceEligibility(user, term, timestamp);
	}

	@Override
	public PaginableRecordTypeDao<PlayerRecord> getDao()
	{
		return getPlayerManager().getDao();
	}
}
