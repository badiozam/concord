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
package com.i4one.base.core;

import com.i4one.base.model.client.Client;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.user.User;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * This class represents an e-mail template that is loaded
 * from the database and used to send e-mails to different
 * recipients
 *
 * @author Hamid Badiozamani
 */
public class LegacyEmailTemplate extends BaseLoggable
{
	/** The from key suffix */
	public static final String FROM_SUFFIX = "from";

	/** This Bcc key suffix */
	public static final String BCC_SUFFIX = "bcc";

	/** This Reply-To key suffix */
	public static final String REPLYTO_SUFFIX = "replyto";

	/** This Enabled key suffix */
	public static final String ENABLED_SUFFIX = "enabled";

	/** The subject key suffix */
	public static final String SUBJECT_SUFFIX = "subject";

	/** The HTML body key suffix */
	public static final String HTMLBODY_SUFFIX = "htmlbody";

	/** The text body key suffix */
	public static final String TEXTBODY_SUFFIX = "textbody";

	/** The client e-mail address */
	public static final String CLIENTEMAIL = "clientemail";

	/** The opt out URL */
	public static final String OPTOUT_HREF = "optout";

	// The from address
	//
	private com.i4one.base.model.message.Message from;

	// The subject
	//
	private com.i4one.base.model.message.Message subject;

	// The HTML body w/ variables
	//
	private com.i4one.base.model.message.Message htmlBody;

	// The text body w/ variables
	//
	private com.i4one.base.model.message.Message textBody;

	// The Bcc array of e-mails to send this e-mail to
	//
	private com.i4one.base.model.message.Message bcc;

	// The reply-to array of e-mails
	//
	private com.i4one.base.model.message.Message replyTo;

	// Whether to actually send this e-mail out or not
	//
	private boolean isEnabled;

	// This is the reference to the client object passed
	// in the constructor.
	//
	private final SingleClient client;

	// This is the client-level subhash
	//
	private HashMap<String, Object> subvars;

	/**
	 * The constructor
	 *
	 * @param client The client for whom to send the e-mail
	 * @param configKey The configuration key that points to
	 * 	the value that holds all template information
	 */
	public LegacyEmailTemplate(SingleClient client, String configKey)
	{
		// Save the client and transport cache instances
		//
		this.client = client;

		// Load the options for each sub-key
		//
		com.i4one.base.model.message.Message fromOpt = client.getMessage(configKey + "." + FROM_SUFFIX);
		com.i4one.base.model.message.Message replytoOpt = client.getMessage(configKey + "." + REPLYTO_SUFFIX);
		com.i4one.base.model.message.Message enabledOpt = client.getMessage(configKey + "." + ENABLED_SUFFIX);
		com.i4one.base.model.message.Message bccOpt = client.getMessage(configKey + "." + BCC_SUFFIX);
		com.i4one.base.model.message.Message subjectOpt = client.getMessage(configKey + "." + SUBJECT_SUFFIX);
		com.i4one.base.model.message.Message htmlbodyOpt = client.getMessage(configKey + "." + HTMLBODY_SUFFIX);
		com.i4one.base.model.message.Message textbodyOpt = client.getMessage(configKey + "." + TEXTBODY_SUFFIX);

		// If any of these are missing throw an exception
		//
		if ( 
			!fromOpt.exists() || 
			!bccOpt.exists() || 
			!replytoOpt.exists() || 
			!enabledOpt.exists() || 
			!subjectOpt.exists() || 
			!htmlbodyOpt.exists() ||
			!textbodyOpt.exists() )
		{
			getLogger().debug("fromOpt" + fromOpt);
			getLogger().debug("bccOpt" + bccOpt);
			getLogger().debug("replytoOpt" + replytoOpt);
			getLogger().debug("enabledOpt" + enabledOpt);
			getLogger().debug("subjectOpt" + subjectOpt);
			getLogger().debug("htmlbodyOpt" + htmlbodyOpt);
			getLogger().debug("textbodyOpt" + textbodyOpt);

			throw new IllegalArgumentException("Sorry, the " + configKey + " e-mail template is not properly set up.");
		}
		else
		{
			// We're good to go, save all of the options
			//
			from = fromOpt;
			subject = subjectOpt;
			htmlBody = htmlbodyOpt;
			textBody = textbodyOpt;

			// This is the list of bcc e-mail addresses that
			// are comma delimited
			//
			bcc = bccOpt;

			// This is the list of reply-to e-mail addresses
			// that are comma delimited
			//
			replyTo = replytoOpt;

			// Whether to send this e-mail out or not
			//
			isEnabled = Utils.defaultIfNaB(enabledOpt.getValue(), false);
		}

		// Initialize the rest of the object
		//
		init();
	}

	/**
	 * The initializer constructor
	 *
	 * @param client The client for whom the e-mail is going out
	 * @param from The from address for this e-mail
	 * @param subject The subject of the e-mail
	 * @param htmlbody The html body of the e-mail
	 * @param textbody The text body of the e-mail
	 */
	public LegacyEmailTemplate(SingleClient client, String from, String subject, String htmlbody, String textbody)
	{
		// System.out.println("-- Thread " + Thread.currentThread().getId() + ": Creating e-mail template.");

		this.client = client;

		// Initialize the template
		//
		this.from = new com.i4one.base.model.message.Message();
		this.subject = new com.i4one.base.model.message.Message();
		this.htmlBody = new com.i4one.base.model.message.Message();
		this.textBody = new com.i4one.base.model.message.Message();

		this.from.setValue(from);
		this.subject.setValue(subject);
		this.htmlBody.setValue(htmlbody);
		this.textBody.setValue(textbody);

		this.bcc = new com.i4one.base.model.message.Message();
		this.replyTo = new com.i4one.base.model.message.Message();

		this.bcc.setValue("");
		this.replyTo.setValue("");

		this.isEnabled = true;

		// System.out.println("-- Thread " + Thread.currentThread().getId() + ": EmailTemplate initializing.");

		// Initialize the rest of the object
		//
		init();
	}

	/**
	 * Initialize any variables that are not specific to a constructor
	 */
	private void init()
	{
		// Create the subhash
		//
		subvars = new HashMap<>();

		// System.out.println("-- Thread " + Thread.currentThread().getId() + ": Init loading client variables.");

		// Now populate the substitution variables
		//
		subvars.put(Client.class.getSimpleName(), client);
		// subvars.put(CLIENTBASEURL, client.getClient().get_baseurl());

		// System.out.println("-- Thread " + Thread.currentThread().getId() + ": Done initializing.");
	}

	/**
	 * This method sends an e-mail to a single recipient
	 *
	 * @param attrs The name-value pairs to use for substituting body string
	 *              variables
	 * @param to The e-mail address to send the e-mail to
	 *
	 * @throws AddressException if any of the e-mail addresses were not properly formatted
	 * @throws MessagingException if the message could not be sent
	 * @throw SQLException If there was a problem getting a client instance
	 */
	public void sendEmail(Map<String, Object> attrs, String to) throws AddressException, MessagingException
	{
		// Merge the two with the attributes passed in overwriting any keys in 
		// the default substitution variables
		//
		HashMap<String, Object> subs = new HashMap<>(attrs.size() + subvars.size());
		subs.putAll(subvars);
		subs.putAll(attrs);

		// The opt out href can be built for a given user based on the incoming attributes
		//
		subs.put(OPTOUT_HREF, getOptOutURL(client, subs));

		getLogger().debug("Sending e-mail to {} with bcc {} from {}", to,  bcc, from);

		// Get a mail session
		//
		Session session = client.getMailSession();

		// Create a new MIME message
		//
		MimeMessage newMsg = new MimeMessage(session);

		MessageManager messageManager = Base.getInstance().getMessageManager();

		// Set the headers
		//
		newMsg.setFrom(new InternetAddress(messageManager.buildMessage(from, subs).replaceAll("<..*$", "") + "<" + client.getEmail() + ">"));
		newMsg.setSubject( messageManager.buildMessage(subject, subs));

		// This can be multiple addresses but we'll only use one for now
		//
		InternetAddress[] replyTos = null;
		if ( !Utils.isEmpty(replyTo.getValue()) )
		{
			replyTos = new InternetAddress[1];
			replyTos[0] = new InternetAddress(messageManager.buildMessage(replyTo, subs));
		}

		newMsg.setReplyTo(replyTos);
		newMsg.addRecipients(Message.RecipientType.TO, to);

		// Add the Bcc only if it exists
		//
		if ( !Utils.isEmpty(bcc.getValue()) )
		{
			newMsg.addRecipients(Message.RecipientType.BCC, messageManager.buildMessage(bcc, subs));
		}

		Multipart mp = new MimeMultipart("alternative");

		// Only include the text body if it isn't empty
		//
		String builtTextBody = messageManager.buildMessage(textBody, subs);
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
		String builtHtmlBody = messageManager.buildMessage(htmlBody, subs);
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
			if ( isEnabled )
			{
				getLogger().debug("Sending e-mail to {}", to);
	
				// Create a new connection, send and close the connection to the
				// e-mail server
				//
				Transport.send(newMsg);
			}
			// If the template is not enabled
			//
			else
			{
				// Pause for a few hundred ms to simulate having sent the e-mail
				//
				try
				{
					// Thread.sleep(ClientFactory.randomNumber(900) + 100);
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
	}

	/**
	 * This method sends an e-mail to a member (as well as Bcc's)
	 *
	 * @param attrs The name-value pairs to use for substituting body string
	 *              variables
	 *
	 * @param currUser The member to whom this e-mail is to go to
	 *
	 * @throws AddressException if any of the e-mail addresses were not properly formatted
	 * @throws MessagingException if the message could not be sent
	 */
	public void sendEmail(Map<String, Object> attrs, User currUser) throws AddressException, MessagingException
	{
		// Add all member variables to the hash map
		//
		attrs.put(User.class.getSimpleName(), currUser);

		// Send the e-mail
		//
		sendEmail(attrs, currUser.getEmail());
	}

	/**
	 * Determines whether the e-mail should go out or not
	 * by testing to make sure that the body to be sent is
	 * not empty
	 *
	 * @param isHtml Whether the recipient wishes to receive html
	 *      e-mails or not
	 * 
	 * @return Whether to actually send e-mail or not
	 */
	public boolean doSend(boolean isHtml)
	{
		// Whether we've disabled this e-mail or not
		//
		if ( isHtml )
		{
			// Don't send an empty html body
			//
			return !Utils.isEmpty(htmlBody.getValue());
		}
		else
		{
			// Don't send an empty text body
			//
			return !Utils.isEmpty(textBody.getValue());
		}
	}

	/**
	 * Returns the opt-out URL for a particular member
	 *
	 * @param client The client we're to build the URL for
	 * @param attrs The attributes that contain the substitution variables for the link
	 *
	 * @return A URL for the member to go to in order to be opted out
	 */
	public static String getOptOutURL(SingleClient client, Map<String, Object> attrs)
	{
		com.i4one.base.model.message.Message optout = new com.i4one.base.model.message.Message();
		optout.setValue(client.getOptionValue("emails.optout"));

		return Base.getInstance().getMessageManager().buildMessage(optout, attrs);
		// return client.getClient().get_baseurl() + "/nonmembers/register/optout.jsp?" + Parameters.PARAM_OPTOUT + "=" + memberid;
	}

	/**
	 * Get the enabled flag
	 * 
	 * @return The enabled flag
	 */
	public boolean getEnabled()
	{
		return this.isEnabled;
	}

	/**
	 * Set the enabled flag
	 * 
	 * @param isEnabled The new enabled flag
	 */
	public void setEnabled( boolean isEnabled )
	{
		this.isEnabled = isEnabled;
	}

	public String getBcc()
	{
		return bcc.getValue();
	}

	public void setBcc(String bcc)
	{
		this.bcc.setValue(bcc);
	}

	public String getReplyTo()
	{
		return replyTo.getValue();
	}

	public void setReplyTo(String replyTo)
	{
		this.replyTo.setValue(replyTo);
	}
}
