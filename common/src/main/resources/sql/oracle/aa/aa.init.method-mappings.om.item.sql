   /**
    * Item method mappings
    */
    
        /**
         * Item mm - assignVersionPid
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-assign-version-pid', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'assignVersionPid', 'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');
         
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-assign-version-pid-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-assign-version-pid');

        /**
         * Item mm - assignObjectPid
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-assign-object-pid', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'assignObjectPid', 'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');
         
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-assign-object-pid-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-assign-object-pid');
   
        /**
         * Item mm - assignContentPid
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-assign-content-pid', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'assignContentPid', 'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');
         
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-assign-content-pid-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-assign-content-pid');

        /**
         * Item mm - create
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-create', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'item', 'escidoc:mm-item-create');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-create-2', 'info:escidoc/names:aa:1.0:resource:item:context-new', 'extractObjid:/item/properties/context/@href|/item/properties/context/@objid', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 2, 0, '', 'escidoc:mm-item-create');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-create-3', 'info:escidoc/names:aa:1.0:resource:item:content-model-new', 'extractObjid:/item/properties/content-model/@href|/item/properties/content-model/@objid', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 2, 0, '', 'escidoc:mm-item-create');

        /**
         * Item mm - delete
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-2', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'delete', 'info:escidoc/names:aa:1.0:action:delete-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-2-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-2');


        /**
         * Item mm - retrieve
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieve', 'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve');

        /**
         * Item mm - retrieve relations
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-relations', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveRelations', 'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-relations', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-relations');

        /**
         * Item mm - retrieve component
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-component', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveComponent', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-component', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-component');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-component-2', 'info:escidoc/names:aa:1.0:resource:component-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-component');

        /**
         * Item mm - retrieve component properties
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-component-properties', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveComponentProperties', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-component-properties', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-component-properties');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-component-properties-2', 'info:escidoc/names:aa:1.0:resource:component-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-component-properties');


/**
         * Item mm - retrieve component md-records
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-component-md-records', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveComponentMdRecords', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-component-md-records', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-component-md-records');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-component-md-records-2', 'info:escidoc/names:aa:1.0:resource:component-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-component-md-records');

/**
         * Item mm - retrieve component md-record
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-component-md-record', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveComponentMdRecord', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-component-md-record', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-component-md-record');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-component-md-record-2', 'info:escidoc/names:aa:1.0:resource:component-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-component-md-records');



        /**
         * Item mm - retrieve components
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-components', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveComponents', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-components', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-components');


        /**
         * Item mm - retrieve content
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-content', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveContent', 
  'info:escidoc/names:aa:1.0:action:retrieve-content', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-content', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-content');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-content-2', 'info:escidoc/names:aa:1.0:resource:component:item', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-content');


        /**
         * Item mm - retrieve metadata record
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-metadata-record', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveMdRecord', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-metadata-record', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-metadata-record');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-metadata-record-2', 'info:escidoc/names:aa:1.0:resource:metadata-record-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-metadata-record');


        /**
         * Item mm - retrieve metadata record CONTENT
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-metadata-record-content', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveMdRecordContent', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-metadata-record-content', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-metadata-record-content');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-metadata-record-content-2', 'info:escidoc/names:aa:1.0:resource:metadata-record-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-metadata-record-content');



     /**
         * Item mm - retrieve dc record CONTENT
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-dc-record-content', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveDcRecordContent', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-dc-record-content', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-dc-record-content');


        /**
         * Item mm - retrieve metadata records
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-metadata-records', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveMdRecords', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-metadata-records', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-metadata-records');


        /**
         * Item mm - retrieve content stream
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-content-stream', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveContentStream', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-content-stream', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-content-stream');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-content-stream-2', 'info:escidoc/names:aa:1.0:resource:content-stream-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-content-stream');

        /**
         * Item mm - redirect content service
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-redirect-content-service', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'redirectContentService', 'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-redirect-content-service', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-redirect-content-service');

        /**
         * Item mm - update content stream
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-update-content-stream', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'updateContentStream', 
  'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-update-content-stream', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-update-content-stream');


        /**
         * Item mm - delete content stream
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-delete-content-stream', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'deleteContentStream', 
  'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-delete-content-stream', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-delete-content-stream');


        /**
         * Item mm - retrieve content stream content
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-content-stream-content', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveContentStreamContent', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-content-stream-content', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-content-stream-content');


        /**
         * Item mm - retrieve content streams
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-content-streams', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveContentStreams', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-content-streams', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-content-streams');


        /**
         * Item mm - update content streams
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-update-content-streams', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'updateContentStreams', 
  'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-update-content-streams', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-update-content-streams');


        /**
         * Item mm - delete content streams
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-delete-content-streams', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'deleteContentStreams', 
  'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-delete-content-streams', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-delete-content-streams');


        /**
         * Item mm - retrieve properties
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-properties', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveProperties', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-properties', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-properties');


        /**
         * Item mm - retrieve resources
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-resources', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveResources', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 1, 1, 
  'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-resources', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-resources');

        /**
         * Item mm - retrieve version-history
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-resources-version-history', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveVersionHistory', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 1, 1, 
  'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-resources-version-history', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-resources-version-history');


        /**
         * Item mm - retrieve resource
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-resources-resource', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveResource', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 1, 1, 
  'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-resources-resource', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-resources-resource');

        /**
         * Item mm - retrieveParents
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-retrieve-parents', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveParents', 
  'info:escidoc/names:aa:1.0:action:retrieve-item', 1, 1,
  'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-retrieve-parents', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-retrieve-parents');

        /**
         * Item mm - update
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-update', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'update', 'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-update');

        /**
         * Item - update metadata record content
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-md-record-content-update', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'updateMdRecordContent', 'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-md-record-content-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-md-record-content-update');


  		/**
         * Item mm - addContentRelations
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-addContentRelations', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'addContentRelations', 'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-item-addContentRelations', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-addContentRelations');


		/**
         * Item mm - removeContentRelations
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-removeContentRelations', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'removeContentRelations', 'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-item-removeContentRelations', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-removeContentRelations');

        /**
         * Item mm - create component
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-create-component', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'createComponent', 
  'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-create-component', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-create-component');


        /**
         * Item mm - create metadata record
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-create-metadata-record', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'createMetadataRecord', 
  'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-create-metadata-record', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-create-metadata-record');

        /**
         * Item mm - delete component
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-delete-component', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'deleteComponent', 
  'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-delete-component', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-delete-component');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-delete-component-2', 'info:escidoc/names:aa:1.0:resource:component-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-delete-component');

        /**
         * Item mm - update component
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-update-component', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'updateComponent', 
  'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-update-component', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-update-component');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-update-component-2', 'info:escidoc/names:aa:1.0:resource:component-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-update-component');


        /**
         * Item mm - update metadata record
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-update-metadata-record', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'updateMetadataRecord', 
  'info:escidoc/names:aa:1.0:action:update-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-update-metadata-record', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-update-metadata-record');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-update-metadata-record-2', 'info:escidoc/names:aa:1.0:resource:metadata-record-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-update-metadata-record');


        /**
         * Item mm - submit
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-submit', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'submit', 'info:escidoc/names:aa:1.0:action:submit-item',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-submit-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-submit');


        /**
         * Item mm - release
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-release', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'release', 'info:escidoc/names:aa:1.0:action:release-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-release-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-release');

        /**
         * Item mm - revise
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-revise', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'revise', 'info:escidoc/names:aa:1.0:action:revise-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-revise-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-revise');


        /**
         * Item mm - withdraw
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-withdraw', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'withdraw', 'info:escidoc/names:aa:1.0:action:withdraw-item', 
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-withdraw-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-withdraw');


        /**
         * Item mm - lock
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-lock', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'lock', 'info:escidoc/names:aa:1.0:action:lock-item',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-lock-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-lock');


        /**
         * Item mm - unlock
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-item-unlock', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'unlock', 'info:escidoc/names:aa:1.0:action:unlock-item',
  1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-item-unlock-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-item-unlock');


        /**
         * Item mm - retrieve items
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-container-retrieve-items', 'de.escidoc.core.om.service.interfaces.ItemHandlerInterface', 'retrieveItems', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', 0, 1);

  