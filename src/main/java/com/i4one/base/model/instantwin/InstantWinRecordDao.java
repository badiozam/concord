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

import com.i4one.base.dao.RecordType;
import com.i4one.base.dao.terminable.TerminableClientRecordTypeDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface InstantWinRecordDao extends TerminableClientRecordTypeDao<InstantWinRecord>
{
	/**
	 * Increment the number of winners associated with the given instant win
	 * 
	 * @param instantWinid The instant win item whose count is to be incremented
	 */
	public void incrementWinnerCount(int instantWinid);

	/**
	 * Determines whether the given instantWin has any associations
	 * 
	 * @param instantWinid The instantWin to look up
	 * 
	 * @return True if there is at least one association to the instantWin, false otherwise.
	 */
	public boolean hasAssociations(int instantWinid);

	/**
	 * Determines whether a given feature is associated with a given instantWin.
	 * Note that this query does not use any column qualifiers or pagination.
	 * 
	 * @param feature The feature (i.e. table name) 
	 * @param id The id of the feature (i.e. ser)
	 * @param instantWinid The id of the instantWin to associate
	 * 
	 * @return True if the feature is associated with the instantWin, false otherwise
	 */
	public boolean isAssociated(String feature, int id, int instantWinid);

	/**
	 * Associate a instantWin with a given feature.
	 * Note that this query does not use any column qualifiers or pagination.
	 * 
	 * @param feature The feature (i.e. table name) 
	 * @param id The id of the feature (i.e. ser)
	 * @param instantWinid The id of the instantWin to associate
	 */
	public void associate(String feature, int id, int instantWinid);

	/**
	 * Associate a instantWin with a given feature.
	 * Note that this query does not use any column qualifiers or pagination.
	 * 
	 * @param feature The feature (i.e. table name) 
	 * @param id The id of the feature (i.e. ser)
	 * @param instantWinid The id of the instantWin to associate
	 */
	public void dissociate(String feature, int id, int instantWinid);

	/**
	 * Look up instant win records by a particular feature.
	 * 
	 * @param feature The feature (i.e. table name) 
	 * @param id The id of the feature (i.e. ser)
	 * @param pagination The qualifier/pagination info
	 * 
	 * @return A (potentially empty) list of instant-wins for the feature
	 */
	List<InstantWinRecord> getByFeature(String feature, int id, PaginationFilter pagination);

	/**
	 * Gets all features associated with this instantWin.
	 * 
	 * @param instantWinid The instantWin to search
	 * @param pagination The qualifier/pagination info
	 * 
	 * @return A (potentially empty) list of features that are associated with this instantWin
	 */
	public List<RecordType> getByInstantWin(int instantWinid, PaginationFilter pagination);
}
