/**
 * Role Grant initialization.
 */

        /**
         * The System Administrator user gets the role System-Administrator.
         */
INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:grant1', 'escidoc:exuser1', 'escidoc:role-system-administrator', 'escidoc:exuser1', CURRENT_TIMESTAMP);


        /**
         * The System Inspector user gets the role System-Inspector.
         */
INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:grant2', 'escidoc:exuser2', 'escidoc:role-system-inspector', 'escidoc:exuser1', CURRENT_TIMESTAMP);

    
        /**
         * The Depositor user gets the roles administrator, depositor, moderator.
         */
INSERT INTO aa.role_grant
    (id, user_id, role_id, creator_id, creation_date, object_id, object_title, object_href) 
    values
    ('escidoc:grant41', 'escidoc:exuser4', 'escidoc:role-administrator', 'escidoc:exuser1', CURRENT_TIMESTAMP, 'escidoc:ex1', 'A simple Context', '/ir/context/escidoc:ex1');    	    			
    
INSERT INTO aa.role_grant
    (id, user_id, role_id, creator_id, creation_date, object_id, object_title, object_href) 
    values
    ('escidoc:grant42', 'escidoc:exuser4', 'escidoc:role-depositor', 'escidoc:exuser1', CURRENT_TIMESTAMP, 'escidoc:ex1', 'A simple Context', '/ir/context/escidoc:ex1');   
    
// This has been added as temporarily fix for the problem of issue #172
INSERT INTO aa.role_grant
    (id, user_id, role_id, creator_id, creation_date, object_id, object_title, object_href) 
    values
    ('escidoc:grant43', 'escidoc:exuser4', 'escidoc:role-moderator', 'escidoc:exuser1', CURRENT_TIMESTAMP, 'escidoc:ex1', 'A simple Context', '/ir/context/escidoc:ex1');   

    