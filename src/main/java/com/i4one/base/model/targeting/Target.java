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
package com.i4one.base.model.targeting;


/**
 * @author Hamid Badiozamani
 */
public interface Target
{
	/**
	 * Whether this target type matches the input key. For example, a
	 * BirthYearTarget might match any key that begins with
	 * 'base.users:age-' regardless of the actual range that follows.
	 * 
	 * @param key The key to match
	 * 
	 * @return True if this target type matches the key.
	 */
	public boolean matches(String key);

	/**
	 * Returns they key of this target. The key identifies a given target
	 * exactly such that two targets with the same key are considered to
	 * be equal (e.g. base.users-age:18-24, base.users-gender:m,
	 * promotion.codes:5, etc.).
	 * 
	 * @return The key of the target.
	 */
	public String getKey();

	/**
	 * Returns the title of this target. This is the human readable title
	 * of the target and is typically set by TargetResolvers that have access
	 * the request session.
	 * 
	 * @return The title of this target.
	 */
	public String getTitle();

	/**
	 * Sets the title of this target. 
	 * 
	 * @param title The new title of this target.
	 */
	public void setTitle(String title);

	/**
	 * Returns the name of this target. This roughly serves the same purpose
	 * as class name (e.g. age, gender, promotion.codes, etc.) with a few
	 * exceptions where smaller specializations may be necessary.
	 * 
	 * @return The name of the target
	 */
	public String getName();

	/**
	 * Whether the given target has the nullifying value or not. The nullifying value
	 * matches all members and thus renders the target essentially ineffective. Thus,
	 * if this target has an internal state that would match all members, this method
	 * must return true.
	 * 
	 * @return True if this object is still the default setting.
	 */
	public boolean isDefault();
}
