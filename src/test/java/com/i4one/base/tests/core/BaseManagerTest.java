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
package com.i4one.base.tests.core;

import com.i4one.base.dao.DaoManager;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminManager;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.adminhistory.AdminHistoryRecordDao;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import com.i4one.base.model.adminprivilege.Privilege;
import com.i4one.base.model.adminprivilege.PrivilegeManager;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balance.BalanceRecordDao;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.client.SiteGroupManager;
import com.i4one.base.model.client.SiteGroupRecordDao;
import com.i4one.base.model.client.ClientOption;
import com.i4one.base.model.client.ClientOptionManager;
import com.i4one.base.model.client.ClientOptionRecordDao;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.client.SingleClientRecord;
import com.i4one.base.model.client.SingleClientRecordDao;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageRecordDao;
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.admin.auth.AuthAdmin;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import com.i4one.test.MockRequestState;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/**
 * @author Hamid Badiozamani
 */
@Ignore
public abstract class BaseManagerTest extends BaseTest
{
	private DaoManager daoManager;

	private Authenticator<Admin> adminAuthenticator;
	private AdminManager simpleAdminManager;
	private PrivilegeManager simplePrivilegeManager;
	private BalanceManager cachedBalanceManager;
	private SingleClientManager cachedSingleClientManager;
	private SiteGroupManager cachedSiteGroupManager;
	private ClientOptionManager cachedClientOptionManager;
	private AdminPrivilegeManager cachedAdminPrivilegeManager;

	private Admin rootAdmin;
	private Admin firstAdmin;
	private Admin secondAdmin;
	private Admin secondAdminRW;

	private SingleClient firstClient;
	private SingleClient secondClient;

	private Balance firstClientDefaultBalance;
	private Balance secondClientDefaultBalance;

	private SiteGroup firstSiteGroup;
	private SiteGroup secondSiteGroup;

	private MockRequestState requestState;

	@Before
	public void setUp() throws Exception
	{
		// Set up two clients using the DAO object in order to preserve
		// the abstraction layer
		//
		SingleClientRecord firstClientRecord = new SingleClientRecord();
		firstClientRecord.setParentid(1);
		firstClientRecord.setName("first");
		firstClientRecord.setDescr("First Client");
		firstClientRecord.setEmail("email1@i4oneinteractive.com");
		firstClientRecord.setCountry("US");
		firstClientRecord.setLanguages("en");
		firstClientRecord.setTz("PST8PDT");
		firstClientRecord.setDomain("localhost");

		SingleClientRecord secondClientRecord = new SingleClientRecord();
		secondClientRecord.setParentid(1);
		secondClientRecord.setName("second");
		secondClientRecord.setDescr("Second Client");
		secondClientRecord.setEmail("email2@i4oneinteractive.com");
		secondClientRecord.setCountry("US");
		secondClientRecord.setLanguages("en");
		secondClientRecord.setTz("PST8PDT");
		secondClientRecord.setDomain("localhost");

		getClientRecordDao().insert(firstClientRecord);
		getClientRecordDao().insert(secondClientRecord);

		// Clear the caches
		//
		getCachedSingleClientManager().init();

		firstClient = getCachedSingleClientManager().getClient("first");
		secondClient = getCachedSingleClientManager().getClient("second");

		assertTrue(getFirstClient().exists());
		assertTrue(getSecondClient().exists());

		assertTrue(getFirstClient().getSer() == firstClientRecord.getSer());
		assertTrue(getSecondClient().getSer() == secondClientRecord.getSer());

		firstSiteGroup = new SiteGroup();
		firstSiteGroup.setClient(firstClient);
		firstSiteGroup.setTitle("First Client Group");
		firstSiteGroup.setClients( Arrays.asList(new SingleClient[]{firstClient}) );
		getCachedSiteGroupManager().create(firstSiteGroup);
		assertTrue(firstSiteGroup.exists());

		secondSiteGroup = new SiteGroup();
		secondSiteGroup.setClient(secondClient);
		secondSiteGroup.setTitle("Second Client Group");
		getCachedSiteGroupManager().create(secondSiteGroup);
		firstSiteGroup.setClients( Arrays.asList(new SingleClient[]{firstClient, secondClient}) );
		assertTrue(secondSiteGroup.exists());

		getSiteGroupRecordDao().associate(firstSiteGroup.getSer(), firstClient.getSer());

		getSiteGroupRecordDao().associate(secondSiteGroup.getSer(), firstClient.getSer());
		getSiteGroupRecordDao().associate(secondSiteGroup.getSer(), secondClient.getSer());

		rootAdmin = new Admin();
		rootAdmin.setUsername("rootadmin");
		rootAdmin.setPassword("root");
		rootAdmin.setName("Hamid Badiozamani");
		rootAdmin.setEmail("root@asdf.com");

		firstAdmin = new Admin();
		firstAdmin.setUsername("readwrite");
		firstAdmin.setPassword("rwpassword");
		firstAdmin.setName("Hamid Badiozamani");
		firstAdmin.setEmail("readwrite@asdf.com");

		secondAdmin = new Admin();
		secondAdmin.setUsername("readonly");
		secondAdmin.setPassword("ropassword");
		secondAdmin.setName("Hamid Badiozamani");
		secondAdmin.setEmail("readonly@asdf.com");

		secondAdminRW = new Admin();
		secondAdminRW.setUsername("2ndreadwrite");
		secondAdminRW.setPassword("rwpassword");
		secondAdminRW.setName("Hamid Badiozamani");
		secondAdminRW.setEmail("2ndreadwrite@asdf.com");

		getSimpleAdminManager().create(rootAdmin);
		getSimpleAdminManager().create(firstAdmin);
		getSimpleAdminManager().create(secondAdmin);
		getSimpleAdminManager().create(secondAdminRW);

		getSimpleAdminManager().updatePassword(rootAdmin);
		getSimpleAdminManager().updatePassword(firstAdmin);
		getSimpleAdminManager().updatePassword(secondAdmin);
		getSimpleAdminManager().updatePassword(secondAdminRW);

		// These are default settings for our clients
		//
		makeDefaultClientOptions(getFirstClient());
		getCachedClientOptionManager().init();

		// Grant the first admin write privileges to all
		//
		Privilege writeAll = new Privilege();
		writeAll.setFeature("*.*");
		writeAll.setReadWrite(true);
		writeAll = getSimplePrivilegeManager().lookupPrivilege(writeAll);

		ClientAdminPrivilege rootAdminRights = new ClientAdminPrivilege();
		rootAdminRights.setAdmin(rootAdmin);
		rootAdminRights.setClient(SingleClient.getRoot());
		rootAdminRights.setPrivilege(writeAll);

		getCachedAdminPrivilegeManager().init();
		getCachedAdminPrivilegeManager().grant(rootAdminRights);

		ClientAdminPrivilege firstAdminRights = new ClientAdminPrivilege();
		firstAdminRights.setAdmin(firstAdmin);
		firstAdminRights.setClient(firstClient);
		firstAdminRights.setPrivilege(writeAll);

		getCachedAdminPrivilegeManager().grant(firstAdminRights);

		ClientAdminPrivilege secondAdminRights = new ClientAdminPrivilege();
		secondAdminRights.setAdmin(secondAdminRW);
		secondAdminRights.setClient(secondClient);
		secondAdminRights.setPrivilege(writeAll);

		getCachedAdminPrivilegeManager().grant(secondAdminRights);

		// Create default e-mails
		//
		createEmails(firstClient);
		createEmails(secondClient);

		// Create default balances
		//
		Balance firstBalance = new Balance();
		firstBalance.getDelegate().setClientid(firstClient.getSer());
		firstBalance.getDelegate().setFeatureid(firstClient.getSer());
		firstBalance.getDelegate().setFeature("base.clients");
		firstBalance.setSingleName(new IString("Point"));
		firstBalance.setPluralName(new IString("Points"));

		Balance secondBalance = new Balance();
		secondBalance.getDelegate().setClientid(secondClient.getSer());
		secondBalance.getDelegate().setFeatureid(secondClient.getSer());
		secondBalance.getDelegate().setFeature("base.clients");
		secondBalance.setSingleName(new IString("Point"));
		secondBalance.setPluralName(new IString("Points"));

		getCachedBalanceManager().create(firstBalance);
		getCachedBalanceManager().create(secondBalance);

		// We have to set up the request/response for historical and privileged
		// admin managers to work
		//
		setRequestURI(getBaseURL() + "/base/user/index.html");

		firstClientDefaultBalance = getCachedBalanceManager().getDefaultBalance(firstClient);
		secondClientDefaultBalance = getCachedBalanceManager().getDefaultBalance(secondClient);
	}

	public void setRequestURI(String requestURI) throws Exception
	{
		requestState.getMockRequest().setRequestURI(requestURI);
		requestState.setResponse(new MockHttpServletResponse());
		requestState.init();

		preHandle(requestState.getRequest(), requestState.getResponse());
	}

	public void logAdminIn(Admin admin) throws Exception
	{
		logAdminIn(admin, getFirstClient());
	}

	public void logAdminIn(Admin admin, SingleClient client) throws Exception
	{
		logAdminOut();

		// Assume we're going to test admin stuff which will have the Admin attribute set
		//
		setRequestURI(getBaseURL(client) + "/base/admin/index.html");

		getLogger().debug("Logging in admin " + admin);
		getAdminAuthenticator().login(getRequestState().getModel(), admin, getRequestState().getResponse());

		// We have to be the browser and set the cookies coming from the response
		//
		initCookies();

		assertEquals(admin, UserAdminModelInterceptor.getAdmin(getRequestState().getRequest()));
		assertEquals(admin, getRequestState().getAdmin());
	}

	public void logAdminOut()
	{
		Admin logoutAdmin = new Admin();
		getAdminAuthenticator().logout(logoutAdmin, getRequestState().getResponse());

		// We have to be the browser and set the cookies coming from the response
		//
		initCookies();

		Admin stillLoggedIn = UserAdminModelInterceptor.getAdmin(getRequestState().getRequest());
		assertFalse(stillLoggedIn.exists());

		// Assume we're going to test user stuff which won't have the Admin attribute set
		//
		getRequestState().getMockRequest().setRequestURI(getBaseURL() + "/base/user/index.html");
	}

	protected void initCookies()
	{
		// We start off with our current cookies
		//
		Cookie[] cookies = getRequestState().getRequest().getCookies();
		if ( cookies == null )
		{
			cookies = new Cookie[0];
		}

		Map<String, Cookie> cookieMap = Arrays.asList(cookies)
			.stream()
			.collect(Collectors.toMap( Cookie::getName, (cookie) -> { return cookie; } ));

		for ( Cookie currCookie : getRequestState().getResponse().getCookies() )
		{
			if ( currCookie.getMaxAge() != 0 )
			{
				cookieMap.put(currCookie.getName(), currCookie);
			}
			else
			{
				cookieMap.remove(currCookie.getName());
			}
		}

		getRequestState().getMockRequest().setCookies(cookieMap.values().toArray(new Cookie[cookieMap.size()]));

		// Since the cookies have changed, the model may have changed as well
		//
		getRequestState().init();
	}

	/**
	 * Pre-handle a given request using the interceptor chain
	 *
	 * @param request The incoming request to preHandle
	 * @param response The outgoing response that was set
	 *
	 * @return The ModelAndView object returned by the testHandler
	 * @throws java.lang.Exception
	 */
	private boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Map<String, HandlerMapping> handlerMappings = applicationContext.getBeansOfType(HandlerMapping.class);
		for ( HandlerMapping handlerMapping : handlerMappings.values() )
		{
			HandlerExecutionChain chain = handlerMapping.getHandler(request);

			if ( chain != null )
			{
				for (HandlerInterceptor interceptor : chain.getInterceptors() )
				{
					getLogger().debug("preHandle calling on interceptor: " + interceptor);
					boolean doNext = interceptor.preHandle(request, response, 0);
					if ( !doNext )
					{
						// Handler interceptor put a stop to the rest of the chain
						//
						return false;
					}
				}
			}
		}

		return true;
	}

	private void createEmails(SingleClient client)
	{
		createEmailMessage(client, "emails.base.create.enabled", "true");
		createEmailMessage(client, "emails.base.create.from", "Test <hamid@i4oneinteractive.com>");
		createEmailMessage(client, "emails.base.create.bcc", "");
		createEmailMessage(client, "emails.base.create.replyto", "hamid@i4oneinteractive.com");
		createEmailMessage(client, "emails.base.create.subject", "Welcome to " + client.getDescr());
		createEmailMessage(client, "emails.base.create.htmlbody", "<html>Hi $User.firstname, thanks for joining.</html>");
		createEmailMessage(client, "emails.base.create.textbody", "Hi $User.firstname, thanks for joining.");
	}

	private void createEmailMessage(SingleClient client, String name, String value)
	{
		Message message = new Message();
		message.setClient(client);
		message.setLanguage(client.getLocale().getLanguage());
		message.setKey(name);
		message.setValue(value);

		getMessageRecordDao().insert(message.getDelegate());
		assertTrue(message.exists());
	}

	private void makeDefaultClientOptions(SingleClient client)
	{
		makeClientOption(client, "validate.User.email", "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?:[a-z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\\b$");
		makeClientOption(client, "validate.User.phone", "^[0-9]{10}$|^$");
		makeClientOption(client, "validate.User.username", "^[\\w\\d\\.-]+$");
		makeClientOption(client, "validate.User.firstname", "^[A-Za-z ,\\.'-]+$");
		makeClientOption(client, "validate.User.lastname", "^[A-Za-z ,\\.'-]+$");
		makeClientOption(client, "validate.User.zipcode", "^\\d{5}$");
		makeClientOption(client, "validate.User.password", "^.{6,}$");
		makeClientOption(client, "validate.User.gender", "^[mfo]$");
		makeClientOption(client, "validate.User.city", "^.{2,}$");
		makeClientOption(client, "validate.User.state", "^.{2,}$");
		makeClientOption(client, "options.tz", "PST8PDT");
		makeClientOption(client, "options.country", "US");
		makeClientOption(client, "options.language", "en");
	}

	private void makeClientOption(SingleClient client, String key, String value)
	{
		ClientOption clientOption = new ClientOption();
		clientOption.setClient(client);
		clientOption.setKey(key);
		clientOption.setValue(value);

		getCachedClientOptionManager().create(clientOption);
	}

	public MockRequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(MockRequestState requestState)
	{
		this.requestState = requestState;
	}

	public DaoManager getDaoManager()
	{
		return daoManager;
	}

	@Autowired
	public void setDaoManager(DaoManager daoManager)
	{
		this.daoManager = daoManager;
	}

	public Authenticator<Admin> getAdminAuthenticator()
	{
		return adminAuthenticator;
	}

	@Autowired
	public void setAdminAuthenticator(Authenticator<Admin> adminAuthenticator)
	{
		this.adminAuthenticator = adminAuthenticator;
	}

	public AdminManager getSimpleAdminManager()
	{
		return simpleAdminManager;
	}

	@Autowired
	public void setSimpleAdminManager(AdminManager simpleAdminManager)
	{
		this.simpleAdminManager = simpleAdminManager;
	}

	public SingleClientManager getCachedSingleClientManager()
	{
		return cachedSingleClientManager;
	}

	@Autowired
	public void setCachedSingleClientManager(SingleClientManager cachedSingleClientManager)
	{
		this.cachedSingleClientManager = cachedSingleClientManager;
	}

	public ClientOptionManager getCachedClientOptionManager()
	{
		return cachedClientOptionManager;
	}

	@Autowired
	public void setCachedClientOptionManager(ClientOptionManager cachedClientOptionManager)
	{
		this.cachedClientOptionManager = cachedClientOptionManager;
	}

	public AdminPrivilegeManager getCachedAdminPrivilegeManager()
	{
		return cachedAdminPrivilegeManager;
	}

	@Autowired
	public void setCachedAdminPrivilegeManager(AdminPrivilegeManager cachedAdminPrivilegeManager)
	{
		this.cachedAdminPrivilegeManager = cachedAdminPrivilegeManager;
	}

	public PrivilegeManager getSimplePrivilegeManager()
	{
		return simplePrivilegeManager;
	}

	@Autowired
	public void setSimplePrivilegeManager(PrivilegeManager simplePrivilegeManager)
	{
		this.simplePrivilegeManager = simplePrivilegeManager;
	}

	public SiteGroupManager getCachedSiteGroupManager()
	{
		return cachedSiteGroupManager;
	}

	@Autowired
	public void setCachedSiteGroupManager(SiteGroupManager cachedSiteGroupManager)
	{
		this.cachedSiteGroupManager = cachedSiteGroupManager;
	}

	public BalanceManager getCachedBalanceManager()
	{
		return cachedBalanceManager;
	}

	@Autowired
	public void setCachedBalanceManager(BalanceManager cachedBalanceManager)
	{
		this.cachedBalanceManager = cachedBalanceManager;
	}

	public String getBaseURL()
	{
		return getBaseURL(getFirstClient());
	}

	public String getBaseURL(SingleClient client)
	{
		return "/" + client.getName() + "/" + client.getLocale().getLanguage();
	}

	public SingleClient getFirstClient()
	{
		return firstClient;
	}

	public void setFirstClient(SingleClient firstClient)
	{
		this.firstClient = firstClient;
	}

	public SingleClient getSecondClient()
	{
		return secondClient;
	}

	public void setSecondClient(SingleClient secondClient)
	{
		this.secondClient = secondClient;
	}

	public Admin getRootAdmin()
	{
		return rootAdmin;
	}

	public void setRootAdmin(Admin rootAdmin)
	{
		this.rootAdmin = rootAdmin;
	}

	public Admin getFirstAdmin()
	{
		return firstAdmin;
	}

	public void setFirstAdmin(AuthAdmin firstAdmin)
	{
		this.firstAdmin = firstAdmin;
	}

	public Admin getSecondAdmin()
	{
		return secondAdmin;
	}

	public void setSecondAdmin(AuthAdmin secondAdmin)
	{
		this.secondAdmin = secondAdmin;
	}

	public Admin getSecondAdminRW()
	{
		return secondAdminRW;
	}

	public void setSecondAdminRW(Admin secondAdminRW)
	{
		this.secondAdminRW = secondAdminRW;
	}

	public SiteGroup getFirstSiteGroup()
	{
		return firstSiteGroup;
	}

	public void setFirstSiteGroup(SiteGroup firstSiteGroup)
	{
		this.firstSiteGroup = firstSiteGroup;
	}

	public SiteGroup getSecondSiteGroup()
	{
		return secondSiteGroup;
	}

	public void setSecondSiteGroup(SiteGroup secondSiteGroup)
	{
		this.secondSiteGroup = secondSiteGroup;
	}

	public Balance getFirstClientDefaultBalance()
	{
		return firstClientDefaultBalance;
	}

	public void setFirstClientDefaultBalance(Balance firstClientDefaultBalance)
	{
		this.firstClientDefaultBalance = firstClientDefaultBalance;
	}

	public Balance getSecondClientDefaultBalance()
	{
		return secondClientDefaultBalance;
	}

	public void setSecondClientDefaultBalance(Balance secondClientDefaultBalance)
	{
		this.secondClientDefaultBalance = secondClientDefaultBalance;
	}

	protected SingleClientRecordDao getClientRecordDao()
	{
		return (SingleClientRecordDao) getDaoManager().getNewDao("base.JdbcSingleClientRecordDao");
	}

	protected ClientOptionRecordDao getClientOptionRecordDao()
	{
		return (ClientOptionRecordDao) getDaoManager().getNewDao("base.JdbcClientOptionRecordDao");
	}

	protected SiteGroupRecordDao getSiteGroupRecordDao()
	{
		return (SiteGroupRecordDao) getDaoManager().getNewDao("base.JdbcSiteGroupRecordDao");
	}

	protected MessageRecordDao getMessageRecordDao()
	{
		return (MessageRecordDao) getDaoManager().getNewDao("base.JdbcMessageRecordDao");
	}

	protected AdminHistoryRecordDao getAdminHistoryRecordDao()
	{
		return (AdminHistoryRecordDao) getDaoManager().getNewDao("base.JdbcAdminHistoryRecordDao");
	}

	protected BalanceRecordDao getBalanceRecordDao()
	{
		return (BalanceRecordDao) getDaoManager().getNewDao("base.JdbcBalanceRecordDao");
	}
}
