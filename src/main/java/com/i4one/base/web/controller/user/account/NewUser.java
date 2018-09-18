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
package com.i4one.base.web.controller.user.account;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.client.ClientSettings;
import com.i4one.base.web.controller.Model;
import com.restfb.types.User;
import java.util.Calendar;
import java.util.Date;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Hamid Badiozamani
 */
/* @GroupSequence({EmptyCheck.class, ContentCheck.class}) */
public class NewUser extends ClearPasswordUser
{
	static final long serialVersionUID = 42L;

	private transient boolean hasAgreedTerms;
	private transient String facebookAccessToken;
	private transient String facebookId;
	private transient com.restfb.types.User fbUser;

	private transient Model model;
	private transient ReCaptcha reCaptcha;
	private transient String recaptcha_response_field;
	private transient String recaptcha_challenge_field;

	private transient ClientSettings clientSettings;

	private transient boolean solvedCaptcha;

	public NewUser()
	{
		solvedCaptcha = false;

		setCanEmail(true);
	}

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		// Bypass captcha if it's not enabled
		//
		if ( !clientSettings.isCaptchaEnabled() )
		{
			solvedCaptcha = true;
		}

		// We only require the user to solve this once. Also, since the library doesn't allow for multiple calls
		// for the same challenge, validation would return false on a second call (although ideally an object
		// shouldn't be validated twice).
		//
		if ( !solvedCaptcha )
		{
			getLogger().debug("Challenge field: " + getRecaptcha_challenge_field());
			ReCaptchaResponse response = reCaptcha.checkAnswer(model.getRequest().getRemoteAddr(),
				getRecaptcha_challenge_field(),
				getRecaptcha_response_field());

			if ( !response.isValid() )
			{
				errors.addError(new ErrorMessage("reCaptcha", "msg.base.User.invalidCaptcha", "Invalid captcha: $response.errorMessage", new Object[]{"response", response}));
			}
			else
			{
				solvedCaptcha = true;
			}
		}

		return errors;
	}

	@Override
	protected void validatePassword(Errors errors)
	{
		// The user is signing in with Facebook so they don't need a password
		//
		if ( isPasswordNeeded() )
		{
			super.validatePassword(errors);
		}
		else
		{
			// Might be a good idea to set their password to their facebookId?
		}
	}

	public boolean isPasswordNeeded()
	{
		// A password is necessary if the user isn't signing up with Facebook
		//
		return Utils.isEmpty(facebookId);
	}

	@Size(min = 2, message = "msg.base.User.invalidUsername")
	@Pattern(regexp = "^[^@]*$", message = "msg.base.User.usernameNotEmail")
	@Override
	public String getUsername()
	{
		return super.getUsername();
	}

	@NotBlank(message = "msg.base.User.invalidEmail")
	@Email(message = "msg.base.User.invalidEmail")
	@Override
	public String getEmail()
	{
		return super.getEmail();
	}

	@Override
	public String getFirstName()
	{
		return super.getFirstName();
	}

	@Override
	public String getLastName()
	{
		return super.getLastName();
	}

	@Override
	public void setStreet(String address)
	{
		super.setStreet(Utils.trimString(address));
	}

	@Override
	public String getCity()
	{
		return super.getCity();
	}

	@Override
	public void setCity(String city)
	{
		super.setCity(Utils.trimString(city));
	}

	@Override
	public String getState()
	{
		return super.getState();
	}

	@Override
	public String getZipcode()
	{
		return Utils.forceEmptyStr(super.getZipcode()).replaceAll("[ \t]*", "");
	}

	@Override
	public String getHomePhone()
	{
		return super.getHomePhone();
	}

	@Min(value = 1, message = "msg.base.User.invalidBirthDD")
	@Max(value = 31, message = "msg.base.User.invalidBirthDD")
	@Override
	public int getBirthDD()
	{
		return super.getBirthDD();
	}

	@Min(value = 1, message = "msg.base.User.invalidBirthMM")
	@Max(value = 12, message = "msg.base.User.invalidBirthMM")
	@Override
	public int getBirthMM()
	{
		return super.getBirthMM();
	}

	@Min(value = 1911, message = "msg.base.User.invalidBirthYYYY")
	@Max(value = 2099, message = "msg.base.User.invalidBirthYYYY")
	@Override
	public int getBirthYYYY()
	{
		return super.getBirthYYYY();
	}

	@NotBlank(message = "msg.base.User.invalidCellPhone")
	@Override
	public String getCellPhone()
	{
		return super.getCellPhone();
	}

	@AssertTrue(message = "msg.base.User.invalidHasAgreedTerms")
	public boolean getHasAgreedTerms()
	{
		return hasAgreedTerms;
	}

	public void setHasAgreedTerms(boolean hasAgreedTerms)
	{
		this.hasAgreedTerms = hasAgreedTerms;
	}

	public String getFacebookAccessToken()
	{
		return facebookAccessToken;
	}

	public void setFacebookAccessToken(String facebookAccessToken)
	{
		this.facebookAccessToken = facebookAccessToken;
	}

	public String getFacebookId()
	{
		return facebookId;
	}

	public void setFacebookId(String facebookId)
	{
		this.facebookId = facebookId;
	}

	public ReCaptcha getReCaptcha()
	{
		return reCaptcha;
	}

	public Model getModel()
	{
		return model;
	}

	public void setModel(Model model)
	{
		this.model = model;
	}

	public String getRecaptcha_response_field()
	{
		return Utils.forceEmptyStr(recaptcha_response_field);
	}

	public void setRecaptcha_response_field(String recaptcha_response_field)
	{
		this.recaptcha_response_field = recaptcha_response_field;
	}

	public String getRecaptcha_challenge_field()
	{
		return Utils.forceEmptyStr(recaptcha_challenge_field);
	}

	public void setRecaptcha_challenge_field(String recaptcha_challenge_field)
	{
		this.recaptcha_challenge_field = recaptcha_challenge_field;
	}

	public User getFbUser()
	{
		return fbUser;
	}

	public void setFbUser(User fbUser)
	{
		this.fbUser = fbUser;

		setUsername(fbUser.getUsername());
		setFirstName(fbUser.getFirstName());
		setLastName(fbUser.getLastName());
		setGender(fbUser.getGender());
		setEmail(fbUser.getEmail());

		// Remove anything after a comma which might indicate a state or province
		//
		String fbCity = Utils.forceEmptyStr(fbUser.getHometownName());
		if ( !Utils.isEmpty(fbCity) && fbCity.contains(","))
		{
			fbCity = fbCity.substring(0, fbCity.indexOf(","));
		}
		setCity(fbCity);

		Calendar currCalendar = getModel().getRequest().getCalendar();
		Date fbBirthDate = fbUser.getBirthdayAsDate();

		if ( fbBirthDate != null )
		{
			currCalendar.setTime(fbUser.getBirthdayAsDate());
			setBirthCalendar(currCalendar);
		}

		setFacebookId(fbUser.getId());
	}

	public ClientSettings getClientSettings()
	{
		return clientSettings;
	}

	public void setClientSettings(ClientSettings clientSettings)
	{
		this.clientSettings = clientSettings;
		this.reCaptcha = clientSettings.getReCaptcha();
	}

}
