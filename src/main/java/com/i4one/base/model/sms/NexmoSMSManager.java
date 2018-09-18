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
package com.i4one.base.model.sms;

import static com.i4one.base.core.Base.getInstance;
import static com.i4one.base.core.Utils.abbrStr;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.User;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import static java.net.URLEncoder.encode;
import static java.text.MessageFormat.format;
import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("smsManager")
public class NexmoSMSManager extends BaseLoggable implements SMSManager
{
	private static final String APIKEY = "nexmoSMSManager.api_key";
	private static final String APISECRET = "nexmoSMSManager.api_secret";
	private static final String FROMPHONE = "nexmoSMSManager.FromPhoneNo";
	private static final String PHONEPREFIX = "nexmoSMSManager.PhonePrefix";

	private static final int MAXCHARS = 160;

	// The following URL scheme is what has been published in the docs
	// at https://docs.nexmo.com/
	//
	private static final String SENDURL = "https://rest.nexmo.com/sms/json";
	private static final String SENDPARAMS = "from={0}&to={1}&text={2}&api_key={3}&api_secret={4}";

	@PostConstruct
	public void init()
	{
	}

	@Override
	public boolean sendSMS(User user, String message)
	{
		if ( user.exists() && user.getCanSMS() )
		{
			SingleClient client = user.getClient();

			String apiKey = client.getOptionValue(APIKEY);
			String apiSecret = client.getOptionValue(APISECRET);
			String fromPhone = client.getOptionValue(FROMPHONE);
			String phonePrefix = client.getOptionValue(PHONEPREFIX);

			try
			{
				URL url = new URL(SENDURL);
				HttpsURLConnection urlConn = (HttpsURLConnection)url.openConnection();

				/*
				StringBuilder urlParameters = new StringBuilder();
				urlParameters.append("From=").append(URLEncoder.encode(fromPhone, "utf-8"));
				urlParameters.append("&To=").append(URLEncoder.encode(phonePrefix, "utf-8")).append(URLEncoder.encode(user.getCellPhone(), "utf-8"));
				urlParameters.append("&Body=").append(URLEncoder.encode(Utils.abbrStr(message, MAXCHARS), "utf-8"));
				*/
				String urlParameters = format(SENDPARAMS,
					encode(fromPhone, "utf-8"),
					encode(phonePrefix + user.getCellPhone(), "utf-8"),
					encode(abbrStr(message, MAXCHARS), "utf-8"),
					encode(apiKey, "utf-8"),
					encode(apiSecret, "utf-8"));

				byte[] urlParamBytes = urlParameters.getBytes();

				urlConn.setDoOutput(true);
				urlConn.setDoInput(true);
				urlConn.setInstanceFollowRedirects(false); 
				urlConn.setRequestMethod("POST"); 
				urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
				urlConn.setRequestProperty("charset", "utf-8");
				urlConn.setRequestProperty("Content-Length", "" + Integer.toString(urlParamBytes.length));
				urlConn.setUseCaches (false);

				urlConn.connect();
				getLogger().debug("Using parameters: " + urlParameters);

				try ( OutputStream os = urlConn.getOutputStream() )
				{
					os.write(urlParamBytes);
					os.flush();
				}

				// Incoming request is XML that needs to be parsed
				//
				try (InputStreamReader ir = new InputStreamReader(urlConn.getInputStream()) )
				{
					NexmoResponse response = getInstance().getGson().fromJson(ir, NexmoResponse.class);
					getLogger().debug("Received response: " + response.toString());

					return response.isSuccessful();
				}
			}
			catch (IOException ex)
			{
				getLogger().error("Could not send SMS:", ex );
				throw new Errors("SMSManager.sendSMS", new ErrorMessage("msg.SMSManager.sendSMS.error", "Could not send SMS message to: $user", new Object[] { "user", user }, ex));
			}
		}
		else
		{
			// Either the user didn't exist or is not able opted in
			// to receive messages
			//
			return false;
		}
	}
}
