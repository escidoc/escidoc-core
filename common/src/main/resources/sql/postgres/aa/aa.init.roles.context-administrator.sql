    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Context-Administrator</title>
        <para>internal id: escidoc:role-context-administrator</para>
        <para>A Context-Administrator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create contexts.</para>
            </listitem>
            <listitem>
              <para>retrieve, modify, delete, open, close contexts (s)he created.</para>
            </listitem>
          </itemizedlist></para>
        <para>This role is a unlimited role. (Has no scope-definitions).</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-context-administrator', 'Context-Administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:policy-context-administrator', 'escidoc:role-context-administrator',
'<Policy PolicyId="Context-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
            info:escidoc/names:aa:1.0:action:create-context 
            info:escidoc/names:aa:1.0:action:retrieve-context 
            info:escidoc/names:aa:1.0:action:update-context 
            info:escidoc/names:aa:1.0:action:delete-context 
            info:escidoc/names:aa:1.0:action:close-context 
            info:escidoc/names:aa:1.0:action:open-context 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Context-Administrator-policy-rule-1" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:create-context 
                  </AttributeValue>
                  <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
              </ActionMatch>
          </Action>
      </Actions>
    </Target>
  </Rule>
  <Rule RuleId="Context-Administrator-policy-rule-2" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:retrieve-context 
                    info:escidoc/names:aa:1.0:action:update-context 
                    info:escidoc/names:aa:1.0:action:delete-context 
                    info:escidoc/names:aa:1.0:action:open-context 
                    info:escidoc/names:aa:1.0:action:close-context 
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
            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:context:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
    </Condition>
  </Rule>
</Policy>');
