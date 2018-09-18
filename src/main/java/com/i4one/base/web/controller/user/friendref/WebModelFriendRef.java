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
package com.i4one.base.web.controller.user.friendref;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.friendref.FriendRef;
import java.util.Set;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Hamid Badiozamani
 */
public class WebModelFriendRef extends FriendRef
{
	private Set<FriendRef> pastFriendRefs;

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		String firstnameRegex = getClient().getOptionValue("validate.User.firstname");
		if ( !getFirstName().matches(firstnameRegex) )
		{
			errors.addError(new ErrorMessage("firstname", "msg.base.FriendRef.invalidFirstname", "Invalid first name: $item.firstname", new Object[]{"item", this}));
		}

		String lastnameRegex = getClient().getOptionValue("validate.User.lastname");
		if ( !getLastName().matches(lastnameRegex) )
		{
			errors.addError(new ErrorMessage("lastname", "msg.base.FriendRef.invalidLastname", "Invalid last name: $item.lastname", new Object[]{"item", this}));
		}

		return errors;
	}

	@NotBlank(message = "msg.base.User.invalidEmail")
	@Email(message = "msg.base.User.invalidEmail")
	@Override
	public String getEmail()
	{
		return super.getEmail();
	}

	public Set<FriendRef> getPastFriendRefs()
	{
		return pastFriendRefs;
	}

	public void setPastFriendRefs(Set<FriendRef> pastFriendRefs)
	{
		this.pastFriendRefs = pastFriendRefs;
	}

}
