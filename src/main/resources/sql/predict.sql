-- @author Hamid Badiozamani
--
BEGIN WORK;
--
-- PREDICTIONS
--
CREATE SCHEMA predict;

-- By having events split into separate terms we can more easily track internal competitions within a term
-- as well as inter-term competitions
--
CREATE TABLE predict.terms
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,

	-- title varchar(128) NOT NULL,
	title text NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX terms_pindex ON predict.terms(sitegroupid, title);

CREATE TABLE predict.categories
(
 	ser SERIAL PRIMARY KEY,
 
 	clientid int NOT NULL,
 	sitegroupid int NOT NULL,
 
 	-- title varchar(128) NOT NULL CONSTRAINT empty_check CHECK (length(trim(both ' ' from title)) > 0),
 	title text NOT NULL CONSTRAINT valid_title CHECK (btrim(title) NOT LIKE '{"__":""}' AND btrim(title) != ''),
 	thumbnailurl varchar(256) NOT NULL,
 	detailpicurl varchar(256) NOT NULL,

 	descr text NOT NULL DEFAULT '{"en":""}',
 
 	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE (sitegroupid, title)
);
CREATE INDEX ON predict.categories(clientid);

CREATE TABLE predict.events
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
 	sitegroupid int NOT NULL,

	categoryid int,
	actualoutcomeid int,

	minbid int,
	maxbid int,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	closesby bigint NOT NULL CONSTRAINT valid_closesby CHECK (closesby <= endtm),
	postsby bigint NOT NULL CONSTRAINT valid_postsby CHECK (postsby >= endtm),
	postedtm bigint CONSTRAINT valid_postedtm CHECK (postedtm > endtm),

	title text NOT NULL,		-- Intendend length 128
	brief text NOT NULL,		-- HTML (intended length 256)
	promo text NOT NULL,		-- Plain (intended length 256)

	-- The source to reference
	--
	reference text NOT NULL,	-- HTML (intended length: 512)

	usagecount int NOT NULL DEFAULT 0,

	descr text NOT NULL, -- Long HTML

	CONSTRAINT categoryid_fk FOREIGN KEY(categoryid) REFERENCES predict.categories(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(clientid, title, starttm, endtm)
);
CREATE INDEX ON predict.events(sitegroupid, starttm, endtm);

-- A list of all possible outcomes and their likelihood
--
CREATE TABLE predict.eventoutcomes
(
	ser SERIAL PRIMARY KEY,

	eventid int NOT NULL,

	descr text NOT NULL,

	-- The likelihood directly determines the payout: 1 / likelihood is how much of the
	-- bid amount(s) the user will receive
	--
	likelihood float NOT NULL CONSTRAINT valid_likelihood CHECK (likelihood <= 1.0),

	-- The base line probability is what is set by the admin at the start of the event
	--
	baseline float NOT NULL CONSTRAINT valid_baseline CHECK (baseline <= 1.0),

	-- How many people selected this outcome so far
	--
	usagecount int NOT NULL DEFAULT 0,

	-- Whether or not to update the likelihood of this outcome based on activity
	--
	locklikelihood boolean NOT NULL DEFAULT false,

	-- The last time the outcome's statistics were updated
	--
	updatetime int NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	-- Whether the outcome actualized or not
	--
	actualized boolean,

	CONSTRAINT eventid_fk FOREIGN KEY(eventid) REFERENCES predict.events(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX eventoutcomes_pindex ON predict.eventoutcomes(eventid, descr);
ALTER TABLE predict.events ADD CONSTRAINT outcomeid_fk FOREIGN KEY(actualoutcomeid) REFERENCES predict.eventoutcomes(ser) ON UPDATE CASCADE ON DELETE CASCADE;

-- User-placed bets
--
CREATE TABLE predict.eventpredictions
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	termid int NOT NULL,
	itemid int NOT NULL,
	eventoutcomeid int NOT NULL,

	quantity int NOT NULL,
	payout float NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),
	correct int,
	postedtm bigint,

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT eventid_fk FOREIGN KEY(itemid) REFERENCES predict.events(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT termid_fk FOREIGN KEY(termid) REFERENCES predict.terms(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT eventoutcomeid_fk FOREIGN KEY(eventoutcomeid) REFERENCES predict.eventoutcomes(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(itemid, userid, timestamp)
);
CREATE INDEX eventpredictions_pindex ON predict.eventpredictions(itemid);
CREATE INDEX eventpredictions_qindex ON predict.eventpredictions(eventoutcomeid);

-- Current chart/player rankings
--
CREATE TABLE predict.players
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	termid int NOT NULL,

	total int NOT NULL,
	pending int NOT NULL,
	correct int NOT NULL,
	incorrect int NOT NULL,

	score int NOT NULL,
	winstreak int NOT NULL,
	accuracy float NOT NULL,

	scorerank int NOT NULL,
	winstreakrank int NOT NULL,
	accuracyrank int NOT NULL,

	lastplayedtime int NOT NULL,
	updatetime int NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT termid_fk FOREIGN KEY(termid) REFERENCES predict.terms(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX players_pindex ON predict.players(userid, termid);
CREATE INDEX players_sindex ON predict.players(termid, score);
CREATE INDEX players_tindex ON predict.players(termid, correct);

COMMIT;
