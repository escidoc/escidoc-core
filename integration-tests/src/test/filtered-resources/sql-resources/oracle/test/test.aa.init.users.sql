/**
 * System-Administrator: 
 *      -No scope-defs, role valid for all object-types
 *      -May do everything
 * System-Inspector: 
 *      -No Scope-Defs, role valid for all object-types
 *      -May do all retrieve-operations
 * Administrator: 
 *      -Scope-Defs on item, container, context with attribute context
 *      -Scope-defs on grant with attribute assigned-on + context
 *      -retrieve, update, open, and close the context,
 *      -create, retrieve and revoke grants for objects,
 *      -retrieve, revise, and withdraw containers and items
 *      -retrieve user-accounts.
 * Author: 
 *      -Scope-Defs on container, item with attribute collection
 *      -Scope-Def on staging-file with no attributes
 *      -May create and retrieve container and items
 *      -May update,delete... items and containers in status pending or in-revision
 *      -May retrieve content
 * Collaborator: 
 *      -Scope-Def on item with attributes context, container, item, component
 *      -Scope-Def on component with attribute component
 *      -May retrieve items and content
 * Audience: 
 *      -Scope-Def on component with attribute component
 *      -May retrieve content with visibility='audience'
 * Depositor: 
 *      -Scope-Defs on context, container, item, component with attribute context
 *      -Scope-Def on staging-file with no attributes
 *      -May create containers and items
 *      -May update, delete items + containers in status pending or in-revision he created
 *      -May submit items + containers
 *      -May retrieve the items, containers he created
 * Ingester: 
 *      -No Scope-Defs, role valid for all object-types
 *      -May ingest
 * MD-Editor: 
 *      -Scope-Defs on context, container, item, component with attribute context
 *      -May retrieve, update and lock containers + items in state submitted + released
 *      -May submit an item he modified
 * Moderator: 
 *      -Scope-Defs on context, container, item, component with attribute context
 *      -May retrieve + release containers + items
 * Context-Administrator: 
 *      -No Scope-Defs
 *      -May retrieve, update, delete, open, close contexts (s)he created
 * Content-Relation-Administrator: 
 *      -No Scope-Defs
 *      -May retrieve, update, delete, open, close content-relations (s)he created
 * Privileged-Viewer: 
 *      -Scope-Def on component with attribute context
 *      -May retrieve content
 * Statistics-Editor: 
 *      -Scope-Defs on scope, aggregation-definition, report-definition, statistic-data with attribute scope
 *      -May create, retrieve, update + delete scopes, aggregation-definitions, report-definitions + statistic-data
 * Statistics-Reader: 
 *      -Scope-def on report with attribute scope
 *      -May retrieve Reports
 */

/**
 * Used persistent objects:
 * context: /ir/context/escidoc:persistent3
 * orgUnit: /oum/organizational-unit/escidoc:persistent31
 */
/**
 * User initialization
 */
    /**
     * System Administrator user (Super user).
     */   
INSERT INTO aa.user_account
	(id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
	 VALUES
	('escidoc:testsystemadministrator',
	1,
    'Test System Administrator User',
    'testsystemadministrator',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testsystemadministratoremailattribute', 'escidoc:testsystemadministrator','email', 'test.systemadministrator@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testsystemadministrator', 'escidoc:testsystemadministrator', 'testsystemadministrator', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:testsystemadministratorgrant1', 'escidoc:testsystemadministrator', 'escidoc:role-system-administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP);



    /**
     * System Administrator user1 (Super user1).
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testsystemadministrator1',
    1,
    'Test System Administrator User1',
    'testsystemadministrator1',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testsystemadministratoremailattribute1', 'escidoc:testsystemadministrator1','email', 'test.systemadministrator@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testsystemadministrator1', 'escidoc:testsystemadministrator1', 'testsystemadministrator1', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:testsystemadministratorgrant11', 'escidoc:testsystemadministrator1', 'escidoc:role-system-administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP);



    /**
     * Administrator user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testadministrator',
    1,
    'Test Administrator User',
    'testadministrator',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testadministratoremailattribute', 'escidoc:testadministrator','email', 'test.administrator@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testadministrator', 'escidoc:testadministrator', 'testadministrator', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date)
     VALUES
    ('escidoc:testadministratorgrant1', 
    'escidoc:testadministrator', 
    'escidoc:role-administrator', 
    'escidoc:persistent3', 
    'PubMan Test Collection', 
    '/ir/context/escidoc:persistent3',
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);



    /**
     * OU-Administrator user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testouadministrator',
    1,
    'Test OU-Administrator User',
    'testouadministrator',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testouadministratoremailattribute', 'escidoc:testouadministrator','email', 'test.ouadministrator@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testouadministrator', 'escidoc:testouadministrator', 'testouadministrator', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date)
     VALUES
    ('escidoc:testouadministratorgrant1', 
    'escidoc:testouadministrator', 
    'escidoc:role-ou-administrator', 
    'escidoc:persistent11', 
    'Test Org Unit', 
    '/oum/organizational-unit/escidoc:persistent11',
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);



    /**
     * Audience user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testaudience',
    1,
    'Test Audience User',
    'testaudience',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testaudienceemailattribute', 'escidoc:testaudience','email', 'test.audience@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testaudience', 'escidoc:testaudience', 'testaudience', 1999999999999);



    /**
     * Author user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testauthor',
    1,
    'Test Author User',
    'testauthor',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testauthoremailattribute', 'escidoc:testauthor','email', 'test.author@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testauthor', 'escidoc:testauthor', 'testauthor', 1999999999999);



    /**
     * Collaborator user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testcollaborator',
    1,
    'Test Collaborator User',
    'testcollaborator',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testcollaboratoremailattribute', 'escidoc:testcollaborator','email', 'test.collaborator@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testcollaborator', 'escidoc:testcollaborator', 'testcollaborator', 1999999999999);



    /**
     * Depositor user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testdepositor',
    1,
    'Test Depositor User',
    'testdepositor',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testdepositoremailattribute', 'escidoc:testdepositor','email', 'test.depositor@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testdepositor', 'escidoc:testdepositor', 'testdepositor', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date)
     VALUES
    ('escidoc:testdepositorgrant1', 
    'escidoc:testdepositor', 
    'escidoc:role-depositor', 
    'escidoc:persistent3', 
    'PubMan Test Collection', 
    '/ir/context/escidoc:persistent3',
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);



    /**
     * Depositor user2.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testdepositor2',
    1,
    'Test Depositor User2',
    'testdepositor2',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testdepositor2emailattribute', 'escidoc:testdepositor2','email', 'test.depositor2@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testdepositor2', 'escidoc:testdepositor2', 'testdepositor2', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date)
     VALUES
    ('escidoc:testdepositorgrant2', 
    'escidoc:testdepositor2', 
    'escidoc:role-depositor', 
    'escidoc:persistent3', 
    'PubMan Test Collection', 
    '/ir/context/escidoc:persistent3',
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);


    /**
     * Depositor user3.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testdepositor3',
    1,
    'Test Depositor User3',
    'testdepositor3',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testdepositor3emailattribute', 'escidoc:testdepositor3','email', 'test.depositor3@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testdepositor3', 'escidoc:testdepositor3', 'testdepositor3', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date)
     VALUES
    ('escidoc:testdepositorgrant3', 
    'escidoc:testdepositor3', 
    'escidoc:role-depositor', 
    'escidoc:persistent10', 
    'WALS Context', 
    '/ir/context/escidoc:persistent10',
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);


    /**
     * Ingester user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testingester',
    1,
    'Test Ingester User',
    'testingester',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testingesteremailattribute', 'escidoc:testingester','email', 'test.ingester@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testingester', 'escidoc:testingester', 'testingester', 1999999999999);



    /**
     * MD-Editor user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testmdeditor',
    1,
    'Test Md-Editor User',
    'testmdeditor',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testsymdeditoremailattribute', 'escidoc:testmdeditor','email', 'test.mdeditor@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testmdeditor', 'escidoc:testmdeditor', 'testmdeditor', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date)
     VALUES
    ('escidoc:testmdeditorgrant1', 
    'escidoc:testmdeditor', 
    'escidoc:role-md-editor', 
    'escidoc:persistent3', 
    'PubMan Test Collection', 
    '/ir/context/escidoc:persistent3',
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);



    /**
     * Moderator user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testmoderator',
    1,
    'Test Moderator User',
    'testmoderator',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testmoderatoremailattribute', 'escidoc:testmoderator','email', 'test.moderator@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testmoderator', 'escidoc:testmoderator', 'testmoderator', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date)
     VALUES
    ('escidoc:testmoderatorgrant1', 
    'escidoc:testmoderator', 
    'escidoc:role-moderator', 
    'escidoc:persistent3', 
    'PubMan Test Collection', 
    '/ir/context/escidoc:persistent3',
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);



    /**
     * Context-Administrator user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testcontextadministrator',
    1,
    'Test Context-Administrator User',
    'testcontextadministrator',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testcontextadministratoremailattribute', 'escidoc:testcontextadministrator','email', 'test.contextadministrator@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testcontextadministrator', 'escidoc:testcontextadministrator', 'testcontextadministrator', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:testcontextadministratorgrant1', 
    'escidoc:testcontextadministrator', 
    'escidoc:role-context-administrator', 
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);



    /**
     * Content-Relation-Manager user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testcontentrelationmanager',
    1,
    'Test Content-Relation-Manager User',
    'testcontentrelationmanager',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testcontentrelationmanageremailattribute', 'escidoc:testcontentrelationmanager','email', 'test.contentrelationmanager@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testcontentrelationmanager', 'escidoc:testcontentrelationmanager', 'testcontentrelationmanager', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:testcontentrelationmanagergrant1', 
    'escidoc:testcontentrelationmanager', 
    'escidoc:role-content-relation-manager', 
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);



    /**
     * Privileged-Viewer user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testprivilegedviewer',
    1,
    'Test Privileged-Viewer User',
    'testprivilegedviewer',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testprivilegedvieweremailattribute', 'escidoc:testprivilegedviewer','email', 'test.privilegedviewer@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testprivilegedviewer', 'escidoc:testprivilegedviewer', 'testprivilegedviewer', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date)
     VALUES
    ('escidoc:testprivilegedviewergrant1', 
    'escidoc:testprivilegedviewer', 
    'escidoc:role-privileged-viewer', 
    'escidoc:persistent3', 
    'PubMan Test Collection', 
    '/ir/context/escidoc:persistent3',
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);



    /**
     * Statistics-Editor user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:teststatisticseditor',
    1,
    'Test Statistics-Editor User',
    'teststatisticseditor',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:teststatisticseditoremailattribute', 'escidoc:teststatisticseditor','email', 'test.statisticseditor@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:teststatisticseditor', 'escidoc:teststatisticseditor', 'teststatisticseditor', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date)
     VALUES
    ('escidoc:teststatisticseditorgrant1', 
    'escidoc:teststatisticseditor', 
    'escidoc:role-statistics-editor', 
    'escidoc:scope3', 
    'Scope with id escidoc:scope3', 
    '/statistic/scope/escidoc:scope3',
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);



    /**
     * Statistics-Reader user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:teststatisticsreader',
    1,
    'Test Statistics-Reader User',
    'teststatisticsreader',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:teststatisticsreaderemailattribute', 'escidoc:teststatisticsreader','email', 'test.statisticsreader@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:teststatisticsreader', 'escidoc:teststatisticsreader', 'teststatisticsreader', 1999999999999);

INSERT INTO aa.role_grant 
    (id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date)
     VALUES
    ('escidoc:teststatisticsreadergrant1', 
    'escidoc:teststatisticsreader', 
    'escidoc:role-statistics-reader', 
    'escidoc:scope3', 
    'Scope with id escidoc:scope3', 
    '/statistic/scope/escidoc:scope3',
    '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP);



    /**
     * System-Inspector user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:testsysteminspector',
    1,
    'Test System-Inspector User',
    'testsysteminspector',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testsysteminspectoremailattribute', 'escidoc:testsysteminspector','email', 'test.systeminspector@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:testsysteminspector', 'escidoc:testsysteminspector', 'testsysteminspector', 1999999999999);

    /**
     * Grant Test user.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:test',
    1,
    'Test User',
    'test',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testemailattribute', 'escidoc:test','email', 'test.test@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:test', 'escidoc:test', 'test', 1999999999999);

INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:testouattribute', 'escidoc:test','o', 'escidoc:persistent31', '1');



    /**
     * Grant Test user1.
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:test1',
    1,
    'Test User1',
    'test1',
    'escidoc',
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP,
    '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:test1emailattribute', 'escidoc:test1','email', 'test.test1@user', '1');

INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:test1', 'escidoc:test1', 'test1', 1999999999999);

INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('${escidoc.creator.user}ouattribute', '${escidoc.creator.user}','o', 'escidoc:ex3', '1');

INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser2ouattribute', 'escidoc:exuser2','o', 'escidoc:ex3', '1');

INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser4ouattribute', 'escidoc:exuser4','o', 'escidoc:ex3', '1');

/**
 * Role Grant initialization.
 */
    
        /**
         * The Depositor user gets the roles administrator, depositor, moderator.
         */
INSERT INTO aa.role_grant
    (id, user_id, role_id, creator_id, creation_date, object_id, object_title, object_href) 
    values
    ('escidoc:grant41', '${escidoc.creator.user}', 'escidoc:role-administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, 'escidoc:ex1', 'A simple Context', '/ir/context/escidoc:ex1');    	    			
    
INSERT INTO aa.role_grant
    (id, user_id, role_id, creator_id, creation_date, object_id, object_title, object_href) 
    values
    ('escidoc:grant42', '${escidoc.creator.user}', 'escidoc:role-depositor', '${escidoc.creator.user}', CURRENT_TIMESTAMP, 'escidoc:ex1', 'A simple Context', '/ir/context/escidoc:ex1');   
    
// This has been added as temporarily fix for the problem of issue #172
INSERT INTO aa.role_grant
    (id, user_id, role_id, creator_id, creation_date, object_id, object_title, object_href) 
    values
    ('escidoc:grant43', '${escidoc.creator.user}', 'escidoc:role-moderator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, 'escidoc:ex1', 'A simple Context', '/ir/context/escidoc:ex1');   
