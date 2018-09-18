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
import com.i4one.predict.model.player.Player;

/**
 * This class is used to update a player's rank within the PlayerStatsJob iteration loop.
 * 
 * @author Hamid Badiozamani
 */
abstract class RankUpdater
{
	private PlayerStatsJob parent;
	private PaginationFilter pagination;

	public RankUpdater(PlayerStatsJob parent)
	{
		this.parent = parent;
	}

	/**
	 * Initialize the pagination variable to the start location
	 */
	public abstract void initPagination();

	/**
	 * Perform the update on the player object and considering the pagination
	 * variable's state.
	 * 
	 * @param player The player whose rank we're to update
	 * @param rowNo The row number of the player as counted from the start of the iteration
	 */
	public abstract void execute(Player currPlayer, int rowNo);

	public PaginationFilter getPagination()
	{
		return pagination;
	}

	public void setPagination(PaginationFilter pagination)
	{
		this.pagination = pagination;
	}

	public PlayerStatsJob getParent()
	{
		return parent;
	}

	public void setParent(PlayerStatsJob parent)
	{
		this.parent = parent;
	}

}
