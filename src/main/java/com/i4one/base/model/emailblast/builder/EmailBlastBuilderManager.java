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
package com.i4one.base.model.emailblast.builder;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface EmailBlastBuilderManager extends PaginableManager<EmailBlastBuilderRecord,EmailBlastBuilder>
{
	/**
	 * Get all email blast builders for a given client.
	 * 
	 * @param client The client for which to get the builders
	 * @param pagination The sort/ordering info
	 * 
	 * @return A (potentially empty) set of all fragments for the given client.
	 */
	public Set<EmailBlastBuilder> getAllBuilders(SingleClient client, PaginationFilter pagination);

	/**
	 * Build an e-mail blast builder with the given mappings.
	 * 
	 * @param builder The builder to create the e-mail blast
	 * 	
	 * @return The resultant of the e-mail blast that was created using the
	 * 	given variable mappings.
	 */
	public ReturnType<EmailBlast> build(EmailBlastBuilder builder);

	/**
	 * Save an e-mail blast builder's state.
	 * 
	 * @param builder The builder for which to save the state.
	 */
	public void saveState(EmailBlastBuilder builder);
}