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
package com.i4one.base.web.controller.admin.client;

import com.i4one.base.model.Errors;
import com.i4one.base.model.message.Message;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import java.util.List;
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
// XXX: There are issues with using @Transactional in this class and interface
// based proxying. Consider moving the transactional methods into managers
// to avoid instead
//
@Controller
public class StyleContentSettingsController extends BaseAdminViewController
{
	private static final String STYLE_KEY = "style.cssc";

	private static final String HEAD_KEY = "content.head";

	private static final String HEADER_KEY = "content.header";
	private static final String FOOTER_KEY = "content.footer";

	private static final String MAINSTART_KEY = "content.main.start";
	private static final String MAINEND_KEY = "content.main.end";

	private static final String FRONTPAGE_KEY = "msg.base.user.index.body";
	private static final String MEMBERSHOME_KEY = "msg.base.user.index.membersbody";

	@Override
	public Model initRequest(HttpServletRequest request, Object object)
	{
		Model retVal = super.initRequest(request, object);

		return retVal;
	}

	@RequestMapping(value = "**/base/admin/client/style", method = RequestMethod.GET)
	public Model viewStyleSettings(@ModelAttribute("settings") StyleSettings settings,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		List<Message> contentMessages = getMessageManager().getAllMessages(model.getSingleClient(), STYLE_KEY);
		settings.setContentMessages(contentMessages);

		return initResponse(model, response, settings);
	}

	@RequestMapping(value = "**/base/admin/client/style", method = RequestMethod.POST)
	//@Transactional(readOnly = false)
	public Model updateStyleSettings(@ModelAttribute("settings") StyleSettings settings, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		try
		{
			List<Message> contentMessages = settings.getContentMessages(model.getSingleClient(), STYLE_KEY);
			contentMessages.forEach( (message) -> { getMessageManager().update(message); } );

			success(model, "msg.base.admin.client.style.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.client.style.failure", result, errors);
		}

		return initResponse(model, response, settings);
	}

	@RequestMapping(value = "**/base/admin/client/content", method = RequestMethod.GET)
	public Model viewContentSettings(@ModelAttribute("settings") ContentSettings settings,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		List<Message> headMessages = getMessageManager().getAllMessages(model.getSingleClient(), HEAD_KEY);
		List<Message> headerMessages = getMessageManager().getAllMessages(model.getSingleClient(), HEADER_KEY);
		List<Message> footerMessages = getMessageManager().getAllMessages(model.getSingleClient(), FOOTER_KEY);

		settings.setHeadMessages(headMessages);
		settings.setHeaderMessages(headerMessages);
		settings.setFooterMessages(footerMessages);

		List<Message> mainStartMessages = getMessageManager().getAllMessages(model.getSingleClient(), MAINSTART_KEY);
		List<Message> mainEndMessages = getMessageManager().getAllMessages(model.getSingleClient(), MAINEND_KEY);

		settings.setMainStartMessages(mainStartMessages);
		settings.setMainEndMessages(mainEndMessages);

		return initResponse(model, response, settings);
	}

	@RequestMapping(value = "**/base/admin/client/content", method = RequestMethod.POST)
	//@Transactional(readOnly = false)
	public Model updateContentSettings(@ModelAttribute("settings") ContentSettings settings, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		try
		{
			List<Message> headMessages = settings.getHeadMessages(model.getSingleClient(), HEAD_KEY);
			List<Message> headerMessages = settings.getHeaderMessages(model.getSingleClient(), HEADER_KEY);
			List<Message> footerMessages = settings.getFooterMessages(model.getSingleClient(), FOOTER_KEY);

			headMessages.forEach( (message) -> { getMessageManager().update(message); } );
			headerMessages.forEach( (message) -> { getMessageManager().update(message); } );
			footerMessages.forEach( (message) -> { getMessageManager().update(message); } );

			List<Message> mainStartMessages = settings.getMainStartMessages(model.getSingleClient(), MAINSTART_KEY);
			List<Message> mainEndMessages = settings.getMainEndMessages(model.getSingleClient(), MAINEND_KEY);

			mainStartMessages.forEach( (message) -> { getMessageManager().update(message); } );
			mainEndMessages.forEach( (message) -> { getMessageManager().update(message); } );

			success(model, "msg.base.admin.client.content.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.client.content.failure", result, errors);
		}

		return initResponse(model, response, settings);
	}

	@RequestMapping(value = "**/base/admin/client/frontpage", method = RequestMethod.GET)
	public Model viewFrontPageContentSettings(@ModelAttribute("settings") FrontPageContentSettings settings,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		List<Message> frontPageMessages = getMessageManager().getAllMessages(model.getSingleClient(), FRONTPAGE_KEY);
		settings.setBodyMessages(frontPageMessages);

		return initResponse(model, response, settings);
	}

	@RequestMapping(value = "**/base/admin/client/frontpage", method = RequestMethod.POST)
	//@Transactional(readOnly = false)
	public Model updateFrontPageContentSettings(@ModelAttribute("settings") FrontPageContentSettings settings, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		try
		{
			List<Message> frontPageMessages = settings.getBodyMessages(model.getSingleClient(), FRONTPAGE_KEY);
			frontPageMessages.forEach( (message) -> { getMessageManager().update(message); } );

			success(model, "msg.base.admin.client.frontpage.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.client.frontpage.failure", result, errors);
		}

		return initResponse(model, response, settings);
	}

	@RequestMapping(value = "**/base/admin/client/membershome", method = RequestMethod.GET)
	public Model viewFrontPageContentSettings(@ModelAttribute("settings") MembersHomePageContentSettings settings,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		List<Message> membersHomeMessages = getMessageManager().getAllMessages(model.getSingleClient(), MEMBERSHOME_KEY);
		settings.setBodyMessages(membersHomeMessages);

		return initResponse(model, response, settings);
	}

	@RequestMapping(value = "**/base/admin/client/membershome", method = RequestMethod.POST)
	//@Transactional(readOnly = false)
	public Model updateFrontPageContentSettings(@ModelAttribute("settings") MembersHomePageContentSettings settings, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		try
		{
			List<Message> membersHomeMessages = settings.getBodyMessages(model.getSingleClient(), MEMBERSHOME_KEY);
			membersHomeMessages.forEach( (message) -> { getMessageManager().update(message); } );

			success(model, "msg.base.admin.client.membershome.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.client.membershome.failure", result, errors);
		}

		return initResponse(model, response, settings);
	}
}