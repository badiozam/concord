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
package com.i4one.base.model.usermessage;

import com.i4one.base.model.manager.terminable.TerminableManager;

/**
 * Manages user messages. To retrieve a set of messages to be displayed to all users
 * in the given client group, a SiteGroupPagination must be specified. For messages
 * to a single user within a client group the UserPagination must also be chained.
 * 
 * For example, the following will get the specific messages target to the user but
 * not any general messages:
 * 
 * <code>getUserMessageManager().getLive(new UserPagination(user, new SiteGroupPagination(sitegroups))); </code>
 * 
 * Whereas the code below will get all general messages but no user-specific messages:
 * 
 * <code>getUserMessageManager().getLive(new SiteGroupPagination(sitegroups))); </code>
 * 
 * @author Hamid Badiozamani
 */
public interface UserMessageManager extends TerminableManager<UserMessageRecord, UserMessage>
{
	public boolean exists(UserMessage userMessage);
}
