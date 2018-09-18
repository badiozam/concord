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
package com.i4one.base.web.controller.admin.accesscodes;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.accesscode.AccessCode;
import com.i4one.base.model.accesscode.AccessCodeRecord;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.web.controller.TerminableInstantWinnableWebModel;
import com.i4one.base.web.controller.TerminableTriggerableWebModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.AutoPopulatingList;
import org.springframework.util.AutoPopulatingList.ElementFactory;

/**
 * @author Hamid Badiozamani
 */
public class WebModelAccessCode extends AccessCode implements ElementFactory<String>
{
	public static final int DEFAULT_ACCESS_CODE_PAGES = 2;

	private final transient TerminableTriggerableWebModel<AccessCodeRecord,AccessCode> triggerable;
	private final transient TerminableInstantWinnableWebModel<AccessCodeRecord,AccessCode> instantWinning;

	private transient List<String> accessCodePages;

	public WebModelAccessCode()
	{
		super();

		triggerable = new TerminableTriggerableWebModel<>(this);

		accessCodePages = new AutoPopulatingList<>(this);
		instantWinning = new TerminableInstantWinnableWebModel<>(this);
	}

	protected WebModelAccessCode(AccessCodeRecord delegate)
	{
		super(delegate);

		triggerable = new TerminableTriggerableWebModel<>(this);

		accessCodePages = new AutoPopulatingList<>(this);
		instantWinning = new TerminableInstantWinnableWebModel<>(this);
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		retVal = triggerable.validateModel(retVal);
		retVal = instantWinning.validateModel(retVal);

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		triggerable.setModelOverrides();
		instantWinning.setModelOverrides();

		setPages(getFilteredAccessCodePages());

		// We have to set the overrides here after the balance triggers have been
		// set to our filtered results
		//
		super.setOverrides();
	}

	protected List<String> getFilteredAccessCodePages()
	{
		return getAccessCodePages().stream()
			.filter( (page) -> { return !Utils.isEmpty(page); } )
			.collect(Collectors.toList());
	}

	@Override
	public void setPages(Collection<String> newPages)
	{
		super.setPages(newPages);

		// We have to force a reload of the pages since they may have
		// changed in the database
		//
		accessCodePages.clear();
	}


	public List<String> getAccessCodePages()
	{
		if ( accessCodePages == null || accessCodePages.isEmpty() )
		{
			getLogger().debug("getAccessCodePages() initializing accessCodePages from " + Utils.toCSV(getPages()));

			Collection<String> pages = getPages();
			List<String> backingList = new ArrayList<>(pages);

			accessCodePages = new AutoPopulatingList<>(backingList, this);

			// This ensures there is always one element that can be added to a poll
			//
			accessCodePages.get(backingList.size());
		}

		// This creates the elements up to this point if they didn't already exist
		//
		accessCodePages.get(DEFAULT_ACCESS_CODE_PAGES - 1);

		return accessCodePages;
	}

	public void setAccessCodePages(List<String> accessCodePages)
	{
		this.accessCodePages = accessCodePages;
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> newTriggers)
	{
		// The super class implementation is needed for both exclusive and
		// non-exclusive triggers to be set so that upon form submission
		// the most accurate information is displayed
		//
		super.setBalanceTriggers(newTriggers);

		triggerable.setBalanceTriggers(newTriggers);
	}

	public TerminableTriggerableWebModel<AccessCodeRecord, AccessCode> getTriggerable()
	{
		return triggerable;
	}

	@Override
	public void setInstantWins(Collection<InstantWin> newInstantWins)
	{
		super.setInstantWins(newInstantWins);

		instantWinning.setInstantWins(newInstantWins);
	}

	public TerminableInstantWinnableWebModel<AccessCodeRecord, AccessCode> getInstantWinning()
	{
		return instantWinning;
	}

	@Override
	public String createElement(int index) throws AutoPopulatingList.ElementInstantiationException
	{
		return "";
	}
}