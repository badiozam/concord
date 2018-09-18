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
package com.i4one.base.web.controller.admin.instantwins;

import com.i4one.base.model.manager.attachable.NonExclusiveTerminablePagination;
import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.manager.GenericFeature;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class InstantWinAttachmentController extends BaseAdminViewController
{
	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		TerminablePagination pagination = new NonExclusiveTerminablePagination(model.getTimeInSeconds(),
			new ClientPagination(model.getSingleClient(), SimplePaginationFilter.NONE));

		// We get all live and future instantwins to present to the user
		//
		Set<InstantWin> instantwins = new LinkedHashSet<>();
		instantwins.addAll(getInstantWinManager().getLive(pagination));

		pagination.setFuture();
		instantwins.addAll(getInstantWinManager().getByRange(pagination));

		model.put("instantWins", instantwins);

		if ( modelAttribute instanceof WebModelInstantWinAttachment )
		{
			WebModelInstantWinAttachment attachment = (WebModelInstantWinAttachment)modelAttribute;
			if ( attachment.getInstantWins().isEmpty() )
			{
				initAttachment(model, attachment);
			}
		}

		return model;
	}

	private void initAttachment(Model model, WebModelInstantWinAttachment attachment)
	{
		Set<InstantWin> attachedInstantWins = getInstantWinManager().getAllInstantWinsByFeature(attachment.getFeature(),
			new NonExclusiveTerminablePagination(model.getTimeInSeconds(), SimplePaginationFilter.NONE));

		// These instantwins are already attached, there's really no need to reattach them
		// unless they become detached by the time the page gets resubmitted
		//
		attachment.setInstantWins( attachedInstantWins );

		Set<InstantWin> unattachedInstantWins = ((Set<InstantWin>)model.get("instantWins"))
			.stream()
			.filter( (instantwin) -> { return !attachedInstantWins.contains(instantwin); } )
			.collect(Collectors.toSet());

		model.put("unattachedInstantWins", toRadioButtonMapping(unattachedInstantWins, InstantWin::getTitle, model.getLanguage()));
	}

	@RequestMapping(value = "**/admin/instantwins/attach", method = RequestMethod.GET)
	public Model attachInstantWin(@ModelAttribute("attachment") WebModelInstantWinAttachment attachment,
					BindingResult result,
					@RequestParam(value = "featureid", required = true) Integer featureId,
					@RequestParam(value = "featurename", required = true) String featureName,
					@RequestParam(value = "redir", required = false) String redir,
					@RequestParam(value = "displayname", required = false) String displayname,
					HttpServletRequest request, HttpServletResponse response)
	{
		attachment.setFeature(new GenericFeature(featureId, featureName));

		Model model = initRequest(request, attachment);
		attachment.setRedir(redir);
		attachment.setDisplayName(displayname);

		return initResponse(model, response, attachment);
	}

	@RequestMapping(value = "**/admin/instantwins/attach", method = RequestMethod.POST)
	public Model doAttachInstantWin(@ModelAttribute("attachment") WebModelInstantWinAttachment attachment,
					BindingResult result,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, attachment);

		try
		{
			attachment.getInstantWins().stream().forEach( (instantWin) ->
			{
				getInstantWinManager().associate(attachment.getFeature(), instantWin);
			});

			success(model, "msg.base.admin.instantwins.attach.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.instantwins.attach.failure", result, errors);
		}

		initAttachment(model, attachment);
		return initResponse(model, response, attachment);
	}

	@RequestMapping(value = "**/admin/instantwins/detach", method = RequestMethod.GET)
	public void detachInstantWin( @RequestParam(value = "instantwinid", required = true) Integer instantwinId,
					@RequestParam(value = "featureid", required = true) Integer featureId,
					@RequestParam(value = "featurename", required = true) String featureName,
					@RequestParam(value = "section", required = false) String section,
					@RequestParam(value = "redir", required = false) String redir,
					HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);

		InstantWin instantwin = new InstantWin();
		instantwin.setSer(instantwinId);
		instantwin.loadedVersion();

		GenericFeature feature = new GenericFeature(featureId, featureName);
		if ( getInstantWinManager().dissociate(feature, instantwin) )
		{
			// If we make it this far, we were successfull
			//
			success(model, "msg.base.admin.instantwins.detach.success");
		}
		else
		{
			fail(model, "msg.base.admin.instantwins.detach.failure", null, null);
		}


		String redirURL = redir;
		if ( Utils.isEmpty(redirURL))
		{
			redirURL = model.getBaseURL() + "/"
				+ feature.getDelegate().getSchemaName()
				+ "/admin/"
				+ feature.getDelegate().getTableName()
				+ "/update.html?id=" + feature.getSer();
		}

		redirURL = redirURL + "#" + section;
		response.sendRedirect(redirURL);

		redirOnSuccess(model, null, null, null, redirURL, request, response);
	}
}
