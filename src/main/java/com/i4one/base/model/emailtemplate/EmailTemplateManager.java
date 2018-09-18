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
package com.i4one.base.model.emailtemplate;

import com.i4one.base.dao.ClientRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface EmailTemplateManager extends PaginableManager<EmailTemplateRecord,EmailTemplate>
{
	/**
	 * Get a specific e-mail template by key.
	 * 
	 * @param client The client to search
	 * @param key The key identifying the template
	 * 
	 * @return The e-mail template, or a non-existent object if not found.
	 */
	public EmailTemplate getEmailTemplate(SingleClient client, String key);

	/**
	 * Get all of the balances for a specific client
	 * 
	 * @param client The client for which to retrieve all templates for
	 * @param pagination The pagination to use for sorting
	 * 
	 * @return A (potentially empty) list of all e-mail templates for the given client
	 */
	public Set<EmailTemplate> getAllEmailTemplates(SingleClient client, PaginationFilter pagination);

	/**
	 * Get the current settings for the given manager.
	 * 
	 * @param client The client for which to retrieve the settings.
	 * @param prefix The prefix identifying the e-mail template
	 * 
	 * @return The current e-mail template settings
	 */
	public EmailTemplateSettings getSettings(SingleClient client, String prefix);

	/**
	 * Update e-mail template settings.
	 * 
	 * @param settings The new e-mail template settings to update
	 * 
	 * @return The result of the updated settings
	 */
	public ReturnType<EmailTemplateSettings> updateSettings(EmailTemplateSettings settings);

	@Override
	public ClientRecordTypeDao<EmailTemplateRecord> getDao();
}
