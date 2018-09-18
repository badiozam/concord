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
package com.i4one.base.model.user;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Authenticable;
import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.UserType;
import com.i4one.base.model.client.SingleClient;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import org.springframework.security.crypto.codec.Hex;

/**
 * @author Hamid Badiozamani
 */
public class User extends BaseSingleClientType<UserRecord> implements Authenticable, SingleClientType<UserRecord>, UserType
{
	static final long serialVersionUID = 42L;

	/** The user is still pending verification */
	public static final int STATUS_PENDING = 0;

	/** The user has been verified */
	public static final int STATUS_VERIFIED = 1;

	public User()
	{
		super(new UserRecord());
	}

	protected User(UserRecord delegate)
	{
		super(delegate);
	}

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		validateNames(errors);
		validateCity(errors);
		validateState(errors);
		validatePhoneNumbers(errors);
		validateGender(errors);
		validateEmail(errors);
		validateZipcode(errors);

		return errors;
	}

	protected void validateNames(Errors errors)
	{
		String firstnameRegex = getClient().getOptionValue("validate.User.firstname");
		if ( !getFirstName().matches(firstnameRegex) )
		{
			errors.addError(new ErrorMessage("firstName", "msg.base.User.invalidFirstname", "Invalid first name: $item.firstName", new Object[]{"item", this}));
		}

		String lastnameRegex = getClient().getOptionValue("validate.User.lastname");
		if ( !getLastName().matches(lastnameRegex) )
		{
			errors.addError(new ErrorMessage("lastName", "msg.base.User.invalidLastname", "Invalid last name: $item.lastName", new Object[]{"item", this}));
		}
	}

	protected void validateCity(Errors errors)
	{
		String cityRegex = getClient().getOptionValue("validate.User.city");
		if ( !getCity().matches(cityRegex) )
		{
			errors.addError(new ErrorMessage("city", "msg.base.User.invalidCity", "Invalid city: $item.city", new Object[]{"item", this}));
		}
	}

	protected void validateState(Errors errors)
	{
		String stateRegex = getClient().getOptionValue("validate.User.state");
		if ( !getState().matches(stateRegex) )
		{
			errors.addError(new ErrorMessage("state", "msg.base.User.invalidState", "Invalid state: $item.state", new Object[]{"item", this}));
		}
	}

	protected void validatePhoneNumbers(Errors errors)
	{
		validateHomePhoneNumber(errors);
		validateCellPhoneNumber(errors);
	}

	protected void validateHomePhoneNumber(Errors errors)
	{
		setHomePhone(Utils.forceEmptyStr(getHomePhone()).replaceAll("[^0-9]*", ""));
		if ( !validPhoneNumber(getHomePhone()))
		{
			errors.addError(new ErrorMessage("homePhone", "msg.base.User.invalidHomePhone", "Invalid home phone: $item.homePhone", new Object[]{"item", this}));
		}
	}

	protected void validateCellPhoneNumber(Errors errors)
	{
		setCellPhone(Utils.forceEmptyStr(getCellPhone()).replaceAll("[^0-9]*", ""));
		if ( !validPhoneNumber(getCellPhone()))
		{
			errors.addError(new ErrorMessage("cellPhone", "msg.base.User.invalidCellPhone", "Invalid mobile phone: $item.cellPhone", new Object[]{"item", this}));
		}
	}

	protected boolean validPhoneNumber(String phoneNo)
	{
		String phoneRegex = getClient().getOptionValue("validate.User.phone");
		return Utils.forceEmptyStr(phoneNo).matches(phoneRegex);
	}

	protected void validateGender(Errors errors)
	{
		String genderRegex = getClient().getOptionValue("validate.User.gender");
		if ( !getGender().matches(genderRegex))
		{
			errors.addError(new ErrorMessage("gender", "msg.base.User.invalidGender", "Invalid gender: $item.gender", new Object[]{"item", this}));
		}
	}

	protected void validateEmail(Errors errors)
	{
		if ( !getEmail().matches(getClient().getOptionValue("validate.User.email")) )
		{
			errors.addError(new ErrorMessage("email", "msg.base.User.invalidEmail", "Invalid e-mail address: $item.email", new Object[]{"item", this}));
		}
	}

	protected void validateZipcode(Errors errors)
	{
		if ( !getZipcode().matches(getClient().getOptionValue("validate.User.zipcode")))
		{
			errors.addError(new ErrorMessage("zipcode", "msg.base.User.invalidZipcode", "Invalid ZIP code: $item.zipcode", new Object[]{"item", this}));
		}
	}

	@Override
	public void setUser(User user)
	{
		// Can't set ourselves to be something else. But some classes
		// will expect this to work (e.g. Controllers that set the user
		// to be the logged in user) so if the two objects are the same
		// we can silently ignore, otherwise we'll throw an exception
		// to indicate that the operation was unsuccessful
		//
		if ( user != null && user.exists() && this.exists() && !user.equals(this) )
		{
			throw new UnsupportedOperationException("Can't set ourselves to be something else.");
		}
	}

	@Override
	public User getUser()
	{
		return this;
	}

	public boolean isVerified()
	{
		return getDelegate().getStatus() == STATUS_VERIFIED;
	}

	public int getStatus()
	{
		return getDelegate().getStatus();
	}

	public void setStatus(int status)
	{
		getDelegate().setStatus(status);
	}

	@Override
	public String getEmail()
	{
		return getDelegate().getEmail();
	}

	public void setEmail(String val)
	{
		getDelegate().setEmail(Utils.forceEmptyStr(val).toLowerCase());
	}

	@Override
	public String getPassword()
	{
		return getDelegate().getPassword();
	}

	public void setPassword(String val) throws NoSuchAlgorithmException
	{
		getDelegate().setPassword(new String(Hex.encode(Utils.getHash(Utils.forceEmptyStr(val), "SHA-256"))));
	}

	public boolean isForceUpdate()
	{
		return getDelegate().getForceUpdate();
	}

	public void setForceUpdate(boolean forceUpdate)
	{
		getDelegate().setForceUpdate(forceUpdate);
	}

	public String getMD5Password() throws NoSuchAlgorithmException
	{
		return Utils.getMD5Hex(Utils.forceEmptyStr(getDelegate().getPassword()));
	}

	@Override
	public String getUsername()
	{
		return getDelegate().getUsername();
	}

	public void setUsername(String val)
	{
		getDelegate().setUsername(Utils.trimString(Utils.forceEmptyStr(val).toLowerCase()));
	}

	public String getCity()
	{
		return getDelegate().getCity();
	}

	public void setCity(String city)
	{
		getDelegate().setCity(city);
	}

	public String getFirstName()
	{
		return getDelegate().getFirstname();
	}

	public void setFirstName(String firstname)
	{
		getDelegate().setFirstname(firstname);
	}

	public String getGender()
	{
		return String.valueOf(getDelegate().getGender());
	}

	public void setGender(String gender)
	{
		// Always store gender in lower case
		//
		getDelegate().setGender(Utils.defaultIfEmpty(gender, "o").toLowerCase(getClient().getLocale()).charAt(0));
	}

	public Boolean getIsMarried()
	{
		return getDelegate().getIsMarried();
	}

	public void setIsMarried(Boolean isMarried)
	{
		getDelegate().setIsMarried(isMarried);
	}

	public String getLastName()
	{
		return getDelegate().getLastname();
	}

	public void setLastName(String lastname)
	{
		getDelegate().setLastname(lastname);
	}

	public String getStreet()
	{
		return getDelegate().getStreet();
	}

	public void setStreet(String street)
	{
		getDelegate().setStreet(street);
	}

	public String getState()
	{
		return getDelegate().getState();
	}

	public void setState(String state)
	{
		getDelegate().setState(state);
	}

	public String getZipcode()
	{
		return getDelegate().getZipcode();
	}

	public void setZipcode(String zipcode)
	{
		getDelegate().setZipcode(zipcode);
	}

	public String getCellPhone()
	{
		return getDelegate().getCellphone();
	}

	public void setCellPhone(String cellphone)
	{
		getDelegate().setCellphone(cellphone);
	}

	public String getHomePhone()
	{
		return getDelegate().getHomephone();
	}

	public void setHomePhone(String homephone)
	{
		getDelegate().setHomephone(homephone);
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	public int getBirthMM()
	{
		return getDelegate().getBirthmm() == null ? 0 : getDelegate().getBirthmm();
	}

	public void setBirthMM(int birthmm)
	{
		getDelegate().setBirthmm(birthmm);
	}

	public int getBirthDD()
	{
		return getDelegate().getBirthdd() == null ? 0 : getDelegate().getBirthdd();
	}

	public void setBirthDD(int birthdd)
	{
		getDelegate().setBirthdd(birthdd);
	}

	public int getBirthYYYY()
	{
		return getDelegate().getBirthyyyy() == null ? 0 : getDelegate().getBirthyyyy();
	}

	public void setBirthYYYY(int birthyyyy)
	{
		getDelegate().setBirthyyyy(birthyyyy);
	}

	public Calendar getBirthCalendar()
	{
		return getBirthCalendar(getCalendar());
	}

	public Calendar getBirthCalendar(Calendar now)
	{
		if ( getDelegate().getBirthdd() == null || getDelegate().getBirthmm() == null || getDelegate().getBirthyyyy() == null)
		{
			return (Calendar) now.clone();
		}
		else
		{
			Calendar retVal = (Calendar) now.clone();
			retVal.set(Calendar.MONTH, getDelegate().getBirthmm() - 1);
			retVal.set(Calendar.DAY_OF_MONTH, getDelegate().getBirthdd());
			retVal.set(Calendar.YEAR, getDelegate().getBirthyyyy());

			return retVal;
		}
	}

	public void setBirthCalendar(Calendar birthCalendar)
	{
		getDelegate().setBirthmm(birthCalendar.get(Calendar.MONTH) + 1);
		getDelegate().setBirthdd(birthCalendar.get(Calendar.DAY_OF_MONTH));
		getDelegate().setBirthyyyy(birthCalendar.get(Calendar.YEAR));
	}

	public Integer getAgeInYears()
	{
		return getAgeInYears(getCalendar());
	}

	public Integer getAgeInYears(Calendar now)
	{
		Calendar birthCalendar = getBirthCalendar(now);

		int retVal = now.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
		if (now.get(Calendar.DAY_OF_YEAR) <= birthCalendar.get(Calendar.DAY_OF_YEAR))
		{
			// Haven't hit birthday yet
			//
			retVal--;
		}

		return retVal;
	}

	public Calendar getCalendar()
	{
		return Calendar.getInstance(getClient().getTimeZone(), getClient().getLocale());
	}

	public Date getCreateTime()
	{
		return Utils.toDate(getCreateTimeSeconds());
	}

	public Integer getCreateTimeSeconds()
	{
		return getDelegate().getCreatetime();
	}

	public void setCreateTimeSeconds(Integer createTimeSeconds)
	{
		getDelegate().setCreatetime(createTimeSeconds);
	}

	public Date getLastLoginTime()
	{
		return Utils.toDate(getLastLoginTimeSeconds());
	}

	public Integer getLastLoginTimeSeconds()
	{
		return getDelegate().getLastlogintime();
	}

	public void setLastLoginTimeSeconds(Integer lastLoginTimeSeconds)
	{
		getDelegate().setLastlogintime(lastLoginTimeSeconds);
	}

	public Boolean getCanEmail()
	{
		return getDelegate().getCanEmail();
	}

	public void setCanEmail(Boolean canEmail)
	{
		getDelegate().setCanEmail(canEmail);
	}

	public Boolean getCanSMS()
	{
		return getDelegate().getCanSMS();
	}

	public void setCanSMS(Boolean canSMS)
	{
		getDelegate().setCanSMS(canSMS);
	}

	public Boolean getCanCall()
	{
		return getDelegate().getCanCall();
	}

	public void setCanCall(Boolean canCall)
	{
		getDelegate().setCanCall(canCall);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		String username = Utils.forceEmptyStr(getUsername());
		String email = Utils.forceEmptyStr(getEmail());

		return username + "-" + email;
	}

	@Override
	protected boolean equalsInternal(RecordTypeDelegator<UserRecord> right)
	{
		if ( right instanceof User)
		{
			User rightUser = (User)right;
			return Objects.equals(getUsername(), rightUser.getUsername()) &&
				Objects.equals(getEmail(), rightUser.getEmail());
		}
		else
		{
			return false;
		}
	}
}
