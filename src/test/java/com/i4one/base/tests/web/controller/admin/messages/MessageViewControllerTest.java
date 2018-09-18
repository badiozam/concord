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
package com.i4one.base.tests.web.controller.admin.messages;

import com.i4one.base.model.message.Message;
import com.i4one.base.tests.web.controller.admin.AdminViewControllerTest;
import com.i4one.base.web.controller.BaseViewController;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.messages.MessageList;
import com.i4one.base.web.controller.admin.messages.MessageViewController;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

/**
 * @author Hamid Badiozamani
 */
public class MessageViewControllerTest extends AdminViewControllerTest
{
	private MessageViewController messageViewController;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		initRequest(new MockHttpServletRequest("GET", getBaseURL() + "/base/admin/messages/index.html"));

		logAdminIn();
		preHandle(getMockRequest(), getMockResponse());

		Message message = new Message();
		message.setClient(getFirstClient());
		message.setKey("msg.test");
		message.setValue("Test value");
		message.setLanguage(getFirstClient().getLocale().getLanguage());
		getMessageManager().create(message);
	}

	@Test
	public void displayMessagesTest() throws Exception
	{
		MessageList messageList = new MessageList();

		BindingResult result = new BeanPropertyBindingResult(messageList, "messageList");
		Model model = getMessageViewController().displayMessages(messageList, result, getMockRequest(), getMockResponse());
		assertNotNull(model);
		assertFalse(result.hasErrors());

		messageList = (MessageList)model.get(MessageViewController.MESSAGELIST);
		assertNotNull(messageList);
		assertFalse(messageList.getUpdateMessages().isEmpty());
	}

	@Override
	public BaseViewController getViewController()
	{
		return getMessageViewController();
	}

	public MessageViewController getMessageViewController()
	{
		return messageViewController;
	}

	@Autowired
	public void setMessageViewController(MessageViewController messageViewController)
	{
		this.messageViewController = messageViewController;
	}

}
