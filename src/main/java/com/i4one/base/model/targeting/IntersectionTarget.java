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

import com.i4one.base.model.manager.targetable.ActivityTarget;

/**
 * A target that combines two other targets to allow retrieving users that belong
 * to both targets.
 * 
 * @author Hamid Badiozamani
 */
public class IntersectionTarget extends BaseTarget
{
	private static final String KEY_PREFIX = "base.intersect";

	private final TargetResolver targetResolver;

	private Target firstTarget;
	private Target secondTarget;

	public IntersectionTarget(String title, TargetResolver targetResolver)
	{
		super(title);

		this.targetResolver = targetResolver;
		parse(title);
	}

	/**
	 * Attempts to guess whether the second sub target requires a database
	 * hit when using the contains(..) method or not. This is useful for
	 * cases where preloading the second set of users to compare against the
	 * first might be more efficient than testing each individual member of
	 * the first set against the second.
	 * 
	 * @return True if the contains method requires a database hit.
	 */
	public boolean isHeavyContains()
	{
		// If the second target is an activity target, then we know for
		// sure it needs to hit the database to determine membership
		//
		return (secondTarget instanceof ActivityTarget);
	}

	@Override
	public String getKey()
	{
		return KEY_PREFIX + ":" + firstTarget.getKey() + "&" + secondTarget.getKey();
	}

	@Override
	public String getName()
	{
		return KEY_PREFIX;
	}

	@Override
	public boolean isDefault()
	{
		return firstTarget.isDefault() && secondTarget.isDefault();
	}

	private void parse(String target)
	{
		String[] targetSplit = target.split(":", 2);
		if ( targetSplit.length != 2 )
		{
			throw new IllegalArgumentException("Expecting format " + getName() + ":<target1>&<target2> but got '" + target + "'");
		}

		String[] subTargets = targetSplit[1].split("&");
		if ( subTargets.length != 2 )
		{
			throw new IllegalArgumentException("Expecting format " + getName() + ":<target1>&<target2> but got '" + target + "'");
		}
		else
		{
			firstTarget = targetResolver.resolveKey(subTargets[0]);
			if ( firstTarget == null )
			{
				throw new IllegalArgumentException("Invalid subtarget '" + subTargets[0] + "' for "+ getName());
			}

			secondTarget = targetResolver.resolveKey(subTargets[1]);
			if ( secondTarget == null )
			{
				throw new IllegalArgumentException("Invalid subtarget '" + subTargets[1] + "' for "+ getName());
			}
		}
	}

	public Target getFirstTarget()
	{
		return firstTarget;
	}

	public void setFirstTarget(Target firstTarget)
	{
		this.firstTarget = firstTarget;
	}

	public Target getSecondTarget()
	{
		return secondTarget;
	}

	public void setSecondTarget(Target secondTarget)
	{
		this.secondTarget = secondTarget;
	}

}
