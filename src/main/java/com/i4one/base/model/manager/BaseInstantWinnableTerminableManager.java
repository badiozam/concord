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
package com.i4one.base.model.manager;

import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.dao.terminable.TerminableClientRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.instantwinnable.BaseInstantWinnableManager;
import com.i4one.base.model.manager.instantwinnable.InstantWinnableManager;
import com.i4one.base.model.manager.instantwinnable.TerminableInstantWinnableClientType;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for managers that have instant winnable and terminable objects. The terminable is handled by
 * an implementation manager, while the instant winnable functionality is managed by the super-class.
 * 
 * @author Hamid Badiozamani
 */
public abstract class BaseInstantWinnableTerminableManager<U extends TerminableClientRecordType, T extends TerminableInstantWinnableClientType<U, ?>> extends BaseInstantWinnableManager<U, T> implements InstantWinnableManager<U,T>,TerminableManager<U, T>
{
	@Override
	public abstract TerminableManager<U, T> getImplementationManager();

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		ReturnType<T> retVal = super.create(item);

		// Handled by TerminableExclusiveBalanceManager.setOverrides
		//
		//syncExclusiveInstantWinTimes(retVal.getPost());

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		ReturnType<T> retVal = super.update(item);

		// Handled by TerminableExclusiveBalanceManager.setOverrides
		//
		//syncExclusiveInstantWinTimes(retVal.getPost());

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> clone(T item)
	{
		ReturnType<T> retVal = super.clone(item);
		T cloned = retVal.getPost();

		// We ensure that our exclusive cloned instant wins have the same start/end times as we do.
		// Consider turning this into its own manager method: InstantWinManager.getExclusiveInstantWin(item)
		//
		Set<InstantWin> instantWins = getInstantWinManager().getAllInstantWinsByFeature(cloned, SimplePaginationFilter.NONE);
		instantWins.forEach( (instantWin) ->
		{
			// We only alter the triggers that we know belong to us
			//
			if ( instantWin.isExclusive() )
			{
				instantWin.setStartTimeSeconds(item.getStartTimeSeconds());
				instantWin.setEndTimeSeconds(item.getEndTimeSeconds());

				getInstantWinManager().update(instantWin);
			}
		});

		return retVal;
	}

	@Override
	public Set<T> getLive(TerminablePagination pagination)
	{
		return initSet(getImplementationManager().getLive(pagination));
	}

	@Override
	public Set<T> getByRange(TerminablePagination pagination)
	{
		return initSet(getImplementationManager().getByRange(pagination));
	}

	@Override
	public List<ReturnType<T>> importCSV(InputStream stream, Supplier<T> instantiator, Function<T,Boolean> preprocessor, Consumer<ReturnType<T>> postprocessor)
	{
		List<ReturnType<T>> imported = super.importCSV(stream, instantiator, preprocessor, postprocessor.andThen( (item) ->
			{
				T createdItem = item.getPost();

				updateExclusiveInstantWins(createdItem);
				initModelObject(createdItem);
			}
		));

		return imported;
	}

	@Override
	public TerminableClientRecordTypeDao<U> getDao()
	{
		return getImplementationManager().getDao();
	}
}