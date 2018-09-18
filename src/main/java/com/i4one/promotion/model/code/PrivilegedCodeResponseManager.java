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
package com.i4one.promotion.model.code;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.activity.BaseActivityPrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedCodeResponseManager extends BaseActivityPrivilegedManager<CodeResponseRecord, CodeResponse, Code> implements CodeResponseManager
{
	private CodeResponseManager codeResponseManager;

	@Override
	public User getUser(CodeResponse item)
	{
		return item.getUser();
	}

	@Override
	public CodeResponseManager getImplementationManager()
	{
		return getCodeResponseManager();
	}

	@Override
	public SingleClient getClient(CodeResponse item)
	{
		return item.getCode().getClient();
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<CodeResponse>> processCodes(String codeStr, User user, TerminablePagination pagination)
	{
		List<ReturnType<CodeResponse>> retVal = getCodeResponseManager().processCodes(codeStr, user, pagination);

		// The user has to have had access to each code retrieved before
		// we allow the transaction to go through
		//
		retVal.forEach( (ReturnType<CodeResponse> retCodeResponse) ->
		{
			checkWrite(retCodeResponse.getPost(), getUser(retCodeResponse.getPost()), "create");
		});

		return retVal;
	}

	public CodeResponseManager getCodeResponseManager()
	{
		return codeResponseManager;
	}

	@Autowired
	@Qualifier("promotion.InstantWinningCodeResponseManager")
	public void setCodeResponseManager(CodeResponseManager codeResponseManager)
	{
		this.codeResponseManager = codeResponseManager;
	}

	@Override
	public ActivityReport getReport(CodeResponse item, TopLevelReport report, PaginationFilter pagination)
	{
		return getCodeResponseManager().getReport(item, report, pagination);
	}
}
