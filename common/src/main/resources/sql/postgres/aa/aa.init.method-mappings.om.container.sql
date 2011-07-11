   /**
    * Container method mappings
    */

        /**
         * Container mm - assignObjectPid
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-assign-object-pid', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'assignObjectPid', 'info:escidoc/names:aa:1.0:action:update-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-assign-object-pid-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-assign-object-pid');

        /**
         * Container mm - assignVersionPid
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-assign-version-pid', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'assignVersionPid', 'info:escidoc/names:aa:1.0:action:update-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-assign-version-pid-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-assign-version-pid');

        /**
         * Container mm - create
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-container-create', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-container', 
  true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'container', 'escidoc:mm-container-create');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-2', 'info:escidoc/names:aa:1.0:resource:container:context-new', 'extractObjid:/container/properties/context/@href|/container/properties/context/@objid', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 2, false, '', 'escidoc:mm-container-create');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-3', 'info:escidoc/names:aa:1.0:resource:container:content-model-new', 'extractObjid:/container/properties/content-model/@href|/container/properties/content-model/@objid', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 2, false, '', 'escidoc:mm-container-create');

	/**
         * Container mm - addContentRelations
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-addContentRelations', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'addContentRelations', 'info:escidoc/names:aa:1.0:action:update-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-container-addContentRelations', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-addContentRelations');


		/**
         * Container mm - removeContentRelations
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-removeContentRelations', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'removeContentRelations', 'info:escidoc/names:aa:1.0:action:update-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-container-removeContentRelations', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-removeContentRelations');

        /**
         * Container mm - delete
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-delete', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'delete', 'info:escidoc/names:aa:1.0:action:delete-container',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-delete-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-delete');


        /**
         * Container mm - retrieve
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-3', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieve', 'info:escidoc/names:aa:1.0:action:retrieve-container',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-3-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-3');

        /**
         * Container mm - retrieve relations
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-relations', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveRelations', 'info:escidoc/names:aa:1.0:action:retrieve-container',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-relations', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-relations');


        /**
         * Container mm - retrieve metadata record
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-metadata-record', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveMdRecord', 
  'info:escidoc/names:aa:1.0:action:retrieve-container',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-metadata-record', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-metadata-record');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-metadata-record-1', 'info:escidoc/names:aa:1.0:resource:metadata-record-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-metadata-record');


   /**
         * Container mm - retrieve metadata record CONTENT
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-metadata-record-content', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveMdRecordContent', 
  'info:escidoc/names:aa:1.0:action:retrieve-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-metadata-record-content', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-metadata-record-content');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-metadata-record-content-2', 'info:escidoc/names:aa:1.0:resource:metadata-record-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-metadata-record-content');



     /**
         * Container mm - retrieve dc record CONTENT
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-dc-record-content', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveDcRecordContent', 
  'info:escidoc/names:aa:1.0:action:retrieve-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-dc-record-content', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-dc-record-content');


        /**
         * Container mm - retrieve metadata records
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-metadata-records', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveMdRecords', 
  'info:escidoc/names:aa:1.0:action:retrieve-container',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-metadata-records', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-metadata-records');


        /**
         * Container mm - retrieve properties
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-properties', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveProperties', 
  'info:escidoc/names:aa:1.0:action:retrieve-container',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-properties', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-properties');


        /**
         * Container mm - retrieve resources
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-resources', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveResources', 
  'info:escidoc/names:aa:1.0:action:retrieve-container', true, true, 
  'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-resources', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-resources');


        /**
         * Container mm - retrieve resource
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-resource', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveResource', 
  'info:escidoc/names:aa:1.0:action:retrieve-container', true, true, 
  'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-resource', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-resource');

        /**
         * Container mm - retrieveStructMap
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-struct-map', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveStructMap', 'info:escidoc/names:aa:1.0:action:retrieve-container',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-sruct-map-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-struct-map');

        /**
         * Container mm - retrieve version-history
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-version-history', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveVersionHistory', 
  'info:escidoc/names:aa:1.0:action:retrieve-container', true, true, 
  'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-version-history', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-version-history');


        /**
         * Container mm - retrieveParents
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-parents', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveParents', 
  'info:escidoc/names:aa:1.0:action:retrieve-container', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-parents', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-parents');

        /**
         * Container mm - update
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-update', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'update', 'info:escidoc/names:aa:1.0:action:update-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-update');


        /**
         * Container mm - add tocs
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-add-tocs', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'addTocs', 
  'info:escidoc/names:aa:1.0:action:add-members-to-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-add-tocs', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-add-tocs');

        /**
         * Container mm - add members
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-add-members', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'addMembers', 
  'info:escidoc/names:aa:1.0:action:add-members-to-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-add-members', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-add-members');


        /**
         * Container mm - create item
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-create-item', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'createItem', 
 'info:escidoc/names:aa:1.0:action:add-members-to-container', 
 true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-item', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-create-item');

			/* creating an item may be restricted to create it inside of a specific container.
			   Therefore, here the container id is used beside object-type and context */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-container-create-item-2', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'createItem', 
  'info:escidoc/names:aa:1.0:action:create-item', 
  true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-item-2', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'item', 'escidoc:mm-container-create-item-2');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-item-2-2', 'info:escidoc/names:aa:1.0:resource:item:context-new', 'extractObjid:/item/properties/context/@href|/item/properties/context/@objid', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 2, false, '', 'escidoc:mm-container-create-item-2');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-item-2-3', 'info:escidoc/names:aa:1.0:resource:item:container-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-create-item-2');

        /**
         * Container mm - create container
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-create-container', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'createContainer', 
  'info:escidoc/names:aa:1.0:action:add-members-to-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-container', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-create-container');

			/* creating an container may be restricted to create it inside of a specific container.
			   Therefore, here the container id is used beside object-type and context */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-container-create-container-2', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'createContainer', 
  'info:escidoc/names:aa:1.0:action:create-container', 
  true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-container-2', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'container', 'escidoc:mm-container-create-container-2');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-container-2-2', 'info:escidoc/names:aa:1.0:resource:container:context-new', 'extractObjid:/container/properties/context/@href|/container/properties/context/@objid', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 2, false, '', 'escidoc:mm-container-create-container-2');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-container-2-3', 'info:escidoc/names:aa:1.0:resource:container:container-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-create-container-2');


        /**
         * Container mm - create metadata record
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-create-metadata-record', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'createMetadataRecord', 
  'info:escidoc/names:aa:1.0:action:update-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-create-metadata-record', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-create-metadata-record');


        /**
         * Container mm - update admin descriptor
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-update-admin-descriptor', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'updateAdminDescriptor', 
  'info:escidoc/names:aa:1.0:action:update-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-update-admin-descriptor', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-update-admin-descriptor');


        /**
         * Container mm - update metadata record
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-update-metadata-record', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'updateMetadataRecord', 
  'info:escidoc/names:aa:1.0:action:update-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-update-metadata-record', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-update-metadata-record');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-update-metadata-record-2', 'info:escidoc/names:aa:1.0:resource:metadata-record-id', '', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-update-metadata-record');


        /**
         * Container mm - remove members
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-remove-members', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'removeMembers', 
  'info:escidoc/names:aa:1.0:action:remove-members-from-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-remove-members', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-remove-members');


        /**
         * Container mm - submit
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-submit', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'submit', 'info:escidoc/names:aa:1.0:action:submit-container',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-submit-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-submit');


        /**
         * Container mm - release
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-release', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'release', 'info:escidoc/names:aa:1.0:action:release-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-release-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-release');

        /**
         * Container mm - revise
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-revise', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'revise', 'info:escidoc/names:aa:1.0:action:revise-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-revise-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-revise');


        /**
         * Container mm - withdraw
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-withdraw', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'withdraw', 'info:escidoc/names:aa:1.0:action:withdraw-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-withdraw-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-withdraw');


        /**
         * Container mm - lock
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-lock', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'lock', 'info:escidoc/names:aa:1.0:action:lock-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-lock-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-lock');


        /**
         * Container mm - unlock
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-unlock', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'unlock', 'info:escidoc/names:aa:1.0:action:unlock-container', 
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-unlock-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-unlock');


        /**
         * Container mm - retrieve tocs
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-tocs', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveTocs', 
  'info:escidoc/names:aa:1.0:action:retrieve-container', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-tocs-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-tocs');


        /**
         * Container mm - retrieve members
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-container-retrieve-members', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveMembers', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-container-retrieve-members-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-container-retrieve-members');

        /**
         * Container mm - retrieve containers
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-container-retrieve-containers', 'de.escidoc.core.om.service.interfaces.ContainerHandlerInterface', 'retrieveContainers', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', 
  false, true);

