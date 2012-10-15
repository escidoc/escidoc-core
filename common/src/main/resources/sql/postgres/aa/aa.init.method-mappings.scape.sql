/*Ingest SCAPE - ingest intellectual entity */
 
/* method mappings for scape */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-ingest-intellectualentity', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:ingest-intellectualentity',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-retrieve-intellectualentity', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:retrieve-intellectualentity',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-post-intellectualentity', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:post-intellectualentity',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-put-intellectualentity', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:put-intellectualentity',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-retrieve-intellectualentity-set', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:retrieve-intellectualentity-set',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-retrieve-intellectualentity-version-set', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:retrieve-intellectualentity-version-set',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-retrieve-intellectualentity-lifecycle', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:retrieve-intellectualentity-lifecycle',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-retrieve-file', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:retrieve-file',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-retrieve-bitstream', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:retrieve-bitstream',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-retrieve-representation', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:retrieve-representation',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-put-representation', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:put-representation',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-search-intellectualentity', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:search-intellectualentity',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-search-file', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:search-file',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-search-representation', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:search-representation',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');



/* invocation mappings for scape */
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-ingest-intellectualentity', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-ingest-intellectualentity');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-retrieve-intellectualentity', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-retrieve-intellectualentity');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-post-intellectualentity', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-post-intellectualentity');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-put-intellectualentity', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-put-intellectualentity');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-retrieve-intellectualentity-set', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-retrieve-intellectualentity-set');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-retrieve-intellectualentity-version-set', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-retrieve-intellectualentity-version-set');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-retrieve-intellectualentity-lifecycle', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-retrieve-intellectualentity-lifecycle');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-retrieve-file', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-retrieve-file');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-retrieve-bitstream', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-retrieve-bitstream');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-retrieve-representation', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-retrieve-representation');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-put-representation', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-put-representation');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-search-intellectualentity', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-search-intellectualentity');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-search-file', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-search-file');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-search-representation', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-search-representation');
