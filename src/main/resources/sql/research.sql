-- @author Hamid Badiozamani
--
BEGIN WORK;

--
-- SURVEYS
--
CREATE SCHEMA research;

CREATE TABLE research.surveycategories
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
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(sitegroupid, title)
);
CREATE INDEX ON research.surveycategories(clientid);

-- A survey is just a collection of questions
--
CREATE TABLE research.surveys
(
	ser SERIAL PRIMARY KEY,

 	clientid int NOT NULL,
	sitegroupid int NOT NULL,
	categoryid int,

	-- title varchar(128) NOT NULL,
	title text NOT NULL,
	questioncount int NOT NULL CONSTRAINT valid_questioncount CHECK (questioncount >= 0), -- Total number of questions in the survey
	perpage int NOT NULL, -- Number of questions per page (0 = all on one page)
	randomize boolean NOT NULL, -- Whether to randomize the order of questions

	intro text NOT NULL,
	outro text NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT categoryid_fk FOREIGN KEY(categoryid) REFERENCES research.surveycategories(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(categoryid, title, starttm, endtm)
);
CREATE INDEX ON research.surveys(sitegroupid, starttm, endtm);

CREATE TABLE research.surveyquestions
(
	ser SERIAL PRIMARY KEY,

	surveyid int NOT NULL,

	answercount int NOT NULL CONSTRAINT valid_answercount CHECK (answercount >= 0),
	orderweight int NOT NULL,

	-- Question types:
	-- 0 = Single Answer Radio
	-- 1 = Single Answer Select
	-- 2 = Multiple Answer Checkboxes
	-- 3 = Open ended single
	-- 4 = Open ended multi
	--
	questiontype int NOT NULL CONSTRAINT valid_questiontype CHECK (questiontype IN (0, 1, 2, 3, 4)),

	-- Min and Max number of responses for multiple answers
	-- 
	minresponses int NOT NULL CONSTRAINT valid_minresponses CHECK (minresponses >= 0),
	maxresponses int NOT NULL CONSTRAINT valid_maxresponses CHECK (maxresponses >= minresponses),

	-- Regular expression for valid open answers
	--
	validanswer text NOT NULL DEFAULT ('.*'),

	-- The actual question
	--
	question text NOT NULL,

	CONSTRAINT surveyid_fk FOREIGN KEY(surveyid) REFERENCES research.surveys(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX ON research.surveyquestions(surveyid, question);

-- A list of all possible answers to a question
--
CREATE TABLE research.surveyanswers
(
	ser SERIAL PRIMARY KEY,

	questionid int NOT NULL,
	orderweight int NOT NULL,

	answer text NOT NULL,

	CONSTRAINT questionid_fk FOREIGN KEY(questionid) REFERENCES research.surveyquestions(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX ON research.surveyanswers(questionid, answer);

-- Information about the respondents
--
CREATE TABLE research.surveyrespondents
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	startpage int NOT NULL,
	currentpage int NOT NULL CONSTRAINT valid_currentpage CHECK (currentpage >= startpage),
	hasfinished boolean NOT NULL DEFAULT false,

	timestamp int NOT NULL,
	updatetime int NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES research.surveys(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(userid, itemid)
);
CREATE INDEX ON research.surveyrespondents(itemid);

-- User data for single and multi answer questions
--
CREATE TABLE research.surveyresponses
(
	ser SERIAL PRIMARY KEY,

	respondentid int NOT NULL,
	questionid int NOT NULL,

	answerid int,
	openanswer text,
	language varchar(2),

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT respondentid_fk FOREIGN KEY(respondentid) REFERENCES research.surveyrespondents(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT questionid_fk FOREIGN KEY(questionid) REFERENCES research.surveyquestions(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT answerid_fk FOREIGN KEY(answerid) REFERENCES research.surveyanswers(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT answer_check CHECK ( NOT(answerid IS NULL AND openanswer IS NULL) )
);
CREATE INDEX ON research.surveyresponses(respondentid, questionid);
CREATE INDEX ON research.surveyresponses(questionid);
CREATE INDEX ON research.surveyresponses(answerid);

-- Branching by answer
--
CREATE TABLE research.surveybranches
(
	ser SERIAL PRIMARY KEY,

	surveyidfrom int NOT NULL,
	surveyidto int NOT NULL,

	-- The order to display each branched research in
	--
	orderweight int NOT NULL,

	-- The answerid that the triggers the branch
	--
	answerid int NOT NULL,

	CONSTRAINT surveyidfrom_fk FOREIGN KEY(surveyidfrom) REFERENCES research.surveys(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT surveyidto_fk FOREIGN KEY(surveyidto) REFERENCES research.surveys(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT answerid_fk FOREIGN KEY(answerid) REFERENCES research.surveyanswers(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX ON research.surveybranches(surveyidfrom);

CREATE TABLE research.pollcategories
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
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(sitegroupid, title)
);
CREATE INDEX ON research.pollcategories(clientid);

-- A poll is one question the results of which are displayed to the user after
-- they have answered
--
CREATE TABLE research.polls
(
	ser SERIAL PRIMARY KEY,

 	clientid int NOT NULL,
 	sitegroupid int NOT NULL,
	categoryid int,

	-- The title of the poll (which may also be the question)
	--
	title text NOT NULL,
	randomize boolean NOT NULL DEFAULT true, -- Whether to randomize the order of questions

	answercount int NOT NULL CONSTRAINT valid_answercount CHECK (answercount >= 0),
	orderweight int NOT NULL,

	-- Question types:
	-- 0 = Single Answer Radio
	-- 1 = Single Answer Select
	-- 2 = Multiple Answer Checkboxes
	--
	questiontype int NOT NULL CONSTRAINT valid_questiontype CHECK (questiontype IN (0, 1, 2)),

	-- Min and Max number of responses for multiple answers
	-- 
	minresponses int NOT NULL CONSTRAINT valid_minresponses CHECK (minresponses >= 0) DEFAULT 1,
	maxresponses int NOT NULL CONSTRAINT valid_maxresponses CHECK (maxresponses >= minresponses) DEFAULT 1,

	intro text NOT NULL,
	outro text NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	pollingstarttm bigint NOT NULL CONSTRAINT valid_pollingstarttm CHECK (pollingendtm >= pollingstarttm AND pollingendtm <= endtm),
	pollingendtm bigint NOT NULL CONSTRAINT valid_pollingendtm CHECK (pollingendtm > pollingstarttm),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT categoryid_fk FOREIGN KEY(categoryid) REFERENCES research.pollcategories(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(sitegroupid, title, starttm, endtm)
);
CREATE INDEX ON research.polls(sitegroupid, starttm, endtm);

-- A list of all possible answers to a poll
--
CREATE TABLE research.pollanswers
(
	ser SERIAL PRIMARY KEY,

	pollid int NOT NULL,
	orderweight int NOT NULL,

	answer text NOT NULL,

	CONSTRAINT pollid_fk FOREIGN KEY(pollid) REFERENCES research.polls(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX ON research.pollanswers(pollid, answer);

-- User data for each poll
--
CREATE TABLE research.pollresponses
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	answerid int NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES research.polls(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT answerid_fk FOREIGN KEY(answerid) REFERENCES research.pollanswers(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(userid, itemid)
);
CREATE INDEX ON research.pollresponses(itemid);

COMMIT;
