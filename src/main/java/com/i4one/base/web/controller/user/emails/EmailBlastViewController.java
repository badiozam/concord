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
package com.i4one.base.web.controller.user.emails;

import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EmailBlastViewController extends BaseUserEmailBlastController
{
	@Override
	public boolean isAuthRequired()
	{
		return true;
	}
 
	@RequestMapping(value="**/base/user/emails/{id}.html", produces="text/html")
	public void viewEmail(@PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);

		EmailBlast emailBlast = getEmailBlastManager().getById(id);
		User user = getRequestState().getModel().getUser();

		if ( emailBlast.exists() )
		{
			Map<String, Object> emailModel = getEmailBlastSender().getEmailBlastModel(user, emailBlast);

			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream())))
			{
				String builtHtmlBody = getMessageManager().buildMessage(emailBlast.getEmailTemplate().getHtmlBodyMessage(model.getLanguage()), emailModel);
				
				writer.write(builtHtmlBody);
			}

			recordEmailResponse(user, emailBlast);
		}
		else
		{
			getLogger().debug("E-mail blast not found, returning empty response");
		}
	}
}
