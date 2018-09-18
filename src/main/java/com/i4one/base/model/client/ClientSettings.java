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
package com.i4one.base.model.client;

import com.i4one.base.core.Utils;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.Errors;
import com.i4one.base.model.i18n.IString;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaImpl;

/**
 * @author Hamid Badiozamani
 */
public class ClientSettings extends BaseLoggable
{
	private SingleClient client;

	private IString defBalSingle;
	private IString defBalPlural;

	private String fbAppid;
	private String fbSecret;

	private String recaptchaPrivateKey;
	private String recaptchaPublicKey;

	public ClientSettings()
	{
		defBalSingle = new IString();
		defBalPlural = new IString();
	}

	public Errors validate()
	{
		Errors errors = new Errors();

		errors.merge("client", getClient().validate());

		return errors;
	}

	public SingleClient getClient()
	{
		return client;
	}

	public void setClient(SingleClient client)
	{
		this.client = client;
	}

	public IString getDefBalSingle()
	{
		return defBalSingle;
	}

	public void setDefBalSingle(IString defBalSingle)
	{
		this.defBalSingle = defBalSingle;
	}

	public IString getDefBalPlural()
	{
		return defBalPlural;
	}

	public void setDefBalPlural(IString defBalPlural)
	{
		this.defBalPlural = defBalPlural;
	}

	public String getFbAppid()
	{
		return fbAppid;
	}

	public void setFbAppid(String fbAppid)
	{
		this.fbAppid = fbAppid;
	}

	public String getFbSecret()
	{
		return fbSecret;
	}

	public void setFbSecret(String fbSecret)
	{
		this.fbSecret = fbSecret;
	}

	public boolean isCaptchaEnabled()
	{
		return !Utils.isEmpty(getRecaptchaPublicKey()) && !Utils.isEmpty(getRecaptchaPrivateKey());
	}

	public String getRecaptchaPrivateKey()
	{
		return recaptchaPrivateKey;
	}

	public void setRecaptchaPrivateKey(String recaptchaPrivateKey)
	{
		this.recaptchaPrivateKey = recaptchaPrivateKey;
	}

	public String getRecaptchaPublicKey()
	{
		return recaptchaPublicKey;
	}

	public void setRecaptchaPublicKey(String recaptchaPublicKey)
	{
		this.recaptchaPublicKey = recaptchaPublicKey;
	}

	public ReCaptcha getReCaptcha()
	{
		ReCaptchaImpl retVal = new ReCaptchaImpl();

		retVal.setPublicKey(getRecaptchaPublicKey());
		retVal.setPrivateKey(getRecaptchaPrivateKey());

		return retVal;
	}

	public ClientOption getFbAppidOption()
	{
		return getClientOptionInternal("fb.appid", getFbAppid());
	}

	public ClientOption getFbSecretOption()
	{
		return getClientOptionInternal("fb.secret", getFbSecret());
	}

	public ClientOption getRecaptchaPublicKeyOption()
	{
		return getClientOptionInternal("base.recaptchaPublicKey", getRecaptchaPublicKey());
	}

	public ClientOption getRecaptchaPrivateKeyOption()
	{
		return getClientOptionInternal("base.recaptchaPrivateKey", getRecaptchaPrivateKey());
	}

	private ClientOption getClientOptionInternal(String key, String value)
	{
		ClientOption retVal = new ClientOption();
		retVal.setClient(getClient());
		retVal.setKey(key);
		retVal.setValue(value);

		return retVal;
	}
}
