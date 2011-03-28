   /**
    * Context method mappings
    */
        /**
         * Context mm - create
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-context-create', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-context',
  1, 1);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'context', 'escidoc:mm-context-create');

        /**
         * Context mm - delete
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-delete', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'delete', 'info:escidoc/names:aa:1.0:action:delete-context', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-delete-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-delete');


        /**
         * Context mm - retrieve
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-retrieve', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'retrieve', 'info:escidoc/names:aa:1.0:action:retrieve-context',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-retrieve');

        /**
         * Context mm - retrieve properties
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-retrieve-properties', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'retrieveProperties', 'info:escidoc/names:aa:1.0:action:retrieve-context',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-retrieve-properties', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-retrieve-properties');

        /**
         * Context mm - retrieve admin descriptors
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-retrieve-admin-descriptors', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'retrieveAdminDescriptors', 
  'info:escidoc/names:aa:1.0:action:retrieve-context',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-retrieve-admin-descriptors', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-retrieve-admin-descriptors');


        /**
         * Context mm - retrieve admin descriptor
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-retrieve-admin-descriptor', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'retrieveAdminDescriptor', 
  'info:escidoc/names:aa:1.0:action:retrieve-context',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-retrieve-admin-descriptor', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-retrieve-admin-descriptor');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-retrieve-admin-descriptor-1', 'info:escidoc/names:aa:1.0:resource:admin-descriptor-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-retrieve-admin-descriptor');


        /**
         * Context mm - retrieve resource
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-retrieve-resource', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'retrieveResource',
  'info:escidoc/names:aa:1.0:action:retrieve-context', 1, 1,
  'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-retrieve-resource', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0,
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-retrieve-resource');

        /**
         * Context mm - retrieve resources
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-retrieve-resources', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'retrieveResources', 
  'info:escidoc/names:aa:1.0:action:retrieve-context', 1, 1, 
  'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-retrieve-resources', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-retrieve-resources');


        /**
         * Context mm - update
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-update', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'update', 
  'info:escidoc/names:aa:1.0:action:update-context',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-update', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-update');


        /**
         * Context mm - update admin descriptor
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-update-admin-descriptor', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'updateAdminDescriptor', 
  'info:escidoc/names:aa:1.0:action:update-context',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-update-admin-descriptor', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-update-admin-descriptor');

         
        /**
         * Context mm - close
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-close', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'close', 'info:escidoc/names:aa:1.0:action:close-context', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-close-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-close');

          
        /**
         * Context mm - open
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-open', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'open', 'info:escidoc/names:aa:1.0:action:open-context', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-open-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-open');
      

        /**
         * Context mm - retrieve members
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-context-retrieve-members', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'retrieveMembers', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', 1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-context-retrieve-members-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-context-retrieve-members');


        /**
         * Context mm - retrieve contexts
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-context-retrieve-contexts', 'de.escidoc.core.om.service.interfaces.ContextHandlerInterface', 'retrieveContexts', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', 0, 1);
