/**
 * Actions
 */
    /**
     * Common actions.
     */
        /**
         * Dummy action for the filtered retrieval of objects.
         */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-objects-filtered', 'info:escidoc/names:aa:1.0:action:retrieve-objects-filtered');
        
        /**
         * Dummy action for testing attribute fetching.
         */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-find-attribute', 'info:escidoc/names:aa:1.0:action:find-attribute');
         
     /**
     * Ingest (external) action.
     */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-ingest', 'info:escidoc/names:aa:1.0:action:ingest');
        


    /**
     * AA (external) action.
     */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-logout', 'info:escidoc/names:aa:1.0:action:logout');
    
    -- AA PDP
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-evaluate', 'info:escidoc/names:aa:1.0:action:evaluate');
    
    -- AA Role
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-role', 'info:escidoc/names:aa:1.0:action:create-role');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-role', 'info:escidoc/names:aa:1.0:action:delete-role');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-role', 'info:escidoc/names:aa:1.0:action:retrieve-role');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-role', 'info:escidoc/names:aa:1.0:action:update-role');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-roles', 'info:escidoc/names:aa:1.0:action:retrieve-roles');

    -- AA user account
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-user-account', 'info:escidoc/names:aa:1.0:action:create-user-account');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-user-account', 'info:escidoc/names:aa:1.0:action:delete-user-account');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-user-account', 'info:escidoc/names:aa:1.0:action:retrieve-user-account');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-current-user-account', 'info:escidoc/names:aa:1.0:action:retrieve-current-user-account');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-user-account', 'info:escidoc/names:aa:1.0:action:update-user-account');
    
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-activate-user-account', 'info:escidoc/names:aa:1.0:action:activate-user-account');
    
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-deactivate-user-account', 'info:escidoc/names:aa:1.0:action:deactivate-user-account');

    -- AA grant
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-grant', 'info:escidoc/names:aa:1.0:action:create-grant');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-grant', 'info:escidoc/names:aa:1.0:action:retrieve-grant');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-revoke-grant', 'info:escidoc/names:aa:1.0:action:revoke-grant');

    -- AA user group
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-user-group', 'info:escidoc/names:aa:1.0:action:create-user-group');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-user-group', 'info:escidoc/names:aa:1.0:action:delete-user-group');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-user-group', 'info:escidoc/names:aa:1.0:action:retrieve-user-group');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-user-group', 'info:escidoc/names:aa:1.0:action:update-user-group');
    
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-activate-user-group', 'info:escidoc/names:aa:1.0:action:activate-user-group');
    
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-deactivate-user-group', 'info:escidoc/names:aa:1.0:action:deactivate-user-group');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-add-user-group-selectors', 'info:escidoc/names:aa:1.0:action:add-user-group-selectors');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-remove-user-group-selectors', 'info:escidoc/names:aa:1.0:action:remove-user-group-selectors');

    -- AA group grant
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-user-group-grant', 'info:escidoc/names:aa:1.0:action:retrieve-user-group-grant');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-user-group-grant', 'info:escidoc/names:aa:1.0:action:create-user-group-grant');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-revoke-user-group-grant', 'info:escidoc/names:aa:1.0:action:revoke-user-group-grant');

    -- AA unsecured actions
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-unsecured-actions', 'info:escidoc/names:aa:1.0:action:create-unsecured-actions');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-unsecured-actions', 'info:escidoc/names:aa:1.0:action:delete-unsecured-actions');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-unsecured-actions', 'info:escidoc/names:aa:1.0:action:retrieve-unsecured-actions');

    
    /**
      * AA (internal) actions.
      */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-method-mappings', 'info:escidoc/names:aa:1.0:action:retrieve-method-mappings');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-check-user-privilege', 'info:escidoc/names:aa:1.0:action:check-user-privilege');


    /**
      * ADM (internal) actions.
      */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-objects', 'info:escidoc/names:aa:1.0:action:delete-objects');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-get-purge-status', 'info:escidoc/names:aa:1.0:action:get-purge-status');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-get-reindex-status', 'info:escidoc/names:aa:1.0:action:get-reindex-status');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-decrease-reindex-status', 'info:escidoc/names:aa:1.0:action:decrease-reindex-status');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-reindex', 'info:escidoc/names:aa:1.0:action:reindex');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-get-repository-info', 'info:escidoc/names:aa:1.0:action:get-repository-info');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-get-index-configuration', 'info:escidoc/names:aa:1.0:action:get-index-configuration');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-load-examples', 'info:escidoc/names:aa:1.0:action:load-examples');

    /**
     * CMM actions.
     */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-content-model', 'info:escidoc/names:aa:1.0:action:create-content-model');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-content-model', 'info:escidoc/names:aa:1.0:action:delete-content-model');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-content-model', 'info:escidoc/names:aa:1.0:action:retrieve-content-model');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-content-model', 'info:escidoc/names:aa:1.0:action:update-content-model');

    /**
     * MM actions.
     */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-metadata-schema', 'info:escidoc/names:aa:1.0:action:create-metadata-schema');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-metadata-schema', 'info:escidoc/names:aa:1.0:action:delete-metadata-schema');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-metadata-schema', 'info:escidoc/names:aa:1.0:action:retrieve-metadata-schema');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-metadata-schema', 'info:escidoc/names:aa:1.0:action:update-metadata-schema');

    /**
     * OM actions.
     */
    -- OM - container
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-container', 'info:escidoc/names:aa:1.0:action:create-container');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-container', 'info:escidoc/names:aa:1.0:action:delete-container');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-container', 'info:escidoc/names:aa:1.0:action:update-container');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-container', 'info:escidoc/names:aa:1.0:action:retrieve-container');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-submit-container', 'info:escidoc/names:aa:1.0:action:submit-container');
    
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-release-container', 'info:escidoc/names:aa:1.0:action:release-container');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-revise-container', 'info:escidoc/names:aa:1.0:action:revise-container');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-withdraw-container', 'info:escidoc/names:aa:1.0:action:withdraw-container');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-container-move-to-context', 'info:escidoc/names:aa:1.0:action:container-move-to-context');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-add-members-to-container', 'info:escidoc/names:aa:1.0:action:add-members-to-container');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-remove-members-from-container', 'info:escidoc/names:aa:1.0:action:remove-members-from-container');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-lock-container', 'info:escidoc/names:aa:1.0:action:lock-container');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-unlock-container', 'info:escidoc/names:aa:1.0:action:unlock-container');

    -- OM - context
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-context', 'info:escidoc/names:aa:1.0:action:create-context');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-context', 'info:escidoc/names:aa:1.0:action:delete-context');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-context', 'info:escidoc/names:aa:1.0:action:retrieve-context');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-context', 'info:escidoc/names:aa:1.0:action:update-context');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-close-context', 'info:escidoc/names:aa:1.0:action:close-context');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-open-context', 'info:escidoc/names:aa:1.0:action:open-context');

    -- OM - item
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-item', 'info:escidoc/names:aa:1.0:action:create-item');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-item', 'info:escidoc/names:aa:1.0:action:delete-item');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-item', 'info:escidoc/names:aa:1.0:action:retrieve-item');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-item', 'info:escidoc/names:aa:1.0:action:update-item');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-submit-item', 'info:escidoc/names:aa:1.0:action:submit-item');
    
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-release-item', 'info:escidoc/names:aa:1.0:action:release-item');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-revise-item', 'info:escidoc/names:aa:1.0:action:revise-item');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-withdraw-item', 'info:escidoc/names:aa:1.0:action:withdraw-item');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-content', 'info:escidoc/names:aa:1.0:action:retrieve-content');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-item-move-to-context', 'info:escidoc/names:aa:1.0:action:item-move-to-context');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-lock-item', 'info:escidoc/names:aa:1.0:action:lock-item');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-unlock-item', 'info:escidoc/names:aa:1.0:action:unlock-item');

        -- OM - content relations
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-content-relation', 'info:escidoc/names:aa:1.0:action:create-content-relation');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-content-relation', 'info:escidoc/names:aa:1.0:action:delete-content-relation');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-content-relation', 'info:escidoc/names:aa:1.0:action:retrieve-content-relation');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-content-relation', 'info:escidoc/names:aa:1.0:action:update-content-relation');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-submit-content-relation', 'info:escidoc/names:aa:1.0:action:submit-content-relation');
    
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-release-content-relation', 'info:escidoc/names:aa:1.0:action:release-content-relation');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-revise-content-relation', 'info:escidoc/names:aa:1.0:action:revise-content-relation');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-withdraw-content-relation', 'info:escidoc/names:aa:1.0:action:withdraw-content-relation');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-lock-content-relation', 'info:escidoc/names:aa:1.0:action:lock-content-relation');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-unlock-content-relation', 'info:escidoc/names:aa:1.0:action:unlock-content-relation');

	-- OM semantic-store
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:query-semantic-store', 'info:escidoc/names:aa:1.0:action:query-semantic-store');

	-- OM xml-schema
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-xml-schema', 'info:escidoc/names:aa:1.0:action:create-xml-schema');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-xml-schema', 'info:escidoc/names:aa:1.0:action:delete-xml-schema');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-xml-schema', 'info:escidoc/names:aa:1.0:action:retrieve-xml-schema');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-xml-schema', 'info:escidoc/names:aa:1.0:action:update-xml-schema');

    -- OM fedora deviation
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-fedora-deviation-get-datastream-dissimination', 'info:escidoc/names:aa:1.0:action:fedora-deviation-get-datastream-dissimination');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-fedora-deviation-export', 'info:escidoc/names:aa:1.0:action:fedora-deviation-export');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-fedora-deviation-get-fedora-description', 'info:escidoc/names:aa:1.0:action:fedora-deviation-get-fedora-description');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-fedora-deviation-cache', 'info:escidoc/names:aa:1.0:action:fedora-deviation-cache');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-fedora-deviation-remove-from-cache', 'info:escidoc/names:aa:1.0:action:fedora-deviation-remove-from-cache');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-fedora-deviation-replace-in-cache', 'info:escidoc/names:aa:1.0:action:fedora-deviation-replace-in-cache');


    /**
     * OAI actions
     */
    -- OAI set-definition
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:create-set-definition', 'info:escidoc/names:aa:1.0:action:create-set-definition');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:update-set-definition', 'info:escidoc/names:aa:1.0:action:update-set-definition');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:retrieve-set-definition', 'info:escidoc/names:aa:1.0:action:retrieve-set-definition');
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:delete-set-definition', 'info:escidoc/names:aa:1.0:action:delete-set-definition');

    
    
    /**
     * OUM actions.
     */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-organizational-unit', 'info:escidoc/names:aa:1.0:action:create-organizational-unit');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-organizational-unit', 'info:escidoc/names:aa:1.0:action:delete-organizational-unit');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-organizational-unit', 'info:escidoc/names:aa:1.0:action:retrieve-organizational-unit');
    
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-children-of-organizational-unit', 'info:escidoc/names:aa:1.0:action:retrieve-children-of-organizational-unit');
    
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-parents-of-organizational-unit', 'info:escidoc/names:aa:1.0:action:retrieve-parents-of-organizational-unit');        

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-organizational-unit', 'info:escidoc/names:aa:1.0:action:update-organizational-unit');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-close-organizational-unit', 'info:escidoc/names:aa:1.0:action:close-organizational-unit');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-open-organizational-unit', 'info:escidoc/names:aa:1.0:action:open-organizational-unit');

    /**
     * SM actions.
     */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-aggregation-definition', 'info:escidoc/names:aa:1.0:action:create-aggregation-definition');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-aggregation-definition', 'info:escidoc/names:aa:1.0:action:delete-aggregation-definition');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-aggregation-definition', 'info:escidoc/names:aa:1.0:action:retrieve-aggregation-definition');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-aggregation-definition', 'info:escidoc/names:aa:1.0:action:update-aggregation-definition');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-report-definition', 'info:escidoc/names:aa:1.0:action:create-report-definition');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-report-definition', 'info:escidoc/names:aa:1.0:action:delete-report-definition');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-report-definition', 'info:escidoc/names:aa:1.0:action:retrieve-report-definition');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-report-definition', 'info:escidoc/names:aa:1.0:action:update-report-definition');
   
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-report', 'info:escidoc/names:aa:1.0:action:retrieve-report');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-statistic-data', 'info:escidoc/names:aa:1.0:action:create-statistic-data');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-scope', 'info:escidoc/names:aa:1.0:action:create-scope');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-delete-scope', 'info:escidoc/names:aa:1.0:action:delete-scope');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-scope', 'info:escidoc/names:aa:1.0:action:retrieve-scope');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-update-scope', 'info:escidoc/names:aa:1.0:action:update-scope');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-preprocess-statistics', 'info:escidoc/names:aa:1.0:action:preprocess-statistics');

    /**
     * ST actions.
     */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-create-staging-file', 'info:escidoc/names:aa:1.0:action:create-staging-file');

INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-retrieve-staging-file', 'info:escidoc/names:aa:1.0:action:retrieve-staging-file');
    
     /**
     * TME actions.
     */
INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:action-extract-metadata', 'info:escidoc/names:aa:1.0:action:extract-metadata');


INSERT INTO aa.actions (id, name) VALUES
    ('escidoc:mm-aa-retrieve-permission-filter-query', 'info:escidoc/names:aa:1.0:action:retrieve-permission-filter-query');


