-- @author Hamid Badiozamani
--
BEGIN WORK;
--
-- QUIZ GAME
--
CREATE SCHEMA quiz;

-- By having events split into separate terms we can more easily track internal competitions within a term
-- as well as inter-term competitions
--
CREATE TABLE quiz.categories
(
 	ser SERIAL PRIMARY KEY,
 
 	clientid int NOT NULL,
 	sitegroupid int NOT NULL,
 
 	-- title varchar(128) NOT NULL CONSTRAINT empty_check CHECK (length(trim(both ' ' from title)) > 0),
 	title text NOT NULL CONSTRAINT valid_title CHECK (btrim(title) NOT LIKE '{"%":""}' AND btrim(title) != ''),
 	thumbnailurl varchar(256) NOT NULL,
 	detailpicurl varchar(256) NOT NULL,

 	descr text NOT NULL DEFAULT '{"en":""}',
 
 	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE (clientid, title)
);

CREATE TABLE quiz.events
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
 	sitegroupid int NOT NULL,

	questionbankid int NOT NULL,
	categoryid int,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

 	title text NOT NULL CONSTRAINT valid_title CHECK (btrim(title) NOT LIKE '{"%":""}' AND btrim(title) != ''),

	intro text NOT NULL,
	outro text NOT NULL,

	CONSTRAINT categoryid_fk FOREIGN KEY(categoryid) REFERENCES quiz.categories(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX events_pindex ON quiz.events(clientid, title, starttm, endtm);

CREATE TABLE quiz.questionbanks
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
 	sitegroupid int NOT NULL,

 	title text NOT NULL CONSTRAINT valid_title CHECK (btrim(title) NOT LIKE '{"%":""}' AND btrim(title) != ''),

	questioncount int NOT NULL,

 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX questionbanks_pindex ON quiz.questionbanks(title, clientid, sitegroupid);

-- A list of questions belonging to a question bank
--
CREATE TABLE quiz.questions
(
	ser SERIAL PRIMARY KEY,

	questionbankid int NOT NULL,

	-- Question types:
	-- 0 = Single Answer Radio
	-- 1 = Single Answer Select
	-- 2 = Multiple Answer Select
	-- 3 = Type in
	--
	questiontype int NOT NULL CONSTRAINT valid_questiontype CHECK (questiontype IN (0, 1, 2, 3)),
 	question text NOT NULL CONSTRAINT valid_question CHECK (btrim(question) NOT LIKE '{"%":""}' AND btrim(question) != ''),

	CONSTRAINT questionbankid_fk FOREIGN KEY(questionbankid) REFERENCES quiz.questionbanks(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX questions_pindex ON quiz.questions(questionbankid, question);

CREATE TABLE quiz.answers
(
	ser SERIAL PRIMARY KEY,

	questionid int NOT NULL,

 	answer text NOT NULL CONSTRAINT valid_answer CHECK (btrim(answer) NOT LIKE '{"%":""}' AND btrim(answer) != ''),
	correct bool NOT NULL,

	CONSTRAINT questionid_fk FOREIGN KEY(questionid) REFERENCES quiz.questions(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX answers_pindex ON quiz.answers(questionid, correct);

-- Players
--
CREATE TABLE quiz.players
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	-- The question the user started on
	--
	startquestion int NOT NULL,

	-- The question the user is currently on
	--
	currquestion int NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_DATE)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES quiz.events(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE UNIQUE INDEX players_pindex ON quiz.players(userid, itemid);
CREATE UNIQUE INDEX players_sindex ON quiz.players(itemid, userid);

-- Actual responses to questions
--
CREATE TABLE quiz.responses
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	answerid int NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_DATE)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES quiz.questions(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT answerid_fk FOREIGN KEY(answerid) REFERENCES quiz.answers(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(userid, itemid),
	UNIQUE(itemid, userid)
);
CREATE INDEX responses_pindex ON quiz.responses(userid, itemid);
CREATE INDEX responses_sindex ON quiz.responses(itemid, userid);

COMMIT;
