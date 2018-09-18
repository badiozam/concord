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

import com.i4one.base.core.Utils;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.BaseAttachableClientType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class SimpleInstantWinnable<U extends ClientRecordType, T extends SingleClientType<U>> extends BaseAttachableClientType<U,T> implements InstantWinnableClientType<U,T>
{
	private final Set<InstantWin> instantWins;
	private final Set<ExclusiveInstantWin<T>> exclusiveInstantWins;
	private final Set<InstantWin> nonExclusiveInstantWins;

	public SimpleInstantWinnable(T forward)
	{
		super(forward);

		instantWins = new LinkedHashSet<>();
		exclusiveInstantWins = new LinkedHashSet<>();
		nonExclusiveInstantWins = new LinkedHashSet<>();

		initAttachables();
	}

	protected ExclusiveInstantWin<T> newExclusiveInstantWin()
	{
		return new ExclusiveInstantWin<>(getForward());
	}

	private void initAttachables()
	{
		initInstantWins();
	}

	private void initInstantWins()
	{
		exclusiveInstantWins.clear();
		nonExclusiveInstantWins.clear();

		getInstantWins().stream().forEach( (currInstantWin) ->
		{
			if ( currInstantWin.isExclusive() )
			{
				ExclusiveInstantWin<T> exclusiveInstantWin = newExclusiveInstantWin();

				// If the types are the same, we can skip this step entirely
				//
				if ( !exclusiveInstantWin.getClass().equals(currInstantWin.getClass()) )
				{
					exclusiveInstantWin.fromInstantWin(currInstantWin);
				}
				else
				{
					exclusiveInstantWin = (ExclusiveInstantWin<T>)currInstantWin;
				}

				exclusiveInstantWins.add(exclusiveInstantWin);
			}
			else
			{
				getLogger().debug("Instant win {} is NOT exclusive, skipping", currInstantWin);
				nonExclusiveInstantWins.add(currInstantWin);
			}
		});

		instantWins.clear();
		instantWins.addAll(exclusiveInstantWins);
		instantWins.addAll(nonExclusiveInstantWins);
	}

	@Override
	protected final Errors validateInternal()
	{
		Errors retVal = super.validateInternal();

		validateInstantWins(retVal);

		return retVal;
	}

	private void validateInstantWins(Errors retVal)
	{
		int i = 0;
		for ( InstantWin instantWin : getExclusiveInstantWins() )
		{
			String currKey = "exclusiveInstantWins[" + i + "]";

			// Include any errors with the instant win itself
			//
			retVal.merge(currKey, validateInstantWin(instantWin));
			
			i++;
		}

	}

	protected Errors validateInstantWin(InstantWin instantWin)
	{
		Errors retVal = instantWin.validate();

		// Make sure the instant win's clients belong to this item's
		//
		if ( !belongsTo(instantWin.getClient()) )
		{
			retVal.addError(new ErrorMessage("client", "msg.base.InstantWinnable.exlusiveInstantWins.clientMismatch", "The instant win $instantWin does not belong to the same group of clients as this item", new Object[]{"item", this, "instantWin", instantWin}));
		}

		return retVal;

	}

	@Override
	public void setOverridesInternal()
	{
		super.setOverridesInternal();

		getExclusiveInstantWins().forEach( (item) -> { getLogger().debug("Setting instant win overrides for {}", item); item.setOverrides(); });
	}

	@Override
	public boolean fromCSVInternal(List<String> csv)
	{
		if ( super.fromCSVInternal(csv) )
		{
			instantWins.clear();

			int numInstantWins = Utils.defaultIfNaN(csv.get(0), 0); csv.remove(0);

			boolean retVal = true;
			for ( int i = 0; i < numInstantWins && retVal; i++ )
			{
				InstantWin instantWin = new InstantWin();
				retVal &= instantWin.fromCSVList(csv);

				instantWins.add(instantWin);
			}

			initInstantWins();
			return retVal;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected StringBuilder toCSVInternal(boolean header)
	{
		StringBuilder retVal = super.toCSVInternal(header);

		if (header)
		{
			// XXX: Needs to be i18n
			retVal.append("# of Instant Wins").append(",");

			int i = 0;
			for ( InstantWin currInstantWin : instantWins )
			{
				i++;
				retVal.append(i).append(" ").append(currInstantWin.toCSV(true));
			}
		}
		else
		{
			retVal.append(instantWins.size()).append(",");
			for ( InstantWin currInstantWin : instantWins )
			{
				retVal.append(currInstantWin.toCSV(false));
			}
		}

		return retVal;
	}

	@Override
	public Set<InstantWin> getInstantWins()
	{
		return Collections.unmodifiableSet(instantWins);
	}

	@Override
	public void setInstantWins(Collection<InstantWin> iws)
	{
		instantWins.clear();
		instantWins.addAll(iws);

		initInstantWins();
	}

	@Override
	public Set<ExclusiveInstantWin<T>> getExclusiveInstantWins()
	{
		return Collections.unmodifiableSet(exclusiveInstantWins);
	}

	@Override
	public Set<InstantWin> getNonExclusiveInstantWins()
	{
		return Collections.unmodifiableSet(nonExclusiveInstantWins);
	}
}
