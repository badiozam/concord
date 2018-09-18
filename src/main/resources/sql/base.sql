-- @author Hamid Badiozamani
--
BEGIN WORK;

CREATE SCHEMA base;

CREATE TABLE base.clients
(
	ser SERIAL PRIMARY KEY,

	parentid int NOT NULL,
	name varchar(64) NOT NULL,
	email varchar(128),
	descr varchar(256) NOT NULL,

	country varchar(2) NOT NULL DEFAULT 'us',
	languages varchar(256) NOT NULL DEFAULT 'en',
	tz varchar(64) NOT NULL DEFAULT 'PST8PDT',

	domain varchar(256) NOT NULL DEFAULT 'www.i4oneinteractive.com',

	operator text NOT NULL DEFAULT '',
	address text NOT NULL DEFAULT '',
	url text NOT NULL DEFAULT '',
	logourl text NOT NULL DEFAULT '',

	CONSTRAINT parentid_fk FOREIGN KEY(parentid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(name)
);
-- CREATE UNIQUE INDEX clients_pindex ON base.clients(name);
CREATE INDEX ON base.clients(parentid);
CREATE INDEX ON base.clients(domain);
INSERT INTO base.clients (ser, parentid, name, country, languages, tz, descr) VALUES (0, 0, 'NONE', 'US', 'en', 'PST8PDT', 'This node is considered empty and is only here to satisfy foreign key constraints');
INSERT INTO base.clients (parentid, name, country, languages, tz, descr) VALUES (0, 'ROOT', 'US', 'en', 'PST8PDT', 'The root node parent with defaults');

CREATE TABLE base.sitegroups
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	title varchar(128) NOT NULL,

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	UNIQUE(clientid, title)
);
INSERT INTO base.sitegroups (clientid, title) SELECT ser, name FROM base.clients WHERE ser > 0;

CREATE TABLE base.sitegroupmap
(
	ser SERIAL PRIMARY KEY,

	sitegroupid int NOT NULL,
	clientid int NOT NULL,

	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX ON base.sitegroupmap(clientid, sitegroupid);
INSERT INTO base.sitegroupmap (sitegroupid, clientid) SELECT ser, clientid FROM base.sitegroups;

CREATE TABLE base.clientoptions
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,

	key varchar(128) NOT NULL,
	value text NOT NULL,

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(clientid, key)
);
-- CREATE UNIQUE INDEX clientoptions_pindex ON base.clientoptions(clientid, key);
INSERT INTO base.clientoptions (clientid, key, value) SELECT ser, 'options.tz', 'PST8PDT' FROM base.clients WHERE name='ROOT';
INSERT INTO base.clientoptions (clientid, key, value) SELECT ser, 'options.language', 'en' FROM base.clients WHERE name='ROOT';
INSERT INTO base.clientoptions (clientid, key, value) SELECT ser, 'options.country', 'US' FROM base.clients WHERE name='ROOT';

-- This is a table of messages that holds keys which can be translated based on language and client
--
CREATE TABLE base.messages
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,

	language char(2) NOT NULL DEFAULT ('en'),
	key varchar(128) NOT NULL,
	value text NOT NULL,

	updatetime int NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(clientid, language, key)
);
--CREATE UNIQUE INDEX messages_pindex ON base.messages(clientid, language, key);

--CREATE TABLE base.reports
--(
--	ser SERIAL PRIMARY KEY,
--
--	clientid int NOT NULL,
--	parentid int,
--
--	total int NOT NULL DEFAULT 0,
--	reporttype varchar(64) NOT NULL,
--	displaytype varchar(16) NOT NULL,
--	title varchar(256) NOT NULL,
--	displayname varchar(256) NOT NULL,
--	properties text NOT NULL DEFAULT '{}',
--
--	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
--	CONSTRAINT parentid_fk FOREIGN KEY(parentid) REFERENCES base.reports(ser) ON UPDATE CASCADE ON DELETE CASCADE,
--
--	UNIQUE(clientid, parentid, title)
--);

CREATE TABLE base.reports
(
	ser SERIAL PRIMARY KEY,

	parentid int DEFAULT NULL,

	title text NOT NULL,			-- The unique title of the report (e.g. UserLastLogin, ActivityReport[trivias:5])
	total int NOT NULL,			-- The total represents how many records were processed by this report

	starttm bigint NOT NULL DEFAULT 0,	-- The starting timestamp of usage records
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),
						-- The cutoff timestamp of the usage records processed by this record

						-- The timestamp the report was compiled
	lastser INT NOT NULL,			-- The last serial number processed by the report
	properties text NOT NULL,		-- The JSON representation of the report tree

	-- The time the report started compiling by the system
	--
	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT valid_timestamp CHECK (timestamp >= endtm),
	CONSTRAINT reportid_fk FOREIGN KEY(parentid) REFERENCES base.reports(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(title, starttm, endtm, parentid)
);

CREATE TABLE base.reportdata
(
	ser SERIAL PRIMARY KEY,

	reportid int NOT NULL,
	lastser int NOT NULL,	-- The last serial number processed, can be used to create incremental updates
	timestamp int NOT NULL,	-- The last timestamp of a record processed, can be used to create incremental updates

	CONSTRAINT reportid_fk FOREIGN KEY(reportid) REFERENCES base.reports(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	UNIQUE (reportid, lastser)
);

CREATE TABLE base.users
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,

	username varchar(256) NOT NULL CONSTRAINT caseinsensitive_username CHECK(username=lower(username)),
	password varchar(256) NOT NULL DEFAULT '',
	email varchar(256) NOT NULL CONSTRAINT caseinsensitive_email CHECK(email=lower(email)),

	firstname varchar(100),
	middle varchar(100),
	lastname varchar(100),
	street varchar(256),
	city varchar(100),
	state varchar(4),
	zipcode varchar(10),

	cellphone varchar(32) NOT NULL,
	homephone varchar(32),

	gender char(1) CONSTRAINT valid_gender CHECK (gender IN ('m', 'f', 'o')),

	-- User's current status
	-- 
	-- 0: Pending
	-- 1: Verified
	--
	status int NOT NULL CONSTRAINT valid_status CHECK (status IN (0, 1)),

	ismarried boolean,
	forceupdate boolean NOT NULL DEFAULT false,

	-- Global opt-ins, these must be respected regardless of which client
	-- the user logs in under
	--
	canemail boolean NOT NULL DEFAULT false, 
	cansms boolean NOT NULL DEFAULT false,
	cancall boolean NOT NULL DEFAULT false,

	birthmm smallint CONSTRAINT valid_birthmm CHECK (birthmm > 0 AND birthmm < 13),
	birthdd smallint CONSTRAINT valid_birthdd CHECK (birthdd > 0 AND birthdd < 32),
	birthyyyy int CONSTRAINT valid_birthyyyy CHECK (birthyyyy > EXTRACT(year FROM CURRENT_DATE) - 111 AND birthyyyy < (EXTRACT(year FROM CURRENT_DATE))),
	lastbirthyear int NOT NULL DEFAULT 0,

	createtime int NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),
	updatetime int CONSTRAINT valid_updatetime CHECK (updatetime >= createtime),
	lastlogintime int CONSTRAINT valid_lastlogintime CHECK (lastlogintime >= createtime),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(cellphone),
	UNIQUE(email),
	UNIQUE(username)
);

-- Generic name-value user-associated data intended for short-term use.
-- This data is mean to be periodically purged based on timestamp. A typical
-- use case would be generated verification codes or session information.
--
-- For longer term data, a separate relationship table structure needs
-- to be created to allow for better performance and validation (e.g.
-- custom profile data, facebook tokens, etc)
--
CREATE TABLE base.userdata
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	key text NOT NULL,
	value text NOT NULL,

	duration int NOT NULL DEFAULT 86400,
	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	UNIQUE(userid, key)
);

-- Preferences that are defined by a particular client, and presented to all users
-- who visit sites in a given sitegroup.
--
CREATE TABLE base.preferences
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,

	orderweight int NOT NULL,

	-- The name of the preference, this is an IString that will be displayed
	-- to users to select
	--
	title text NOT NULL,

	-- Question types:
	-- 0 = Single Answer Radio
	-- 1 = Single Answer Select
	-- 2 = Multiple Answer Checkboxes
	-- 3 = Open answer single line
	-- 4 = Open answer multi line
	--
	questiontype int NOT NULL CONSTRAINT valid_questiontype CHECK (questiontype IN (0, 1, 2, 3, 4)),

	answercount int NOT NULL CONSTRAINT valid_answercount CHECK (answercount >= 0),

	-- The value for this key to default to for all users when they sign up
	--
	defaultvalue text NOT NULL DEFAULT(''),

	-- Min and Max number of responses for multiple answers
	-- 
	minresponses int NOT NULL CONSTRAINT valid_minresponses CHECK (minresponses >= 0),
	maxresponses int NOT NULL CONSTRAINT valid_maxresponses CHECK (maxresponses >= minresponses),

	-- Regular expression for valid open answers
	--
	validanswer text NOT NULL DEFAULT ('.*'),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(clientid, title)
);
CREATE INDEX ON base.preferences(sitegroupid);

-- A list of all possible answers to a preference
--
CREATE TABLE base.preferenceanswers
(
	ser SERIAL PRIMARY KEY,

	preferenceid int NOT NULL,
	orderweight int NOT NULL,

	answer text NOT NULL,

	CONSTRAINT prefid_fk FOREIGN KEY(preferenceid) REFERENCES base.preferences(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(preferenceid, answer)
);
CREATE INDEX ON base.preferenceanswers(preferenceid);

-- An individual user's preferences
--
CREATE TABLE base.userpreferences
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	-- Either the answer id must be given or the value
	answerid int,
	value text,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT prefid_fk FOREIGN KEY(itemid) REFERENCES base.preferences(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	CONSTRAINT prefanswer_sanity CHECK ( (answerid IS NULL AND value IS NOT NULL) OR (answerid IS NOT NULL AND value IS NULL) )
);
CREATE INDEX base_userpreferenceanswers_pindex ON base.userpreferences(userid, itemid);
CREATE INDEX base_userpreferenceanswers_sindex ON base.userpreferences(itemid);

-- A user's log in event to a particular site
--
CREATE TABLE base.userlogins
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	srcip varchar(64) NOT NULL DEFAULT '',
	useragent varchar(256) NOT NULL DEFAULT '',

	-- The number of times the user logged in, this can be incremented such that
	-- multiple logins with the same information can be consolidated to save space
	--
	quantity int NOT NULL CONSTRAINT valid_quantity CHECK (quantity > 0),

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT clientid_fk FOREIGN KEY(itemid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE (itemid, userid, timestamp)
);

-- Individual or group-wide user messages
--
CREATE TABLE base.usermessages
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,

	orderweight int NOT NULL,

	-- If a userid is not specified, the message will be displayed to all
	-- users in that client or sitegroup
	--
	userid int,

	title text NOT NULL,
	message text NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(sitegroupid, userid, message, starttm, endtm)
);
CREATE INDEX ON base.usermessages(sitegroupid, starttm, endtm);

-- Friend referral
--
CREATE TABLE base.friendrefs
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,

	-- The member performing the referral
	--
	userid int NOT NULL,

	-- Information about the friend
	--
	friendid int,
	email varchar(256) NOT NULL CONSTRAINT caseinsensitive_email CHECK(email=lower(email)),
	firstname varchar(100) NOT NULL,
	lastname varchar(100) NOT NULL,
	message text NOT NULL,

	-- The time the referral was sent
	--
	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX ON base.friendrefs(email);
CREATE UNIQUE INDEX ON base.friendrefs(friendid);
CREATE INDEX base_friendrefs_sindex ON base.friendrefs(userid);

CREATE TABLE base.balances
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,

	featureid int NOT NULL,
	feature varchar(128) NOT NULL,

	singlename text NOT NULL,
	pluralname text NOT NULL,

	-- An active balance is one that can still be updated/displayed
	--
	active boolean NOT NULL DEFAULT true,

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE RESTRICT
);
INSERT INTO base.balances (ser, clientid, featureid, feature, singlename, pluralname, active) VALUES (0, 0, 0, 'base.clients', '<Default>', '<Default>', true);
CREATE UNIQUE INDEX ON base.balances(featureid, feature);
CREATE INDEX ON base.balances(clientid);

-- Member transaction log
--
CREATE TABLE base.transactions
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	userid int NOT NULL,

	-- Some transactions may not work on balances in which case
	-- these fields would be set to NULL
	--
	balid int,
	prevbal int,
	balxacted int,
	newbal int CONSTRAINT newbal_sanity CHECK (prevbal + balxacted = newbal),

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),
	srcip varchar(64) NOT NULL,
	descr varchar(1024) NOT NULL DEFAULT '--',

	status varchar(64) NOT NULL DEFAULT 'Successful' CONSTRAINT status_bounds CHECK (status IN ('Successful', 'Failed')),

	-- A transaction group is where multiple transactions are recorded for the same action
	--
	parentid int,

	CONSTRAINT balid_sanity CHECK ( (balid IS NULL AND prevbal IS NULL AND balxacted IS NULL AND newbal IS NULL) OR (balid IS NOT NULL AND prevbal IS NOT NULL AND balxacted IS NOT NULL AND newbal IS NOT NULL) ),

	CONSTRAINT parentid_fk FOREIGN KEY(parentid) REFERENCES base.transactions(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT balid_fk FOREIGN KEY(balid) REFERENCES base.balances(ser) ON UPDATE CASCADE ON DELETE RESTRICT
);
CREATE INDEX base_transactions_pindex ON base.transactions(userid, balid);
CREATE INDEX base_transactions_sindex ON base.transactions(clientid, timestamp);
CREATE INDEX base_transactions_tindex ON base.transactions(parentid);

CREATE TABLE base.userbalances
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	total int NOT NULL,
	balid int NOT NULL,
	createtime int NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),
	updatetime int NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT total_check CHECK (total >= 0),
	CONSTRAINT time_check CHECK (createtime <= updatetime),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT balid_fk FOREIGN KEY(balid) REFERENCES base.balances(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(userid, balid)
);
CREATE INDEX ON base.userbalances(balid);

CREATE TABLE base.admins
(
	ser SERIAL PRIMARY KEY,

	username varchar(256) NOT NULL,
	password varchar(256) NOT NULL DEFAULT '',
	email varchar(256) NOT NULL,
	name varchar(256) NOT NULL,
	forceupdate boolean NOT NULL DEFAULT false,

	UNIQUE(username),
	UNIQUE(email)
);
--CREATE UNIQUE INDEX  admins_pindex ON base.admins(username);
INSERT INTO base.admins (username, password, email, name) VALUES ('i4one', '967b1404d18b79ae352061f633fc827b3f4b7fce7828d76b69dac22daacb0201', 'hamid@i4oneinteractive.com', 'Super-user');

CREATE TABLE base.adminprivileges
(
	ser SERIAL PRIMARY KEY,

	feature varchar(128) NOT NULL,
	readwrite boolean NOT NULL DEFAULT false,

	UNIQUE(feature, readwrite)
);
-- CREATE UNIQUE INDEX adminprivileges_pindex ON base.adminprivileges(feature, readwrite);
INSERT INTO base.adminprivileges (feature, readwrite) VALUES ('*.*', false);
INSERT INTO base.adminprivileges (feature, readwrite) VALUES ('*.*', true);

CREATE TABLE base.clientadmins
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	adminid int NOT NULL,
	privid int NOT NULL,

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT adminid_fk FOREIGN KEY(adminid) REFERENCES base.admins(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT privid FOREIGN KEY(privid) REFERENCES base.adminprivileges(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(clientid, privid, adminid)
);
-- CREATE UNIQUE INDEX clientadmins_pindex ON base.clientadmins(adminid, clientid, privid);
-- INSERT INTO base.clientadmins (sitegroupid, adminid, privid) SELECT c.ser, a.ser, p.ser FROM base.sitegroups c, base.admins a, base.adminprivileges p WHERE c.title='ROOT' AND a.username='i4one' AND p.feature='*.*';
INSERT INTO base.clientadmins (clientid, adminid, privid) SELECT c.ser, a.ser, p.ser FROM base.clients c, base.admins a, base.adminprivileges p WHERE c.name='ROOT' AND a.username='i4one' AND p.feature='*.*';

CREATE TABLE base.adminhistory
(
	ser SERIAL PRIMARY KEY,

	adminid int NOT NULL,
	clientid int NOT NULL,
	parentid int,

	feature varchar(128) NOT NULL,	-- Table name
	featureid int NOT NULL,		-- Table ser
	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	action varchar(64) NOT NULL,	-- Method name
	srcip varchar(64) NOT NULL,
	descr varchar(256) NOT NULL,

	-- The serialized versions of the object used for reverting changes
	-- 
	before text,
	after text,

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT parentid_fk FOREIGN KEY(parentid) REFERENCES base.adminhistory(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT adminid_fk FOREIGN KEY(adminid) REFERENCES base.admins(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX base_adminhistory_pindex ON base.adminhistory(clientid);
CREATE INDEX base_adminhistory_sindex ON base.adminhistory(timestamp);
CREATE INDEX base_adminhistory_tindex ON base.adminhistory(feature, featureid);
CREATE INDEX base_adminhistory_qindex ON base.adminhistory(parentid);

CREATE TABLE base.instantwins
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	title varchar(128) NOT NULL,

	userid int,
	percentwin float NOT NULL DEFAULT 0.0,

	winnermsg text NOT NULL,
	losermsg text NOT NULL,

	winnerlimit int NOT NULL,
	-- userwinlimit int NOT NULL, -- Might be useful to have a user win multiple times
	winnercount int NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	exclusive boolean NOT NULL DEFAULT false,

	CONSTRAINT percent_check CHECK(percentwin <= 1.0),
	CONSTRAINT winnerlimit_check CHECK(winnerlimit = 0 OR winnercount <= winnerlimit),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX ON base.instantwins(title, userid, percentwin);
CREATE INDEX ON base.instantwins(clientid, starttm, endtm);

CREATE TABLE base.instantwinners
(
	ser SERIAL PRIMARY KEY,

	itemid int NOT NULL,
	userid int NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),
	transactionid int,

	CONSTRAINT instantwinid_fk FOREIGN KEY(itemid) REFERENCES base.instantwins(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT transactionid_fk FOREIGN KEY(transactionid) REFERENCES base.transactions(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(userid, itemid)
);
CREATE INDEX ON base.instantwinners(itemid);

-- m-n relationship here allows for using the same instant-win across multiple events and/or
-- using multiple instant-win items for the same event
--
CREATE TABLE base.featureinstantwins
(
	ser SERIAL PRIMARY KEY,

	iwid int NOT NULL,

	feature varchar(128) NOT NULL,
	featureid int NOT NULL,

	CONSTRAINT iwid_fk FOREIGN KEY(iwid) REFERENCES base.instantwins(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(iwid, feature, featureid)
);
CREATE INDEX ON base.featureinstantwins(feature, featureid);

-- Balance triggers
--
CREATE TABLE base.balancetriggers
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	balid int NOT NULL,

	-- title varchar(128) NOT NULL CONSTRAINT valid_title CHECK (btrim(title) != ''),
	title text NOT NULL CONSTRAINT valid_title CHECK (btrim(title) NOT LIKE '{"__":""}' AND btrim(title) != ''),

	amount int NOT NULL CONSTRAINT valid_amount CHECK (amount != 0),

	synced boolean NOT NULL DEFAULT false,
	frequency int NOT NULL,
	maxuserusage int NOT NULL DEFAULT 0,
	maxglobalusage int NOT NULL DEFAULT 0,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	exclusive boolean NOT NULL DEFAULT false,

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT balid_fk FOREIGN KEY(balid) REFERENCES base.balances(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(clientid, balid, title)
);
CREATE INDEX base_balancetriggers_sindex ON base.balancetriggers(clientid, starttm, endtm);

-- m-n relationship here allows for using the same trigger across multiple items and/or
-- using multiple triggers for the same event
--
CREATE TABLE base.featuretriggers
(
	ser SERIAL PRIMARY KEY,

	balancetriggerid int NOT NULL,

	feature varchar(128) NOT NULL,
	featureid int NOT NULL,

	CONSTRAINT balancetriggerid_fk FOREIGN KEY(balancetriggerid) REFERENCES base.balancetriggers(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(balancetriggerid, feature, featureid)
);
CREATE INDEX ON base.featuretriggers(feature, featureid);

CREATE TABLE base.userbalancetriggers
(
	ser SERIAL PRIMARY KEY,

	itemid int NOT NULL,
	userid int NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),
	transactionid int NOT NULL,

	CONSTRAINT balancetriggerid_fk FOREIGN KEY(itemid) REFERENCES base.balancetriggers(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT transactionid_fk FOREIGN KEY(transactionid) REFERENCES base.transactions(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(itemid, userid, "timestamp")
);
CREATE INDEX base_userbalancetriggers_sindex ON base.userbalancetriggers(userid, itemid);

-- 1 - n relationship that defines how much of what balance something costs
--
CREATE TABLE base.balanceexpenses
(
	ser SERIAL PRIMARY KEY,

	balanceid int NOT NULL,
	amount int NOT NULL CONSTRAINT valid_amount CHECK (amount > 0),

	feature varchar(128) NOT NULL,
	featureid int NOT NULL,

	CONSTRAINT balanceid_fk FOREIGN KEY(balanceid) REFERENCES base.balances(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(balanceid, feature, featureid)
);

CREATE TABLE base.userbalanceexpenses
(
	ser SERIAL PRIMARY KEY,

	itemid int NOT NULL,
	userid int NOT NULL,

	-- The number of times the expense was incurred in a single transaction
	-- (e.g. for buying more than one raffle entry, prize, etc.)
	--
	quantity int NOT NULL DEFAULT 1,
	activityid int NOT NULL CONSTRAINT valid_activityid CHECK (activityid > 0),

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),
	transactionid int NOT NULL DEFAULT 0,

	CONSTRAINT balanceexpenseid_fk FOREIGN KEY(itemid) REFERENCES base.balanceexpenses(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT transactionid_fk FOREIGN KEY(transactionid) REFERENCES base.transactions(ser) ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED,

	UNIQUE(itemid, activityid),
	UNIQUE(itemid, userid, "timestamp")
);
CREATE INDEX base_usersbalanceexpenses_sindex ON base.userbalanceexpenses(userid, itemid);

CREATE TABLE base.facebooktokens
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	clientid int NOT NULL,
	fbid varchar(256) NOT NULL,
	accesstoken varchar(512) NOT NULL,
	expiration int NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(userid, fbid),
	UNIQUE(clientid, userid),
	UNIQUE(clientid, fbid)
);

-- An e-mail template defines just a template that is to be used
-- when sending out an e-mail to a set of users regardless of whether
-- the request is from a user, system-generated or part of an e-mail blast.
--
CREATE TABLE base.emailtemplates
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,

	-- A unique identifying key. Some typical values could be:
	--
	-- "userManager.resetPassword" => Password reset e-mail
	-- "userManager.updatePassword" => Password was updated e-mail
	-- "userManager.create" => User sign up e-mail
	-- "friendRefManager.create" => Friend referral e-mail
	-- "emailblasts:23" => E-mail blast w/ ser number 23
	-- 
	-- The manager will walk up the ancestry tree if a key is not
	-- found in order to provide defaults that can be overriden.
	--
	key varchar(128) NOT NULL,

	fromaddr varchar(256) NOT NULL,
	bcc varchar(256) NOT NULL DEFAULT '',

	replyto varchar(256) NOT NULL DEFAULT '',
	subject text NOT NULL,

	htmlbody text NOT NULL,
	textbody text NOT NULL DEFAULT '',

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	UNIQUE (clientid, key)
);

CREATE TABLE base.emailblastbuilders
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,

	title text NOT NULL,
	emailtemplateid int NOT NULL,

	target text NOT NULL DEFAULT '',
	targetsql text NOT NULL DEFAULT '',

	savedstate text NOT NULL DEFAULT '',

	CONSTRAINT emailtemplateid_fk FOREIGN KEY(emailtemplateid) REFERENCES base.emailtemplates(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(clientid, title)
);
CREATE INDEX ON base.emailblastbuilders(clientid, title);

CREATE TABLE base.emailblasts
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,

	title text NOT NULL,
	emailtemplateid int NOT NULL,
	maturetm bigint NOT NULL,

	sendstarttm bigint,
	sendendtm bigint,

	-- 0 = Pending
	-- 1 = Sending
	-- 2 = Do pause
	-- 3 = Paused
	-- 4 = Do resume
	-- 5 = Completed
	-- 6 = Error
	--
	status int NOT NULL,

	-- The number of users to whom this e-mail blast is to be sent. This count
	-- is to be updated as soon as the job starts 
	--
	totalcount int NOT NULL,

	-- The actual number of e-mails sent, this count is to be periodically updated
	-- by threads that fragment and send out with totalsent = totalsent + <num>
	-- statements
	--
	totalsent int NOT NULL,

	-- The target SQL returns a set userids to which this email blast is
	-- to be sent. The query need not be concerned with a user's global
	-- opt-in flag as that is to be checked programmatically at the time
	-- the e-mail is queued by the system. If the targetsql is left empty
	-- then all users from the requesting client are to receive the e-mail.
	--
	target text NOT NULL DEFAULT '',
	targetsql text NOT NULL DEFAULT '',

	-- The optional schedule for recurring e-mail blasts
	--
	schedule varchar(64) NOT NULL DEFAULT '',

	CONSTRAINT status_check CHECK(status IN (0,1,2,3,4,5,6)),
	CONSTRAINT emailtemplateid_fk FOREIGN KEY(emailtemplateid) REFERENCES base.emailtemplates(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(clientid, title, maturetm)
);
CREATE INDEX ON base.emailblasts(clientid, maturetm);

-- Temporary table to fragment large queries
--
CREATE TABLE base.emailblastfragments
(
	ser SERIAL PRIMARY KEY,

	emailblastid int NOT NULL,

	fragoffset int NOT NULL,
	fraglimit int NOT NULL,

	-- The count will be the same as the limit
	-- except for the last fragment which
	-- typically falls short of the limit
	--
	fragcount int NOT NULL,
	fragsent int NOT NULL,

	-- The thread that owns this fragment
	--
	owner varchar(1024) NOT NULL,
	lastupdatetm bigint NOT NULL,

	-- 0 = Pending
	-- 1 = Sending
	-- 2 = Do pause
	-- 3 = Paused
	-- 4 = Do resume
	-- 5 = Completed
	-- 6 = Error
	--
	status int NOT NULL,

	CONSTRAINT status_check CHECK(status IN (0,1,2,3,4,5,6)),
	CONSTRAINT emailblastid_fk FOREIGN KEY(emailblastid) REFERENCES base.emailblasts(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX ON base.emailblastfragments(emailblastid, fragoffset);
CREATE INDEX ON base.emailblastfragments(emailblastid);

-- This is a temporary table used to house users to whom an e-mail blast is to be
-- sent. The data is discarded after the e-mail blast has been completed. The
-- primary purpose is to take a snapshot of users prior to starting an e-mail
-- blast so that fragmentation is consistent.
--
CREATE TABLE base.emailblastusers
(
	userid int NOT NULL,
	emailblastid int NOT NULL,

	processed boolean NOT NULL DEFAULT false,

	CONSTRAINT users_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT emailblastid_fk FOREIGN KEY(emailblastid) REFERENCES base.emailblasts(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(userid, emailblastid)
);
CREATE INDEX ON base.emailblastusers(emailblastid);

CREATE TABLE base.emailblastresponses
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	quantity int NOT NULL CONSTRAINT valid_quantity CHECK (quantity > 0),
	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	-- 0 = bounce back
	-- 1 = opened
	--
	status int NOT NULL,

	CONSTRAINT status_check CHECK(status IN (0,1)),
	CONSTRAINT users_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES base.emailblasts(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(userid, itemid)
);
CREATE INDEX ON base.emailblastresponses(itemid);

-- The basic schema for a category, to be created for specific
-- features (i.e. shoppingcategories, triviacategories, etc.)
-- 
--
-- CREATE TABLE base.categories
-- (
-- 	ser SERIAL PRIMARY KEY,
-- 
-- 	clientid int NOT NULL,
-- 	sitegroupid int NOT NULL,
-- 
-- 	title varchar(128) NOT NULL,
-- 	thumbnailurl varchar(256) NOT NULL,
-- 	descr text NOT NULL,
-- 
-- 	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
-- 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
--
--	UNIQUE(sitegroupid, title)
-- );
-- CREATE INDEX ON base.categories(clientid);

-- A dynamic page is one that is programmed to appear and disappear based on start/end times
--
CREATE TABLE base.pages
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,

	title text NOT NULL,
	body text NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(clientid, title, starttm, endtm)
);
CREATE INDEX ON base.pages(sitegroupid, starttm, endtm);

CREATE TABLE base.accesscodes
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	title varchar(128) NOT NULL,

	descr text NOT NULL,
	accessdenied text NOT NULL,

	code varchar(128) NOT NULL,
	pages text NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	UNIQUE(clientid, code, starttm)
);
CREATE INDEX ON base.accesscodes(clientid, starttm, endtm);

CREATE TABLE base.accesscoderesponses
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES base.accesscodes(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(userid, itemid)
);

COMMIT;
