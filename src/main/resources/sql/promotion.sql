-- @author Hamid Badiozamani
--
BEGIN WORK;

-- Basic table layout for user data
--
-- CREATE TABLE promotion.coderesponses
-- (
--	ser SERIAL PRIMARY KEY,
--
--	userid int NOT NULL,
--	itemid int NOT NULL,
--
--	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),
--
--	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
--	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES <something>(ser) ON UPDATE CASCADE ON DELETE CASCADE,
--
--	UNIQUE(userid, itemid),
--);
-- CREATE INDEX ON promotion.coderesponses(itemid);

--
-- PROMOTIONS
--
CREATE SCHEMA promotion;

-- A promotion click thru is an item that users click on in order to be counted
-- as having participated
--
CREATE TABLE promotion.clickthrus
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,

	-- title varchar(128) NOT NULL,
	title text NOT NULL,
	outro text NOT NULL,

	url varchar(1024) NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(clientid, url, starttm, endtm)
);
CREATE INDEX ON promotion.clickthrus(sitegroupid, starttm, endtm);

-- User data for each clickthru entry
--
CREATE TABLE promotion.clickthruresponses
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,
	quantity int NOT NULL CONSTRAINT valid_quantity CHECK (quantity > 0),

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES promotion.clickthrus(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(userid, itemid)
);
CREATE INDEX ON promotion.clickthruresponses(itemid);

-- A promotion code is just an item that users enter in order to be counted as
-- having participated
--
CREATE TABLE promotion.codes
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,

	title text NOT NULL,
	code varchar(128) NOT NULL,

	outro text NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	-- XXX: This has potentially serious consequences: removing a client that has an item that spans across a given
	-- sitegroup could wipe out items that other clients in that sitegroup depend on
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT
);
CREATE UNIQUE INDEX ON promotion.codes(clientid, LOWER(code), starttm, endtm);
CREATE UNIQUE INDEX ON promotion.codes(sitegroupid, LOWER(code), starttm, endtm);
CREATE INDEX ON promotion.codes(sitegroupid, starttm, endtm);

-- User data for each code entry
--
CREATE TABLE promotion.coderesponses
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES promotion.codes(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(userid, itemid)
);
CREATE INDEX ON promotion.coderesponses(itemid);

CREATE TABLE promotion.socialphrases
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,

	title text NOT NULL,
	phrases text NOT NULL, -- JSON array of phrases, all must be matched in any order for credit

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT
);
CREATE UNIQUE INDEX ON promotion.socialphrases(clientid, LOWER(phrases), starttm, endtm);
CREATE UNIQUE INDEX ON promotion.socialphrases(sitegroupid, LOWER(phrases), starttm, endtm);
CREATE INDEX ON promotion.socialphrases(sitegroupid, starttm, endtm);

-- User data for each social phrase entry
--
CREATE TABLE promotion.socialphraseresponses
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,
	quantity int NOT NULL CONSTRAINT valid_quantity CHECK (quantity > 0),

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES promotion.socialphrases(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(userid, itemid)
);
CREATE INDEX ON promotion.socialphraseresponses(itemid);

-- Trivia games
--

CREATE TABLE promotion.triviacategories
(
 	ser SERIAL PRIMARY KEY,
 
 	clientid int NOT NULL,
	sitegroupid int NOT NULL,
 
 	-- title varchar(128) NOT NULL,
	title text NOT NULL,
 	thumbnailurl varchar(256) NOT NULL,
 	detailpicurl varchar(256) NOT NULL,

 	descr text NOT NULL DEFAULT '{"en":""}',
 
 	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(sitegroupid, title)
);
CREATE INDEX ON promotion.triviacategories(clientid);

CREATE TABLE promotion.trivias
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,
	categoryid int NOT NULL,

	-- The correct answer for this game, we default it to 0
	-- in order to allow the trivia game to be inserted w/o
	-- a correctanswer since the actual foreign key check will
	-- happen at the end of the transaction
	--
	correctanswerid int NOT NULL DEFAULT 0,

	-- The title is used for describing what the game is for
	--
 	-- title varchar(128) NOT NULL,
	title text NOT NULL,
	randomize boolean NOT NULL DEFAULT true, -- Whether to randomize the order of questions

	answercount int NOT NULL CONSTRAINT valid_answercount CHECK (answercount >= 0),
	orderweight int NOT NULL,

	-- Question types (ignored for open ended):
	-- 0 = Single Answer Radio
	-- 1 = Single Answer Select
	--
	questiontype int NOT NULL CONSTRAINT valid_questiontype CHECK (questiontype IN (0, 1)),

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	-- The intro will actually contain the prompt for the question
	--
	intro text NOT NULL,
	outro text NOT NULL,

 	descr text NOT NULL DEFAULT '{"en":""}',
 
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT categoryid_fk FOREIGN KEY(categoryid) REFERENCES promotion.triviacategories(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(categoryid, title, starttm, endtm)
);
CREATE INDEX ON promotion.trivias(sitegroupid, starttm, endtm);

-- A list of all possible answers to a question
--
CREATE TABLE promotion.triviaanswers
(
	ser SERIAL PRIMARY KEY,

	triviaid int NOT NULL,
	orderweight int NOT NULL,

	answer varchar(1024) NOT NULL,

	CONSTRAINT triviaid_fk FOREIGN KEY(triviaid) REFERENCES promotion.trivias(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(triviaid, answer)
);
CREATE INDEX ON promotion.triviaanswers(triviaid);
ALTER TABLE promotion.trivias ADD CONSTRAINT correctanswerid_fk FOREIGN KEY(correctanswerid) REFERENCES promotion.triviaanswers(ser) ON UPDATE CASCADE ON DELETE RESTRICT INITIALLY DEFERRED;

-- User data for each trivia question
--
CREATE TABLE promotion.triviaresponses
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	answerid int NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES promotion.trivias(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT answerid_fk FOREIGN KEY(answerid) REFERENCES promotion.triviaanswers(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(userid, itemid)
);
CREATE INDEX ON promotion.triviaresponses(itemid);

-- Events page
--

CREATE TABLE promotion.eventcategories
(
 	ser SERIAL PRIMARY KEY,
 
 	clientid int NOT NULL,
	sitegroupid int NOT NULL,
 
 	-- title varchar(128) NOT NULL,
	title text NOT NULL,
 	thumbnailurl varchar(256) NOT NULL,
 	detailpicurl varchar(256) NOT NULL,

 	descr text NOT NULL DEFAULT '{"en":""}',
 
 	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(sitegroupid, title)
);
CREATE INDEX ON promotion.eventcategories(clientid);

CREATE TABLE promotion.events
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,
	categoryid int NOT NULL,

	-- The title is used for describing what the game is for
	--
	title text NOT NULL,

	orderweight int NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	-- The intro will actually contain the prompt for the question
	--
 	detailpicurl varchar(256) NOT NULL DEFAULT '',
	sponsorurl varchar(256) NOT NULL DEFAULT '',

	intro text NOT NULL,

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT categoryid_fk FOREIGN KEY(categoryid) REFERENCES promotion.eventcategories(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(categoryid, title, starttm, endtm)
);
CREATE INDEX ON promotion.events(sitegroupid, starttm, endtm);

-- User data for each event
--
CREATE TABLE promotion.eventresponses
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES promotion.events(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(userid, itemid)
);
CREATE INDEX ON promotion.eventresponses(itemid);


COMMIT;
