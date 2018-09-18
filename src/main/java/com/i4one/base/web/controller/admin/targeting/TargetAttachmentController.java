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
package com.i4one.base.web.controller.admin.targeting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.i4one.base.core.Base;
import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.emailblast.EmailBlastManager;
import com.i4one.base.model.manager.GenericFeature;
import com.i4one.base.model.targeting.GenericTarget;
import com.i4one.base.model.targeting.Target;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.base.web.controller.admin.GenericWebModelAttachment;
import com.i4one.base.web.persistence.Persistence;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
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
public class TargetAttachmentController extends BaseAdminViewController
{
	private EmailBlastManager emailBlastManager;

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelTargetable )
		{
			WebModelTargetable targetable = (WebModelTargetable)modelAttribute;

			Map<String, String> validGenders = new LinkedHashMap<>();
			String[] validGendersArray = Utils.forceEmptyStr(model.buildMessage("msg.base.User.validGenders")).split("\n");
			Arrays.asList(validGendersArray)
				.stream()
				.map( (currGender) -> { return Utils.trimString(currGender); } )
				.forEach( (currGender) -> { validGenders.put(currGender, model.buildMessage("msg.base.User.gender." + currGender));} );
			model.put("validGenders", validGenders);
		}

		return model;
	}

	@Override
	public Model initResponse(Model model, HttpServletResponse response, Object modelAttribute)
	{
		Model retVal = super.initResponse(model, response, modelAttribute);

		if ( modelAttribute instanceof WebModelTargetable )
		{
			WebModelTargetable targetable = (WebModelTargetable)modelAttribute;

			if ( targetable.getFeature().getFeatureName().equalsIgnoreCase("base.emailblasts"))
			{
				EmailBlast emailBlast = getEmailBlastManager().getById(targetable.getFeature().getSer());
				if ( emailBlast.exists() )
				{
					targetable.setTargets(emailBlast.getTargets());
				}
			}
		}

		return retVal;
	}
	@RequestMapping(value = "**/admin/targeting/target", method = RequestMethod.GET)
	public Model attach(@ModelAttribute("targetable") WebModelTargetable targetable,
					BindingResult result,
					@RequestParam(value = "featureid", required = false) Integer featureId,
					@RequestParam(value = "featurename", required = false) String featureName,
					@RequestParam(value = "redir", required = false) String redir,
					@RequestParam(value = "displayname", required = false) String displayname,
					@RequestParam(value = "targetkey", required = false) String targetKey,
					HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Model model;

		if ( !Utils.isEmpty(targetKey))
		{
			String attachmentJSON = (String)Persistence.getObject(request, ATTACHMENT_KEY);
			GenericWebModelAttachment webModelAttachment = Base.getInstance().getJacksonObjectMapper().readValue(attachmentJSON, GenericWebModelAttachment.class);

			targetable.setFeature(webModelAttachment.getFeature());
			targetable.setDisplayName(webModelAttachment.getDisplayName());
			targetable.setRedir(webModelAttachment.getRedir());

			model = initRequest(request, targetable);
			if ( targetable.getFeature().getFeatureName().equalsIgnoreCase("base.emailblasts"))
			{
				EmailBlast emailBlast = getEmailBlastManager().getById(targetable.getFeature().getSer());
				if ( emailBlast.exists() )
				{
					Set<Target> targets = new HashSet<>(emailBlast.getTargets());
					targets.add(new GenericTarget(targetKey));

					emailBlast.setTargets(targets);

					getEmailBlastManager().update(emailBlast);
					success(model, "msg.base.admin.targeting.target.success");
				}
			}
			else
			{
				// XXX: Add generalized targeting code here
			}

			// External targeting mode complete
			//
			Persistence.putObject(request, response, ATTACHMENT_KEY, null);
			model.put(ATTACHMENT_KEY, null);
		}
		else
		{
			targetable.setFeature(new GenericFeature(featureId, featureName));
			model = initRequest(request, targetable);

			targetable.setRedir(redir);
			targetable.setDisplayName(displayname);
		}

		return initResponse(model, response, targetable);
	}

	@RequestMapping(value = "**/admin/targeting/target", method = RequestMethod.POST)
	public Model doAttach(@ModelAttribute("targetable") WebModelTargetable targetable,
					BindingResult result,
					@RequestParam(value = "external", required = false) Boolean external,
					HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException
	{
		Model model = initRequest(request, targetable);

		if ( external != null && external )
		{
			// External targeting mode selected
			//
			Persistence.putObject(request, response, ATTACHMENT_KEY, targetable.toJSONString());
			model.put(ATTACHMENT_KEY, targetable);
		}
		else
		{
			try
			{
				if ( targetable.getFeature().getFeatureName().equalsIgnoreCase("base.emailblasts"))
				{
					EmailBlast emailBlast = getEmailBlastManager().getById(targetable.getFeature().getSer());
					if ( emailBlast.exists() )
					{
						Set<Target> targets = new HashSet<>(emailBlast.getTargets());
						if ( !targetable.getAgeGender().isDefault() )
						{
							targets.add(targetable.getAgeGender());
						}
	
						if ( !targetable.getZipCode().isDefault() )
						{
							targets.add(targetable.getZipCode());
						}
	
						emailBlast.setTargets(targets);
	
						getEmailBlastManager().update(emailBlast);
						success(model, "msg.base.admin.targeting.target.success");
					}
				}
				else
				{
					// XXX: Add generalized targeting code here
				}
			}
			catch (Errors errors)
			{
				fail(model, "msg.base.admin.targeting.target.failure", result, errors);
			}
		}

		return initResponse(model, response, targetable);
	}

	public EmailBlastManager getEmailBlastManager()
	{
		return emailBlastManager;
	}

	@Autowired
	public void setEmailBlastManager(EmailBlastManager emailBlastManager)
	{
		this.emailBlastManager = emailBlastManager;
	}

}
