-- @author Hamid Badiozamani
--
BEGIN WORK;
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'fb.appid', '');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'fb.secret', '');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'twilioSMSManager.AuthToken', '');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'twilioSMSManager.AccountSID', '');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'twilioSMSManager.FromPhoneNo', '');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'twilioSMSManager.PhonePrefix', '+1');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'nexmoSMSManager.api_key', '');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'nexmoSMSManager.api_secret', '');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'nexmoSMSManager.PhonePrefix', '1');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'nexmoSMSManager.FromPhoneNo', '');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'friendRefManager.enabled', 'true');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'friendRefManager.maxFriends', '0');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'friendRefManager.tricklePercentage', '0.1');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'fileManager.httpBaseURL', 'http://files.i4oneinteractive.com/');
INSERT INTO base.clientoptions (clientid, key, value) VALUES (1, 'emails.optout', '${request.fullbaseurl}/base/user/account/optout.html?username=$User.ser');
COMMIT;
