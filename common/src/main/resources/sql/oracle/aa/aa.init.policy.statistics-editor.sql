DECLARE
  TMP_CLOB CLOB := NULL;
  SRC_CHUNK CLOB;
BEGIN
  DBMS_LOB.CREATETEMPORARY(TMP_CLOB, TRUE);
  DBMS_LOB.OPEN(TMP_CLOB, DBMS_LOB.LOB_READWRITE);
 
 SRC_CHUNK := '<Policy PolicyId="Statistics-editor-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
                                    info:escidoc/names:aa:1.0:action:create-aggregation-definition 
                                    info:escidoc/names:aa:1.0:action:create-report-definition 
                                    info:escidoc/names:aa:1.0:action:create-statistic-data 
                                    info:escidoc/names:aa:1.0:action:delete-aggregation-definition 
                                    info:escidoc/names:aa:1.0:action:delete-report-definition 
                                    info:escidoc/names:aa:1.0:action:retrieve-scope 
                                    info:escidoc/names:aa:1.0:action:retrieve-aggregation-definition 
                                    info:escidoc/names:aa:1.0:action:retrieve-report-definition 
                                    info:escidoc/names:aa:1.0:action:update-scope 
                                    info:escidoc/names:aa:1.0:action:update-report-definition 
                                    info:escidoc/names:aa:1.0:action:preprocess-statistics</AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Statistics-Editor-policy-rule-0" Effect="Permit">
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
  ('escidoc:statistics-editor-policy-1', 'escidoc:role-statistics-editor', TMP_CLOB);
END;
/

