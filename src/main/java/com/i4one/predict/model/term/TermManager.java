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
package com.i4one.predict.model.term;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.terminable.TerminableManager;

/**
 * @author Hamid Badiozamani
 */
public interface TermManager extends TerminableManager<TermRecord, Term>
{
	/**
	 * Look up a term by title
	 *
	 * @param client The client the term belongs to
	 * @param title The title of the event
	 *
	 * @return The event or a non-existent Event object
	 */
	public Term getTerm(SingleClient client, String title);

	/**
	 * Get the latest term. This is either the live term (if one exists),
	 * or the last term that was live.
	 * 
	 * @param client The client the term belongs to
	 * 
	 * @return The latest term or an empty term if there were never any
	 * 	terms associated with this client.
	 */
	public Term getLatestTerm(SingleClient client);

	/**
	 * Get the current live term
	 *
	 * @param client The client the term belongs to
	 *
	 * @return The event or a non-existent Event object
	 */
	public Term getLiveTerm(SingleClient client);

	/**
	 * Get the settings for a given client.
	 * 
	 * @param client The client for which to retrieve the friend referral settings
	 * 
	 * @return The friend referral settings for the client
	 */
	public TermSettings getSettings(SingleClient client);

	/**
	 * Update the friend referral settings.
	 * 
	 * @param settings The new settings to update
	 * 
	 * @return The result of the updated settings
	 */
	public ReturnType<TermSettings> updateSettings(TermSettings settings);
}
