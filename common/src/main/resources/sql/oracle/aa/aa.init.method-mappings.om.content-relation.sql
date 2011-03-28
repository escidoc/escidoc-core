   /**
    * Content Relation method mappings
    */
    
        /**
         * Content Relation mm - assignObjectPid
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-assign-object-pid', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'assignObjectPid', 'info:escidoc/names:aa:1.0:action:update-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');
         
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-assign-object-pid-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-assign-object-pid');
   
        /**
         * Content Relation mm - create
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-create', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'content-relation', 'escidoc:mm-content-relation-create');

        /**
         * Content Relation mm - delete
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-2', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'delete', 'info:escidoc/names:aa:1.0:action:delete-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-2-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-2');

        /**
         * Content Relation mm - retrieve
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-retrieve', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'retrieve', 'info:escidoc/names:aa:1.0:action:retrieve-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-retrieve');

        /**
         * Content Relation mm - retrieve metadata record
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-retrieve-metadata-record', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'retrieveMdRecord', 
  'info:escidoc/names:aa:1.0:action:retrieve-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve-metadata-record', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-retrieve-metadata-record');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve-metadata-record-2', 'info:escidoc/names:aa:1.0:resource:metadata-record-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-retrieve-metadata-record');

        /**
         * Content Relation mm - retrieve metadata record CONTENT
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-retrieve-metadata-record-content', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'retrieveMdRecordContent', 
  'info:escidoc/names:aa:1.0:action:retrieve-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve-metadata-record-content', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-retrieve-metadata-record-content');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve-metadata-record-content-2', 'info:escidoc/names:aa:1.0:resource:metadata-record-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-retrieve-metadata-record-content');

	     /**
         * Content Relation mm - retrieve dc record CONTENT
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-retrieve-dc-record-content', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'retrieveDcRecordContent', 
  'info:escidoc/names:aa:1.0:action:retrieve-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve-dc-record-content', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-retrieve-dc-record-content');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve-dc-record-content-2', 'info:escidoc/names:aa:1.0:resource:dc', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-retrieve-dc-record-content');

        /**
         * Content Relation mm - retrieve metadata records
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-retrieve-metadata-records', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'retrieveMdRecords', 
  'info:escidoc/names:aa:1.0:action:retrieve-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve-metadata-records', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-retrieve-metadata-records');

        /**
         * Content Relation mm - retrieve properties
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-retrieve-properties', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'retrieveProperties', 
  'info:escidoc/names:aa:1.0:action:retrieve-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve-properties', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-retrieve-properties');

        /**
         * Content Relation mm - retrieve resources
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-retrieve-resources', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'retrieveResources', 
  'info:escidoc/names:aa:1.0:action:retrieve-content-relation', 1, 1, 
  'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve-resources', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-retrieve-resources');

        /**
         * Content Relation mm - update
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-update', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'update', 'info:escidoc/names:aa:1.0:action:update-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-update');

        /**
         * Content Relation - update metadata record content
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-md-record-content-update', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'updateMdRecordContent', 'info:escidoc/names:aa:1.0:action:update-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-md-record-content-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-md-record-content-update');

        /**
         * Content Relation mm - create metadata record
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-create-metadata-record', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'createMetadataRecord', 
  'info:escidoc/names:aa:1.0:action:update-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-create-metadata-record', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-create-metadata-record');

        /**
         * Content Relation mm - update metadata record
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-update-metadata-record', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'updateMetadataRecord', 
  'info:escidoc/names:aa:1.0:action:update-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-update-metadata-record', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-update-metadata-record');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-update-metadata-record-2', 'info:escidoc/names:aa:1.0:resource:metadata-record-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-update-metadata-record');

        /**
         * Content Relation mm - submit
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-submit', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'submit', 'info:escidoc/names:aa:1.0:action:submit-content-relation',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-submit-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-submit');

        /**
         * Content Relation mm - release
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-release', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'release', 'info:escidoc/names:aa:1.0:action:release-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-release-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-release');

        /**
         * Content Relation mm - revise
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-revise', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'revise', 'info:escidoc/names:aa:1.0:action:revise-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-revise-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-revise');

        /**
         * Content Relation mm - withdraw
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-withdraw', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'withdraw', 'info:escidoc/names:aa:1.0:action:withdraw-content-relation', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-withdraw-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-withdraw');

        /**
         * Content Relation mm - lock
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-lock', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'lock', 'info:escidoc/names:aa:1.0:action:lock-content-relation',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-lock-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-lock');

        /**
         * Content Relation mm - unlock
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-content-relation-unlock', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'unlock', 'info:escidoc/names:aa:1.0:action:unlock-content-relation',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-unlock-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-content-relation-unlock');
 
        /**
         * Content Relation mm - retrieve content relations
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-content-relation-retrieve-content-relations', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'retrieveContentRelations', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', 0, 1);
  
  
       /**
         * Content Relation mm - retrieve registered predicates
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-content-relation-retrieve-registered-predicates', 'de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface', 'retrieveRegisteredPredicates', 
  'info:escidoc/names:aa:1.0:action:retrieve-registered-predicates', 1, 1);
  
 
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-content-relation-retrieve-registered-predicates', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'none', 'escidoc:mm-content-relation-retrieve-registered-predicates');
  
 