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
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Registers resolvers and resolves a list of multiple targets.
 *
 * @author Hamid Badiozamani
 */
public interface TargetResolver
{
	/**
	 * Resolves a target given a key.
	 * 
	 * @param key The key to resolve.
	 * 
	 * @return The resolved target or null if no matching target was found.
	 */
	public Target resolveKey(String key);

	/**
	 * Gets a human readable i18n titles matching the given target.
	 * 
	 * @param target The target to resolve.
	 * 
	 * @return The title of the given target or null if the target is not
	 * 	resolvable.
	 */
	public IString getTitle(Target target);

	/**
	 * Resolve a set of users for the given targets
	 * 
	 * @param target The targets to resolve 
	 * @param offset The number of items to skip
	 * @param limit The maximum number of items to return
	 * 
	 * @return A (potentially empty) set of users that belong the given target.
	 */
	public Set<User> resolveUsers(Target target, int offset, int limit);

	/**
	 * Determines whether a given user matches a target or not
	 * 
	 * @param target The target to check
	 * @param user The user to test
	 * 
	 * @return True if the user belongs to the given target.
	 */
	public boolean contains(Target target, User user);

	/**
	 * Resolves a collection of targets to their concrete implementations. This
	 * is used to convert generic and delegated implementations to concrete
	 * implementations that can be used to resolve users.
	 * 
	 * @param targets The targets to resolve.
	 * 
	 * @return The unique set of resolved targets.
	 */
	public Set<Target> resolveTargets(Collection<Target> targets);

	/**
	 * Resolve a set of users for the given targets
	 * 
	 * @param targets The set of targets to resolve (e.g. base.users:m,
	 * 	promotion.codes:33,base.users:18-24)
	 * @param offset The number of items to skip
	 * @param limit The maximum number of items to return
	 * 
	 * @return A (potentially empty) set of users that belong to any of the
	 * 	given targets.
	 */
	public Set<User> resolveUsers(Set<Target> targets, int offset, int limit);

	/**
	 * Determines whether a given user matches the set of targets
	 * 
	 * @param targets The targets to check
	 * @param user The user to test
	 * 
	 * @return True if the user belongs to the given target set.
	 */
	public boolean contains(Set<Target> targets, User user);

	/**
	 * Gets all of the human readable i18n titles matching the given
	 * targets.
	 * 
	 * @param targets The targets to resolve.
	 * 
	 * @return A map of all targets and their corresponding titles.
	 */
	public Map<Target, IString> getTitles(Set<Target> targets);

	/**
	 * Registers a list resolver to be checked when resolving targets.
	 * 
	 * @param listResolver The list resolver to register
	 * 
	 * @return True if the resolver was registered.
	 */
	public boolean registerResolver(TargetListResolver listResolver);
	
	/**
	 * Unregisters a previously registered list resolver.
	 * 
	 * @param listResolver The list resolver to register.
	 * 
	 * @return True if the resolver was unregistered.
	 */
	public boolean unregisterResolver(TargetListResolver listResolver);
}
