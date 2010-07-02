    /*
     * ST method mappings
     */
        /**
         * StagingFile mm - create
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-st-create', 'de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-staging-file', true, true);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-st-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, false, 'staging-file', 'escidoc:mm-st-create');

          
        /**
         * StagingFile mm - retrieve
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-st-retrieve', 'de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface', 'retrieve', 
  'info:escidoc/names:aa:1.0:action:retrieve-staging-file', true, true, 
  'de.escidoc.core.common.exceptions.application.notfound.StagingFileNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-st-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, false, '', 'escidoc:mm-st-retrieve');

        