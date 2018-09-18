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
package com.i4one.base.model.instantwin;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.terminable.TerminableManager;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface InstantWinManager extends TerminableManager<InstantWinRecord, InstantWin>
{
	/**
	 * Update and associate the given instantWins with the incoming item. Any new instantWins
	 * are created, any existing ones will be updated. All instantWins will be associated
	 * with the incoming item. All previous associations with other items for any of
	 * the incoming instantWins is preserved. Only instantWins with amounts not equaling zero
	 * will be created.
	 * 
	 * @param item The item to associate the instantWins with
	 * @param instantWins The instantWins to update and associate with the item.
	 * 
	 * @return A list of the updated instant win objects
	 */
	public List<ReturnType<InstantWin>> updateExclusiveInstantWins(RecordTypeDelegator<?> item, Collection<InstantWin> instantWins);

	/**
	 * Link the incoming instantWin to the given item.
	 * 
	 * @param item The item to associate the instantWin with
	 * @param instantWin The instantWin to associate
	 * 
	 * @return True if the item was newly associated, false otherwise
	 */
	public boolean associate(RecordTypeDelegator<?> item, InstantWin instantWin);

	/**
	 * Link the incoming instantWin to the given manager method.
	 * 
	 * @param manager The manager for which the instantWin is to be associated
	 * @param method The method in the manager for which the instantWin is to be associated
	 * @param instantWin The instantWin to associate
	 * 
	 * @return True if the manager/method was newly associated, false otherwise
	 */
	public boolean associate(Manager<?, ?> manager, String method, InstantWin instantWin);

	/**
	 * Unlink the incoming instantWin from the given item. All other associations remain intact.
	 * 
	 * @param item The item to dissociate the instantWin from
	 * @param instantWin The instantWin to dissociate
	 * 
	 * @return True if the item was previously associated and was properly dissociated, false otherwise
	 */
	public boolean dissociate(RecordTypeDelegator<?> item, InstantWin instantWin);

	/**
	 * Unlink the incoming instantWin from the given manager method. All other associations remain intact.
	 * 
	 * @param manager The manager for which the instantWin is to be dissociated
	 * @param method The method in the manager for which the instantWin is to be dissociated
	 * @param instantWin The instantWin to dissociate
	 * 
	 * @return True if the manager/method was previously associated and was properly dissociated, false otherwise
	 */
	public boolean dissociate(Manager<?, ?> manager, String method, InstantWin instantWin);

	/**
	 * Get all instant-wins for a particular feature
	 * 
	 * @param item The item for which the instant-win is associated
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of instantWins to run for the given item
	 */
	public Set<InstantWin> getAllInstantWinsByFeature(RecordTypeDelegator<?> item, PaginationFilter pagination);

	/**
	 * Get all instant-wins for a particular manager
	 * 
	 * @param manager The manager whose method is associated with the instant win
	 * @param method The specific method of the manager that invokes the instant win
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of instantWins to run for the given item
	 */
	public Set<InstantWin> getAllInstantWinsByManager(Manager<?, ?> manager, String method, PaginationFilter pagination);

	/**
	 * Get all features as a list of Generic Feature types for the given feature
	 * 
	 * @param instantWin The instant-win to look up
	 * 
	 * @return All of the associated features of the instant-win
	 */
	public Set<RecordTypeDelegator<?>> getAllFeatureInstantWins(InstantWin instantWin);
}