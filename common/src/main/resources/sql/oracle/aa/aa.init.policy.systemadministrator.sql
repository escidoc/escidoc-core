DECLARE
  TMP_CLOB CLOB := NULL;
  SRC_CHUNK CLOB;
BEGIN
  DBMS_LOB.CREATETEMPORARY(TMP_CLOB, TRUE);
  DBMS_LOB.OPEN(TMP_CLOB, DBMS_LOB.LOB_READWRITE);
 
 SRC_CHUNK := '<Policy PolicyId="System-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>
    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
              info:escidoc/names:aa:1.0:action:retrieve-method-mappings 
              info:escidoc/names:aa:1.0:action:ingest 
              info:escidoc/names:aa:1.0:action:check-user-privilege 
              info:escidoc/names:aa:1.0:action:create-role 
              info:escidoc/names:aa:1.0:action:delete-role 
              info:escidoc/names:aa:1.0:action:retrieve-role 
              info:escidoc/names:aa:1.0:action:update-role 
              info:escidoc/names:aa:1.0:action:evaluate 
              info:escidoc/names:aa:1.0:action:find-attribute 
              info:escidoc/names:aa:1.0:action:retrieve-roles 
              info:escidoc/names:aa:1.0:action:create-user-account 
              info:escidoc/names:aa:1.0:action:delete-user-account 
              info:escidoc/names:aa:1.0:action:retrieve-user-account 
              info:escidoc/names:aa:1.0:action:update-user-account 
              info:escidoc/names:aa:1.0:action:activate-user-account 
              info:escidoc/names:aa:1.0:action:deactivate-user-account 
              info:escidoc/names:aa:1.0:action:create-grant 
              info:escidoc/names:aa:1.0:action:retrieve-grant 
              info:escidoc/names:aa:1.0:action:retrieve-current-grants 
              info:escidoc/names:aa:1.0:action:revoke-grant 
              info:escidoc/names:aa:1.0:action:create-user-group 
              info:escidoc/names:aa:1.0:action:delete-user-group 
              info:escidoc/names:aa:1.0:action:retrieve-user-group 
              info:escidoc/names:aa:1.0:action:update-user-group 
              info:escidoc/names:aa:1.0:action:activate-user-group 
              info:escidoc/names:aa:1.0:action:deactivate-user-group 
              info:escidoc/names:aa:1.0:action:add-user-group-selectors 
              info:escidoc/names:aa:1.0:action:remove-user-group-selectors 
              info:escidoc/names:aa:1.0:action:create-user-group-grant 
              info:escidoc/names:aa:1.0:action:retrieve-user-group-grant 
              info:escidoc/names:aa:1.0:action:revoke-user-group-grant 
              info:escidoc/names:aa:1.0:action:logout 
              info:escidoc/names:aa:1.0:action:create-unsecured-actions 
              info:escidoc/names:aa:1.0:action:delete-unsecured-actions 
              info:escidoc/names:aa:1.0:action:retrieve-unsecured-actions 
              info:escidoc/names:aa:1.0:action:create-metadata-schema 
              info:escidoc/names:aa:1.0:action:delete-metadata-schema 
              info:escidoc/names:aa:1.0:action:retrieve-metadata-schema 
              info:escidoc/names:aa:1.0:action:update-metadata-schema 
              info:escidoc/names:aa:1.0:action:create-container 
              info:escidoc/names:aa:1.0:action:delete-container 
              info:escidoc/names:aa:1.0:action:update-container 
              info:escidoc/names:aa:1.0:action:retrieve-container 
              info:escidoc/names:aa:1.0:action:submit-container 
              info:escidoc/names:aa:1.0:action:release-container 
              info:escidoc/names:aa:1.0:action:revise-container 
              info:escidoc/names:aa:1.0:action:withdraw-container 
              info:escidoc/names:aa:1.0:action:container-move-to-context 
              info:escidoc/names:aa:1.0:action:add-members-to-container 
              info:escidoc/names:aa:1.0:action:remove-members-from-container 
              info:escidoc/names:aa:1.0:action:lock-container 
              info:escidoc/names:aa:1.0:action:unlock-container 
              info:escidoc/names:aa:1.0:action:create-content-model 
              info:escidoc/names:aa:1.0:action:delete-content-model 
              info:escidoc/names:aa:1.0:action:retrieve-content-model 
              info:escidoc/names:aa:1.0:action:update-content-model 
              info:escidoc/names:aa:1.0:action:create-context 
              info:escidoc/names:aa:1.0:action:delete-context ';
                        

        DBMS_LOB.WRITEAPPEND(TMP_CLOB, LENGTH(SRC_CHUNK), SRC_CHUNK);
        SRC_CHUNK := '              info:escidoc/names:aa:1.0:action:retrieve-context 
              info:escidoc/names:aa:1.0:action:update-context 
              info:escidoc/names:aa:1.0:action:close-context 
              info:escidoc/names:aa:1.0:action:open-context 
              info:escidoc/names:aa:1.0:action:create-item 
              info:escidoc/names:aa:1.0:action:delete-item 
              info:escidoc/names:aa:1.0:action:retrieve-item 
              info:escidoc/names:aa:1.0:action:update-item 
              info:escidoc/names:aa:1.0:action:submit-item 
              info:escidoc/names:aa:1.0:action:release-item 
              info:escidoc/names:aa:1.0:action:revise-item 
              info:escidoc/names:aa:1.0:action:withdraw-item 
              info:escidoc/names:aa:1.0:action:retrieve-content 
              info:escidoc/names:aa:1.0:action:item-move-to-context 
              info:escidoc/names:aa:1.0:action:lock-item 
              info:escidoc/names:aa:1.0:action:unlock-item 
              info:escidoc/names:aa:1.0:action:create-toc 
              info:escidoc/names:aa:1.0:action:delete-toc 
              info:escidoc/names:aa:1.0:action:retrieve-toc 
              info:escidoc/names:aa:1.0:action:update-toc 
              info:escidoc/names:aa:1.0:action:submit-toc 
              info:escidoc/names:aa:1.0:action:release-toc 
              info:escidoc/names:aa:1.0:action:revise-toc 
              info:escidoc/names:aa:1.0:action:withdraw-toc 
              info:escidoc/names:aa:1.0:action:create-content-relation 
              info:escidoc/names:aa:1.0:action:delete-content-relation 
              info:escidoc/names:aa:1.0:action:retrieve-content-relation 
              info:escidoc/names:aa:1.0:action:update-content-relation 
              info:escidoc/names:aa:1.0:action:submit-content-relation 
              info:escidoc/names:aa:1.0:action:release-content-relation 
              info:escidoc/names:aa:1.0:action:revise-content-relation 
              info:escidoc/names:aa:1.0:action:withdraw-content-relation 
              info:escidoc/names:aa:1.0:action:lock-content-relation 
              info:escidoc/names:aa:1.0:action:unlock-content-relation 
              info:escidoc/names:aa:1.0:action:query-semantic-store 
              info:escidoc/names:aa:1.0:action:create-xml-schema 
              info:escidoc/names:aa:1.0:action:delete-xml-schema 
              info:escidoc/names:aa:1.0:action:retrieve-xml-schema 
              info:escidoc/names:aa:1.0:action:update-xml-schema 
              info:escidoc/names:aa:1.0:action:create-organizational-unit 
              info:escidoc/names:aa:1.0:action:delete-organizational-unit 
              info:escidoc/names:aa:1.0:action:retrieve-organizational-unit 
              info:escidoc/names:aa:1.0:action:retrieve-children-of-organizational-unit 
              info:escidoc/names:aa:1.0:action:retrieve-parents-of-organizational-unit 
              info:escidoc/names:aa:1.0:action:update-organizational-unit 
              info:escidoc/names:aa:1.0:action:open-organizational-unit 
              info:escidoc/names:aa:1.0:action:close-organizational-unit 
              info:escidoc/names:aa:1.0:action:fmdh-export 
              info:escidoc/names:aa:1.0:action:fadh-get-datastream-dissemination 
              info:escidoc/names:aa:1.0:action:fddh-get-fedora-description 
              info:escidoc/names:aa:1.0:action:create-aggregation-definition 
              info:escidoc/names:aa:1.0:action:delete-aggregation-definition 
              info:escidoc/names:aa:1.0:action:retrieve-aggregation-definition 
              info:escidoc/names:aa:1.0:action:update-aggregation-definition 
              info:escidoc/names:aa:1.0:action:create-report-definition 
              info:escidoc/names:aa:1.0:action:delete-report-definition 
              info:escidoc/names:aa:1.0:action:retrieve-report-definition 
              info:escidoc/names:aa:1.0:action:update-report-definition 
              info:escidoc/names:aa:1.0:action:retrieve-report 
              info:escidoc/names:aa:1.0:action:create-statistic-data 
              info:escidoc/names:aa:1.0:action:create-scope 
              info:escidoc/names:aa:1.0:action:delete-scope 
              info:escidoc/names:aa:1.0:action:retrieve-scope 
              info:escidoc/names:aa:1.0:action:update-scope 
              info:escidoc/names:aa:1.0:action:preprocess-statistics 
              info:escidoc/names:aa:1.0:action:create-staging-file 
              info:escidoc/names:aa:1.0:action:retrieve-staging-file 
              info:escidoc/names:aa:1.0:action:extract-metadata 
              info:escidoc/names:aa:1.0:action:delete-objects 
              info:escidoc/names:aa:1.0:action:get-purge-status 
              info:escidoc/names:aa:1.0:action:get-reindex-status 
              info:escidoc/names:aa:1.0:action:decrease-reindex-status 
              info:escidoc/names:aa:1.0:action:reindex 
              info:escidoc/names:aa:1.0:action:get-index-configuration 
              info:escidoc/names:aa:1.0:action:load-examples 
    		  info:escidoc/names:aa:1.0:action:create-set-definition 
    		  info:escidoc/names:aa:1.0:action:update-set-definition 
			  info:escidoc/names:aa:1.0:action:delete-set-definition
              info:escidoc/names:aa:1.0:action:fedora-deviation-get-datastream-dissimination 
              info:escidoc/names:aa:1.0:action:fedora-deviation-export 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="System-Administrator-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>
      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>';
 
  DBMS_LOB.WRITEAPPEND(TMP_CLOB, LENGTH(SRC_CHUNK), SRC_CHUNK);
  DBMS_LOB.CLOSE(TMP_CLOB);
 
  INSERT INTO aa.escidoc_policies (id, role_id, xml) VALUES 
  ('escidoc:policy-system-administrator', 'escidoc:role-system-administrator', TMP_CLOB);
END;
/

