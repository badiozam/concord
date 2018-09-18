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
package com.i4one.promotion.web.controller.admin.clickthrus;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import com.i4one.promotion.model.clickthru.ClickThru;
import com.i4one.promotion.model.clickthru.ClickThruManager;
import com.i4one.promotion.model.clickthru.ClickThruRecord;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class ClickThruCrudController extends BaseTerminableSiteGroupTypeCrudController<ClickThruRecord, ClickThru>
{
	private ClickThruManager clickThruManager;

	@Override
	public Model initRequest(HttpServletRequest request, ClickThru modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelClickThru )
		{
			WebModelClickThru clickthru = (WebModelClickThru)modelAttribute;
			SingleClient client = model.getSingleClient();
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.promotion.admin.clickthrus";
	}

	@Override
	protected Manager<ClickThruRecord, ClickThru> getManager()
	{
		return getClickThruManager();
	}

	@RequestMapping(value = { "**/promotion/admin/clickthrus/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("clickthru") WebModelClickThru clickthru,
					@RequestParam(value = "id", required = false) Integer clickthruId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model =  createUpdateImpl(clickthru, clickthruId, request, response);

		if ( !clickthru.exists() )
		{
			clickthru.setOutro(model.buildIMessage("promotion.clickThruManager.defaultOutro"));
		}

		return model;
	}

	@RequestMapping(value = "**/promotion/admin/clickthrus/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("clickthru") WebModelClickThru clickthru, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(clickthru, result, request, response);
	}

	@RequestMapping(value = { "**/promotion/admin/clickthrus/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer clickthruId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(clickthruId, request, response);
	}

	public ClickThruManager getClickThruManager()
	{
		return clickThruManager;
	}

	@Autowired
	public void setClickThruManager(ClickThruManager clickThruManager)
	{
		this.clickThruManager = clickThruManager;
	}
}
