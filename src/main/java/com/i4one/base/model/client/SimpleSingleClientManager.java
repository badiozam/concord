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
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.NullTopLevelReport;
import com.i4one.base.model.report.ReportManager;
import com.i4one.base.model.report.TopLevelReport;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleSingleClientManager extends BaseSimpleManager<SingleClientRecord,SingleClient> implements SingleClientManager
{
	private SiteGroupManager siteGroupManager;
	private BalanceManager balanceManager;
	private ClientOptionManager clientOptionManager;
	private ReportManager reportManager;

	@Override
	protected ReturnType<SingleClient> createInternal(SingleClient item)
	{
		ReturnType<SingleClient> retVal = super.createInternal(item);

		SingleClient created = retVal.getPost();

		Balance balance = new Balance();
		balance.setSingleName(new IString("Point"));
		balance.setPluralName(new IString("Points"));

		balance.getDelegate().setClientid(created.getSer());
		balance.getDelegate().setFeature(created.getFeatureName());
		balance.getDelegate().setFeatureid(created.getSer());

		ReturnType<Balance> createdBalance = getBalanceManager().create(balance);
		retVal.addChain(getBalanceManager(), "create", createdBalance);

		SiteGroup selfSiteGroup = new SiteGroup();
		selfSiteGroup.setClient(created);
		selfSiteGroup.setTitle(item.getName());

		ReturnType<SiteGroup> createdSiteGroup = getSiteGroupManager().create(selfSiteGroup);
		retVal.addChain(getSiteGroupManager(), "create", createdSiteGroup);

		if ( !getSiteGroupManager().associate(createdSiteGroup.getPost(), created) )
		{
			throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getSiteGroupManager().getInterfaceName() + ".associatefailed", "Could not associate new client with self site group", new Object[] { "item", created, "sitegroup", createdSiteGroup.getPost()}));
		}


		return retVal;
	}

	/**
	 * Look up a client by name
	 *
	 * @param name The name of the client
	 *
	 * @return The client with the given name or a non-existent client if not found
	 */
	@Override
	public SingleClient getClient(String name)
	{
		return convertDelegate(getDao().getSingleClient(name));
	}

	@Override
	public SingleClient getClientByDomain(String domain)
	{
		return convertDelegate(getDao().getByDomain(domain));
	}

	/**
	 * Look up a client by serial number
	 *
	 * @param ser The serial number of the client
	 * 
	 * @return The client with the given serial number or null if not found
	 */
	@Override
	public SingleClient getClient(int ser)
	{
		return getById(ser);
	}

	@Override
	public SingleClient getParent(SingleClient client)
	{
		getLogger().trace("Looking up parent client of " + client);

		SingleClientRecord clientRecord = getDao().getBySer(client.getSer());
		getLogger().trace("Found record " + clientRecord);

		if ( clientRecord.getSer() == SingleClient.getRoot().getSer())
		{
			return SingleClient.getRoot();
		}
		else
		{
			getLogger().trace("Parent has serial no. " + clientRecord.getParentid());
			return getById(clientRecord.getParentid());
		}
	}

	/**
	 * Prepares a client for return back to the caller
	 *
	 * @param client The client to initialize
	 *
	 * @return The initialized client
	 */
	@Override
	protected SingleClient initModelObject(SingleClient client)
	{
		if ( !client.exists() )
		{
			return client;
		}
		else
		{
			// Load the parents and ancestors of this client
			//
			initParents(client);

			return client;
		}
	}

	/**
	 * Loads the parents and ancestors of a client recursively
	 *
	 * @param client The client whose ancestors to load
	 */
	private void initParents(SingleClient client)
	{
		if ( !client.equals(SingleClient.getRoot()) )
		{
			// Using our internal cache, look up the client's parent/ancestors
			//
			if ( client.getParent().exists() )
			{
				client.setParent(getClient(client.getParent().getSer()));
			}
		}
	}

	@Override
	public List<SingleClient> getAllClients(SingleClient parentClient, PaginationFilter pagination)
	{
		getLogger().trace("Entering getAllClients(..) for " + parentClient.getName());

		ArrayList<SingleClient> retVal = new ArrayList<>();

		// Go through all of the children and add all their children
		//
		List<SingleClientRecord> childClients = getDao().getChildClients(parentClient.getSer(), pagination);
		childClients.stream().forEach((client) ->
		{
			retVal.addAll(getAllClients(new SingleClient(client), pagination));
		});

		// Only add this value if the node does not contain any children
		//
		if ( childClients.isEmpty() )
		{
			getLogger().trace(parentClient.getName() + " is a leaf node adding to the list");
			retVal.add(parentClient);
		}

		getLogger().trace("Leaving getAllClients(..) for " + parentClient.getName());
		return retVal;
	}

	@Override
	public TopLevelReport getReport(SingleClient item, TopLevelReport report, PaginationFilter pagination)
	{
		TopLevelReport dbReport = getReportManager().loadReport(report);

		if ( dbReport instanceof NullTopLevelReport )
		{
			if ( report.getSubReports().isEmpty())
			{
				// If the report has no demographics, it's useless. We allow
				// the caller to set custom sub-reports, demographics, etc.
				// but otherwise, we'll load the default demographics for the
				// client before beginning processing
				//
				getReportManager().populateReport(report);
				getLogger().debug("Populated report with: " + Utils.toCSV(report.getSubReports()));
			}
			else
			{
				getLogger().debug("Incoming report has the following demographics: " + Utils.toCSV(report.getSubReports()));
			}

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

	@Override
	public ClientSettings getSettings(SingleClient client)
	{
		ClientSettings retVal = new ClientSettings();

		retVal.setClient(client);

		Balance defBal = getBalanceManager().getDefaultBalance(client);

		if ( defBal.exists() )
		{
			retVal.setDefBalPlural(defBal.getPluralName());
			retVal.setDefBalSingle(defBal.getSingleName());
		}
		else
		{
			// XXX: Client not set up properly, the balance object should exist
			//
			throw new Errors();
		}

		ClientOption fbAppid = getClientOptionManager().getOption(client, "fb.appid");
		ClientOption fbSecret = getClientOptionManager().getOption(client, "fb.secret");

		retVal.setFbAppid(fbAppid.getValue());
		retVal.setFbSecret(fbSecret.getValue());

		ClientOption reCaptchaPublicKey = getClientOptionManager().getOption(client, "base.recaptchaPublicKey");
		ClientOption reCaptchaPrivateKey = getClientOptionManager().getOption(client, "base.recaptchaPrivateKey");

		retVal.setRecaptchaPublicKey(reCaptchaPublicKey.getValue());
		retVal.setRecaptchaPrivateKey(reCaptchaPrivateKey.getValue());

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClientSettings> updateSettings(ClientSettings settings)
	{
		getLogger().debug("Updating settings for client " + settings.getClient());
		
		Errors mergedErrors = new Errors();

		ReturnType<ClientSettings> retVal = new ReturnType<>();
		retVal.setPre(getSettings(settings.getClient()));

		// We only allow the e-mail, description, language, country and
		// timezone to be changed at this point which is why we load the
		// database copy of the client and only overwrite
		// those two elements before updating
		//
		SingleClient client = getClient(settings.getClient().getSer());
		client.setLanguages(settings.getClient().getLanguages());
		client.setTimeZoneID(settings.getClient().getTimeZoneID());

		client.setEmail(settings.getClient().getEmail());
		client.setDescr(settings.getClient().getDescr());
		client.setDomain(settings.getClient().getDomain());

		client.setLogoURL(settings.getClient().getLogoURL());
		client.setOperator(settings.getClient().getOperator());
		client.setAddress(settings.getClient().getAddress());
		client.setURL(settings.getClient().getURL());

		ReturnType<SingleClient> updatedClient;
		try
		{
			updatedClient = update(client);

			retVal.addChain("update", updatedClient);
			settings.setClient(updatedClient.getPost());
		}
		catch (Errors errors)
		{
			mergedErrors.merge("client", errors);
		}

		try
		{
			ReturnType<ClientOption> fbAppidUpdate = getClientOptionManager().update(settings.getFbAppidOption());
			retVal.addChain(getClientOptionManager(), "update", fbAppidUpdate);
		}
		catch (Errors errors)
		{
			mergedErrors.merge("fbAppid", errors);
		}

		try
		{
			ReturnType<ClientOption> fbSecretUpdate = getClientOptionManager().update(settings.getFbSecretOption());
			retVal.addChain(getClientOptionManager(), "update", fbSecretUpdate);
		}
		catch (Errors errors)
		{
			mergedErrors.merge("fbSecret", errors);
		}

		try
		{
			ReturnType<ClientOption> recaptchaPublicKeyUpdate = getClientOptionManager().update(settings.getRecaptchaPublicKeyOption());
			retVal.addChain(getClientOptionManager(), "update", recaptchaPublicKeyUpdate);
		}
		catch (Errors errors)
		{
			mergedErrors.merge("recaptchaPublicKey", errors);
		}

		try
		{
			ReturnType<ClientOption> recaptchaPrivateKeyUpdate = getClientOptionManager().update(settings.getRecaptchaPrivateKeyOption());
			retVal.addChain(getClientOptionManager(), "update", recaptchaPrivateKeyUpdate);
		}
		catch (Errors errors)
		{
			mergedErrors.merge("recaptchaPrivateKey", errors);
		}

		if ( mergedErrors.hasErrors() )
		{
			throw mergedErrors;
		}
		else
		{
			// Default balance options
			//
			Balance defBal = getBalanceManager().getDefaultBalance(settings.getClient());
			if ( !defBal.exists() )
			{
				// XXX: Client not set up properly, the balance object should exist
				//
				throw new Errors();
			}
			else
			{
				defBal.setSingleName(settings.getDefBalSingle());
				defBal.setPluralName(settings.getDefBalPlural());
	
				ReturnType<Balance> defBalUpdate = getBalanceManager().update(defBal);
				retVal.addChain(getBalanceManager(), "update", defBalUpdate);
			}
	
			retVal.setPost(settings);

			return retVal;
		}
	}

	@Override
	public SingleClientRecordDao getDao()
	{
		return (SingleClientRecordDao) super.getDao();
	}

	@Override
	public SingleClient emptyInstance()
	{
		return new SingleClient();
	}

	public ReportManager getReportManager()
	{
		return reportManager;
	}

	@Autowired
	public void setReportManager(ReportManager reportManager)
	{
		this.reportManager = reportManager;
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
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

	public SiteGroupManager getSiteGroupManager()
	{
		return siteGroupManager;
	}

	@Autowired
	public void setSiteGroupManager(SiteGroupManager siteGroupManager)
	{
		this.siteGroupManager = siteGroupManager;
	}

}
