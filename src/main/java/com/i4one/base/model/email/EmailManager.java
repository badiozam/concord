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
package com.i4one.base.model.email;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.user.User;
import java.util.Map;

/**
 * @author Hamid Badiozamani
 */
public interface EmailManager
{
	/**
	 * Send an e-mail to the user if lapse seconds has passed since the last
	 * time we sent the user an e-mail
	 *
	 * @param client The client which contains the template
	 * @param user The user to send the e-mail to
	 * @param template The template as defined in the messages section
	 * @param subVars The substitution variables to use
	 *
	 * @return If the e-mail was sent and the last e-mail time was updated, false otherwise
	 *
	 * @throws Exception
	 */
	public boolean sendEmail(SingleClient client, User user, String template, Map<String, Object> subVars) throws Exception;

	/**
	 * Send an e-mail to a given e-mail address from the given client. 
	 *
	 * @param client The client from which the e-mail template is to be loaded
	 * @param email The e-mail address to send the e-mail to
	 * @param template The template as defined in the messages section
	 * @param subVars The substitution variables to use
	 *
	 * @return If the e-mail was sent and the last e-mail time was updated, false otherwise
	 *
	 * @throws Exception
	 */
	public boolean sendEmail(SingleClient client, String email, String template, Map<String, Object> subVars) throws Exception;

	/**
	 * Send an e-mail to a user based off of the given template
	 * 
	 * @param user The user to whom the e-mail is to be sent
	 * @param template The template to use
	 * @param model The model variables to be used for substitution
	 * 
	 * @return True if the e-mail was sent, false otherwise
	 */
	public boolean sendEmail(User user, EmailTemplate template, Map<String, Object> model);
	
	/**
	 * Send an e-mail to the given e-mail address based off of the given template
	 * 
	 * @param to The e-mail address to send to
	 * @param lang The language in which to build the message to send
	 * @param template The template to use
	 * @param model The model variables to be used for substitution
	 * 
	 * @return True if the e-mail was sent, false otherwise
	 */
	public boolean sendEmail(String to, String lang, EmailTemplate template, Map<String, Object> model);

	/**
	 * Builds and appends a standard model for e-mails that are going to any user from
	 * a particular client.
	 * 
	 * @param client The client that is sending the e-mail
	 * @param model The model to append variables to
	 * 
	 * @return A model of common e-mail substitution variables.
	 */
	public Map<String, Object> buildEmailModel(SingleClient client, Map<String, Object> model);

	/**
	 * Builds and appends a standard model for e-mails that are going to a particular user from
	 * a particular client.
	 * 
	 * @param client The client that is sending the e-mail
	 * @param user The user to which the e-mail is being sent
	 * @param model The model to append variables to
	 * 
	 * @return A model of common e-mail substitution variables.
	 */
	public Map<String, Object> buildUserEmailModel(SingleClient client, User user, Map<String, Object> model);
}
