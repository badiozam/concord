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
package com.i4one.base.web.controller.admin.pages;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import com.i4one.base.model.page.Page;
import com.i4one.base.model.page.PageManager;
import com.i4one.base.model.page.PageRecord;
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
public class PageCrudController extends BaseTerminableSiteGroupTypeCrudController<PageRecord, Page>
{
	private PageManager pageManager;

	@Override
	public Model initRequest(HttpServletRequest request, Page modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelPage )
		{
			WebModelPage page = (WebModelPage)modelAttribute;
			SingleClient client = model.getSingleClient();
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.pages";
	}

	@Override
	protected Manager<PageRecord, Page> getManager()
	{
		return getPageManager();
	}

	@RequestMapping(value = { "**/base/admin/pages/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("page") WebModelPage page,
					@RequestParam(value = "id", required = false) Integer pageId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model =  createUpdateImpl(page, pageId, request, response);

		if ( !page.exists() )
		{
			page.setTitle(model.buildIMessage("base.pageManager.defaultTitle"));
			page.setBody(model.buildIMessage("base.pageManager.defaultBody"));
		}

		return model;
	}

	@RequestMapping(value = "**/base/admin/pages/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("page") WebModelPage page, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(page, result, request, response);
	}

	@RequestMapping(value = { "**/base/admin/pages/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer pageId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(pageId, request, response);
	}

	public PageManager getPageManager()
	{
		return pageManager;
	}

	@Autowired
	public void setPageManager(PageManager pageManager)
	{
		this.pageManager = pageManager;
	}
}
