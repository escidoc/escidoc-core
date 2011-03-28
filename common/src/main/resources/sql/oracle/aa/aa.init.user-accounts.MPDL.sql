/**
 * User initialization
 */
    /**
     * Super user (roland)
     */   
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:user42',
	1,
    'roland',
    'roland',
    'Shibboleth-Handle-1',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user42ouattribute', 'escidoc:user42','o', 'escidoc:persistent1', '1');
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user42emailattribute', 'escidoc:user42','email', 'roland@roland', '1');

    /*
     * Inspector (Read only super user)
     */
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:user44',
	1,
    'Inspector (Read Only Super User)',
    'inspector',
    'inspector',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user44ouattribute', 'escidoc:user44','o', 'escidoc:persistent1', '1');

INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user44emailattribute', 'escidoc:user44','email', 'inspector@superuser', '1');

    /**
     * Test Depositor Scientist
     */
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:user1',
	1,
    'Test Depositor Scientist',
    'test_dep_scientist',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user1ouattribute', 'escidoc:user1','o', 'escidoc:persistent1', '1');


    /**
     * Test Depositor Library
     */ 
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:user2',
	1,
    'Test Depositor Library',
    'test_dep_lib',
    'pubman',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user2ouattribute', 'escidoc:user2','o', 'escidoc:persistent1', '1');
    

    /**
     * Test Editor
     */ 
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:user3',
	1,
    'Test Editor',
    'test_editor',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user3ouattribute', 'escidoc:user3','o', 'escidoc:persistent1', '1');


    /**
     * Test Author
     */     
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:user4',
	1,
    'Test Author',
    'test_author',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user4ouattribute', 'escidoc:user4','o', 'escidoc:persistent1', '1');
    

    /**
     * Lexus user
     */  
     //FIXME: check org.unit id and context id 
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:user5',
	1,
    'Lexus',
    'lexus',
    'lexus',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user5ouattribute', 'escidoc:user5','o', 'escidoc:persistent1', '1');

    /**
     * Test Statistic Editor
     */  
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:user6',
	1,
    'TestStatistics Editor',
    'test_statistics_editor',
    'test_statistics_editor',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user6ouattribute', 'escidoc:user6','o', 'escidoc:persistent1', '1');

    /**
     * Test Statistic Reader
     */  
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:user7',
	1,
    'TestStatistics Reader',
    'test_statistics_reader',
    'test_statistics_reader',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user7ouattribute', 'escidoc:user7','o', 'escidoc:persistent1', '1');

    /**
     * Test Privileged Viewer
     */  
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:user8',
	1,
    'TestPrivileged Viewer',
    'test_privileged_viewer',
    'test_privileged_viewer',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:user8ouattribute', 'escidoc:user8','o', 'escidoc:persistent1', '1');

    
