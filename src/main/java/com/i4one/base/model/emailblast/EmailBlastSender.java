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
package com.i4one.base.model.emailblast;

import com.i4one.base.model.user.User;
import java.util.Collection;
import java.util.Map;

/**
 * @author Hamid Badiozamani
 */
public interface EmailBlastSender
{
	/**
	 * Send an e-mail blast to its target.
	 * 
	 * @param emailBlast The e-mail blast to send
	 */
	public void sendEmailBlast(EmailBlast emailBlast);

	/**
	 * Send an e-mail blast to a single user.
	 * 
	 * @param user The user to send the e-mail blast to
	 * @param emailBlast The e-mail blast to send
	 */
	public void sendEmailBlast(User user, EmailBlast emailBlast);

	/**
	 * Send an e-mail blast to a list of users
	 * 
	 * @param users The users to send the e-mail blast to
	 * @param emailBlast The e-mail blast to send
	 * 
	 * @return The total number of e-mails actually sent.
	 */
	public int sendEmailBlast(Collection<User> users, EmailBlast emailBlast);

	/**
	 * Get a pre-built model that is exposed to the underlying e-mail template
	 * of a given blast when sending out an e-mail blast to a particular user.
	 * 
	 * @param user The user to which the e-mail blast would go to
	 * @param emailBlast The e-mail blast from which the model is to be built
	 * 
	 * @return The model containing all substitution variables
	 */
	public Map<String, Object> getEmailBlastModel(User user, EmailBlast emailBlast);
}
