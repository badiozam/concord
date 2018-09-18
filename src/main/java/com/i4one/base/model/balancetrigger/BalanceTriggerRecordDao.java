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

import com.i4one.base.dao.RecordType;
import com.i4one.base.dao.terminable.TerminableClientRecordTypeDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface BalanceTriggerRecordDao extends TerminableClientRecordTypeDao<BalanceTriggerRecord>
{
	/**
	 * Determines whether the given trigger has any associations
	 * 
	 * @param triggerid The trigger to look up
	 * 
	 * @return True if there is at least one association to the trigger, false otherwise.
	 */
	public boolean hasAssociations(int triggerid);

	/**
	 * Determines whether a given feature is associated with a given trigger.
	 * 
	 * @param feature The feature (i.e. table name) 
	 * @param id The id of the feature (i.e. ser)
	 * @param triggerid The id of the trigger to associate
	 * 
	 * @return True if the feature is associated with the trigger, false otherwise
	 */
	public boolean isAssociated(String feature, int id, int triggerid);

	/**
	 * Associate a trigger with a given feature.
	 * 
	 * @param feature The feature (i.e. table name) 
	 * @param id The id of the feature (i.e. ser)
	 * @param triggerid The id of the trigger to associate
	 */
	public void associate(String feature, int id, int triggerid);

	/**
	 * Disassociate a trigger from a given feature.
	 * 
	 * @param feature The feature (i.e. table name) 
	 * @param id The id of the feature (i.e. ser)
	 * @param triggerid The id of the trigger to disassociate
	 */
	public void dissociate(String feature, int id, int triggerid);

	/**
	 * Look up balance triggers by a particular feature. The wildcard id
	 * of 0 is also considered in addition to the incoming id parameter
	 * 
	 * @param feature The feature (i.e. table name) 
	 * @param id The id of the feature (i.e. ser)
	 * @param pagination The qualifier/pagination info.
	 * 
	 * @return A (potentially empty) list of triggers for the feature
	 */
	public List<BalanceTriggerRecord> getByFeature(String feature, int id, PaginationFilter pagination);

	/**
	 * Gets all features associated with this trigger
	 * 
	 * @param triggerid The trigger to search
	 * @param pagination The qualifier/pagination info.
	 * 
	 * @return A (potentially empty) list of features that are associated with this trigger
	 */
	public List<RecordType> getByTrigger(int triggerid, PaginationFilter pagination);
}