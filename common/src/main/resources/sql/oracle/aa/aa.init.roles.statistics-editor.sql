    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Statistics-Editor</title>

        <para>internal id: escidoc:role-statistics-editor</para>
        <para>A Statistics-Editor is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create statistic-data-records.</para>
            </listitem>
            <listitem>
              <para>create, retrieve and delete aggregation-definitions.</para>
            </listitem>
            <listitem>
              <para>create, retrieve, update and delete report-definitions.</para>
            </listitem>
            <listitem>
              <para>retrieve and update scopes.</para>
            </listitem>
            <listitem>
              <para>preprocess statistic-data.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. It is restricted to a statistic-scope.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-statistics-editor', 'Statistics-Editor', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-editor', 'escidoc:role-statistics-editor', 'aggregation-definition', 
     'info:escidoc/names:aa:1.0:resource:aggregation-definition:scope',
     'scope');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-editor-2', 'escidoc:role-statistics-editor', 'report-definition', 
     'info:escidoc/names:aa:1.0:resource:report-definition:scope',
     'scope');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-editor-3', 'escidoc:role-statistics-editor', 'statistic-data', 
     'info:escidoc/names:aa:1.0:resource:statistic-data:scope',
     'scope');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-editor-4', 'escidoc:role-statistics-editor', 'scope', 
     'urn:oasis:names:tc:xacml:1.0:resource:resource-id',
     'scope');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-editor-5', 'escidoc:role-statistics-editor', 'preprocessing', 
     'info:escidoc/names:aa:1.0:resource:aggregation-definition:scope',
     'scope');

INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:statistics-editor-policy-1', 'escidoc:role-statistics-editor',
'<Policy PolicyId="Statistics-editor-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
</Policy>');
