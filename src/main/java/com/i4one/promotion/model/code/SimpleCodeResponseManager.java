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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.activity.BaseTerminableActivityManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleCodeResponseManager extends BaseTerminableActivityManager<CodeResponseRecord, CodeResponse, Code> implements CodeResponseManager
{
	private CodeManager codeManager;

	@Override
	public CodeResponse emptyInstance()
	{
		return new CodeResponse();
	}

	@Override
	public ReturnType<CodeResponse> update(CodeResponse codeResponse)
	{
		throw new UnsupportedOperationException("Can't update a code response.");
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<CodeResponse>> processCodes(String codeStr, User user, TerminablePagination pagination)
	{
		getLogger().debug("Processing code string " + codeStr + " for user " + user);
		List<ReturnType<CodeResponse>> retVal = new ArrayList<>();
		
		// Note that we only want to check the live codes since past codes might have offers that have
		// since expired and displaying the previously played message would expose them
		//
		Set<Code> codes = getCodeManager().getLiveCodes(codeStr, new TerminablePagination(Utils.currentTimeSeconds(), pagination));

		// We're only processing the first code that's live and available
		//
		List<ReturnType<CodeResponse>> notPlayed = new ArrayList<>();
		for ( Code code : codes)
		{
			CodeResponse codeResponse = new CodeResponse();
			codeResponse.setCode(code);
			codeResponse.setUser(user);

			ReturnType<CodeResponse> processedCodeResponse = create(codeResponse);

			if ( processedCodeResponse.getPre().exists() )
			{
				// Previously played
				//
				notPlayed.add(processedCodeResponse);
			}
			else if ( !processedCodeResponse.getPost().exists() )
			{
				// Expired
				//
				notPlayed.add(processedCodeResponse);
			}
			else if ( processedCodeResponse.getPost().exists() )
			{
				// First successful play, we stop after we hit one
				// because we want to dedicate a whole response page
				// to its outro.
				//
				retVal.add(processedCodeResponse);
				break;
			}

			getLogger().debug("Processed code response " + codeResponse + " for user " + user);
		}

		if ( retVal.isEmpty() )
		{
			// This array could potentially be empty as well, but we
			// want to cover cases where a single code is attempted
			// but has been previously played or expired. At the same
			// time, if there is more than one code with one valid and
			// the other previously played, we only display the valid
			// one
			//
			if ( notPlayed.isEmpty() )
			{
				// We send a "not found" code response here because the callers
				// expect to have something with a user and a pre/post 
				//
				ReturnType<CodeResponse> notFound = new ReturnType<>();

				CodeResponse response = new CodeResponse();
				response.setUser(user);
				response.getCode().setCode(codeStr);

				notFound.setPre(response);
				notFound.setPost(response);

				notPlayed.add(notFound);
			}

			return notPlayed;
		}
		else
		{
			return retVal;
		}
	}

	@Override
	public CodeResponseRecordDao getDao()
	{
		return (CodeResponseRecordDao) super.getDao();
	}

	public CodeManager getCodeManager()
	{
		return codeManager;
	}

	@Autowired
	public void setCodeManager(CodeManager codeManager)
	{
		this.codeManager = codeManager;
	}
}
