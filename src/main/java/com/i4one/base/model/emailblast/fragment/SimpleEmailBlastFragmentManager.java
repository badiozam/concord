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
package com.i4one.base.model.emailblast.fragment;

import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleEmailBlastFragmentManager extends BasePaginableManager<EmailBlastFragmentRecord, EmailBlastFragment> implements EmailBlastFragmentManager
{
	@Override
	public EmailBlastFragment emptyInstance()
	{
		return new EmailBlastFragment();
	}

	@Override
	public Set<EmailBlastFragment> getAllFragments(EmailBlast emailBlast, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByEmailblastid(emailBlast.getSer(), pagination));
	}

	@Override
	public EmailBlastFragment getEmailBlastFragment(EmailBlast emailBlast, int offset)
	{
		return convertDelegate(getDao().getFragment(emailBlast.getSer(), offset));
	}

	@Override
	public EmailBlastFragmentRecordDao getDao()
	{
		return (EmailBlastFragmentRecordDao) super.getDao();
	}

}
