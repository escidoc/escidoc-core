// FIXME: remove these handles of the defined users when the login process has 
// been implemented at FIZ AND MPDL

// FIXME: Indexer user handle is needed for gsearch setup?

/**
 * eSciDoc user handles initialization.
 */
INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:user1', 'escidoc:user1', 'test_dep_scientist', 1999999999999);

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:user2', 'escidoc:user2', 'test_dep_lib', 1999999999999);

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:user3', 'escidoc:user3', 'test_editor', 1999999999999);

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:user4', 'escidoc:user4', 'test_author', 1999999999999);

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:user6', 'escidoc:user6', 'test_statistics_editor', 1999999999999);

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:user7', 'escidoc:user7', 'test_statistics_reader', 1999999999999);

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:user8', 'escidoc:user8', 'test_privileged_viewer', 1999999999999);

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('${escidoc.creator.user}', '${escidoc.creator.user}', 'Shibboleth-Handle-1', 1999999999999);

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:user44', 'escidoc:user44', 'inspector', 1999999999999);

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:exuser6', 'escidoc:exuser6', 'collaborator', 1999999999999);
       