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
package com.i4one.base.model;

import com.i4one.base.model.i18n.IString;

/**
 * A settings interface for managers.
 * 
 * @author Hamid Badiozamani
 */
public interface Settings
{
	/**
	 * Whether the feature is enabled or not.
	 * 
	 * @return True if enabled.
	 */
	public boolean isEnabled();

	/**
	 * Set the enabled flag
	 * 
	 * @param enabled The enabled flag
	 */
	public void setEnabled(boolean enabled);

	/**
	 * The singular form name of the feature this setting is for. This
	 * is intended for use in generalizing attachments of features for
	 * managers.
	 * 
	 * @return The singular name
	 */
	public IString getNameSingle();

	/**
	 * The plural form name of the feature this settings is for. This is
	 * intended for use in generalizing attachments of features for managers.
	 * 
	 * @return The plural form name
	 */
	public IString getNamePlural();
}