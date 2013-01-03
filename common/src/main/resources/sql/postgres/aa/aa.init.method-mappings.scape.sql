/* method mappings for scape */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-intellectualentity-create', 'de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface', 'ingestIntellectualEntity', 
  'info:escidoc/names:aa:1.0:action:create-intellectualentity', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-intellectualentity-async-create', 'de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface', 'ingestIntellectualEntityAsync', 
  'info:escidoc/names:aa:1.0:action:create-intellectualentity-async', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

  INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-intellectualentity-retrieve', 'de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface', 'getIntellectualEntity', 
  'info:escidoc/names:aa:1.0:action:retrieve-intellectualentity', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-metadata-retrieve', 'de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface', 'getMetadata',
  'info:escidoc/names:aa:1.0:action:retrieve-metadata', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-intellectualentity-list-retrieve', 'de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface', 'getIntellectualEntitySet', 
  'info:escidoc/names:aa:1.0:action:retrieve-intellectualentity-list', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-intellectualentity-versions-retrieve', 'de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface', 'getIntellectualEntityVersionSet', 
  'info:escidoc/names:aa:1.0:action:retrieve-intellectualentity-versions', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
  
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-intellectualentity-search-retrieve', 'de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface', 'searchIntellectualEntities', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', false, true);

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-intellectualentity-lifecycle-retrieve', 'de.escidoc.core.om.service.interfaces.LifeCycleHandlerInterface', 'getLifecycleStatus', 
  'info:escidoc/names:aa:1.0:action:retrieve-intellectualentity-lifecycle', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-representation-retrieve', 'de.escidoc.core.om.service.interfaces.RepresentationHandlerInterface', 'getRepresentation', 
  'info:escidoc/names:aa:1.0:action:retrieve-representation', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-representation-search-retrieve', 'de.escidoc.core.om.service.interfaces.RepresentationHandlerInterface', 'searchRepresentations', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', false, true);
  
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-file-retrieve', 'de.escidoc.core.om.service.interfaces.FileHandlerInterface', 'getFile', 
  'info:escidoc/names:aa:1.0:action:retrieve-file', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-file-search-retrieve', 'de.escidoc.core.om.service.interfaces.FileHandlerInterface', 'searchFiles', 
  'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', false, true);

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-bitstream-retrieve', 'de.escidoc.core.om.service.interfaces.BitstreamInterface', 'getBitstream', 
  'info:escidoc/names:aa:1.0:action:retrieve-bitstream', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-intellectualentity-update', 'de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface', 'updateIntellectualEntity', 
  'info:escidoc/names:aa:1.0:action:update-intellectualentity', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
  
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-intellectualentity-metadata-update', 'de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface', 'updateMetadata', 
  'info:escidoc/names:aa:1.0:action:update-intellectualentity-metadata', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-representation-update', 'de.escidoc.core.om.service.interfaces.RepresentationHandlerInterface', 'updateRepresentation', 
  'info:escidoc/names:aa:1.0:action:update-representation', true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
  

  /* invocation mappings for scape */

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-intellectualentity-list-retrieve', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-intellectualentity-list-retrieve');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-metadata-retrieve', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-metadata-retrieve');
          
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-intellectualentity-versions-retrieve', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-intellectualentity-versions-retrieve');
          
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-intellectualentity-lifecycle-retrieve', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-intellectualentity-lifecycle-retrieve');
          
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-file-retrieve', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-file-retrieve');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-bitstream-retrieve', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-bitstream-retrieve');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-intellectualentity-create', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-intellectualentity-create');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-intellectualentity-async-create', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-intellectualentity-async-create');
          
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-intellectualentity-update', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-intellectualentity-update');
          
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-intellectualentity-metadata-update', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-intellectualentity-metadata-update');
          
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-representation-update', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-representation-update');
          
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-intellectualentity-retrieve', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-intellectualentity-retrieve');
