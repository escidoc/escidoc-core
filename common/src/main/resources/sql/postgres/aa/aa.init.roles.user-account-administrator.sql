    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>User-Account-Administrator</title>
        <para>internal id: escidoc:role-user-account-administrator</para>
        <para>A User-Account-Administrator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create user-accounts.</para>
            </listitem>
            <listitem>
              <para>retrieve, update, activate, deactivate user-accounts (s)he created.</para>
            </listitem>
          </itemizedlist></para>
        <para>This role is a unlimited role. (Has no scope-definitions).</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-user-account-administrator', 'User-Account-Administrator', 'escidoc:exuser1', CURRENT_TIMESTAMP, 'escidoc:exuser1',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:policy-user-account-administrator', 'escidoc:role-user-account-administrator',
'<Policy PolicyId="User-Account-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
            info:escidoc/names:aa:1.0:action:create-user-account 
            info:escidoc/names:aa:1.0:action:retrieve-user-account 
            info:escidoc/names:aa:1.0:action:update-user-account 
            info:escidoc/names:aa:1.0:action:activate-user-account 
            info:escidoc/names:aa:1.0:action:deactivate-user-account 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="User-Group-Administrator-policy-rule-1" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:create-user-account 
                  </AttributeValue>
                  <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
              </ActionMatch>
          </Action>
      </Actions>
    </Target>
  </Rule>
  <Rule RuleId="User-Group-Administrator-policy-rule-2" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:retrieve-user-account 
                    info:escidoc/names:aa:1.0:action:update-user-account 
                    info:escidoc/names:aa:1.0:action:activate-user-account 
                    info:escidoc/names:aa:1.0:action:deactivate-user-account 
                  </AttributeValue>
                  <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
              </ActionMatch>
          </Action>
      </Actions>
    </Target>
    <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
    </Condition>
  </Rule>
</Policy>');
