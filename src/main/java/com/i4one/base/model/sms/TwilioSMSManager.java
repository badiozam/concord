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
import com.i4one.base.core.Base64;
import static com.i4one.base.core.Base64.encodeBytes;
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
import javax.net.ssl.HttpsURLConnection;

/**
 * @author Hamid Badiozamani
 */
public class TwilioSMSManager extends BaseLoggable implements SMSManager
{
	private static final String ACCOUNTID = "twilioSMSManager.AccountSID";
	private static final String AUTHTOKEN = "twilioSMSManager.AuthToken";
	private static final String FROMPHONE = "twilioSMSManager.FromPhoneNo";
	private static final String PHONEPREFIX = "twilioSMSManager.PhonePrefix";

	private static final int MAXCHARS = 160;

	// The following URL scheme is what has been published in the docs
	// at https://www.twilio.com/docs/api/rest
	//
	private static final String SENDURL = "https://api.twilio.com/2010-04-01/Accounts/{0}/Messages.json";
	private static final String SENDPARAMS = "From={0}&To={1}&Body={2}";

	public void init()
	{
	}

	@Override
	public boolean sendSMS(User user, String message)
	{
		if ( user.exists() && user.getCanSMS() )
		{
			SingleClient client = user.getClient();

			String accountId = client.getOptionValue(ACCOUNTID);
			String authToken = client.getOptionValue(AUTHTOKEN);
			String fromPhone = client.getOptionValue(FROMPHONE);
			String phonePrefix = client.getOptionValue(PHONEPREFIX);

			String httpBasicAuth = accountId + ":" + authToken;

			String sendURL = format(SENDURL, accountId);
			try
			{
				URL url = new URL(sendURL);
				HttpsURLConnection urlConn = (HttpsURLConnection)url.openConnection();

				/*
				StringBuilder urlParameters = new StringBuilder();
				urlParameters.append("From=").append(URLEncoder.encode(fromPhone, "utf-8"));
				urlParameters.append("&To=").append(URLEncoder.encode(phonePrefix, "utf-8")).append(URLEncoder.encode(user.getCellPhone(), "utf-8"));
				urlParameters.append("&Body=").append(URLEncoder.encode(Utils.abbrStr(message, MAXCHARS), "utf-8"));
				*/
				String urlParameters = format(SENDPARAMS,
					encode(phonePrefix + fromPhone, "utf-8"),
					encode(phonePrefix + user.getCellPhone(), "utf-8"),
					encode(abbrStr(message, MAXCHARS), "utf-8"));

				byte[] urlParamBytes = urlParameters.getBytes();

				urlConn.setDoOutput(true);
				urlConn.setDoInput(true);
				urlConn.setInstanceFollowRedirects(false); 
				urlConn.setRequestMethod("POST"); 
				urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
				urlConn.setRequestProperty("charset", "utf-8");
				urlConn.setRequestProperty("Content-Length", "" + Integer.toString(urlParamBytes.length));
				urlConn.setRequestProperty("Authorization", "Basic " + encodeBytes(httpBasicAuth.getBytes("utf-8"), Base64.DONT_BREAK_LINES));
				urlConn.setUseCaches (false);

				getLogger().debug("Sending request to " + sendURL + " w/ authentication credentials: " + httpBasicAuth);
				urlConn.connect();
				getLogger().debug("Using parameters: " + urlParameters);

				try ( OutputStream os = urlConn.getOutputStream() )
				{
					os.write(urlParamBytes);
					os.flush();
				}

				// Incoming request is JSON that needs to be parsed
				//
				try (InputStreamReader ir = new InputStreamReader(urlConn.getInputStream()) )
				{
					TwilioResponse response = getInstance().getGson().fromJson(ir, TwilioResponse.class);
					getLogger().debug("Received response: " + response.toString());

					return response.getStatus().equals("queued");
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
