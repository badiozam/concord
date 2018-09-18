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

import static com.i4one.base.core.Utils.isEmpty;
import com.i4one.base.model.Errors;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class TriggerViewController extends BaseAdminViewController
{
	@RequestMapping(value = "**/admin/balancetriggers/search", method = RequestMethod.GET )
	public Model search(@ModelAttribute("triggerList") TriggerList triggerList, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, triggerList);

		if ( !isEmpty(triggerList.getFeatureName()) && triggerList.getFeatureId() != null )
		{
			triggerList.setAllTriggers(getBalanceTriggerManager().getAllTriggersByFeature(triggerList.getFeature(), SimplePaginationFilter.NONE));

			if ( triggerList.getTriggers().isEmpty() )
			{
				fail(model, "msg.base.admin.balancetriggers.index.search.failure", null, Errors.NO_ERRORS);
			}

			getLogger().debug("Trigger list = " + triggerList);
		}

		return initResponse(model, response, triggerList);
	}

}
