/**
 * Ingest SCAPE - ingest intellectual entity
 */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-ingest-scape', 'de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface', 'ingestIntellectualEntity', 'info:escidoc/names:aa:1.0:action:ingest-scape',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');


INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-ingest-scape', 'info:escidoc/names:aa:1.0:resource:object-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, '', 'escidoc:mm-ingest-scape');
          
