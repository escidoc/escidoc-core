   /**
    * OUM method mappings
    */
        /**
         * OUM mm - create
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-oum-create', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'create', 
  'info:escidoc/names:aa:1.0:action:create-organizational-unit', true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'organizational-unit', 'escidoc:mm-oum-create');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-create-2', 'info:escidoc/names:aa:1.0:resource:organizational-unit:parent-new', 
  'extractObjid:/organizational-unit/parents/parent/@href|/organizational-unit/parents/parent/@objid', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 12, true, '', 'escidoc:mm-oum-create');

        /**
         * OUM mm - ingest
         */ 
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-oum-ingest', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'ingest', 
  'info:escidoc/names:aa:1.0:action:ingest', true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-ingest-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'organizational-unit', 'escidoc:mm-oum-ingest');


        /**
         * OUM mm - delete
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-delete', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'delete', 
  'info:escidoc/names:aa:1.0:action:delete-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-delete-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-delete');


        /**
         * OUM mm - retrieve
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieve', 
  'info:escidoc/names:aa:1.0:action:retrieve-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve');

        /**
         * OUM mm - retrieveMdRecords
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-mdrecords', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieveMdRecords', 
  'info:escidoc/names:aa:1.0:action:retrieve-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-mdrecords-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve-mdrecords');

        /**
         * OUM mm - retrieveMdRecord
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-mdrecord', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieveMdRecord', 
  'info:escidoc/names:aa:1.0:action:retrieve-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-mdrecord-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve-mdrecord');


        /**
         * OUM mm - retrieveParents
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-parent-ous', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieveParents', 
  'info:escidoc/names:aa:1.0:action:retrieve-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-parent-ous-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve-parent-ous');

        /**
         * OUM mm - retrievePathList
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-path-list', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrievePathList', 
  'info:escidoc/names:aa:1.0:action:retrieve-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-path-list-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve-path-list');

        /**
         * OUM mm - retrieveProperties
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-properties', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieveProperties', 
  'info:escidoc/names:aa:1.0:action:retrieve-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-properties-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve-properties');





        /**
         * OUM mm - update
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-update', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'update', 
  'info:escidoc/names:aa:1.0:action:update-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-update');
 
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-update-2', 'info:escidoc/names:aa:1.0:resource:organizational-unit:parent-new', 
  'extractObjid:/organizational-unit/parents/parent/@href|/organizational-unit/parents/parent/@objid', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 12, true, '', 'escidoc:mm-oum-update');

        /**
         * OUM mm - updateMdRecords
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-update-mdrecords', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'updateMdRecords', 
  'info:escidoc/names:aa:1.0:action:update-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-update-mdrecords-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-update-mdrecords');
 
         /**
         * OUM mm - updateMdRecord
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-update-mdrecord', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'updateMdRecord', 
  'info:escidoc/names:aa:1.0:action:update-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-update-mdrecord-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-update-mdrecord');
 
         
        /**
         * OUM mm - updateParents
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-update-parent-ous', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'updateParents', 
  'info:escidoc/names:aa:1.0:action:update-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-update-parent-ous-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-update-parent-ous');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-update-parent-ous-2', 'info:escidoc/names:aa:1.0:resource:organizational-unit:parent-new', 
  'extractObjid:/parents/parent/@href|/parents/parent/@objid', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 12, true, '', 'escidoc:mm-oum-update-parent-ous');

        /**
         * OUM mm - open
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-open', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'open', 
  'info:escidoc/names:aa:1.0:action:open-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-open-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-open');

        /**
         * OUM mm - close
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-close', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'close', 
  'info:escidoc/names:aa:1.0:action:close-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-close-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-close');

        /**
         * OUM mm - retrieve resource
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-resource', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieveResource',
  'info:escidoc/names:aa:1.0:action:retrieve-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-resource', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0,
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve-resource');

        /**
         * OUM mm - retrieve resources
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-resources', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieveResources', 
  'info:escidoc/names:aa:1.0:action:retrieve-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-resources-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve-resources');


        /**
         * OUM mm - retrieve children
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-children', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieveChildObjects', 
  'info:escidoc/names:aa:1.0:action:retrieve-children-of-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-children-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve-children');
          

        /**
         * OUM mm - retrieve parents
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-parents', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieveParentObjects', 
  'info:escidoc/names:aa:1.0:action:retrieve-parents-of-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-parents-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve-parents');

        /**
         * OUM mm - retrieve successors
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-successors', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieveSuccessors', 
  'info:escidoc/names:aa:1.0:action:retrieve-organizational-unit', true, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-oum-retrieve-successors-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-oum-retrieve-successors');


        /**
         * OUM mm - retrieve organizational units
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-oum-retrieve-organizational-units', 'de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface', 'retrieveOrganizationalUnits', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', false, true,
  'de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException');

