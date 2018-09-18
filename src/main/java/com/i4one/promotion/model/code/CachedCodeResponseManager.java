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
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedCodeResponseManager extends SimpleCodeResponseManager implements CodeResponseManager
{
	@Cacheable(value = "codeResponseManager", key = "target.makeKey(#code, #user)")
	@Override
	public CodeResponse getActivity(Code code, User user)
	{
		return super.getActivity(code, user);
	}

	@CacheEvict(value = "codeResponseManager", allEntries = true )
	@Override
	public ReturnType<CodeResponse> create(CodeResponse codeResponse)
	{
		return super.create(codeResponse);
	}

	@CacheEvict(value = "codeResponseManager", allEntries = true )
	@Override
	public List<ReturnType<CodeResponse>> processCodes(String codeStr, User user, TerminablePagination pagination)
	{
		return super.processCodes(codeStr, user, pagination);
	}

	@CacheEvict(value = "codeResponseManager", allEntries = true )
	@Override
	public ReturnType<CodeResponse> update(CodeResponse respondent)
	{
		return super.update(respondent);
	}

	@CacheEvict(value = "codeResponseManager", allEntries = true)
	@Override
	public CodeResponse remove(CodeResponse respondent)
	{
		return super.remove(respondent);
	}
}
