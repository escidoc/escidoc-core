/**
 * Create Roles for grant-tests.
 *
 */

INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-grant-test1', 'Grant Test1', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test1-1', 'escidoc:role-grant-test1', 'item', 
     'info:escidoc/names:aa:1.0:resource:item-id',
     'item');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test1-2', 'escidoc:role-grant-test1', 'container', 
     'info:escidoc/names:aa:1.0:resource:container-id',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test1-3', 'escidoc:role-grant-test1', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');

INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:grant-test1-policy-1', 'escidoc:role-grant-test1',
'<Policy PolicyId="Audience-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target><Subjects><AnySubject/></Subjects><Resources><AnyResource/></Resources><Actions><AnyAction/></Actions></Target></Policy>');
  


  
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-grant-test2', 'Grant Test2', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test2-1', 'escidoc:role-grant-test2', 'item', 
     'info:escidoc/names:aa:1.0:resource:item-id',
     'item');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test2-2', 'escidoc:role-grant-test2', 'container', 
     'info:escidoc/names:aa:1.0:resource:container-id',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test2-3', 'escidoc:role-grant-test2', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');

INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:grant-test2-policy-1', 'escidoc:role-grant-test2',
'<Policy PolicyId="Audience-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target><Subjects><AnySubject/></Subjects><Resources><AnyResource/></Resources><Actions><AnyAction/></Actions></Target></Policy>');
  

  
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-grant-test3', 'Grant Test3', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test3-1', 'escidoc:role-grant-test3', 'item', 
     'info:escidoc/names:aa:1.0:resource:item-id',
     'item');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test3-2', 'escidoc:role-grant-test3', 'container', 
     'info:escidoc/names:aa:1.0:resource:container-id',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test3-3', 'escidoc:role-grant-test3', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');

INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:grant-test3-policy-1', 'escidoc:role-grant-test3',
'<Policy PolicyId="Audience-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target><Subjects><AnySubject/></Subjects><Resources><AnyResource/></Resources><Actions><AnyAction/></Actions></Target></Policy>');

  
  
  
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-grant-test4', 'Grant Test4', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test4-1', 'escidoc:role-grant-test4', 'item', 
     'info:escidoc/names:aa:1.0:resource:item-id',
     'item');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test4-2', 'escidoc:role-grant-test4', 'container', 
     'info:escidoc/names:aa:1.0:resource:container-id',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test4-3', 'escidoc:role-grant-test4', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');

INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:grant-test4-policy-1', 'escidoc:role-grant-test4',
'<Policy PolicyId="Audience-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target><Subjects><AnySubject/></Subjects><Resources><AnyResource/></Resources><Actions><AnyAction/></Actions></Target></Policy>');
  

  
  
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-grant-test5', 'Grant Test5', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test5-1', 'escidoc:role-grant-test5', 'item', 
     'info:escidoc/names:aa:1.0:resource:item-id',
     'item');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test5-2', 'escidoc:role-grant-test5', 'container', 
     'info:escidoc/names:aa:1.0:resource:container-id',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-grant-test5-3', 'escidoc:role-grant-test5', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');

INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:grant-test5-policy-1', 'escidoc:role-grant-test5',
'<Policy PolicyId="Audience-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target><Subjects><AnySubject/></Subjects><Resources><AnyResource/></Resources><Actions><AnyAction/></Actions></Target></Policy>');
  

