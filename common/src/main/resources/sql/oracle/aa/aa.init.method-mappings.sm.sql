    /*
     * SM method mappings
     */
        /**
         * AggregationDefinitionHandler mm - create
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-aggregation-definition-create', 'de.escidoc.core.sm.service.interfaces.AggregationDefinitionHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-aggregation-definition', 1, 1);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-aggregation-definition-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'aggregation-definition', 'escidoc:mm-aggregation-definition-create');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-aggregation-definition-create-2', 'info:escidoc/names:aa:1.0:resource:aggregation-definition:scope-new', 'extractObjid:/aggregation-definition/scope/@objid|/aggregation-definition/scope/@href', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 2, 0, '', 'escidoc:mm-aggregation-definition-create');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-aggregation-definition-create-3', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'aggregation-definition', 'escidoc:mm-aggregation-definition-create');

        /**
         * AggregationDefinitionHandler mm - delete
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-aggregation-definition-delete', 'de.escidoc.core.sm.service.interfaces.AggregationDefinitionHandlerInterface', 'delete', 'info:escidoc/names:aa:1.0:action:delete-aggregation-definition', 1, 1, 'de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-aggregation-definition-delete-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-aggregation-definition-delete');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-aggregation-definition-delete-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'aggregation-definition', 'escidoc:mm-aggregation-definition-delete');

        /**
         * AggregationDefinitionHandler mm - retrieve
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-aggregation-definition-retrieve', 'de.escidoc.core.sm.service.interfaces.AggregationDefinitionHandlerInterface', 'retrieve', 'info:escidoc/names:aa:1.0:action:retrieve-aggregation-definition', 1, 1, 'de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-aggregation-definition-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-aggregation-definition-retrieve');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-aggregation-definition-retrieve-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'aggregation-definition', 'escidoc:mm-aggregation-definition-retrieve');


INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-aggregation-definition-retrieve-list', 'de.escidoc.core.sm.service.interfaces.AggregationDefinitionHandlerInterface', 'retrieveAggregationDefinitions', 'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', 0, 1);

        /**
         * ReportDefinitionHandler mm - create
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-report-definition-create', 'de.escidoc.core.sm.service.interfaces.ReportDefinitionHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-report-definition', 1, 1);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-definition-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'report-definition', 'escidoc:mm-report-definition-create');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-definition-create-2', 'info:escidoc/names:aa:1.0:resource:report-definition:scope-new', 'extractObjid:/report-definition/scope/@objid|/report-definition/scope/@href', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 2, 0, '', 'escidoc:mm-report-definition-create');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-definition-create-3', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'report-definition', 'escidoc:mm-report-definition-create');

        /**
         * ReportDefinitionHandler mm - delete
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-report-definition-delete', 'de.escidoc.core.sm.service.interfaces.ReportDefinitionHandlerInterface', 'delete', 'info:escidoc/names:aa:1.0:action:delete-report-definition', 1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-definition-delete-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-report-definition-delete');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-definition-delete-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'report-definition', 'escidoc:mm-report-definition-delete');

        /**
         * ReportDefinitionHandler mm - retrieve
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-report-definition-retrieve', 'de.escidoc.core.sm.service.interfaces.ReportDefinitionHandlerInterface', 'retrieve', 'info:escidoc/names:aa:1.0:action:retrieve-report-definition', 1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-definition-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-report-definition-retrieve');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-definition-retrieve-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'report-definition', 'escidoc:mm-report-definition-retrieve');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-report-definition-retrieve-list', 'de.escidoc.core.sm.service.interfaces.ReportDefinitionHandlerInterface', 'retrieveReportDefinitions', 'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', 0, 1);

        /**
         * ReportDefinitionHandler mm - update
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-report-definition-update', 'de.escidoc.core.sm.service.interfaces.ReportDefinitionHandlerInterface', 'update', 'info:escidoc/names:aa:1.0:action:update-report-definition', 1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-definition-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-report-definition-update');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-definition-update-2', 'info:escidoc/names:aa:1.0:resource:report-definition:scope-new', 'extractObjid:/report-definition/scope/@objid|/report-definition/scope/@href', 1, 
          'http://www.w3.org/2001/XMLSchema#string', 2, 0, '', 'escidoc:mm-report-definition-update');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-definition-update-3', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'report-definition', 'escidoc:mm-report-definition-update');

          
        /**
         * ReportHandler mm - retrieve
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-report-retrieve', 'de.escidoc.core.sm.service.interfaces.ReportHandlerInterface', 'retrieve', 'info:escidoc/names:aa:1.0:action:retrieve-report', 1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', 'extractObjid:report-parameters/report-definition/@objid|report-parameters/report-definition/@href', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 2, 0, '', 'escidoc:mm-report-retrieve');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-report-retrieve-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'report', 'escidoc:mm-report-retrieve');
          
        /**
         * StatisticDataHandler mm - create
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-statistic-data-create', 'de.escidoc.core.sm.service.interfaces.StatisticDataHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-statistic-data', 1, 1);


INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-statistic-data-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'statistic-data', 'escidoc:mm-statistic-data-create');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-statistic-data-create-2', 'info:escidoc/names:aa:1.0:resource:statistic-data:scope-new', 'extractObjid:/statistic-record/scope/@objid|/statistic-record/scope/@href', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 2, 0, '', 'escidoc:mm-statistic-data-create');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-statistic-data-create-3', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'statistic-data', 'escidoc:mm-statistic-data-create');

        /**
         * ScopeHandler mm - create
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-scope-create', 'de.escidoc.core.sm.service.interfaces.ScopeHandlerInterface', 'create', 'info:escidoc/names:aa:1.0:action:create-scope', 1, 1);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-scope-create-1', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'scope', 'escidoc:mm-scope-create');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-scope-create-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'scope', 'escidoc:mm-scope-create');

        /**
         * ScopeHandler mm - delete
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-scope-delete', 'de.escidoc.core.sm.service.interfaces.ScopeHandlerInterface', 'delete', 'info:escidoc/names:aa:1.0:action:delete-scope', 1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-scope-delete-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-scope-delete');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-scope-delete-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'scope', 'escidoc:mm-scope-delete');

        /**
         * ScopeHandler mm - retrieve
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-scope-retrieve', 'de.escidoc.core.sm.service.interfaces.ScopeHandlerInterface', 'retrieve', 'info:escidoc/names:aa:1.0:action:retrieve-scope', 1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-scope-retrieve-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-scope-retrieve');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-scope-retrieve-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'scope', 'escidoc:mm-scope-retrieve');

INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-scope-retrieve-list', 'de.escidoc.core.sm.service.interfaces.ScopeHandlerInterface', 'retrieveScopes', 'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered', 0, 1);

        /**
         * ScopeHandler mm - update
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource, resource_not_found_exception)
  VALUES ('escidoc:mm-scope-update', 'de.escidoc.core.sm.service.interfaces.ScopeHandlerInterface', 'update', 'info:escidoc/names:aa:1.0:action:update-scope', 1, 1, 'de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException');

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-scope-update-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-scope-update');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-scope-update-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'scope', 'escidoc:mm-scope-update');

        /**
         * PreprocessingHandler mm - preprocess
         */
INSERT INTO aa.method_mappings (id, class_name, method_name, action_name, exec_before, single_resource)
  VALUES ('escidoc:mm-preprocessing-preprocess', 'de.escidoc.core.sm.service.interfaces.PreprocessingHandlerInterface', 'preprocess', 'info:escidoc/names:aa:1.0:action:preprocess-statistics', 1, 1);

INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-preprocessing-preprocess-1', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 0, 0, '', 'escidoc:mm-preprocessing-preprocess');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-preprocessing-preprocess-2', 'info:escidoc/names:aa:1.0:resource:object-type', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'aggregation-definition', 'escidoc:mm-preprocessing-preprocess');
INSERT INTO aa.invocation_mappings (id, attribute_id, path, position, attribute_type, mapping_type, multi_value, value, method_mapping)
  VALUES ('escidoc-im-preprocessing-preprocess-3', 'info:escidoc/names:aa:1.0:resource:object-type-new', '', 0, 
          'http://www.w3.org/2001/XMLSchema#string', 3, 0, 'aggregation-definition', 'escidoc:mm-preprocessing-preprocess');


          