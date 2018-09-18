-- @author Hamid Badiozamani
--
BEGIN WORK;

--
-- REWARDS
--
CREATE SCHEMA rewards;

-- These records describe a particular prize that is available for players to win
--
CREATE TABLE rewards.prizes
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,

	title text NOT NULL,
	descr text NOT NULL,

 	thumbnailurl varchar(256) NOT NULL,
 	detailpicurl varchar(256) NOT NULL,

	initinventory int NOT NULL CONSTRAINT valid_initinventory CHECK (initinventory >= 0 ),
	currinventory int NOT NULL CONSTRAINT valid_currinventory CHECK (currinventory <= initinventory AND currinventory >= 0),

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,

	UNIQUE(sitegroupid, title)
);
CREATE INDEX ON rewards.prizes(clientid);


-- This is just a transaction log of which features reserved how much (and how much
-- did they return back to the prize's initial inventory). The records are never
-- meant to be updated as items are purchased since that's the function of the 
-- prizewinnings table.
--
CREATE TABLE rewards.prizereservations
(
	ser SERIAL PRIMARY KEY,

	adminid int NOT NULL,

	prizeid int NOT NULL,

	-- The amount can be positive or negative depending on whether we've
	-- put back items into the inventory or taken them out
	--
	amount int NOT NULL,

	-- Towards what feature is this reservation going to?
	--
	featureid int NOT NULL,
	feature varchar(128) NOT NULL,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT adminid_fk FOREIGN KEY(adminid) REFERENCES base.admins(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT prizeid_fk FOREIGN KEY(prizeid) REFERENCES rewards.prizes(ser) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX ON rewards.prizereservations(prizeid);

-- How much of what prize is due to a particular user.
--
CREATE TABLE rewards.prizewinnings
(
	ser SERIAL PRIMARY KEY,

	itemid int NOT NULL,
	userid int NOT NULL,
	quantity int NOT NULL CONSTRAINT valid_quantity CHECK (quantity > 0),

	-- Through which method did the user come into winning this prize? This
	-- would typcially be something like "shoppingpurchases:34" or
	-- "auctionbid:89" or "raffles:3"
	--
	featureid int NOT NULL,
	feature varchar(128) NOT NULL,

	-- When did the user come into winning this prize? In the case of shopping
	-- purchases, this should be the same as the timestamp of the purchase. However,
	-- in the cases of raffles and auctions it will likely differ.
	--
	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT prizeid_fk FOREIGN KEY(itemid) REFERENCES rewards.prizes(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(itemid, userid, timestamp)
);
CREATE INDEX rewards_prizewinnings_pindex ON rewards.prizewinnings(userid);

-- The fulfillment for each prize that is due to a particular user. These records are
-- exclusively created by an administrator who is performing the fulfillment.
--
-- Externally, the application will treat each status as being applied toward
-- the prize inventory quantity such that the any left over quantities that do
-- not have a prizefulfillment record are apparent to the administrator
--
CREATE TABLE rewards.prizefulfillments
(
	ser SERIAL PRIMARY KEY,

	-- The manager should just have a member field "Admin" for the
	-- SimplePrizeFullfilment manager 
	--
	adminid int NOT NULL,

	itemid int NOT NULL,
	quantity int NOT NULL CONSTRAINT valid_quantity CHECK (quantity > 0),

	-- Pending = 0, Shipped, Claimed, Denied
	--
	status int NOT NULL DEFAULT 0,
	notes text NOT NULL DEFAULT '',

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT adminid_fk FOREIGN KEY(adminid) REFERENCES base.admins(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES rewards.prizewinnings(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(adminid, itemid, timestamp)
);
CREATE INDEX ON rewards.prizefulfillments(itemid);

CREATE TABLE rewards.shoppingcategories
(
 	ser SERIAL PRIMARY KEY,
 
 	clientid int NOT NULL,
	sitegroupid int NOT NULL,
 
	title text NOT NULL,
 	thumbnailurl varchar(256) NOT NULL,
 	detailpicurl varchar(256) NOT NULL,

 	descr text NOT NULL DEFAULT '{"en":""}',
 
 	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(sitegroupid, title)
);
CREATE INDEX ON rewards.shoppingcategories(clientid);

CREATE TABLE rewards.shoppings
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,
	categoryid int NOT NULL,

	title text NOT NULL,
	prizeid int NOT NULL,

	-- The reserve is the number of prizes to hold exclusively for this
	-- shopping event. A value of 0 indicates no reserve and the prize's
	-- inventory will be used instead.
	-- 
	initreserve int NOT NULL DEFAULT 0 CONSTRAINT valid_initreserve CHECK (initreserve >= 0),
	currreserve int NOT NULL DEFAULT 0 CONSTRAINT valid_currreserve CHECK (currreserve <= initreserve AND currreserve >= 0),

	intro text NOT NULL,
	outro text NOT NULL,

	-- How many items any individual user can purchase, 0 indicates infinite
	--
	userlimit int NOT NULL CONSTRAINT valid_userlimit CHECK (userlimit >= 0),
	orderweight int NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	purchasestarttm bigint NOT NULL CONSTRAINT valid_purchasestarttm CHECK (starttm <= purchasestarttm),
	purchaseendtm bigint NOT NULL CONSTRAINT valid_purchaseendtm CHECK (purchaseendtm >= purchasestarttm AND purchaseendtm <= endtm),

	soldoutdisplay boolean NOT NULL DEFAULT true, -- Whether to display the shopping if it sells out

	-- XXX: This has potentially serious consequences: removing a client that has an item that spans across a given
	-- sitegroup could wipe out items that other clients in that sitegroup depend on
	CONSTRAINT prize_id_fk FOREIGN KEY(prizeid) REFERENCES rewards.prizes(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT categoryid_fk FOREIGN KEY(categoryid) REFERENCES rewards.shoppingcategories(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(categoryid, title, starttm, endtm)
);
CREATE UNIQUE INDEX rewards_shopping_pindex ON rewards.shoppings(clientid, LOWER(title), starttm, endtm);
CREATE INDEX rewards_shopping_tindex ON rewards.shoppings(sitegroupid, starttm, endtm);

-- User data for each prize purchase entry
--
CREATE TABLE rewards.shoppingpurchases
(
	ser SERIAL PRIMARY KEY,

	itemid int NOT NULL,
	userid int NOT NULL,

	-- Note that the prize winning table's userid says that a user won/is due
	-- a particular prize. Whereas the userid in this table says that a
	-- user participated with a particular shopping game. Right now, the
	-- two values should be identical, but it may be possible to buy a
	-- "gift" for another user, in which case the two would differ.
	--
	prizewinningid int NOT NULL DEFAULT 0,

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES rewards.shoppings(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT prizewinningid_fk FOREIGN KEY(prizewinningid) REFERENCES rewards.prizewinnings(ser) ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED,

	UNIQUE(itemid, userid, timestamp)
);
CREATE INDEX ON rewards.shoppingpurchases(userid, itemid);
CREATE UNIQUE INDEX ON rewards.shoppingpurchases(prizewinningid);

CREATE TABLE rewards.rafflecategories
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
CREATE INDEX ON rewards.rafflecategories(clientid);

CREATE TABLE rewards.raffles
(
	ser SERIAL PRIMARY KEY,

	clientid int NOT NULL,
	sitegroupid int NOT NULL,
	categoryid int NOT NULL,

	title text NOT NULL,
	prizeid int NOT NULL,

	-- The reserve is the number of prizes to hold exclusively for this
	-- raffles event. A value of 0 indicates no reserve and the prize's
	-- inventory will be used instead.
	-- 
	initreserve int NOT NULL DEFAULT 0 CONSTRAINT valid_initreserve CHECK (initreserve >= 0),
	currreserve int NOT NULL DEFAULT 0 CONSTRAINT valid_currreserve CHECK (currreserve <= initreserve AND currreserve >= 0),

	intro text NOT NULL,
	outro text NOT NULL,

	userlimit int NOT NULL CONSTRAINT valid_userlimit CHECK (userlimit >= 0),
	orderweight int NOT NULL,

	starttm bigint NOT NULL,
	endtm bigint NOT NULL CONSTRAINT valid_endtm CHECK (endtm > starttm),

	purchasestarttm bigint NOT NULL CONSTRAINT valid_starttm CHECK (starttm <= purchasestarttm),
	purchaseendtm bigint NOT NULL CONSTRAINT valid_purchaseendtm CHECK (purchaseendtm >= purchasestarttm AND purchaseendtm <= endtm),

	CONSTRAINT prize_id_fk FOREIGN KEY(prizeid) REFERENCES rewards.prizes(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	CONSTRAINT clientid_fk FOREIGN KEY(clientid) REFERENCES base.clients(ser) ON UPDATE CASCADE ON DELETE CASCADE,
 	CONSTRAINT sitegroupid_fk FOREIGN KEY(sitegroupid) REFERENCES base.sitegroups(ser) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT categoryid_fk FOREIGN KEY(categoryid) REFERENCES rewards.rafflecategories(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(categoryid, title, starttm, endtm)
);
CREATE INDEX rewards_raffles_sindex ON rewards.raffles(sitegroupid, starttm, endtm);

-- User data for each raffle
--
CREATE TABLE rewards.raffleentries
(
	ser SERIAL PRIMARY KEY,

	userid int NOT NULL,
	itemid int NOT NULL,

	quantity int NOT NULL CONSTRAINT valid_quantity CHECK (quantity > 0),

	timestamp bigint NOT NULL DEFAULT (EXTRACT(epoch FROM CURRENT_TIMESTAMP)),

	CONSTRAINT userid_fk FOREIGN KEY(userid) REFERENCES base.users(ser) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT itemid_fk FOREIGN KEY(itemid) REFERENCES rewards.raffles(ser) ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE(itemid, userid, timestamp)
);
CREATE INDEX rewards_raffleentries_pindex ON rewards.raffleentries(userid, itemid);

COMMIT;
