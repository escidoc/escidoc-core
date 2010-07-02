/**
 * User initialization
 */
    /**
     * System Administrator user (Super user).
     */   
--INSERT INTO aa.user_account
--	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
--	 VALUES
--	('escidoc:exuser1',
--	true,
--    'System Administrator User',
--    'sysadmin',
--    'eSciDoc',
--    '${escidoc.creator.user}',
--    CURRENT_TIMESTAMP,
--    '${escidoc.creator.user}',
--    CURRENT_TIMESTAMP);

-- this a workaround until the db reation is in right maven phase (where filter could process the sql scripts)
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:exuser1',
	true,
    'System Administrator User',
    'sysadmin',
    'eSciDoc',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);

    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser1ouattribute', 'escidoc:exuser1','o', 'escidoc:ex3', 'TRUE');

INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser1emailattribute', 'escidoc:exuser1','email', 'system.administrator@superuser', 'TRUE');

    /*
     * System Inspector user (Read only super user).
     */
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:exuser2',
	true,
    'System Inspector User (Read Only Super User)',
    'sysinspector',
    'eSciDoc',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser2ouattribute', 'escidoc:exuser2','o', 'escidoc:ex3', 'TRUE');

INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser2emailattribute', 'escidoc:exuser2','email', 'system.inspector@superuser', 'TRUE');
    
    /**
     * Depositor user.
     */
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:exuser4',
	true,
    'Depositor User',
    'depositor',
    'eSciDoc',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser4ouattribute', 'escidoc:exuser4','o', 'escidoc:ex3', 'TRUE');

    /**
     * Ingestor user.
     */
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:exuser5',
	true,
    'Ingestor User',
    'ingester',
    'eSciDoc',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser5ouattribute', 'escidoc:exuser5','o', 'escidoc:ex3', 'TRUE');

    /**
     * Collaborator user.
     */
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:exuser6',
    true,
    'Collaborator User',
    'collaborator',
    'eSciDoc',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);
    
    
   	  