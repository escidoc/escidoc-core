/**
 * User initialization
 */
    /**
     * System Administrator user (Super user).
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
    VALUES
    ('escidoc:exuser1',
    true,
    'System Administrator User',
    '${AdminUsername}',
    '${AdminPassword}',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser1ouattribute', 'escidoc:exuser1', 'o', 'escidoc:ex3', 'TRUE');


    /*
     * System Inspector user (Read only super user).
     */
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
    VALUES
    ('escidoc:exuser2',
    true,
    'System Inspector User (Read Only Super User)',
    '${InspectorUsername}',
    '${InspectorHandle}',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser2ouattribute', 'escidoc:exuser2', 'o', 'escidoc:ex3', 'TRUE');
    
INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:exuser2LoginData', 
    'escidoc:exuser2',
    '${InspectorHandle}', 
    1999999999999);

    
    /**
     * Depositor user.
     */
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
    VALUES
    ('escidoc:exuser4',
    true,
    'Depositor User',
    '${DepositorUsername}',
    '${DepositorPassword}',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_attribute
    (id, user_id, name, value, internal)
     VALUES
    ('escidoc:exuser4ouattribute', 'escidoc:exuser4','o', 'escidoc:ex3', 'TRUE');

   	  