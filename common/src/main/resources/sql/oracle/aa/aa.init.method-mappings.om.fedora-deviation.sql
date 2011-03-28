   /**
    * Fedora Deviation method mappings
    */
    
        /**
         * Fedora Deviation mm - getDatastreamDissimination
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-get-datastream-dissimination', 'de.escidoc.core.om.service.interfaces.FedoraRestDeviationHandlerInterface', 'getDatastreamDissemination', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-get-datastream-dissimination', 1, 1);
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-get-datastream-dissimination-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-get-datastream-dissimination');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-get-datastream-dissimination-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-get-datastream-dissimination');
  
        /**
         * Fedora Deviation mm - getFedoraDescription
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-get-fedora-description', 'de.escidoc.core.om.service.interfaces.FedoraDescribeDeviationHandlerInterface', 'getFedoraDescription', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-get-fedora-description', 1, 1);
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-get-fedora-description-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-get-fedora-description');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-get-fedora-description-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-get-fedora-description');
  
        /**
         * Fedora Deviation mm - export
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-export', 'de.escidoc.core.om.service.interfaces.FedoraRestDeviationHandlerInterface', 'export', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-export', 1, 1);
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-export-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-export');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-export-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-export');
  
        /**
         * Fedora Deviation mm - cache
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-cache', 'de.escidoc.core.om.service.interfaces.FedoraRestDeviationHandlerInterface', 'cache', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-cache', 1, 1);
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-cache-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-cache');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-cache-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-cache');
  
        /**
         * Fedora Deviation mm - removeFromCache
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-remove-from-cache', 'de.escidoc.core.om.service.interfaces.FedoraRestDeviationHandlerInterface', 'removeFromCache', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-remove-from-cache', 1, 1);
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-remove-from-cache-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-remove-from-cache');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-remove-from-cache-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-remove-from-cache');
  
        /**
         * Fedora Deviation mm - replaceInCache
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-replace-in-cache', 'de.escidoc.core.om.service.interfaces.FedoraRestDeviationHandlerInterface', 'replaceInCache', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-replace-in-cache', 1, 1);
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-replace-in-cache-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-replace-in-cache');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-fedora-deviation-replace-in-cache-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'fedora-deviation', 'escidoc:mm-fedora-deviation-replace-in-cache');
  
