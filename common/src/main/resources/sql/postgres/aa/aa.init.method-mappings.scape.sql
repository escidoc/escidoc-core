/*Ingest SCAPE - ingest intellectual entity */
 
/* method mappings for scape */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-retrieve-intellectualentity', 'de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface', 'getIntellectualEntity', 'info:escidoc/names:aa:1.0:action:retrieve-intellectualentity',
  true, true, 'de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException');



/* invocation mappings for scape */
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc:im-retrieve-intellectualentity', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-retrieve-intellectualentity');


