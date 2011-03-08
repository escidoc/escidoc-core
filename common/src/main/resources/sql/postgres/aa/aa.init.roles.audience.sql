    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Audience</title>

        <para>internal id: escidoc:role-audience</para>
        <para>An Audience is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve content of components with visibility=audience if item is released.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. 
        It is restricted to a component.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-audience', 'Audience', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
        /** 
         * Role Audience Scope
         */  
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-audience', 'escidoc:role-audience', 'component', 
     'info:escidoc/names:aa:1.0:resource:component-id',
     'component');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-audience1', 'escidoc:role-audience', 'component', 
     'info:escidoc/names:aa:1.0:resource:component:item:context',
     'component');

INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:audience-policy-1', 'escidoc:role-audience',
'<Policy PolicyId="Audience-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
                    info:escidoc/names:aa:1.0:action:retrieve-content 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>

        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Audience-policy-rule-0" Effect="Permit">
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
                info:escidoc/names:aa:1.0:action:retrieve-content 
            </AttributeValue>
            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>

          </ActionMatch>
        </Action>
      </Actions>
    </Target>
    <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
          <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>

          </Apply>
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">withdrawn</AttributeValue>
        </Apply>
      </Apply>
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
          <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:visibility" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">audience</AttributeValue>
      </Apply>

      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
          <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
      </Apply>
    </Condition>
  </Rule>
</Policy>');
