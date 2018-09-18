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
package com.i4one.predict.web.controller.admin.events;

import com.i4one.base.model.Errors;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.eventoutcome.EventOutcomeManager;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EventOutcomeCrudController extends BaseAdminViewController
{
	private EventOutcomeManager eventOutcomeManager;

	@RequestMapping(value = "**/predict/admin/events/updateoutcome", method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("outcome") EventOutcome outcome,
					@RequestParam(value = "eventid", required = true) Integer eventId,
					@RequestParam(value = "outcomeid", required = false) Integer outcomeId,
					HttpServletRequest request, HttpServletResponse response)
	{
		if ( outcomeId != null )
		{
			outcome.setSer(outcomeId);
			outcome.loadedVersion();
		}

		// Outcomes must belong to an event
		//
		outcome.getEvent().setSer(eventId);

		Model model = initRequest(request, outcome);

		return initResponse(model, response, outcome);
	}

	@RequestMapping(value = "**/predict/admin/events/updateoutcome", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("outcome") EventOutcome outcome, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, outcome);

		try
		{
			if ( outcome.exists() )
			{
				// Reset these values because the statistics need to be recompiled
				//
				outcome.setUpdateTimeSeconds(0);
				outcome.setUsageCount(0);
				outcome.setLikelihood(outcome.getBaseline());

				getEventOutcomeManager().update(outcome);
				success(model, "msg.predict.admin.events.updateoutcome.update.success");
			}
			else
			{
				getEventOutcomeManager().create(outcome);
				success(model, "msg.predict.admin.events.updateoutcome.create.success");
			}

		}
		catch (Errors errors)
		{
			fail(model, "msg.predict.admin.events.updateoutcome.failure", result, errors);
		}

		return initResponse(model, response, outcome);
	}

	@RequestMapping(value = { "**/predict/admin/events/removeoutcome" }, method = RequestMethod.GET)
	public void remove(@RequestParam(value="id") Integer outcomeId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		Model model = initRequest(request, null);

		EventOutcome outcome = new EventOutcome();
		if ( outcomeId != null )
		{
			outcome.setSer(outcomeId);
			outcome.loadedVersion();

			getLogger().debug("Removing outcome " + outcome);
		}

		if ( outcome.exists() )
		{
			// The database should handle the cascading removal thru the use of foreign keys
			//
			getEventOutcomeManager().remove(outcome);

			// If we make it this far, we were successfull
			//
			success(model, "msg.predict.admin.events.removeoutcome.success");
		}
		else
		{
			fail(model, "msg.predict.admin.events.removeoutcome.failure", null, null);
		}

		String redirURL = "index.html";
		response.sendRedirect(redirURL);

		redirOnSuccess(model, null, null, null, redirURL, request, response);
	}

	public EventOutcomeManager getEventOutcomeManager()
	{
		return eventOutcomeManager;
	}

	@Autowired
	public void setEventOutcomeManager(EventOutcomeManager eventOutcomeManager)
	{
		this.eventOutcomeManager = eventOutcomeManager;
	}

}
