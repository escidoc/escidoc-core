/**
 * Role Grant initialization.
 */

        /**
         * The System Administrator user gets the role System-Administrator.
         */
INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:grant1', '${escidoc.creator.user}', 'escidoc:role-system-administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP);


        /**
         * The System Inspector user gets the role System-Inspector.
         */
INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:grant2', 'escidoc:exuser2', 'escidoc:role-system-inspector', '${escidoc.creator.user}', CURRENT_TIMESTAMP);

    
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

    
        /**
         * The Ingester user gets the role Ingester.
         */
INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:grantIngest1', 'escidoc:exuser5', 'escidoc:role-ingester', '${escidoc.creator.user}', CURRENT_TIMESTAMP);
    