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
package com.i4one.base.model.manager.instantwinnable;

import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.instantwin.InstantWinManager;
import com.i4one.base.model.manager.BaseDelegatingManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseInstantWinnableManager<U extends ClientRecordType, T extends InstantWinnableClientType<U,?>> extends BaseDelegatingManager<U,T> implements InstantWinnableManager<U,T>
{
	private InstantWinManager instantWinManager;

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		ReturnType<T> retVal = getImplementationManager().create(item);
		updateExclusiveInstantWins(item);

		// Update the set of instantWins after this call to ensure that the caller has
		// the latest information
		//
		initModelObject(retVal.getPost());
		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		ReturnType<T> retVal = getImplementationManager().update(item);
		updateExclusiveInstantWins(item);

		// Update the set of instantWins after this call to ensure that the caller has
		// the latest information
		//
		initModelObject(retVal.getPost());
		return retVal;
	}

	protected void updateExclusiveInstantWins(T item)
	{
		getLogger().debug("Updating " + item.getInstantWins().size() + " instantWins for " + item);

		getInstantWinManager().updateExclusiveInstantWins(item, item.getInstantWins());
	}

	@Transactional(readOnly = false)
	@Override
	public T remove(T item)
	{
		// Dissociate all instantWins having to do with this item since it's being
		// completely removed. The instantWin itself may or may not be removed by the
		// instantWin manager depending on whether or not there are other associations
		//
		Set<InstantWin> instantWins = getInstantWinManager().getAllInstantWinsByFeature(item, SimplePaginationFilter.NONE);
		instantWins.forEach( (instantWin) ->
		{
			getInstantWinManager().dissociate(item, instantWin);
		});

		return initModelObject(getImplementationManager().remove(item));
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> clone(T item)
	{
		// First get the incoming item's instantWins so we can clone them, too
		//
		Set<InstantWin> instantWins = getInstantWinManager().getAllInstantWinsByFeature(item, SimplePaginationFilter.NONE);

		// Now we can clone the incoming item to get its serial no.
		//
		ReturnType<T> retVal = getImplementationManager().clone(item);

		// And clone the previous item's instantWins and associated them with this one
		//
		List<ReturnType<InstantWin>> clonedInstantWins = new ArrayList<>();
		instantWins.forEach( (instantWin) ->
		{
			InstantWin toAssociate = instantWin;

			// We have to clone exclusive instantWins since these belong to each individual item
			//
			if ( instantWin.isExclusive() )
			{
				// Make sure we have our serial number as part of the title instead of the item we're a clone of
				//
				instantWin.setTitle(new IString("en", retVal.getPost().getFeatureName() + ":" + retVal.getPost().getSer()));

				ReturnType<InstantWin> clonedInstantWin = getInstantWinManager().clone(instantWin);
				clonedInstantWins.add(clonedInstantWin);

				toAssociate = clonedInstantWin.getPost();
			}

			getInstantWinManager().associate(retVal.getPost(), toAssociate);
		});

		//getInstantWinManager().updateInstantWins(item, clonedInstantWins);

		retVal.addChain(getInstantWinManager(), "clone", new ReturnType<>(clonedInstantWins));
		return retVal;
	}

	@Override
	protected T initModelObject(T item)
	{
		item.setInstantWins(getInstantWinManager().getAllInstantWinsByFeature(item, SimplePaginationFilter.NONE));

		return item;
	}

	public InstantWinManager getInstantWinManager()
	{
		return instantWinManager;
	}

	@Autowired
	public void setInstantWinManager(InstantWinManager instantWinManager)
	{
		this.instantWinManager = instantWinManager;
	}
}
