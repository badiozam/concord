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
package com.i4one.predict.web.controller.user;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.WebModel;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.eventprediction.EventPrediction;
import org.apache.commons.validator.UrlValidator;

/**
 * @author Hamid Badiozamani
 */
public class WebModelEventPrediction extends EventPrediction implements WebModel
{
	static final long serialVersionUID = 42L;

	private transient Model model;
	private transient boolean initial;

	@Override
	public void init()
	{
		super.init();

		// Whether this predicition is the first the user has made
		// in this event.
		//
		initial = false;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		this.setTimeStampSeconds(Utils.currentTimeSeconds());
	}

	public String toHighChartsJSON(String lang)
	{
		StringBuilder retVal = new StringBuilder();

		retVal.append("{");
			//retVal.append("'title':{'text':'").append(Utils.jsEscape(getEvent().getTitle().get(lang))).append("'}").append(",");
			retVal.append("'title':{'text':''}").append(",");
			retVal.append("'chart':{'type':'pie'}").append(",");
			retVal.append("'xAxis':{'type':'category'}").append(",");
			retVal.append("'series':[{'data':[");
			boolean isFirst = true;
				for ( EventOutcome outcome : getEvent().getPossibleOutcomes() )
				{
					if ( isFirst ) { isFirst = false; } else { retVal.append(","); }

					retVal.append("{");
						float currCount = outcome.getLikelihood() * 100;
						retVal.append("'y':").append(currCount).append(",");
						retVal.append("'name':\'").append(Utils.jsEscape(breakChartAnswer(outcome.getDescr().get(lang)))).append("\'");
					retVal.append("}");
				}
			retVal.append("]").append(",");
			retVal.append("'name':'").append(Utils.jsEscape(getEvent().getTitle().get(lang))).append("'}]");
		retVal.append("}");

		return retVal.toString();
	}

	// Attempt to break the answer at around this number
	//
	private final int OUTCOME_BREAKPOINT = 30;

	private String breakChartAnswer(String answer)
	{
		return Utils.breakToHTML(answer, OUTCOME_BREAKPOINT);
	}

	public boolean getMinCheck()
	{
		return getQuantity() >= getEvent().getMinBid();
	}

	public boolean getMaxCheck()
	{
		return getQuantity() <= getEvent().getMaxBid();
	}

	public boolean getEventOutcomeExists()
	{
		return getEventOutcome().exists();
	}

	public boolean isNotExpiredEvent()
	{
		return getTimeStampSeconds() <= getEvent().getEndTimeSeconds();
	}

	public boolean isNotPrematureEvent()
	{
		return getTimeStampSeconds() >= getEvent().getStartTimeSeconds();
	}

	public boolean isReferenceURL(String lang)
	{
		String reference = getEvent().getReference().get(lang);
		UrlValidator validator = new UrlValidator();

		return validator.isValid("http://" + reference);
	}

	public String getReferenceURLHost(String lang)
	{
		String reference = getEvent().getReference().get(lang);

		// Return only up to the first slash character
		//
		int slashIndex = reference.indexOf('/');
		if (slashIndex < 0 )
		{
			slashIndex = reference.indexOf('?');
		}

		if ( slashIndex < 0)
		{
			return reference;
		}
		else
		{
			return reference.substring(0, slashIndex);
		}
	}

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		getLogger().debug("Validating event prediction for " + getEvent());
		if ( !getEvent().exists() )
		{
			errors.addError(new ErrorMessage("msg.predict.EventPrediction.event.invalidEvent", "This event item does not exist", new Object[]{"item", this}));
		}
		else if ( !getEvent().isLive(getModel().getTimeInSeconds()) )
		{
			errors.addError(new ErrorMessage("msg.predict.EventPrediction.event.notLive", "This event item is not currently available", new Object[]{"item", this}));
		}

		if ( !getEventOutcomeExists() )
		{
			errors.addError(new ErrorMessage("msg.predict.EventPrediction.invalidEventOutcome", "This selected outcome does not exist", new Object[]{"item", this}));
		}

		if ( !isNotExpiredEvent() )
		{
			errors.addError(new ErrorMessage("msg.predict.EventPrediction.expiredEvent", "This event is no longer available", new Object[]{"item", this}));
		}

		if ( !isNotPrematureEvent() )
		{
			errors.addError(new ErrorMessage("msg.predict.EventPrediction.prematureEvent", "This event is not yet available", new Object[]{"item", this}));
		}

		if ( !getMinCheck() )
		{
			errors.addError(new ErrorMessage("msg.predict.EventPrediction.invalidMinBid", "Your bid amount is below the minimum of $item.event.minBid ", new Object[]{"item", this}));
		}

		if ( !getMaxCheck() )
		{
			errors.addError(new ErrorMessage("msg.predict.EventPrediction.invalidMinBid", "Your bid amount is above the maximum of $item.event.maxBid ", new Object[]{"item", this}));
		}

		// The user has to be set
		//
		if ( !getUser().exists() )
		{
			errors.addError(new ErrorMessage("msg.predict.eventPurchaseManager.create.userdne", "You must be logged in", new Object[] { "user", getUser()}, null));
		}

		return errors;
	}

	@Override
	public Model getModel()
	{
		return model;
	}

	@Override
	public void setModel(Model model)
	{
		this.model = model;
	}

	public boolean isInitial()
	{
		return initial;
	}

	public void setInitial(boolean initial)
	{
		this.initial = initial;
	}

}
