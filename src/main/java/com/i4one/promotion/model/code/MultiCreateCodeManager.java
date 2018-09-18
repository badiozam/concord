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
import com.i4one.base.dao.terminable.TerminableClientRecordTypeDao;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseDelegatingManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class MultiCreateCodeManager extends BaseDelegatingManager<CodeRecord, Code> implements CodeManager
{
	private CodeManager codeManager;

	@Override
	public ReturnType<Code> create(Code item)
	{
		ReturnType<Code> retVal = new ReturnType<>();
		retVal.setPre(item);
		retVal.setPost(item);
	
		String origCodes = item.getCode().replaceAll("\r", "");
		Errors errors = new Errors();

		String[] codes = origCodes.split("\n");
		for ( int i = 0; i < codes.length; i++ )
		{
			String code = codes[i];
			if ( !retVal.getPost().exists() )
			{
				try
				{
					item.setCode(code);
					retVal = getCodeManager().create(item);
				}
				catch (Errors createErrors)
				{
					errors.mergeWithParams(String.valueOf(i), createErrors, "code", code);
				}
			}
			//else if ( !Utils.isEmpty(code) )
			// If an error is thrown, the transaction is aborted entirely...
			// in the future it may be be possible to roll back to a savepoint
			// and attempt the next item but for now we're left doing one code
			// error at a time.
			//
			else if ( !Utils.isEmpty(code) && !errors.hasErrors() )
			{
				try
				{
					ReturnType<Code> cloned = getCodeManager().clone(retVal.getPost());
					cloned.getPost().setCode(code);
					cloned.getPost().setStartTimeSeconds(retVal.getPost().getStartTimeSeconds());
					cloned.getPost().setEndTimeSeconds(retVal.getPost().getEndTimeSeconds());
					cloned.getPost().setTitle(retVal.getPost().getTitle());

					ReturnType<Code> updatedClone = getCodeManager().update(cloned.getPost());
					retVal.addChain("clone", updatedClone);
				}
				catch (Errors updateErrors)
				{
					errors.mergeWithParams(String.valueOf(i), updateErrors, "code", code);
				}
			}
		}

		retVal.getPost().setCode(origCodes);
		if ( errors.hasErrors() )
		{
			throw errors;
		}
		else
		{
			return retVal;
		}
	}

	@Override
	public ReturnType<Code> update(Code item)
	{
		if ( item.getCode().contains("\n"))
		{
			throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".update.nomultiupdate", "Multiple codes can only be updated inividually", new Object[] { "item", item }));
		}
		else
		{
			return getCodeManager().update(item);
		}
	}

	@Override
	public Manager<CodeRecord, Code> getImplementationManager()
	{
		return getCodeManager();
	}

	@Override
	public Set<Code> getLiveCodes(String code, TerminablePagination pagination)
	{
		return getCodeManager().getLiveCodes(code, pagination);
	}

	@Override
	public CodeSettings getSettings(SingleClient client)
	{
		return getCodeManager().getSettings(client);
	}

	@Override
	public ReturnType<CodeSettings> updateSettings(CodeSettings settings)
	{
		return getCodeManager().updateSettings(settings);
	}

	@Override
	public Set<Code> getLive(TerminablePagination pagination)
	{
		return getCodeManager().getLive(pagination);
	}

	@Override
	public Set<Code> getByRange(TerminablePagination pagination)
	{
		return getCodeManager().getByRange(pagination);
	}

	@Override
	public TerminableClientRecordTypeDao<CodeRecord> getDao()
	{
		return getCodeManager().getDao();
	}

	public CodeManager getCodeManager()
	{
		return codeManager;
	}

	@Autowired
	public void setCodeManager(CodeManager instantWinnableCodeManager)
	{
		this.codeManager = instantWinnableCodeManager;
	}

}
