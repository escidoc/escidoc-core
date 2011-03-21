    /**
     * AA method mappings
     */
     
        /**
         * AA mm - findAttribute
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-aa-find-attribute', 'de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface', 'findAttribute', 'info:escidoc/names:aa:1.0:action:find-attribute', true, true);    


        /**
         * AA mm - evaluate
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-aa-evaluate', 'de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface', 'evaluate', 'info:escidoc/names:aa:1.0:action:evaluate', true, true);    
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-aa-evaluate-1', 'info:escidoc/names:aa:1.0:resource:subject-id', '/requests/Request/Subject/Attribute/AttributeValue', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 2, true, '', 'escidoc:mm-aa-evaluate');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-aa-evaluate-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'evaluate', 'escidoc:mm-aa-evaluate');

     
     
        /**
         * AA mm - retrieveMethodMappings
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-aa-1', 'de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface', 'retrieveMethodMappings', 'info:escidoc/names:aa:1.0:action:retrieve-method-mappings', true, true);
  

        /**
         * AA mm - checkUserPrivilege
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-aa-2', 'de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface', 'checkUserPrivilege', 'info:escidoc/names:aa:1.0:action:check-user-privilege', true, true);
  

        /**
         * AA mm - checkUserPrivilegeOnListofObjects
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-aa-3', 'de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface', 'checkUserPrivilegeOnListofObjects', 'info:escidoc/names:aa:1.0:action:check-user-privilege', 
  true, true);


        /**
         * AA mm - create role
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-role-create', 'de.escidoc.core.aa.service.interfaces.RoleHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-role', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-role-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'role', 'escidoc:mm-role-create');


        /**
         * AA mm - delete role
         */

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-role-delete', 'de.escidoc.core.aa.service.interfaces.RoleHandlerInterface', 'delete', 'info:escidoc/names:aa:1.0:action:delete-role', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-role-delete-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-role-delete');
 
  
        /**
         * AA mm - retrieve role
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-role-retrieve', 'de.escidoc.core.aa.service.interfaces.RoleHandlerInterface', 'retrieve', 'info:escidoc/names:aa:1.0:action:retrieve-role', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-role-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-role-retrieve');


        /**
         * AA mm - retrieve resources role
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-role-retrieve-resources', 'de.escidoc.core.aa.service.interfaces.RoleHandlerInterface', 'retrieveResources', 'info:escidoc/names:aa:1.0:action:retrieve-role', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-role-retrieve-resources-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-role-retrieve-resources');

        /**
         * AA mm - retrieves
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-role-retrieve-role-list', 'de.escidoc.core.aa.service.interfaces.RoleHandlerInterface', 'retrieveRoles', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', false, true);

        /**
         * AA mm - update role
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-role-update', 'de.escidoc.core.aa.service.interfaces.RoleHandlerInterface', 'update', 'info:escidoc/names:aa:1.0:action:update-role', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-role-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-role-update');


        /**
         * AA mm - create user account
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-user-account-create', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-user-account', true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'user-account', 'escidoc:mm-user-account-create');

        /**
         * AA mm - delete user account
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-delete', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'delete', 
  'info:escidoc/names:aa:1.0:action:delete-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-delete', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-delete');

        /**
         * AA mm - retrieve user account
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-retrieve', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrieve', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-retrieve');

        /**
         * AA mm - retrieve current user account
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-current-user-account-retrieve', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrieveCurrentUser', 
  'info:escidoc/names:aa:1.0:action:retrieve-current-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-current-user-account-retrieve', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'user-account', 'escidoc:mm-current-user-account-retrieve');

        /**
         * AA mm - retrieve user accounts
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-role-retrieve-user-account-list', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrieveUserAccounts', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', false, true);
  
        /**
         * AA mm - update user account
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-update', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'update', 
  'info:escidoc/names:aa:1.0:action:update-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-update');

       /**
         * AA mm - update password of user account
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-update-password', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'updatePassword', 
  'info:escidoc/names:aa:1.0:action:update-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-update-password', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-update-password');  
         
        /**
         * AA mm - activate user account
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-activate', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'activate', 
  'info:escidoc/names:aa:1.0:action:activate-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-activate-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-activate');

        /**
         * AA mm - deactivate user account
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-deactivate', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'deactivate', 
  'info:escidoc/names:aa:1.0:action:deactivate-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-deactivate-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-deactivate');


        /**
         * AA mm - retrieve resources of user account
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-retrieve-resources', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrieveResources', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-retrieve-resources', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-retrieve-resources');


        /**
         * AA mm - create preference
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-user-account-create-preference', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'createPreference', 'info:escidoc/names:aa:1.0:action:create-user-account', true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-create-preference-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'user-account', 'escidoc:mm-user-account-create-preference');

        /**
         * AA mm - delete preference
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-user-account-delete-preference', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'deletePreference', 'info:escidoc/names:aa:1.0:action:update-user-account', true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-delete-preference-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'user-account', 'escidoc:mm-user-account-delete-preference');
          
        /**
         * AA mm - retrieve preference
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-preferences-retrieve', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrievePreferences', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-preferences-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-preferences-retrieve');
          
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-preference-retrieve', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrievePreference', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-preference-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-preference-retrieve');
          
         /**
          * AA mm - update preference(s)
          */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-preference-update', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'updatePreference', 
  'info:escidoc/names:aa:1.0:action:update-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-preference-update', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-preference-update');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-preferences-update', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'updatePreferences', 
  'info:escidoc/names:aa:1.0:action:update-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-preferences-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-preferences-update');
          
          
        /**
         * AA mm - create attribute
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-user-account-create-attribute', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'createAttribute', 'info:escidoc/names:aa:1.0:action:create-user-account', true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-create-attribute-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'user-account', 'escidoc:mm-user-account-create-attribute');
          
        /**
         * AA mm - retrieve attributes
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-attributes-retrieve', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrieveAttributes', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-attributes-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-attributes-retrieve');
          
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-named-attributes-retrieve', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrieveNamedAttributes', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-named-attributes-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-named-attributes-retrieve');
          
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-attribute-retrieve', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrieveAttribute', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-attribute-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-attribute-retrieve');
          
         /**
          * AA mm - update attribute
          */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-attribute-update', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'updateAttribute', 
  'info:escidoc/names:aa:1.0:action:update-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-attribute-update', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-attribute-update');

        /**
         * AA mm - delete attribute
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-attribute-delete', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'deleteAttribute', 
  'info:escidoc/names:aa:1.0:action:update-user-account', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-attribute-delete', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-attribute-delete');

          
        /**
         * AA mm - create grant
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-create-grant', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'createGrant', 
  'info:escidoc/names:aa:1.0:action:create-grant', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-create-grant-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'user-account', 'escidoc:mm-user-account-create-grant');
          
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-create-grant-2', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-create-grant');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-create-grant-3', 'info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on-new', 
  'extractObjid:/grant/properties/assigned-on/@href|/grant/properties/assigned-on/@objid', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 12, false, '', 'escidoc:mm-user-account-create-grant');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-create-grant-4', 'info:escidoc/names:aa:1.0:resource:user-account:grant:role-new', 
  'extractObjid:/grant/properties/role/@href|/grant/properties/role/@objid', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 12, false, '', 'escidoc:mm-user-account-create-grant');

        /**
         * AA mm - retrieve current grants
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-retrieve-current-grants', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrieveCurrentGrants', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-retrieve-current-grants-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-retrieve-current-grants');


        /**
         * AA mm - retrieve grant
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-retrieve-grant', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrieveGrant', 
  'info:escidoc/names:aa:1.0:action:retrieve-grant', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-retrieve-grant-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-retrieve-grant');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-retrieve-grant-2', 'info:escidoc/names:aa:1.0:resource:grant-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-retrieve-grant');

        /**
         * AA mm - retrieve grants
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-retrieve-grant-list', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrieveGrants', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', false, true);
  
        /**
         * AA mm - revoke grant
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-revoke-grant', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'revokeGrant', 
  'info:escidoc/names:aa:1.0:action:revoke-grant', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-revoke-grant-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-revoke-grant');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-revoke-grant-2', 'info:escidoc/names:aa:1.0:resource:grant-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-revoke-grant');

        /**
         * AA mm - revoke grants
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-account-revoke-grants', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'revokeGrants', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-account-revoke-grants-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-account-revoke-grants');

        /**
         * AA mm - create user group
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-user-group-create', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-user-group', true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'user-group', 'escidoc:mm-user-group-create');

        /**
         * AA mm - delete user group
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-delete', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'delete', 
  'info:escidoc/names:aa:1.0:action:delete-user-group', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-delete', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-delete');

        /**
         * AA mm - retrieve user group
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-retrieve', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'retrieve', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-group', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-retrieve');

        /**
         * AA mm - retrieve user groups
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-user-group-list-retrieve', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'retrieveUserGroups', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', false, true);
  
        /**
         * AA mm - update user group
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-update', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'update', 
  'info:escidoc/names:aa:1.0:action:update-user-group', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-update');

        /**
         * AA mm - activate user group
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-activate', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'activate', 
  'info:escidoc/names:aa:1.0:action:activate-user-group', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-activate-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-activate');

        /**
         * AA mm - deactivate user group
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-deactivate', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'deactivate', 
  'info:escidoc/names:aa:1.0:action:deactivate-user-group', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-deactivate-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-deactivate');


        /**
         * AA mm - retrieve resources of user group
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-retrieve-resources', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'retrieveResources', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-group', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');
  
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-retrieve-resources', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-retrieve-resources');


        /**
         * AA mm - retrieve selectors of user group
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-retrieve-selectors', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'retrieveSelectors', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-group', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-retrieve-selectors', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-retrieve-selectors');


        /**
         * AA mm - add selectors to a user group
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-add-selectors', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'addSelectors', 
  'info:escidoc/names:aa:1.0:action:add-user-group-selectors', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-add-selectors', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-add-selectors');


        /**
         * AA mm - remove selectors from a user group
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-remove-selectors', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'removeSelectors', 
  'info:escidoc/names:aa:1.0:action:remove-user-group-selectors', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-remove-selectors', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-remove-selectors');


        /**
         * AA mm - create user group grant
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-create-grant', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'createGrant', 
  'info:escidoc/names:aa:1.0:action:create-user-group-grant', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-create-grant-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'user-account', 'escidoc:mm-user-group-create-grant');
          
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-create-grant-2', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-create-grant');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-create-grant-3', 'info:escidoc/names:aa:1.0:resource:user-group:grant:assigned-on-new', 
  'extractObjid:/grant/properties/assigned-on/@href|/grant/properties/assigned-on/@objid', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 12, false, '', 'escidoc:mm-user-group-create-grant');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-create-grant-4', 'info:escidoc/names:aa:1.0:resource:user-group:grant:role-new', 
  'extractObjid:/grant/properties/role/@href|/grant/properties/role/@objid', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 12, false, '', 'escidoc:mm-user-group-create-grant');


        /**
         * AA mm - retrieve current grants
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-retrieve-current-grants', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'retrieveCurrentGrants', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-retrieve-current-grants-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-retrieve-current-grants');


        /**
         * AA mm - retrieve grant
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-retrieve-user-group-grant', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'retrieveGrant', 
  'info:escidoc/names:aa:1.0:action:retrieve-user-group-grant', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-retrieve-user-group-grant-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-retrieve-user-group-grant');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-retrieve-user-group-grant-2', 'info:escidoc/names:aa:1.0:resource:grant-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-retrieve-user-group-grant');



        /**
         * AA mm - revoke user group grant
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-revoke-grant', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'revokeGrant', 
  'info:escidoc/names:aa:1.0:action:revoke-user-group-grant', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-revoke-grant-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-revoke-grant');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-revoke-grant-2', 'info:escidoc/names:aa:1.0:resource:grant-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-revoke-grant');

        /**
         * AA mm - revoke user group grants
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-user-group-revoke-grants', 'de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface', 'revokeGrants', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-user-group-revoke-grants-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-user-group-revoke-grants');

		/**
		 * AA mm - logout
		 */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-aa-logout', 'de.escidoc.core.aa.service.interfaces.UserManagementWrapperInterface', 'logout', 
  'info:escidoc/names:aa:1.0:action:logout', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

        /**
         * AA mm - initHandleExpiryTimestamp
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-aa-init-handle-expiry-timestamp', 'de.escidoc.core.aa.service.interfaces.UserManagementWrapperInterface', 'initHandleExpiryTimestamp', 
  'info:escidoc/names:aa:1.0:action:initHandleExpiryTimestamp', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException');

        /**
         * AA mm - create unsecured actions
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-aa-create-unsecured-actions', 'de.escidoc.core.aa.service.interfaces.ActionHandlerInterface', 'createUnsecuredActions', 
  'info:escidoc/names:aa:1.0:action:create-unsecured-actions', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

        /**
         * AA mm - delete unsecured actions
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-aa-delete-unsecured-actions', 'de.escidoc.core.aa.service.interfaces.ActionHandlerInterface', 'deleteUnsecuredActions', 
  'info:escidoc/names:aa:1.0:action:delete-unsecured-actions', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

        /**
         * AA mm - retrieve unsecured actions
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-aa-retrieve-unsecured-actions', 'de.escidoc.core.aa.service.interfaces.ActionHandlerInterface', 'retrieveUnsecuredActions', 
  'info:escidoc/names:aa:1.0:action:retrieve-unsecured-actions', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');
 
        /**
         * AA mm - retrieve permission filter query
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-aa-retrieve-permission-filter-query', 'de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface', 'retrievePermissionFilterQuery',
  'info:escidoc/names:aa:1.0:action:retrieve-permission-filter-query', false, true);
