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
package com.i4one.base.model.balancetrigger;

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
public interface BalanceTriggerManager extends TerminableManager<BalanceTriggerRecord, BalanceTrigger>
{
	/**
	 * Update and associate the given triggers with the incoming item. Any new triggers
	 * are created, any existing ones will be updated. All triggers will be associated
	 * with the incoming item. All previous associations with other items for any of
	 * the incoming triggers is preserved. Only triggers with amounts not equaling zero
	 * will be created.
	 * 
	 * @param item The item to associate the triggers with
	 * @param triggers The triggers to update and associate with the item.
	 * 
	 * @return The list of create/update statuses for each of the incoming triggers
	 */
	public List<ReturnType<BalanceTrigger>> updateExclusiveBalanceTriggers(RecordTypeDelegator<?> item, Collection<BalanceTrigger> triggers);

	/**
	 * Link the incoming trigger to the given item.
	 * 
	 * @param item The item to associate the trigger with
	 * @param triggers The trigger to associate
	 * 
	 * @return True if the item was newly associated, false otherwise
	 */
	public boolean associate(RecordTypeDelegator<?> item, BalanceTrigger triggers);

	/**
	 * Link the incoming trigger to the given manager method.
	 * 
	 * @param manager The manager for which the trigger is to be associated
	 * @param method The method in the manager for which the trigger is to be associated
	 * @param trigger The trigger to associate
	 * 
	 * @return True if the manager/method was newly associated, false otherwise
	 */
	public boolean associate(Manager<?, ?> manager, String method, BalanceTrigger trigger);

	/**
	 * Unlink the incoming trigger from the given item. All other associations remain intact.
	 * 
	 * @param item The item to dissociate the trigger from
	 * @param triggers The trigger to dissociate
	 * 
	 * @return True if the item was previously associated and was properly dissociated, false otherwise
	 */
	public boolean dissociate(RecordTypeDelegator<?> item, BalanceTrigger triggers);

	/**
	 * Unlink the incoming trigger from the given manager method. All other associations remain intact.
	 * 
	 * @param manager The manager for which the trigger is to be dissociated
	 * @param method The method in the manager for which the trigger is to be dissociated
	 * @param trigger The trigger to dissociate
	 * 
	 * @return True if the manager/method was previously associated and was properly dissociated, false otherwise
	 */
	public boolean dissociate(Manager<?, ?> manager, String method, BalanceTrigger trigger);

	/**
	 * Get all triggers for a particular feature item. The item's serial number is
	 * considered as well as the wildcard 0 serial number
	 * 
	 * @param item The item for which the trigger is associated
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of triggers to run for the given item
	 */
	public Set<BalanceTrigger> getAllTriggersByFeature(RecordTypeDelegator<?> item, PaginationFilter pagination);

	/**
	 * Get all triggers for a particular manager method. This trigger is to be
	 * referenced each time the given manager's method is called regardless of
	 * that method's parameters
	 * 
	 * @param manager The item for which the trigger is associated
	 * @param method The method in the manager for which the trigger is associated
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of triggers to run for the given item
	 */
	public Set<BalanceTrigger> getAllTriggersByManager(Manager<?,?> manager, String method, PaginationFilter pagination);

	/**
	 * Get all features as a list of Generic Feature types for the given trigger
	 * 
	 * @param trigger The trigger to look up
	 * 
	 * @return All of the associated features of the trigger
	 */
	public Set<RecordTypeDelegator<?>> getAllFeatureTriggers(BalanceTrigger trigger);
}