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

import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.user.User;
import java.util.Set;

/**
 * Interface for resolving a list of users for a specific target. Each list resolver
 * handles a corresponding type of Target as returned by the resolveKey(..) method.
 * The list resolver thus retrieves a set of users given its corresponding Target type
 * for use by external classes.
 * 
 * @author Hamid Badiozamani
 */
public interface TargetListResolver
{
	/**
	 * Returns the name of the keys this resolver handles.
	 * 
	 * @return The name of the keys this resolver handles.
	 */
	public String getName();

	/**
	 * Converts a given key to its corresponding target object.
	 * 
	 * @param key The key to resolve.
	 * 
	 * @return The corresponding target or null if the key is not
	 *	 convertible.
	 */
	public Target resolveKey(String key);

	/**
	 * Whether this resolver matches the given target
	 * 
	 * @param target The target to test
	 * 
	 * @return True if this resolver matches the given target
	 */
	public boolean matches(Target target);

	/**
	 * Resolves the target to a human readable string
	 * 
	 * @param target The target to resolve.
	 * 
	 * @return The i18n title of the given target or null
	 * 	if this resolver does not handle the given target.
	 */
	public IString getTitle(Target target);

	/**
	 * Gets the list of users associated with the given target.
	 * 
	 * @param target The target for which to retrieve users.
	 * @param users The set of users to populate.
	 * @param offset The number of items to skip
	 * @param limit The maximum number of items to return
	 * 
	 * @return True if the target was resolved.
	 */
	public boolean resolve(Target target, Set<User> users, int offset, int limit);

	/**
	 * Determines whether the given user matches the target or not.
	 * 
	 * @param target The target to check
	 * @param user The user to lookup
	 * 
	 * @return True if the user matches the target, false otherwise.
	 */
	public boolean contains(Target target, User user);
}
