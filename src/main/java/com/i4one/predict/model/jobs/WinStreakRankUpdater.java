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

import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.predict.model.player.Player;

/**
 * @author Hamid Badiozamani
 */
class WinStreakRankUpdater extends RankUpdater
{

	public WinStreakRankUpdater(PlayerStatsJob parent)
	{
		super(parent);
	}

	@Override
	public void initPagination()
	{
		PaginationFilter rankOrderPagination = new SimplePaginationFilter();
		rankOrderPagination.setOrderBy("predict.players.winstreak DESC, predict.players.ser");
		rankOrderPagination.setPerPage(getParent().getIterationUserLimit());
		rankOrderPagination.setCurrentPage(0);

		setPagination(rankOrderPagination);
	}

	@Override
	public void execute(Player player, int rowNo)
	{
		player.setWinStreakRank(getPagination().getCurrentPage() * getPagination().getPerPage() + rowNo);
		getParent().getPlayerManager().update(player);
	}
	
}
