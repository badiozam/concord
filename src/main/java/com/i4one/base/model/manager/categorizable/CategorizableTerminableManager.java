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
package com.i4one.base.model.manager.categorizable;

import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.dao.categorizable.CategorizableRecordTypeDao;
import com.i4one.base.dao.categorizable.CategorizableTerminableClientRecordType;
import com.i4one.base.model.manager.terminable.TerminableClientType;
import com.i4one.base.model.manager.terminable.TerminableManager;

/**
 * Manages items that are in a category. Extends the TerminableManager whose methods
 * ignore any category filtering
 * 
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public interface CategorizableTerminableManager<U extends CategorizableTerminableClientRecordType, T extends TerminableClientType<U>> extends PaginableManager<U,T>, TerminableManager<U, T>
{
	@Override
	public CategorizableRecordTypeDao<U> getDao();
}