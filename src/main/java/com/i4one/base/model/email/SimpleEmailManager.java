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
package com.i4one.base.model.email;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.LegacyEmailTemplate;
import com.i4one.base.core.Utils;
import com.i4one.base.model.client.ClientOptionManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.emailtemplate.EmailTemplateManager;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.user.User;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleEmailManager extends BaseLoggable implements EmailManager
{
	private EmailTemplateManager emailTemplateManager;
	private MessageManager messageManager;

	private ClientOptionManager clientOptionManager;

	public static final String OPTOUT_HREF = "optout";
	public static final String USER = User.class.getSimpleName();
	public static final String ID = "id";
	public static final String SENDER = "sender";
	public static final String BASEURL = "baseurl";

	public static final String YEAR = "yyyy";
	public static final String MONTH = "MM";
	public static final String DAY = "dd";
	public static final String HOUR = "hh";
	public static final String MINUTE = "mm";
	public static final String AMPM = "ampm";

	public static final String HOUR24 = "hh24";
	public static final String DOW = "dow";
	public static final String MMM = "MMM";

	@PostConstruct
	public void init()
	{
	}

	@Override
	public boolean sendEmail(SingleClient client, User user, String template, Map<String, Object> subVars) throws Exception
	{
		if ( user.getCanEmail() )
		{
			LegacyEmailTemplate emailTemplate = new LegacyEmailTemplate(client, template);
			subVars.put(BASEURL, client.getBaseURL(client.getLanguageList().iterator().next()));

			emailTemplate.sendEmail(subVars, user);

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean sendEmail(SingleClient client, String email, String template, Map<String, Object> subVars) throws Exception
	{
		LegacyEmailTemplate emailTemplate = new LegacyEmailTemplate(client, template);

		subVars.put(BASEURL, client.getBaseURL(client.getLanguageList().iterator().next()));
		emailTemplate.sendEmail(subVars, email);

		return true;
	}

	@Override
	public boolean sendEmail(String to, String lang, EmailTemplate template, Map<String, Object> model)
	{
		try
		{
			SingleClient client = template.getClient();

			getLogger().debug("Sending e-mail to {} with bcc {} from {} enabled {}", to, template.getBcc(), template.getFromAddress(), template.isEnabled());
	
			Session session = client.getMailSession();
			MimeMessage newMsg = new MimeMessage(session);
	
			newMsg.setFrom(new InternetAddress(getMessageManager().buildMessage(template.getFromAddressMessage(lang), model).replaceAll("<..*$", "") + "<" + client.getEmail() + ">"));
			newMsg.setSubject(getMessageManager().buildMessage(template.getSubjectMessage(lang), model));
	
			// This can be multiple addresses but we'll only use one for now
			//
			String replyTo = getMessageManager().buildMessage(template.getReplyToMessage(lang), model);

			InternetAddress[] replyTos = null;
			if ( !Utils.isEmpty(Utils.trimString(replyTo)) )
			{
				replyTos = new InternetAddress[1];
				replyTos[0] = new InternetAddress(replyTo);
			}
	
			newMsg.setReplyTo(replyTos);
			newMsg.addRecipients(Message.RecipientType.TO, to);
	
			// Add the Bcc only if it exists
			//
			String bcc =  getMessageManager().buildMessage(template.getBccMessage(lang), model);
			if ( !Utils.isEmpty(Utils.trimString(bcc)) )
			{
				newMsg.addRecipients(Message.RecipientType.BCC, bcc);
			}
	
			Multipart mp = new MimeMultipart("alternative");
	
			// Only include the text body if it isn't empty
			//
			String builtTextBody = getMessageManager().buildMessage(template.getTextBodyMessage(lang), model);
			if ( !Utils.isEmpty(Utils.trimString(builtTextBody)) )
			{
				// Set the plain/text content for the message
				//
				MimeBodyPart textPart = new MimeBodyPart();
				textPart.setContent(builtTextBody, "text/plain; charset=UTF-8");
				mp.addBodyPart(textPart);
	       		}
	
			// Only include the html body if it isn't empty
			//
			String builtHtmlBody = getMessageManager().buildMessage(template.getHtmlBodyMessage(lang), model);
			if ( !Utils.isEmpty(builtHtmlBody))
			{
				MimeBodyPart htmlPart = new MimeBodyPart();
	
				// Create an HTML body part
				//
				htmlPart.setContent(builtHtmlBody, "text/html; charset=UTF-8");
				mp.addBodyPart(htmlPart);
			}
	
			newMsg.setContent(mp);
	
			if ( mp.getCount() > 0 )
			{
				// Save the changes of the message
				//
				newMsg.saveChanges();
		
				// If the template is enabled, send the email
				//
				if ( template.isEnabled() )
				{
					// Create a new connection, send and close the connection to the
					// e-mail server
					//
					Transport.send(newMsg);
				}
				// If the template is not enabled
				//
				else
				{
					try
					{
						Thread.sleep(100);
					}
					catch ( InterruptedException ie )
					{
						getLogger().debug("Interrupted ", ie);
					}
				}
			}
			else
			{
				getLogger().debug("Skipping sending email to {} due to empty HTML and Plain body parts", to);
			}
			return true;
		}
		catch (MessagingException me)
		{
			getLogger().error("Could not send message to {}", to, me);
			return false;
		}
	}

	@Override
	public boolean sendEmail(User user, EmailTemplate template, Map<String, Object> model)
	{
		// XXX: Since the user class has no preferred language entry, we're just using the client's first available language
		//String lang = user.getLanguage();
		String lang = template.getClient().getLanguageList().iterator().next();
		return sendEmail(user.getEmail(), lang, template, model);
	}

	@Override
	public Map<String, Object> buildEmailModel(SingleClient client, Map<String, Object> model)
	{
		model.put(SENDER, client);

		return model;
	}

	@Override
	public Map<String, Object> buildUserEmailModel(SingleClient client, User user, Map<String, Object> model)
	{
		String defaultLang = client.getLanguageList().get(0);

		model.put(USER, user);

		// The base URL depends on the user's language
		//
		String lang = defaultLang; // currUser.getLanguage()
		String baseURL = "http:" + client.getBaseURL(lang) + "/base/user/";

		model.put(BASEURL, baseURL);

		// The optout href depends on the User and baseURL objects being available
		//
		model.put(OPTOUT_HREF, getOptOutURL(client, model));

		model.put(YEAR, getDateFormat("YYYY", client).format(client.getCalendar().getTime()));
		model.put(MONTH, getDateFormat("MM", client).format(client.getCalendar().getTime()));
		model.put(DAY, getDateFormat("dd", client).format(client.getCalendar().getTime()));
		model.put(HOUR, getDateFormat("hh", client).format(client.getCalendar().getTime()));
		model.put(MINUTE, getDateFormat("mm", client).format(client.getCalendar().getTime()));
		model.put(AMPM, getDateFormat("a", client).format(client.getCalendar().getTime()));

		model.put(DOW, getDateFormat("EEE", client).format(client.getCalendar().getTime()));
		model.put(MMM, getDateFormat("MMM", client).format(client.getCalendar().getTime()));
		model.put(HOUR24, getDateFormat("kk", client).format(client.getCalendar().getTime()));

		return model;
	}

	private DateFormat getDateFormat(String format, SingleClient client)
	{
		SimpleDateFormat retVal = new SimpleDateFormat(format, client.getLocale());
		retVal.setTimeZone(client.getTimeZone());

		return retVal;
	}

	/**
	 * Returns the opt-out URL for a particular member
	 *
	 * @param client The client we're to build the URL for
	 * @param attrs The attributes that contain the substitution variables for the link
	 *
	 * @return A URL for the member to go to in order to be opted out
	 */
	protected String getOptOutURL(SingleClient client, Map<String, Object> attrs)
	{
		com.i4one.base.model.message.Message optout = new com.i4one.base.model.message.Message();
		optout.setValue(getClientOptionManager().getOptionValue(client, "emails.optout"));

		return getMessageManager().buildMessage(optout, attrs);
	}

	public EmailTemplateManager getEmailTemplateManager()
	{
		return emailTemplateManager;
	}

	@Autowired
	public void setEmailTemplateManager(EmailTemplateManager emailTemplateManager)
	{
		this.emailTemplateManager = emailTemplateManager;
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	public ClientOptionManager getClientOptionManager()
	{
		return clientOptionManager;
	}

	@Autowired
	public void setClientOptionManager(ClientOptionManager clientOptionManager)
	{
		this.clientOptionManager = clientOptionManager;
	}
}
