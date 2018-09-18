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
package com.i4one.base.web.controller.admin.targeting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.model.targeting.Target;
import com.i4one.base.model.targeting.Targetable;
import com.i4one.base.model.user.targets.AgeGenderTarget;
import com.i4one.base.model.user.targets.ZipCodeTarget;
import com.i4one.base.web.controller.admin.BaseWebModelAttachment;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class WebModelTargetable extends BaseWebModelAttachment implements Targetable
{
	private transient AgeGenderTarget ageGender;
	private transient ZipCodeTarget zipCode;

	private final transient Set<Target> targets;

	public WebModelTargetable()
	{
		ageGender = new AgeGenderTarget("a", 0, 100);
		zipCode = new ZipCodeTarget("", false);
		targets = new HashSet<>();
	}

	@JsonIgnore
	public AgeGenderTarget getAgeGender()
	{
		return ageGender;
	}

	@JsonIgnore
	public void setAgeGender(AgeGenderTarget ageGender)
	{
		this.ageGender = ageGender;
	}

	@JsonIgnore
	public ZipCodeTarget getZipCode()
	{
		return zipCode;
	}

	@JsonIgnore
	public void setZipCode(ZipCodeTarget zipCode)
	{
		this.zipCode = zipCode;
	}

	@JsonIgnore
	@Override
	public Set<Target> getTargets()
	{
		return targets;
	}

	@JsonIgnore
	@Override
	public void setTargets(Collection<Target> targets)
	{
		this.targets.clear();
		this.targets.addAll(targets);
	}
}
