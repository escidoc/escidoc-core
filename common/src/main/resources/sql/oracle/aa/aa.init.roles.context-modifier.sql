    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Context-Modifier</title>
        <para>internal id: escidoc:role-context-modifier</para>
        <para>A Context-Modifier is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve, modify, delete, open, close context granted for.</para>
            </listitem>
            <listitem>
              <para>grant other context-scoped roles to the context (s)he has privileges for (except Context Modifier role).</para>
            </listitem>
          </itemizedlist></para>
        <para>This role is a limited role. 
        It is restricted to a context.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-context-modifier', 'Context-Modifier', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
        /** 
         * Role Context-Modifier Scope
         */  
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-context-modifier', 'escidoc:role-context-modifier', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-context-modifier-2', 'escidoc:role-context-modifier', 'user-account', 
     null,
     null);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-context-modifier-3', 'escidoc:role-context-modifier', 'user-group', 
     null,
     null);


INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:policy-context-modifier', 'escidoc:role-context-modifier',
'<Policy PolicyId="Context-Modifier-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
            info:escidoc/names:aa:1.0:action:close-context 
            info:escidoc/names:aa:1.0:action:open-context 
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
  <Rule RuleId="Context-Modifier-policy-rule-1" Effect="Permit">
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
  </Rule>
  <Rule RuleId="Context-Modifier-policy-rule-2" Effect="Permit">
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
            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-context-modifier:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">Context-Modifier</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:role:name" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Apply>
    </Condition>
  </Rule>
  <Rule RuleId="Context-Modifier-policy-rule-3" Effect="Permit">
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
            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:grant:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-context-modifier:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">Context-Modifier</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:grant:role:name" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Apply>
    </Condition>
  </Rule>
</Policy>');
