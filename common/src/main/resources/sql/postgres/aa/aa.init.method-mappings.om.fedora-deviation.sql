   /**
    * Fedora Deviation method mappings
    */
    
        /**
         * Fedora Deviation mm - getDatastreamDissimination
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-get-datastream-dissimination', 'de.escidoc.core.om.service.interfaces.FedoraAccessDeviationHandlerInterface', 'getDatastreamDissemination', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-get-datastream-dissimination', true, true);
  
        /**
         * Fedora Deviation mm - getFedoraDescription
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-get-fedora-description', 'de.escidoc.core.om.service.interfaces.FedoraDescribeDeviationHandlerInterface', 'getFedoraDescription', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-get-fedora-description', true, true);
  
        /**
         * Fedora Deviation mm - export
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-export', 'de.escidoc.core.om.service.interfaces.FedoraManagementDeviationHandlerInterface', 'export', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-export', true, true);
  
        /**
         * Fedora Deviation mm - cache
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-cache', 'de.escidoc.core.om.service.interfaces.FedoraManagementDeviationHandlerInterface', 'cache', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-cache', true, true);
  
        /**
         * Fedora Deviation mm - removeFromCache
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-remove-from-cache', 'de.escidoc.core.om.service.interfaces.FedoraManagementDeviationHandlerInterface', 'removeFromCache', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-remove-from-cache', true, true);
  
        /**
         * Fedora Deviation mm - replaceInCache
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-fedora-deviation-replace-in-cache', 'de.escidoc.core.om.service.interfaces.FedoraManagementDeviationHandlerInterface', 'replaceInCache', 
  'info:escidoc/names:aa:1.0:action:fedora-deviation-replace-in-cache', true, true);
  
