DECLARE
  TMP_CLOB CLOB := NULL;
  SRC_CHUNK CLOB;
BEGIN
  DBMS_LOB.CREATETEMPORARY(TMP_CLOB, TRUE);
  DBMS_LOB.OPEN(TMP_CLOB, DBMS_LOB.LOB_READWRITE);
 
 SRC_CHUNK := '<Policy PolicyId="OU-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
            info:escidoc/names:aa:1.0:action:create-organizational-unit 
            info:escidoc/names:aa:1.0:action:delete-organizational-unit 
            info:escidoc/names:aa:1.0:action:retrieve-organizational-unit 
            info:escidoc/names:aa:1.0:action:retrieve-children-of-organizational-unit 
            info:escidoc/names:aa:1.0:action:update-organizational-unit 
            info:escidoc/names:aa:1.0:action:close-organizational-unit 
            info:escidoc/names:aa:1.0:action:open-organizational-unit 
            info:escidoc/names:aa:1.0:action:create-grant 
            info:escidoc/names:aa:1.0:action:revoke-grant 
            info:escidoc/names:aa:1.0:action:create-user-group-grant 
            info:escidoc/names:aa:1.0:action:revoke-user-group-grant 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="OU-Administrator-policy-rule-1" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:create-organizational-unit 
                    info:escidoc/names:aa:1.0:action:delete-organizational-unit 
                    info:escidoc/names:aa:1.0:action:retrieve-organizational-unit 
                    info:escidoc/names:aa:1.0:action:retrieve-children-of-organizational-unit 
                    info:escidoc/names:aa:1.0:action:close-organizational-unit 
                    info:escidoc/names:aa:1.0:action:open-organizational-unit 
                </AttributeValue>
                <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </ActionMatch>
        </Action>
      </Actions>
    </Target>
  </Rule>
  <Rule RuleId="OU-Administrator-policy-rule-2" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:update-organizational-unit 
                </AttributeValue>
                <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </ActionMatch>
        </Action>
      </Actions>
    </Target>    
    <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:organizational-unit:hierarchical-parents-new" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-ou-administrator:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
    </Condition>
  </Rule>
  <Rule RuleId="OU-Administrator-policy-rule-3" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:create-grant 
                    info:escidoc/names:aa:1.0:action:revoke-grant 
                </AttributeValue>
                <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </ActionMatch>
        </Action>
      </Actions>
    </Target>
    <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on:hierarchical-parents" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-ou-administrator:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">OU-Administrator</AttributeValue>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:role:name" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
        </Apply>
    </Condition>
  </Rule>
  <Rule RuleId="OU-Administrator-policy-rule-4" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:create-user-group-grant 
                    info:escidoc/names:aa:1.0:action:revoke-user-group-grant 
                </AttributeValue>
                <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </ActionMatch>
        </Action>
      </Actions>
    </Target>
    <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:grant:assigned-on:hierarchical-parents" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-ou-administrator:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">OU-Administrator</AttributeValue>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:grant:role:name" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
        </Apply>
    </Condition>
  </Rule>
</Policy>';
 
  DBMS_LOB.WRITEAPPEND(TMP_CLOB, LENGTH(SRC_CHUNK), SRC_CHUNK);
  DBMS_LOB.CLOSE(TMP_CLOB);
 
  INSERT INTO aa.escidoc_policies (id, role_id, xml) VALUES 
  ('escidoc:policy-ou-administrator', 'escidoc:role-ou-administrator', TMP_CLOB);
END;
/

