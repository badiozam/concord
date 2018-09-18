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
package com.i4one.promotion.model.code.targets;

import com.i4one.base.model.manager.targetable.BaseActivityTargetListResolver;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.promotion.model.code.Code;
import com.i4one.promotion.model.code.CodeManager;
import com.i4one.promotion.model.code.CodeResponse;
import com.i4one.promotion.model.code.CodeResponseManager;
import com.i4one.promotion.model.code.CodeResponseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CodeResponseActivityTargetListResolver extends BaseActivityTargetListResolver<CodeResponseRecord, CodeResponse, Code, CodeResponseTarget>
{
	private CodeManager codeManager;
	private CodeResponseManager codeResponseManager;

	@Override
	protected CodeResponseTarget emptyInstance(String key)
	{
		return new CodeResponseTarget(key);
	}

	@Override
	protected CodeManager getParentManager()
	{
		return getCodeManager();
	}

	@Override
	protected ActivityManager<CodeResponseRecord, CodeResponse, Code> getActivityManager()
	{
		return getCodeResponseManager();
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

	public CodeResponseManager getCodeResponseManager()
	{
		return codeResponseManager;
	}

	@Autowired
	public void setCodeResponseManager(CodeResponseManager codeResponseManager)
	{
		this.codeResponseManager = codeResponseManager;
	}

}
