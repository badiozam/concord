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
package com.i4one.base.web.controller.admin.balancetriggers;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.manager.GenericFeature;
import com.i4one.base.model.manager.attachable.NonExclusiveTerminablePagination;
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
public class TriggerAttachmentController extends BaseAdminViewController
{
	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		TerminablePagination pagination = new NonExclusiveTerminablePagination(model.getTimeInSeconds(),
			new ClientPagination(model.getSingleClient(), SimplePaginationFilter.NONE));

		// We get all live and future triggers to present to the user
		//
		Set<BalanceTrigger> triggers = new LinkedHashSet<>();
		triggers.addAll(getBalanceTriggerManager().getLive(pagination));

		pagination.setFuture();
		triggers.addAll(getBalanceTriggerManager().getByRange(pagination));

		model.put("balanceTriggers", triggers);

		// We need to set the current balance triggers for the attachment to make sure they show up
		// on the check boxes
		//
		if ( modelAttribute instanceof WebModelTriggerAttachment )
		{
			WebModelTriggerAttachment attachment = (WebModelTriggerAttachment)modelAttribute;
			if ( attachment.getBalanceTriggers().isEmpty() )
			{
				initAttachment(model, attachment);
			}
		}

		return model;
	}

	private void initAttachment(Model model, WebModelTriggerAttachment attachment)
	{
		Set<BalanceTrigger> attachedTriggers = getBalanceTriggerManager().getAllTriggersByFeature(attachment.getFeature(),
			new NonExclusiveTerminablePagination(model.getTimeInSeconds(),
				new ClientPagination(model.getSingleClient(), SimplePaginationFilter.NONE)));

		// These triggers are already attached, there's really no need to reattach them
		// unless they become detached by the time the page gets resubmitted
		//
		attachment.setBalanceTriggers( attachedTriggers );

		Set<BalanceTrigger> unattachedTriggers = ((Set<BalanceTrigger>)model.get("balanceTriggers"))
			.stream()
			.filter( (trigger) -> { return !attachedTriggers.contains(trigger); } )
			.collect(Collectors.toSet());

		model.put("unattachedTriggers", toRadioButtonMapping(unattachedTriggers, BalanceTrigger::getTitle, model.getLanguage()));
	}

	@RequestMapping(value = "**/admin/balancetriggers/attach", method = RequestMethod.GET)
	public Model attachTrigger(@ModelAttribute("attachment") WebModelTriggerAttachment attachment,
					BindingResult result,
					@RequestParam(value = "featureid", required = true) Integer featureId,
					@RequestParam(value = "featurename", required = true) String featureName,
					@RequestParam(value = "redirfeatureid", required = false) Integer redirFeatureId,
					@RequestParam(value = "redirfeaturename", required = false) String redirFeatureName,
					@RequestParam(value = "section", required = false) String section,
					@RequestParam(value = "redir", required = false) String redir,
					@RequestParam(value = "displayname", required = false) String displayname,
					HttpServletRequest request, HttpServletResponse response)
	{
		attachment.setFeature(new GenericFeature(featureId, featureName));

		Model model = initRequest(request, attachment);

		if (Utils.isEmpty(redir) && redirFeatureId != null && redirFeatureName != null )
		{
			GenericFeature redirFeature = new GenericFeature(redirFeatureId, redirFeatureName);
			redir = buildRedirURL(model, redirFeature, section);
		}

		attachment.setRedir(redir);
		attachment.setDisplayName(displayname);


		return initResponse(model, response, attachment);
	}

	@RequestMapping(value = "**/admin/balancetriggers/attach", method = RequestMethod.POST)
	public Model doAttachTrigger(@ModelAttribute("attachment") WebModelTriggerAttachment attachment,
					BindingResult result,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, attachment);

		try
		{
			attachment.getBalanceTriggers().stream().forEach( (balanceTrigger) ->
			{
				getBalanceTriggerManager().associate(attachment.getFeature(), balanceTrigger);
			});

			initAttachment(model, attachment);
			success(model, "msg.base.admin.balancetriggers.attach.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.balancetriggers.attach.failure", result, errors);
		}

		return initResponse(model, response, attachment);
	}

	@RequestMapping(value = "**/admin/balancetriggers/detach", method = RequestMethod.GET)
	public void detachTrigger( @RequestParam(value = "triggerid", required = true) Integer triggerId,
					@RequestParam(value = "featureid", required = true) Integer featureId,
					@RequestParam(value = "featurename", required = true) String featureName,
					@RequestParam(value = "redirfeatureid", required = false) Integer redirFeatureId,
					@RequestParam(value = "redirfeaturename", required = false) String redirFeatureName,
					@RequestParam(value = "section", required = false) String section,
					@RequestParam(value = "redir", required = false) String redir,
					HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);

		BalanceTrigger trigger = new BalanceTrigger();
		trigger.setSer(triggerId);
		trigger.loadedVersion();

		GenericFeature feature = new GenericFeature(featureId, featureName);
		if ( getBalanceTriggerManager().dissociate(feature, trigger) )
		{
			// If we make it this far, we were successfull
			//
			success(model, "msg.base.admin.balancetriggers.detach.success");
		}
		else
		{
			fail(model, "msg.base.admin.balancetriggers.detach.failure", null, null);
		}


		String redirURL = redir;
		if ( Utils.isEmpty(redirURL))
		{
			// We redirect to this feature instead of the one we were given. These are set,
			// for example, in the event that we need to attach a trigger to an exclusive
			// instant win
			//
			if ( redirFeatureId != null && redirFeatureName != null )
			{
				GenericFeature redirFeature = new GenericFeature(redirFeatureId, redirFeatureName);
				redirURL = buildRedirURL(model, redirFeature, section);
			}
			else
			{
				redirURL = buildRedirURL(model, feature, section);
			}
		}

		response.sendRedirect(redirURL);

		redirOnSuccess(model, null, null, null, redirURL, request, response);
	}

	protected String buildRedirURL(Model model, GenericFeature feature, String section)
	{
		String redirURL = model.getBaseURL() + "/"
				+ feature.getDelegate().getSchemaName()
				+ "/admin/"
				+ feature.getDelegate().getTableName()
				+ "/update.html?id=" + feature.getSer()
				+ "#" + Utils.forceEmptyStr(section);

		return redirURL;
	}
}
