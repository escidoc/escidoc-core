    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>OU-Administrator</title>
        <para>internal id: escidoc:role-ou-administrator</para>
        <para>A OU-Administrator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create organizational-units, if parent is in scope.</para>
            </listitem>
            <listitem>
              <para>retrieve, modify, open, close organizational-units if parent is in scope.</para>
            </listitem>
            <listitem>
              <para>grant Organizational Unit Administrator privileges for organizational-units in scope.</para>
            </listitem>
          </itemizedlist></para>
        <para>This role is a limited role. 
        It is restricted to an organizational-unit and implicitely to all its children.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-ou-administrator', 'OU-Administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
        /** 
         * Role OU-Administrator Scope
         */  
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-ou-administrator', 'escidoc:role-ou-administrator', 'organizational-unit', 
     'info:escidoc/names:aa:1.0:resource:organizational-unit:hierarchical-parents',
     'organizational-unit');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-ou-administrator-2', 'escidoc:role-ou-administrator', 'user-account', 
     null,
     null);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-ou-administrator-3', 'escidoc:role-ou-administrator', 'user-group', 
     null,
     null);

     
INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:policy-ou-administrator', 'escidoc:role-ou-administrator',
'<Policy PolicyId="OU-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
</Policy>');
