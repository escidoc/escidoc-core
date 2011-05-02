DECLARE
  TMP_CLOB CLOB := NULL;
  SRC_CHUNK CLOB;
BEGIN
  DBMS_LOB.CREATETEMPORARY(TMP_CLOB, TRUE);
  DBMS_LOB.OPEN(TMP_CLOB, DBMS_LOB.LOB_READWRITE);
 
 SRC_CHUNK := '<Policy PolicyId="ContentRelationManager-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Rule RuleId="ContentRelationManager-policy-rule-0" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:create-content-relation 
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
        </Rule>
        <Rule RuleId="ContentRelationManager-policy-rule-1" Effect="Permit">
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
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:content-relation:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
    </Policy>';
 
  DBMS_LOB.WRITEAPPEND(TMP_CLOB, LENGTH(SRC_CHUNK), SRC_CHUNK);
  DBMS_LOB.CLOSE(TMP_CLOB);
 
  INSERT INTO aa.escidoc_policies (id, role_id, xml) VALUES 
  ('escidoc:content-relation-manager-policy-1', 'escidoc:role-content-relation-manager', TMP_CLOB);
END;
/

