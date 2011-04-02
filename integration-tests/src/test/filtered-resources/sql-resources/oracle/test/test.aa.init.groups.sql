/**
 * Create groups where the test-user always belongs to.
 *
 */

/**
 * Used persistent objects:
 * orgUnit: /oum/organizational-unit/escidoc:persistent13
 */
/**
 * Group + GroupMember initialization
 */
    /**
     * Group that contains a user as member.
     */   
INSERT INTO aa.user_group
	(id, label, active, name, description, type, email, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:testgroupwithuser',
	'escidoc:testgroupwithuser',
	1,
    'Test Group with User',
    'Test Group that contains a user as member',
    'USER',
    'test.systemadministrator@user',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP,
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_group_member
    (id, user_group_id, name, type, value)
     VALUES
    ('escidoc:testgroupwithusermember', 'escidoc:testgroupwithuser', 'user-account', 'internal', 'escidoc:test');


    /**
     * Group that contains a orgUnit as member.
     */   
INSERT INTO aa.user_group
    (id, label, active, name, description, type, email, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testgroupwithorgunit',
    'escidoc:testgroupwithorgunit',
    1,
    'Test Group with OrgUnit',
    'Test Group that contains an org unit as member',
    'OU',
    'test.systemadministrator@user',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP,
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_group_member
    (id, user_group_id, name, type, value)
     VALUES
    ('escidoc:testgroupwithorgunitmember', 'escidoc:testgroupwithorgunit', 'o', 'user-attribute', 'escidoc:persistent13');


    /**
     * Group that contains a group as member.
     */   
INSERT INTO aa.user_group
    (id, label, active, name, description, type, email, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testgroupwithgroup',
    'escidoc:testgroupwithgroup',
    1,
    'Test Group with Group',
    'Test Group that contains a Group as member',
    'GROUP',
    'test.systemadministrator@user',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP,
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_group_member
    (id, user_group_id, name, type, value)
     VALUES
    ('escidoc:testgroupwithgroupmember', 'escidoc:testgroupwithgroup', 'user-group', 'internal', 'escidoc:testgroupwithorgunit');


    /**
     * Inactive Group that contains a user as member and has grant as sysadmin.
     */   
INSERT INTO aa.user_group
    (id, label, active, name, description, type, email, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testgroupwithuser1',
    'escidoc:testgroupwithuser1',
    0,
    'Test Group1 with User',
    'Test Group1 that contains a user as member',
    'USER',
    'test.systemadministrator@user',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP,
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_group_member
    (id, user_group_id, name, type, value)
     VALUES
    ('escidoc:testgroupwithusermember1', 'escidoc:testgroupwithuser1', 'user-account', 'internal', 'escidoc:test');
    
INSERT INTO aa.role_grant
    (id, group_id, role_id, creator_id, creation_date)
    VALUES
    ('escidoc:testgroupwithuser1grant1', 
    'escidoc:testgroupwithuser1',
    'escidoc:role-system-administrator',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);


    /**
     * Inactive Group that contains a orgUnit as member and has grant as sysadmin.
     */   
INSERT INTO aa.user_group
    (id, label, active, name, description, type, email, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testgroupwithorgunit1',
    'escidoc:testgroupwithorgunit1',
    0,
    'Test Group1 with OrgUnit',
    'Test Group1 that contains an org unit as member',
    'OU',
    'test.systemadministrator@user',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP,
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_group_member
    (id, user_group_id, name, type, value)
     VALUES
    ('escidoc:testgroupwithorgunitmember1', 'escidoc:testgroupwithorgunit1', 'o', 'user-attribute', 'escidoc:persistent13');

INSERT INTO aa.role_grant
    (id, group_id, role_id, creator_id, creation_date)
    VALUES
    ('escidoc:testgroupwithorgunit1grant1', 
    'escidoc:testgroupwithorgunit1',
    'escidoc:role-system-administrator',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);

    /**
     * Inactive Group that contains a group as member and has grant as sysadmin.
     */   
INSERT INTO aa.user_group
    (id, label, active, name, description, type, email, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testgroupwithgroup1',
    'escidoc:testgroupwithgroup1',
    0,
    'Test Group1 with Group',
    'Test Group1 that contains a Group as member',
    'GROUP',
    'test.systemadministrator@user',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP,
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_group_member
    (id, user_group_id, name, type, value)
     VALUES
    ('escidoc:testgroupwithgroupmember1', 'escidoc:testgroupwithgroup1', 'user-group', 'internal', 'escidoc:testgroupwithorgunit1');

INSERT INTO aa.role_grant
    (id, group_id, role_id, creator_id, creation_date)
    VALUES
    ('escidoc:testgroupwithgroup1grant1', 
    'escidoc:testgroupwithgroup1',
    'escidoc:role-system-administrator',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);

-- inactive test group with external selector and has grant as sysadmin
INSERT INTO aa.user_group
    (id, label, active, name, description, type, email, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testgroupwithexternalselector1',
    'escidoc:testgroupwithexternalselector1',
    0,
    'Test Group1 with external selector',
    'Test Group1 that contains an external selector',
    'SHIB',
    'test.systemadministrator@user',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP,
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_group_member
    (id, user_group_id, name, type, value)
     VALUES
    ('escidoc:testgroupwithattributemember1', 'escidoc:testgroupwithexternalselector1', 'testattribute', 'user-attribute', 'testvalue');

INSERT INTO aa.role_grant
    (id, group_id, role_id, creator_id, creation_date)
    VALUES
    ('escidoc:testgroupwithexternalselector1grant1', 
    'escidoc:testgroupwithexternalselector1',
    'escidoc:role-system-administrator',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);


    /**
     * Testgroup to assign grants.
     */   
INSERT INTO aa.user_group
    (id, label, active, name, description, type, email, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testgroup',
    'escidoc:testgroup',
    1,
    'Test Group',
    'Test Group with no members',
    'GROUP',
    'test.systemadministrator@user',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP,
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);

-- test group with external selector
INSERT INTO aa.user_group
    (id, label, active, name, description, type, email, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testgroupwithexternalselector',
    'escidoc:testgroupwithexternalselector',
    1,
    'Test Group with external selector',
    'Test Group that contains an external selector',
    'SHIB',
    'test.systemadministrator@user',
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP,
    'escidoc:testsystemadministrator',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_group_member
    (id, user_group_id, name, type, value)
     VALUES
    ('escidoc:testgroupwithattributemember', 'escidoc:testgroupwithexternalselector', 'testattribute', 'user-attribute', 'testvalue');

INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testattribute1', 
    'escidoc:test', 
    'testattribute', 
    'testvalue', 
    1);
