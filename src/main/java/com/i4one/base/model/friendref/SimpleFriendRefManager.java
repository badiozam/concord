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
package com.i4one.base.model.friendref;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.ClientOption;
import com.i4one.base.model.client.ClientOptionManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.report.NullTopLevelReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleFriendRefManager extends BasePaginableManager<FriendRefRecord, FriendRef> implements FriendRefManager
{
	private MessageManager messageManager;
	private ClientOptionManager clientOptionManager;
	private UserManager readOnlyUserManager;

	private static final String ENABLED_OPTION = "base.friendRefManager.enabled";
	private static final String MAXFRIENDS_OPTION = "base.friendRefManager.maxFriends";
	private static final String TRICKLEPERCENTAGE_OPTION = "base.friendRefManager.tricklePercentage";
	private static final String TRICKLEDURATION_OPTION = "base.friendRefManager.trickleDuration";
	private static final String DEFAULTMESSAGE_KEY = "base.friendRefManager.defaultMessage";

	@Override
	public FriendRef emptyInstance()
	{
		return new FriendRef();
	}

	@Override
	public Set<FriendRef> getFriendsByUser(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByUserid(user.getSer(), pagination));
	}

	@Override
	public FriendRef getReferral(int id, User user)
	{
		FriendRefRecord record = getDao().getBySer(id, false);
		if ( record != null )
		{
			return new FriendRef(record);
		}
		else
		{
			record = getDao().getByEmail(user.getEmail());
			if ( record != null )
			{
				return new FriendRef(record);
			}
			else
			{
				return new FriendRef();
			}
		}
	}

	@Override
	public FriendRef getReferrer(User user)
	{
		FriendRefRecord record = getDao().getByFriendid(user.getSer());
		if ( record != null )
		{
			return new FriendRef(record);
		}
		else
		{
			return new FriendRef();
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<FriendRef> create(FriendRef item)
	{
		FriendRefSettings settings = getSettings(item.getClient());
		if ( settings.isEnabled() )
		{
			User friend = item.makeOrGetFriend();
	
			if ( getReadOnlyUserManager().existsUser(friend) )
			{
				throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".create.duplicateUser", "This member already exists: $item", new Object[] { "item", item }));
			}
			else
			{
				FriendRef ref = getReferral(0, friend);
				if ( ref.exists() )
				{
					throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".create.duplicateReferral", "This member has already been referred", new Object[] { "item", item, "previous", ref }));
				}
				else
				{
					item.setTimeStampSeconds(Utils.currentTimeSeconds());
					return super.create(item);
				}
			}
		}
		else
		{
			throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".disabled", "Friend referrals are disabled for: $item.client", new Object[] { "item", item }));
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<FriendRef> processReferral(User newUser)
	{
		FriendRefSettings settings = getSettings(newUser.getClient());
		if ( settings.isEnabled() )
		{
			FriendRef ref = getReferral(0, newUser);

			if ( !ref.exists() )
			{
				// Referral doesn't exist, do nothing
				//
				return new ReturnType<>(new FriendRef());
			}
			else
			{
				FriendRefRecord itemRecord = lock(ref);

				if ( ref.getFriend(false).exists() )
				{
					// Referral was already completed, we do nothing
					//
					return new ReturnType<>(new FriendRef());
				}
				else
				{
					// Referral matched we update the record
					//
					ref.setFriend(newUser);

					// We set the overrides and validate here and then
					// call updateInternal since updateInternal requires
					// us to do so, and rather than have update do the
					// this as well as the record locking we take advantage
					// of the fact that the record is already locked.
					//
					ref.setOverrides();

					Errors errors = ref.validate();
					if ( errors.hasErrors() )
					{
						throw errors;
					}
					else
					{
						return updateInternal(itemRecord, ref);
					}
				}
			}
		}
		else
		{
			return new ReturnType<>(new FriendRef());
		}
	}

	@Override
	public FriendRefSettings getSettings(SingleClient client)
	{
		FriendRefSettings retVal = new FriendRefSettings();
		retVal.setClient(client);

		boolean enabled = Utils.defaultIfNaB(getClientOptionManager().getOption(client, ENABLED_OPTION).getValue(), true);
		int maxFriends = Utils.defaultIfNaN(getClientOptionManager().getOption(client, MAXFRIENDS_OPTION).getValue(), 0);
		float tricklePercentage = Utils.defaultIfNaF(getClientOptionManager().getOption(client, TRICKLEPERCENTAGE_OPTION).getValue(), 0.0f);
		int trickleDuration = Utils.defaultIfNaN(getClientOptionManager().getOption(client, TRICKLEDURATION_OPTION).getValue(), 86400 * 30);

		List<Message> defaultMessages = getMessageManager().getAllMessages(client, DEFAULTMESSAGE_KEY);

		retVal.setEnabled(enabled);
		retVal.setMaxFriends(maxFriends);
		retVal.setTricklePercentage(tricklePercentage);
		retVal.setTrickleDuration(trickleDuration);
		retVal.setDefaultMessages(defaultMessages);

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<FriendRefSettings> updateSettings(FriendRefSettings settings)
	{
		ReturnType<FriendRefSettings> retVal = new ReturnType<>(settings);

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		ClientOption enabledOption = getClientOptionManager().getOption(client, ENABLED_OPTION);
		enabledOption.setClient(client);
		enabledOption.setValue(String.valueOf(settings.isEnabled()));

		ClientOption maxFriendsOption = getClientOptionManager().getOption(client, MAXFRIENDS_OPTION);
		maxFriendsOption.setClient(client);
		maxFriendsOption.setValue(String.valueOf(settings.getMaxFriends()));

		ClientOption tricklePercentageOption = getClientOptionManager().getOption(client, TRICKLEPERCENTAGE_OPTION);
		tricklePercentageOption.setClient(client);
		tricklePercentageOption.setValue(String.valueOf(settings.getTricklePercentage()));

		ClientOption trickleDurationOption = getClientOptionManager().getOption(client, TRICKLEDURATION_OPTION);
		trickleDurationOption.setClient(client);
		trickleDurationOption.setValue(String.valueOf(settings.getTrickleDuration()));

		List<Message> defaultMessages = settings.getDefaultMessages(client, DEFAULTMESSAGE_KEY);
		defaultMessages.forEach( (message) -> { getMessageManager().update(message); } );

		getClientOptionManager().update(enabledOption);
		getClientOptionManager().update(maxFriendsOption);
		getClientOptionManager().update(tricklePercentageOption);
		getClientOptionManager().update(trickleDurationOption);

		retVal.setPost(settings);

		return retVal;
	}

	@Override
	public TopLevelReport getReport(FriendRef item, TopLevelReport report, PaginationFilter pagination)
	{
		TopLevelReport dbReport = getReportManager().loadReport(report);

		if ( dbReport instanceof NullTopLevelReport )
		{
			getDao().processReport(item.getDelegate(), report);
			getReportManager().saveReport(report);

			return report;
		}
		else
		{
			getLogger().debug("Loaded manager report successfully!");
			return dbReport;
		}
	}

	public UserManager getReadOnlyUserManager()
	{
		return readOnlyUserManager;
	}

	@Autowired
	@Qualifier("base.ReadOnlyUserManager")
	public void setReadOnlyUserManager(UserManager readOnlyUserManager)
	{
		this.readOnlyUserManager = readOnlyUserManager;
	}

	public ClientOptionManager getClientOptionManager()
	{
		return clientOptionManager;
	}

	@Autowired
	//@Qualifier("base.CachedClientOptionManager")
	public void setClientOptionManager(ClientOptionManager clientOptionManager)
	{
		this.clientOptionManager = clientOptionManager;
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

	@Override
	public FriendRefRecordDao getDao()
	{
		return (FriendRefRecordDao) super.getDao();
	}
}
