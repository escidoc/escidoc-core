DECLARE
  TMP_CLOB CLOB := NULL;
  SRC_CHUNK CLOB;
BEGIN
  DBMS_LOB.CREATETEMPORARY(TMP_CLOB, TRUE);
  DBMS_LOB.OPEN(TMP_CLOB, DBMS_LOB.LOB_READWRITE);
 
 SRC_CHUNK := '<Policy PolicyId="ContentRelationModifier-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
                        info:escidoc/names:aa:1.0:action:delete-content-relation 
                        info:escidoc/names:aa:1.0:action:retrieve-content-relation 
                        info:escidoc/names:aa:1.0:action:update-content-relation 
                        info:escidoc/names:aa:1.0:action:submit-content-relation 
                        info:escidoc/names:aa:1.0:action:release-content-relation 
                        info:escidoc/names:aa:1.0:action:revise-content-relation 
                        info:escidoc/names:aa:1.0:action:withdraw-content-relation 
                        info:escidoc/names:aa:1.0:action:lock-content-relation 
                        info:escidoc/names:aa:1.0:action:unlock-content-relation 
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Rule RuleId="ContentRelationModifier-policy-rule-1" Effect="Permit">
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
  ('escidoc:content-relation-modifier-policy-1', 'escidoc:role-content-relation-modifier', TMP_CLOB);
END;
/

