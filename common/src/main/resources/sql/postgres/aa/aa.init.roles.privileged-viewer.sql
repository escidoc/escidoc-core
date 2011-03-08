    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Privileged-Viewer</title>

        <para>internal id: escidoc:role-privileged-viewer</para>
        <para>A Privileged-Viewer is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve content of items, no matter in what visibility.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. It is restricted to a context.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-privileged-viewer', 'Privileged-Viewer', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-privileged-viewer', 'escidoc:role-privileged-viewer', 'component', 
     'info:escidoc/names:aa:1.0:resource:component:item:context',
     'context');
     
INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:privileged-viewer-policy-1', 'escidoc:role-privileged-viewer',
'<Policy PolicyId="Privileged-Viewer-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
    <Rule RuleId="Privileged-Viewer-policy-rule-0" Effect="Permit">
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
    </Rule>
  </Policy>');
  