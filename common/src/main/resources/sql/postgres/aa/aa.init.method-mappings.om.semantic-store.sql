   /**
    * Semantic Store method mappings
    */
    
        /**
         * Semantic Store mm - spo
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-semantic-store-spo', 'de.escidoc.core.om.service.interfaces.SemanticStoreHandlerInterface', 'spo', 
  'info:escidoc/names:aa:1.0:action:query-semantic-store', true, true);
